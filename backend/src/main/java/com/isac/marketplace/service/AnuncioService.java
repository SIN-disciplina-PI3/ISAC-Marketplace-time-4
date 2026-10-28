package com.isac.marketplace.service;

import com.isac.marketplace.dto.CriarAnuncioDTO;
import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AnuncioService {

    public static final int LIMITE_GRATUITO = 2;

    private final AnuncioRepository anuncioRepository;
    private final CategoriaRepository categoriaRepository;
    private final Clock clock;
    private final boolean moderacaoObrigatoria;
    private final int diasExpiracao;

    public AnuncioService(
            AnuncioRepository anuncioRepository,
            CategoriaRepository categoriaRepository,
            Clock clock,
            @Value("${moderacao.obrigatoria:false}") boolean moderacaoObrigatoria,
            @Value("${anuncio.dias-expiracao:15}") int diasExpiracao
    ) {
        this.anuncioRepository = anuncioRepository;
        this.categoriaRepository = categoriaRepository;
        this.clock = clock;
        this.moderacaoObrigatoria = moderacaoObrigatoria;
        this.diasExpiracao = diasExpiracao;
    }

    public Anuncio criarAnuncio(Loja loja, CriarAnuncioDTO dto) {
        if (loja == null) {
            throw new RegraNegocioException("LOJA_OBRIGATORIA", "A loja proprietária é obrigatória.");
        }
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            throw new RegraNegocioException("TITULO_OBRIGATORIO", "O título do anúncio é obrigatório.");
        }
        if (dto.preco() == null || dto.preco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("PRECO_INVALIDO", "O preço do anúncio deve ser maior que zero.");
        }

        verificarCotaLoja(loja);

        Categoria categoria = null;
        if (dto.categoriaId() != null) {
            categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("CATEGORIA_NAO_ENCONTRADA", "Categoria não encontrada."));
        }

        LocalDateTime agora = LocalDateTime.now(clock);

        Anuncio anuncio = new Anuncio();
        anuncio.setLoja(loja);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(dto.titulo().trim());
        anuncio.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        anuncio.setPreco(dto.preco());
        anuncio.setBairro((dto.bairro() != null && !dto.bairro().isBlank()) ? dto.bairro().trim() : loja.getBairro());
        anuncio.setTipo(dto.tipo() != null ? dto.tipo() : TipoAnuncio.PRODUTO);
        anuncio.setFotoUrl(dto.fotoUrl());
        anuncio.setDisponivel(true);
        anuncio.setDestaque(loja.isAssinanteAtivo());
        anuncio.setDataPublicacao(agora);
        anuncio.setDataExpiracao(agora.plusDays(diasExpiracao));
        anuncio.setAvisoExpiracaoEnviado(false);

        if (moderacaoObrigatoria) {
            anuncio.setStatus(StatusAnuncio.PENDENTE_MODERACAO);
        } else {
            anuncio.setStatus(StatusAnuncio.ATIVO);
        }

        return anuncioRepository.save(anuncio);
    }

    public void verificarCotaLoja(Loja loja) {
        boolean temAssinaturaAtiva = (loja.getPlano() == PlanoLoja.ASSINANTE && loja.getStatusAssinatura() == StatusAssinatura.ATIVA);
        if (!temAssinaturaAtiva) {
            long totalAtivos = anuncioRepository.countByLojaAndStatus(loja, StatusAnuncio.ATIVO);
            if (totalAtivos >= LIMITE_GRATUITO) {
                throw new RegraNegocioException(
                        "COTA_EXCEDIDA",
                        "Limite do plano gratuito atingido (" + LIMITE_GRATUITO + " anúncios ativos). Faça upgrade para o plano Assinante para anunciar sem limites."
                );
            }
        }
    }

    public Anuncio renovar(Long anuncioId, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para renovar este anúncio.");
        }

        if (anuncio.getStatus() == StatusAnuncio.REJEITADO) {
            throw new RegraNegocioException("ANUNCIO_REJEITADO", "Anúncios rejeitados pela moderação não podem ser renovados.");
        }

        LocalDateTime agora = LocalDateTime.now(clock);
        anuncio.setDataExpiracao(agora.plusDays(diasExpiracao));
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDisponivel(true);
        anuncio.setAvisoExpiracaoEnviado(false);

        return anuncioRepository.save(anuncio);
    }

    public Anuncio alterarDisponibilidade(Long anuncioId, boolean disponivel, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para alterar este anúncio.");
        }
        anuncio.setDisponivel(disponivel);
        return anuncioRepository.save(anuncio);
    }

    public Anuncio pausar(Long anuncioId, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para alterar este anúncio.");
        }
        anuncio.setStatus(StatusAnuncio.PAUSADO);
        return anuncioRepository.save(anuncio);
    }

    public Anuncio reativar(Long anuncioId, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para alterar este anúncio.");
        }
        verificarCotaLoja(anuncio.getLoja());

        LocalDateTime agora = LocalDateTime.now(clock);
        if (anuncio.getDataExpiracao().isBefore(agora)) {
            anuncio.setDataExpiracao(agora.plusDays(diasExpiracao));
        }
        anuncio.setStatus(StatusAnuncio.ATIVO);
        return anuncioRepository.save(anuncio);
    }

    public Anuncio aprovarModeracao(Long anuncioId) {
        Anuncio anuncio = buscarPorId(anuncioId);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setMotivoRejeicao(null);
        return anuncioRepository.save(anuncio);
    }

    public Anuncio rejeitarModeracao(Long anuncioId, String motivo) {
        Anuncio anuncio = buscarPorId(anuncioId);
        anuncio.setStatus(StatusAnuncio.REJEITADO);
        anuncio.setMotivoRejeicao(motivo != null ? motivo.trim() : "Violação dos termos da comunidade ISAC.");
        return anuncioRepository.save(anuncio);
    }

    public Anuncio atualizarFoto(Long anuncioId, String fotoUrl, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para alterar a foto deste anúncio.");
        }
        anuncio.setFotoUrl(fotoUrl);
        return anuncioRepository.save(anuncio);
    }

    public Anuncio atualizar(Long anuncioId, CriarAnuncioDTO dto, Loja loja) {
        Anuncio anuncio = buscarPorId(anuncioId);
        if (loja != null && !anuncio.getLoja().getId().equals(loja.getId())) {
            throw new RegraNegocioException("ACESSO_NEGADO", "Você não tem permissão para editar este anúncio.");
        }

        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            anuncio.setTitulo(dto.titulo().trim());
        }
        if (dto.descricao() != null) {
            anuncio.setDescricao(dto.descricao().trim());
        }
        if (dto.preco() != null) {
            if (dto.preco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("PRECO_INVALIDO", "O preço deve ser maior que zero.");
            }
            anuncio.setPreco(dto.preco());
        }
        if (dto.bairro() != null && !dto.bairro().isBlank()) {
            anuncio.setBairro(dto.bairro().trim());
        }
        if (dto.tipo() != null) {
            anuncio.setTipo(dto.tipo());
        }
        if (dto.categoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("CATEGORIA_NAO_ENCONTRADA", "Categoria não encontrada."));
            anuncio.setCategoria(categoria);
        }

        return anuncioRepository.save(anuncio);
    }

    @Transactional(readOnly = true)
    public Anuncio buscarPorId(Long id) {
        return anuncioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("ANUNCIO_NAO_ENCONTRADO", "Anúncio com ID " + id + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Anuncio> listarPorLoja(Loja loja) {
        return anuncioRepository.findByLojaOrderByDataPublicacaoDesc(loja);
    }
}

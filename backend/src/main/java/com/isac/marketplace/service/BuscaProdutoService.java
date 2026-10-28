package com.isac.marketplace.service;

import com.isac.marketplace.dto.AnuncioDTO;
import com.isac.marketplace.dto.ResultadoBuscaDTO;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.BuscaRepository;
import com.isac.marketplace.util.TelefoneUtil;
import com.isac.marketplace.util.TextoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuscaProdutoService {

    private final AnuncioRepository anuncioRepository;
    private final BuscaRepository buscaRepository;
    private final Clock clock;

    public BuscaProdutoService(
            AnuncioRepository anuncioRepository,
            BuscaRepository buscaRepository,
            Clock clock
    ) {
        this.anuncioRepository = anuncioRepository;
        this.buscaRepository = buscaRepository;
        this.clock = clock;
    }

    /**
     * Executa busca com filtros para a vitrine web, com paginação.
     */
    public ResultadoBuscaDTO buscarWeb(String termo, Long categoriaId, String bairro, int pagina, int tamanho) {
        validarTermoSePresente(termo);

        List<Anuncio> filtrados = executarFiltroGeral(termo, categoriaId, bairro);

        // Registra a busca para métricas
        registrarHistoricoBusca(termo != null ? termo.trim() : "", CanalBusca.SITE, filtrados.size(), null);

        int total = filtrados.size();
        int pagSegura = Math.max(0, pagina);
        int tamSeguro = Math.max(1, tamanho);
        int inicio = Math.min(pagSegura * tamSeguro, total);
        int fim = Math.min(inicio + tamSeguro, total);

        List<AnuncioDTO> paginaDTOs = filtrados.subList(inicio, fim)
                .stream()
                .map(AnuncioDTO::fromEntity)
                .toList();

        int totalPaginas = (int) Math.ceil((double) total / tamSeguro);

        return new ResultadoBuscaDTO(paginaDTOs, total, pagSegura, totalPaginas);
    }

    /**
     * Executa busca para o chatbot do WhatsApp.
     * Não repete a mesma loja e limita a quantidade inicial (ex: até 5 resultados).
     */
    public List<AnuncioDTO> buscarParaBot(String termo, String bairro, String telefoneUsuario, int limite) {
        if (termo == null || termo.trim().length() < 2) {
            throw new RegraNegocioException("TERMO_INVALIDO", "O termo de busca deve conter pelo menos 2 caracteres.");
        }

        List<Anuncio> filtrados = executarFiltroGeral(termo, null, bairro);

        // Registra busca com hash do telefone (LGPD)
        String hashTel = telefoneUsuario != null ? TelefoneUtil.gerarHash(telefoneUsuario) : null;
        registrarHistoricoBusca(termo.trim(), CanalBusca.WHATSAPP, filtrados.size(), hashTel);

        // Não repete a mesma loja na resposta do bot
        Set<Long> lojasVistas = new HashSet<>();
        List<AnuncioDTO> resultado = new ArrayList<>();

        for (Anuncio anuncio : filtrados) {
            Long lojaId = anuncio.getLoja().getId();
            if (!lojasVistas.contains(lojaId)) {
                lojasVistas.add(lojaId);
                resultado.add(AnuncioDTO.fromEntity(anuncio));
                if (resultado.size() >= limite) {
                    break;
                }
            }
        }

        return resultado;
    }

    private void validarTermoSePresente(String termo) {
        if (termo != null && !termo.isBlank()) {
            String limpo = termo.trim();
            if (limpo.length() < 2) {
                throw new RegraNegocioException("TERMO_INVALIDO", "O termo de busca deve conter pelo menos 2 caracteres.");
            }
        }
    }

    private List<Anuncio> executarFiltroGeral(String termo, Long categoriaId, String bairro) {
        LocalDateTime agora = LocalDateTime.now(clock);
        List<Anuncio> ativos = anuncioRepository.findAnunciosAtivosEDisponiveis(StatusAnuncio.ATIVO, agora);

        String termoNorm = (termo != null && !termo.isBlank()) ? TextoUtil.normalizar(termo) : null;
        String bairroNorm = (bairro != null && !bairro.isBlank()) ? TextoUtil.normalizar(bairro) : null;

        return ativos.stream()
                .filter(a -> a.isVisivelParaBusca(agora))
                .filter(a -> {
                    if (termoNorm == null) return true;
                    boolean matchTitulo = TextoUtil.contemTermo(a.getTitulo(), termoNorm);
                    boolean matchDesc = a.getDescricao() != null && TextoUtil.contemTermo(a.getDescricao(), termoNorm);
                    return matchTitulo || matchDesc;
                })
                .filter(a -> {
                    if (categoriaId == null) return true;
                    return a.getCategoria() != null && Objects.equals(a.getCategoria().getId(), categoriaId);
                })
                .filter(a -> {
                    if (bairroNorm == null) return true;
                    return TextoUtil.contemTermo(a.getBairro(), bairroNorm);
                })
                .sorted((a1, a2) -> {
                    // Destaque primeiro (assinantes)
                    boolean d1 = Boolean.TRUE.equals(a1.getDestaque());
                    boolean d2 = Boolean.TRUE.equals(a2.getDestaque());
                    if (d1 != d2) {
                        return d1 ? -1 : 1;
                    }
                    // Depois por data de publicação decrescente
                    return a2.getDataPublicacao().compareTo(a1.getDataPublicacao());
                })
                .collect(Collectors.toList());
    }

    private void registrarHistoricoBusca(String termo, CanalBusca canal, int totalResultados, String telefoneHash) {
        Busca busca = new Busca(
                termo.isEmpty() ? "*" : termo,
                canal,
                totalResultados,
                telefoneHash,
                LocalDateTime.now(clock)
        );
        buscaRepository.save(busca);
    }
}

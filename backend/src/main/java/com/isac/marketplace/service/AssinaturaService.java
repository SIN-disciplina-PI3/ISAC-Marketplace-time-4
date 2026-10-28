package com.isac.marketplace.service;

import com.isac.marketplace.exception.RecursoNaoEncontradoException;
import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.PagamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AssinaturaService implements PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(AssinaturaService.class);
    public static final BigDecimal VALOR_PADRAO_CICLO = new BigDecimal("29.90");

    private final LojaRepository lojaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final AnuncioRepository anuncioRepository;
    private final Clock clock;
    private final int diasCiclo;

    public AssinaturaService(
            LojaRepository lojaRepository,
            PagamentoRepository pagamentoRepository,
            AnuncioRepository anuncioRepository,
            Clock clock,
            @Value("${assinatura.dias-ciclo:15}") int diasCiclo
    ) {
        this.lojaRepository = lojaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.anuncioRepository = anuncioRepository;
        this.clock = clock;
        this.diasCiclo = diasCiclo;
    }

    /**
     * Loja solicita upgrade para plano ASSINANTE.
     * Fica com status PENDENTE até confirmação do pagamento.
     */
    public Loja solicitarUpgrade(Long lojaId) {
        Loja loja = buscarLojaPorId(lojaId);
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.PENDENTE);

        lojaRepository.save(loja);

        criarCobranca(loja, VALOR_PADRAO_CICLO, "Upgrade Plano Assinante");
        return loja;
    }

    @Override
    public Pagamento criarCobranca(Loja loja, BigDecimal valor, String referenciaCiclo) {
        Pagamento pagamento = new Pagamento();
        pagamento.setLoja(loja);
        pagamento.setValor(valor != null ? valor : VALOR_PADRAO_CICLO);
        pagamento.setReferenciaCiclo(referenciaCiclo != null ? referenciaCiclo : "Ciclo " + diasCiclo + " dias");
        pagamento.setStatus(StatusPagamento.PENDENTE);
        return pagamentoRepository.save(pagamento);
    }

    /**
     * Confirma o pagamento (feito pelo administrador do ISAC).
     * Ativa a assinatura por 'diasCiclo' dias a partir de hoje.
     */
    @Override
    public Pagamento confirmarPagamentoManual(Long pagamentoId, String confirmadoPor) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PAGAMENTO_NAO_ENCONTRADO", "Pagamento não encontrado."));

        if (pagamento.getStatus() == StatusPagamento.CONFIRMADO) {
            throw new RegraNegocioException("PAGAMENTO_JA_CONFIRMADO", "Este pagamento já foi confirmado anteriormente.");
        }

        LocalDateTime agora = LocalDateTime.now(clock);
        pagamento.setStatus(StatusPagamento.CONFIRMADO);
        pagamento.setConfirmadoEm(agora);
        pagamento.setConfirmadoPor(confirmadoPor != null ? confirmadoPor : "ADMIN_ISAC");

        Loja loja = pagamento.getLoja();
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);
        loja.setDataVencimentoAssinatura(agora.plusDays(diasCiclo));

        lojaRepository.save(loja);
        atualizarDestaqueAnuncios(loja, true);

        return pagamentoRepository.save(pagamento);
    }

    /**
     * Confirma pagamento diretamente pelo ID da loja (cria ou aprova a cobrança pendente).
     */
    public Loja confirmarPagamentoParaLoja(Long lojaId, String confirmadoPor) {
        Loja loja = buscarLojaPorId(lojaId);
        List<Pagamento> pagamentos = pagamentoRepository.findByLojaOrderByIdDesc(loja);

        Pagamento pendente = pagamentos.stream()
                .filter(p -> p.getStatus() == StatusPagamento.PENDENTE)
                .findFirst()
                .orElseGet(() -> criarCobranca(loja, VALOR_PADRAO_CICLO, "Ciclo manual admin"));

        confirmarPagamentoManual(pendente.getId(), confirmadoPor);
        return lojaRepository.findById(lojaId).orElse(loja);
    }

    @Override
    public Pagamento cancelarCobranca(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("PAGAMENTO_NAO_ENCONTRADO", "Pagamento não encontrado."));

        pagamento.setStatus(StatusPagamento.CANCELADO);
        return pagamentoRepository.save(pagamento);
    }

    /**
     * Cancela a assinatura da loja, voltando para o plano gratuito ou status INATIVA.
     */
    public Loja cancelarAssinatura(Long lojaId) {
        Loja loja = buscarLojaPorId(lojaId);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);
        lojaRepository.save(loja);

        ajustarCotaGratuitaParaLoja(loja);
        return loja;
    }

    /**
     * Inativa assinaturas com vencimento anterior a hoje.
     * Regra: Se vence hoje, ainda vale. Sem data de vencimento não é inativada.
     */
    public int inativarAssinaturasVencidas() {
        LocalDateTime agora = LocalDateTime.now(clock);
        LocalDate hoje = agora.toLocalDate();

        List<Loja> assinantesAtivos = lojaRepository.findByPlanoAndStatusAssinatura(PlanoLoja.ASSINANTE, StatusAssinatura.ATIVA);
        int totalInativadas = 0;

        for (Loja loja : assinantesAtivos) {
            LocalDateTime vencimento = loja.getDataVencimentoAssinatura();
            if (vencimento != null && vencimento.toLocalDate().isBefore(hoje)) {
                log.info("Inativando assinatura vencida da loja {} (vencimento: {})", loja.getNomeFantasia(), vencimento);
                loja.setStatusAssinatura(StatusAssinatura.INATIVA);
                lojaRepository.save(loja);
                ajustarCotaGratuitaParaLoja(loja);
                totalInativadas++;
            }
        }

        return totalInativadas;
    }

    /**
     * Quando uma loja perde a assinatura ativa, seus anúncios perdem destaque
     * e os anúncios ativos além do limite gratuito (2) são pausados.
     */
    public void ajustarCotaGratuitaParaLoja(Loja loja) {
        List<Anuncio> anuncios = anuncioRepository.findByLojaOrderByDataPublicacaoDesc(loja);
        int ativosCount = 0;

        for (Anuncio anuncio : anuncios) {
            anuncio.setDestaque(false);
            if (anuncio.getStatus() == StatusAnuncio.ATIVO) {
                ativosCount++;
                if (ativosCount > AnuncioService.LIMITE_GRATUITO) {
                    anuncio.setStatus(StatusAnuncio.PAUSADO);
                    log.info("Anúncio '{}' (ID {}) pausado por exceder cota gratuita da loja {}",
                            anuncio.getTitulo(), anuncio.getId(), loja.getNomeFantasia());
                }
            }
            anuncioRepository.save(anuncio);
        }
    }

    private void atualizarDestaqueAnuncios(Loja loja, boolean destaque) {
        List<Anuncio> anuncios = anuncioRepository.findByLoja(loja);
        for (Anuncio anuncio : anuncios) {
            anuncio.setDestaque(destaque);
            anuncioRepository.save(anuncio);
        }
    }

    private Loja buscarLojaPorId(Long id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("LOJA_NAO_ENCONTRADA", "Loja com ID " + id + " não encontrada."));
    }
}

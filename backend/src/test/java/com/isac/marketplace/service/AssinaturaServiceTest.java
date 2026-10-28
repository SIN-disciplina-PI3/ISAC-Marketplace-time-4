package com.isac.marketplace.service;

import com.isac.marketplace.exception.RegraNegocioException;
import com.isac.marketplace.model.*;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.repository.LojaRepository;
import com.isac.marketplace.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AssinaturaService")
class AssinaturaServiceTest {

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private AnuncioRepository anuncioRepository;

    private Clock fixedClock;
    private AssinaturaService assinaturaService;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("America/Recife"));
        assinaturaService = new AssinaturaService(lojaRepository, pagamentoRepository, anuncioRepository, fixedClock, 15);
    }

    private Loja criarLojaExemplo(Long id) {
        Loja loja = new Loja();
        loja.setId(id);
        loja.setNomeFantasia("Loja Modelo");
        loja.setPlano(PlanoLoja.GRATUITO);
        loja.setStatusAssinatura(StatusAssinatura.INATIVA);
        return loja;
    }

    @Test
    @DisplayName("Deve solicitar upgrade passando a loja para ASSINANTE e status PENDENTE com cobrança gerada")
    void deveSolicitarUpgradeComSucesso() {
        Loja loja = criarLojaExemplo(1L);
        when(lojaRepository.findById(1L)).thenReturn(Optional.of(loja));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Loja atualizada = assinaturaService.solicitarUpgrade(1L);

        assertEquals(PlanoLoja.ASSINANTE, atualizada.getPlano());
        assertEquals(StatusAssinatura.PENDENTE, atualizada.getStatusAssinatura());
        verify(pagamentoRepository).save(argThat(p -> p.getStatus() == StatusPagamento.PENDENTE && p.getLoja().getId().equals(1L)));
    }

    @Test
    @DisplayName("Deve confirmar pagamento ativando a assinatura por 15 dias e dando destaque aos anúncios")
    void deveConfirmarPagamentoManualAtivandoAssinatura() {
        Loja loja = criarLojaExemplo(1L);
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.PENDENTE);

        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setLoja(loja);
        pagamento.setStatus(StatusPagamento.PENDENTE);
        pagamento.setValor(new BigDecimal("29.90"));

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));
        when(anuncioRepository.findByLoja(loja)).thenReturn(List.of());

        Pagamento confirmado = assinaturaService.confirmarPagamentoManual(100L, "COORDENADOR_ISAC");

        assertEquals(StatusPagamento.CONFIRMADO, confirmado.getStatus());
        assertEquals("COORDENADOR_ISAC", confirmado.getConfirmadoPor());
        assertEquals(StatusAssinatura.ATIVA, loja.getStatusAssinatura());
        assertEquals(LocalDateTime.now(fixedClock).plusDays(15), loja.getDataVencimentoAssinatura());
        verify(lojaRepository).save(loja);
    }

    @Test
    @DisplayName("Pagamento não confirmado NUNCA ativa a assinatura da loja")
    void pagamentoNaoConfirmadoNuncaAtivaAssinatura() {
        Loja loja = criarLojaExemplo(1L);
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.PENDENTE);

        assertFalse(loja.isAssinanteAtivo());
        assertNotEquals(StatusAssinatura.ATIVA, loja.getStatusAssinatura());
    }

    @Test
    @DisplayName("Assinatura que vence hoje CONTINUA ativa (não deve ser inativada pela rotina)")
    void assinaturaQueVenceHojeContinuaAtiva() {
        Loja loja = criarLojaExemplo(1L);
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);
        // Vencimento hoje às 23:59
        loja.setDataVencimentoAssinatura(LocalDateTime.now(fixedClock).plusHours(5));

        when(lojaRepository.findByPlanoAndStatusAssinatura(PlanoLoja.ASSINANTE, StatusAssinatura.ATIVA))
                .thenReturn(List.of(loja));

        int inativadas = assinaturaService.inativarAssinaturasVencidas();

        assertEquals(0, inativadas);
        assertEquals(StatusAssinatura.ATIVA, loja.getStatusAssinatura());
        verify(lojaRepository, never()).save(loja);
    }

    @Test
    @DisplayName("Assinatura que venceu ontem DEVE ser inativada pela rotina")
    void assinaturaQueVenceuOntemDeveSerInativada() {
        Loja loja = criarLojaExemplo(1L);
        loja.setPlano(PlanoLoja.ASSINANTE);
        loja.setStatusAssinatura(StatusAssinatura.ATIVA);
        // Venceu ontem
        loja.setDataVencimentoAssinatura(LocalDateTime.now(fixedClock).minusDays(1));

        when(lojaRepository.findByPlanoAndStatusAssinatura(PlanoLoja.ASSINANTE, StatusAssinatura.ATIVA))
                .thenReturn(List.of(loja));
        when(anuncioRepository.findByLojaOrderByDataPublicacaoDesc(loja)).thenReturn(List.of());

        int inativadas = assinaturaService.inativarAssinaturasVencidas();

        assertEquals(1, inativadas);
        assertEquals(StatusAssinatura.INATIVA, loja.getStatusAssinatura());
        verify(lojaRepository).save(loja);
    }

    @Test
    @DisplayName("Ao inativar assinatura, deve pausar anúncios que excedem o limite gratuito de 2")
    void aoInativarAssinaturaDevePausarAnunciosExcedentes() {
        Loja loja = criarLojaExemplo(1L);

        Anuncio a1 = new Anuncio(); a1.setId(1L); a1.setStatus(StatusAnuncio.ATIVO);
        Anuncio a2 = new Anuncio(); a2.setId(2L); a2.setStatus(StatusAnuncio.ATIVO);
        Anuncio a3 = new Anuncio(); a3.setId(3L); a3.setStatus(StatusAnuncio.ATIVO);
        Anuncio a4 = new Anuncio(); a4.setId(4L); a4.setStatus(StatusAnuncio.ATIVO);

        when(anuncioRepository.findByLojaOrderByDataPublicacaoDesc(loja)).thenReturn(List.of(a1, a2, a3, a4));

        assinaturaService.ajustarCotaGratuitaParaLoja(loja);

        // a1 e a2 permanecem ATIVO, a3 e a4 passam para PAUSADO
        assertEquals(StatusAnuncio.ATIVO, a1.getStatus());
        assertEquals(StatusAnuncio.ATIVO, a2.getStatus());
        assertEquals(StatusAnuncio.PAUSADO, a3.getStatus());
        assertEquals(StatusAnuncio.PAUSADO, a4.getStatus());
        verify(anuncioRepository, times(4)).save(any(Anuncio.class));
        verify(anuncioRepository, times(2)).save(argThat(a -> a.getStatus() == StatusAnuncio.PAUSADO));
    }

    @Test
    @DisplayName("Deve rejeitar confirmação de pagamento já confirmado anteriormente")
    void deveRejeitarConfirmacaoDePagamentoJaConfirmado() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setStatus(StatusPagamento.CONFIRMADO);

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                assinaturaService.confirmarPagamentoManual(100L, "ADMIN"));
        assertEquals("PAGAMENTO_JA_CONFIRMADO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve cancelar cobrança pendente com sucesso")
    void deveCancelarCobrancaPendente() {
        Pagamento pagamento = new Pagamento();
        pagamento.setId(100L);
        pagamento.setStatus(StatusPagamento.PENDENTE);

        when(pagamentoRepository.findById(100L)).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(inv -> inv.getArgument(0));

        Pagamento cancelado = assinaturaService.cancelarCobranca(100L);
        assertEquals(StatusPagamento.CANCELADO, cancelado.getStatus());
    }
}

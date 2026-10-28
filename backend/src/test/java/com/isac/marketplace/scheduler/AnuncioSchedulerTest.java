package com.isac.marketplace.scheduler;

import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.repository.AnuncioRepository;
import com.isac.marketplace.whatsapp.WhatsAppGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AnuncioScheduler")
class AnuncioSchedulerTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private WhatsAppGateway whatsAppGateway;

    private Clock fixedClock;
    private AnuncioScheduler scheduler;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T10:00:00Z"), ZoneId.of("America/Recife"));
        scheduler = new AnuncioScheduler(anuncioRepository, whatsAppGateway, fixedClock, 2);
    }

    @Test
    @DisplayName("Deve expirar anúncios com data de expiração anterior a agora")
    void deveExpirarAnunciosVencidos() {
        Anuncio a1 = new Anuncio();
        a1.setId(1L);
        a1.setTitulo("Tapioca Vencida");
        a1.setStatus(StatusAnuncio.ATIVO);
        a1.setDataExpiracao(LocalDateTime.now(fixedClock).minusMinutes(5));

        when(anuncioRepository.findByStatusAndDataExpiracaoBefore(eq(StatusAnuncio.ATIVO), any()))
                .thenReturn(List.of(a1));

        int expirados = scheduler.expirarAnunciosVencidos();

        assertEquals(1, expirados);
        assertEquals(StatusAnuncio.EXPIRADO, a1.getStatus());
        verify(anuncioRepository).save(a1);
    }

    @Test
    @DisplayName("Deve enviar aviso de pré-expiração por WhatsApp 2 dias antes e marcar aviso como enviado")
    void deveEnviarAvisoPreExpiracao() {
        Loja loja = new Loja();
        loja.setNomeFantasia("Tapiocaria");
        loja.setTelefoneWhatsapp("81999990001");

        Anuncio a1 = new Anuncio();
        a1.setId(5L);
        a1.setTitulo("Bolo de Rolo");
        a1.setLoja(loja);
        a1.setStatus(StatusAnuncio.ATIVO);
        a1.setAvisoExpiracaoEnviado(false);
        a1.setDataExpiracao(LocalDateTime.now(fixedClock).plusDays(1)); // expira amanhã

        when(anuncioRepository.findByStatusAndAvisoExpiracaoEnviadoFalseAndDataExpiracaoBetween(
                eq(StatusAnuncio.ATIVO), any(), any()
        )).thenReturn(List.of(a1));

        when(whatsAppGateway.enviarMensagem(eq("81999990001"), contains("Bolo de Rolo"))).thenReturn(true);

        int avisos = scheduler.enviarAvisosPreExpiracao();

        assertEquals(1, avisos);
        assertTrue(a1.getAvisoExpiracaoEnviado());
        verify(whatsAppGateway).enviarMensagem(eq("81999990001"), anyString());
        verify(anuncioRepository).save(a1);
    }
}

package com.isac.marketplace.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.model.ConversaWhatsapp;
import com.isac.marketplace.repository.ConversaWhatsappRepository;
import com.isac.marketplace.util.TelefoneUtil;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do ConversaService (Máquina de Estados)")
class ConversaServiceTest {

    @Mock
    private ConversaWhatsappRepository repository;

    private Clock fixedClock;
    private ConversaService conversaService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-10-28T12:00:00Z"), ZoneId.of("America/Recife"));
        conversaService = new ConversaService(repository, objectMapper, fixedClock);
    }

    @Test
    @DisplayName("Deve salvar e recuperar estado da conversa com dados parciais")
    void deveSalvarERecuperarEstado() {
        String telefone = "81999990001";
        String hash = TelefoneUtil.gerarHash(telefone);

        ConversaWhatsapp entity = new ConversaWhatsapp();
        entity.setTelefoneHash(hash);
        entity.setEtapa("ANUNCIAR_TITULO");
        entity.setDadosParciais("{\"nomeLoja\":\"Tapiocaria\"}");
        entity.setAtualizadoEm(LocalDateTime.now(fixedClock));

        when(repository.findById(hash)).thenReturn(Optional.of(entity));

        Optional<ConversaService.EstadoConversa> estadoOpt = conversaService.obterEstado(telefone);

        assertTrue(estadoOpt.isPresent());
        assertEquals("ANUNCIAR_TITULO", estadoOpt.get().etapa());
        assertEquals("Tapiocaria", estadoOpt.get().dados().get("nomeLoja"));
    }

    @Test
    @DisplayName("Deve expirar estado da conversa após 30 minutos de inatividade e resetar")
    void deveExpirarApos30MinutosDeInatividade() {
        String telefone = "81999990001";
        String hash = TelefoneUtil.gerarHash(telefone);

        ConversaWhatsapp expirada = new ConversaWhatsapp();
        expirada.setTelefoneHash(hash);
        expirada.setEtapa("ANUNCIAR_PRECO");
        expirada.setDadosParciais("{}");
        // Inativo há 35 minutos
        expirada.setAtualizadoEm(LocalDateTime.now(fixedClock).minusMinutes(35));

        when(repository.findById(hash)).thenReturn(Optional.of(expirada));

        Optional<ConversaService.EstadoConversa> estadoOpt = conversaService.obterEstado(telefone);

        assertFalse(estadoOpt.isPresent());
        verify(repository).delete(expirada);
    }

    @Test
    @DisplayName("Deve limpar o estado da conversa com sucesso")
    void deveLimparEstado() {
        String telefone = "81999990001";
        String hash = TelefoneUtil.gerarHash(telefone);

        conversaService.limparEstado(telefone);

        verify(repository).deleteById(hash);
    }

    @Test
    @DisplayName("Deve retornar vazio para telefone nulo ou em branco")
    void deveRetornarVazioParaTelefoneNulo() {
        assertFalse(conversaService.obterEstado(null).isPresent());
        assertFalse(conversaService.obterEstado("").isPresent());
    }
}

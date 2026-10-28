package com.isac.marketplace.whatsapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do LogWhatsAppGateway")
class LogWhatsAppGatewayTest {

    private LogWhatsAppGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new LogWhatsAppGateway();
    }

    @Test
    @DisplayName("Deve registrar mensagens enviadas em memória para verificação de testes")
    void deveRegistrarMensagemEmMemoria() {
        boolean sucesso = gateway.enviarMensagem("81999990001", "Mensagem de teste");

        assertTrue(sucesso);
        assertEquals(1, gateway.getMensagensEnviadas().size());
        assertEquals("81999990001", gateway.getMensagensEnviadas().get(0).telefone());
        assertEquals("Mensagem de teste", gateway.getMensagensEnviadas().get(0).texto());
    }

    @Test
    @DisplayName("Deve limpar histórico de mensagens enviadas")
    void deveLimparHistorico() {
        gateway.enviarMensagem("81999990001", "Msg 1");
        gateway.enviarMensagem("81999990002", "Msg 2");
        assertEquals(2, gateway.getMensagensEnviadas().size());

        gateway.limpar();
        assertEquals(0, gateway.getMensagensEnviadas().size());
    }
}

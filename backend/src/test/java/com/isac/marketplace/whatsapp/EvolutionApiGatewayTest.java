package com.isac.marketplace.whatsapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EvolutionApiGateway")
class EvolutionApiGatewayTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private EvolutionApiGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new EvolutionApiGateway(
                "http://localhost:8081",
                "chave-teste-api",
                "isac",
                httpClient,
                new ObjectMapper()
        );
    }

    @Test
    @DisplayName("Deve normalizar número adicionando 55 se ausente")
    void deveNormalizarNumeroParaEnvio() {
        assertEquals("5581999990001", EvolutionApiGateway.normalizarNumeroParaEnvio("81999990001"));
        assertEquals("5581999990001", EvolutionApiGateway.normalizarNumeroParaEnvio("5581999990001"));
        assertEquals("5581999990001", EvolutionApiGateway.normalizarNumeroParaEnvio("+55 (81) 99999-0001"));
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso quando a Evolution API retorna 200 OK")
    void deveEnviarMensagemComSucesso() throws Exception {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        boolean sucesso = gateway.enviarMensagem("81999990001", "Olá!");

        assertTrue(sucesso);
        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Deve retornar false sem lançar exceção quando a Evolution API retorna erro 500")
    void deveRetornarFalseEmErroHttp() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("{\"error\": \"Falha interna na Evolution\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        boolean sucesso = gateway.enviarMensagem("81999990001", "Olá!");

        assertFalse(sucesso);
    }

    @Test
    @DisplayName("Deve retornar false sem derrubar a aplicação quando ocorre falha de conexão/timeout")
    void deveTratarExcecaoDeConexao() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Connection refused"));

        boolean sucesso = gateway.enviarMensagem("81999990001", "Olá!");

        assertFalse(sucesso);
    }

    @Test
    @DisplayName("Deve retornar false imediatamente para telefone ou texto nulo/vazio")
    void deveRejeitarParametrosNulos() {
        assertFalse(gateway.enviarMensagem(null, "texto"));
        assertFalse(gateway.enviarMensagem("81999990001", null));
        assertFalse(gateway.enviarMensagem("81999990001", "   "));
        verifyNoInteractions(httpClient);
    }
}

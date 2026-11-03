package com.isac.marketplace.storage;

import com.isac.marketplace.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ArmazenamentoSupabaseFotos")
class ArmazenamentoSupabaseFotosTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ArmazenamentoSupabaseFotos armazenamento;

    @BeforeEach
    void setUp() {
        armazenamento = new ArmazenamentoSupabaseFotos(
                "https://xyz.supabase.co",
                "chave-anon-teste",
                "anuncios",
                httpClient
        );
    }

    @Test
    @DisplayName("Deve enviar foto para Supabase Storage e retornar URL pública quando status for 200 OK")
    void deveEnviarFotoComSucesso() throws Exception {
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        byte[] bytes = "conteudo-da-imagem".getBytes();
        String url = armazenamento.armazenar(bytes, "produto.jpg", "image/jpeg");

        assertNotNull(url);
        assertTrue(url.startsWith("https://xyz.supabase.co/storage/v1/object/public/anuncios/"));
        assertTrue(url.endsWith(".jpg"));
        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando Supabase Storage retornar erro HTTP")
    void deveLancarExcecaoEmErroSupabase() throws Exception {
        when(httpResponse.statusCode()).thenReturn(403);
        when(httpResponse.body()).thenReturn("{\"error\": \"Unauthorized\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        byte[] bytes = "conteudo-da-imagem".getBytes();
        assertThrows(RuntimeException.class, () ->
                armazenamento.armazenar(bytes, "produto.jpg", "image/jpeg"));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo vazio antes de chamar HTTP")
    void deveRejeitarArquivoVazio() {
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar(new byte[0], "foto.jpg", "image/jpeg"));
        assertEquals("ARQUIVO_VAZIO", ex.getCodigo());
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("Deve rejeitar arquivo maior que 5 MB")
    void deveRejeitarArquivoMaiorQue5MB() {
        byte[] bytesGrandes = new byte[(int) ArmazenamentoSupabaseFotos.TAMANHO_MAXIMO_BYTES + 10];
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar(bytesGrandes, "foto.jpg", "image/jpeg"));
        assertEquals("ARQUIVO_MUITO_GRANDE", ex.getCodigo());
        verifyNoInteractions(httpClient);
    }

    @Test
    @DisplayName("Deve rejeitar tipo MIME inválido")
    void deveRejeitarTipoMimeInvalido() {
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar("dados".getBytes(), "script.sh", "application/x-sh"));
        assertEquals("TIPO_ARQUIVO_INVALIDO", ex.getCodigo());
        verifyNoInteractions(httpClient);
    }
}

package com.isac.marketplace.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve mapear RegraNegocioException para HTTP 400 com código e mensagem")
    void deveMapearRegraNegocioException() {
        RegraNegocioException ex = new RegraNegocioException("COTA_EXCEDIDA", "Limite atingido");
        ResponseEntity<ErroResposta> response = exceptionHandler.handleRegraNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("COTA_EXCEDIDA", response.getBody().getCodigo());
        assertEquals("Limite atingido", response.getBody().getMensagem());
    }

    @Test
    @DisplayName("Deve mapear RecursoNaoEncontradoException para HTTP 404")
    void deveMapearRecursoNaoEncontradoException() {
        RecursoNaoEncontradoException ex = new RecursoNaoEncontradoException("LOJA_NAO_ENCONTRADA", "Loja inexistente");
        ResponseEntity<ErroResposta> response = exceptionHandler.handleRecursoNaoEncontrado(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("LOJA_NAO_ENCONTRADA", response.getBody().getCodigo());
        assertEquals("Loja inexistente", response.getBody().getMensagem());
    }

    @Test
    @DisplayName("Deve mapear AccessDeniedException para HTTP 403")
    void deveMapearAccessDeniedException() {
        AccessDeniedException ex = new AccessDeniedException("Não permitido");
        ResponseEntity<ErroResposta> response = exceptionHandler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ACESSO_NEGADO", response.getBody().getCodigo());
    }

    @Test
    @DisplayName("Deve mapear MaxUploadSizeExceededException para HTTP 400")
    void deveMapearMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5000000);
        ResponseEntity<ErroResposta> response = exceptionHandler.handleMaxUploadSize(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("ARQUIVO_MUITO_GRANDE", response.getBody().getCodigo());
    }

    @Test
    @DisplayName("Deve mapear exceções não tratadas para HTTP 500 sem vazar stack trace")
    void deveMapearExcecaoGenerica() {
        RuntimeException ex = new RuntimeException("Erro inesperado de banco de dados");
        ResponseEntity<ErroResposta> response = exceptionHandler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO_INTERNO", response.getBody().getCodigo());
        assertFalse(response.getBody().getMensagem().contains("banco de dados"));
    }
}

package com.isac.marketplace.storage;

import com.isac.marketplace.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ArmazenamentoLocalFotos")
class ArmazenamentoLocalFotosTest {

    @TempDir
    Path tempDir;

    private ArmazenamentoLocalFotos armazenamento;

    @BeforeEach
    void setUp() {
        armazenamento = new ArmazenamentoLocalFotos(tempDir.toString());
    }

    @Test
    @DisplayName("Deve armazenar foto válida em disco e retornar caminho relativo")
    void deveArmazenarFotoValida() {
        byte[] bytes = "conteudo-da-imagem-jpg".getBytes();
        String url = armazenamento.armazenar(bytes, "tapioca.jpg", "image/jpeg");

        assertNotNull(url);
        assertTrue(url.startsWith("/uploads/anuncios/"));
        assertTrue(url.endsWith(".jpg"));

        File dir = tempDir.toFile();
        File[] arquivos = dir.listFiles();
        assertNotNull(arquivos);
        assertEquals(1, arquivos.length);
    }

    @Test
    @DisplayName("Deve armazenar imagem PNG e WEBP com extensão correta")
    void deveArmazenarPngEWebp() {
        byte[] bytes = "imagem-fake".getBytes();

        String urlPng = armazenamento.armazenar(bytes, "foto.png", "image/png");
        assertTrue(urlPng.endsWith(".png"));

        String urlWebp = armazenamento.armazenar(bytes, "foto.webp", "image/webp");
        assertTrue(urlWebp.endsWith(".webp"));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo vazio")
    void deveRejeitarArquivoVazio() {
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar(new byte[0], "teste.jpg", "image/jpeg"));
        assertEquals("ARQUIVO_VAZIO", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar arquivo maior que 5 MB")
    void deveRejeitarArquivoMuitoGrande() {
        byte[] bytesGrandes = new byte[(int) ArmazenamentoLocalFotos.TAMANHO_MAXIMO_BYTES + 10];
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar(bytesGrandes, "pesado.jpg", "image/jpeg"));
        assertEquals("ARQUIVO_MUITO_GRANDE", ex.getCodigo());
    }

    @Test
    @DisplayName("Deve rejeitar tipos MIME não permitidos (ex: PDF ou executável)")
    void deveRejeitarTipoNaoPermitido() {
        byte[] bytes = "conteudo".getBytes();
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () ->
                armazenamento.armazenar(bytes, "documento.pdf", "application/pdf"));
        assertEquals("TIPO_ARQUIVO_INVALIDO", ex.getCodigo());
    }
}

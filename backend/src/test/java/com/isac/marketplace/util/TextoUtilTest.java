package com.isac.marketplace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Utilitários de Texto (TextoUtil)")
class TextoUtilTest {

    @Test
    @DisplayName("Deve remover acentuação gráfica e padronizar minúsculas")
    void deveRemoverAcentuacaoECaixa() {
        assertEquals("tapioca", TextoUtil.normalizar("Tapioca"));
        assertEquals("feijao", TextoUtil.normalizar("Feijão"));
        assertEquals("acai", TextoUtil.normalizar("Açaí"));
        assertEquals("enseada dos corais", TextoUtil.normalizar("Enseada dos Coráis"));
    }

    @Test
    @DisplayName("Deve verificar se texto base contém termo ignorando acentos e maiúsculas")
    void deveVerificarContemTermoSemAcentos() {
        assertTrue(TextoUtil.contemTermo("Tapioca com Queijo Coalho", "tapioca"));
        assertTrue(TextoUtil.contemTermo("Bolo de Rolo Tradicional", "bolo"));
        assertTrue(TextoUtil.contemTermo("Feijão Macáçar Fresco", "feijao"));
        assertTrue(TextoUtil.contemTermo("Açaí da Terra", "acai"));
    }

    @Test
    @DisplayName("Deve retornar falso se base ou termo for nulo")
    void deveRetornarFalsoParaNulo() {
        assertFalse(TextoUtil.contemTermo(null, "teste"));
        assertFalse(TextoUtil.contemTermo("base", null));
        assertEquals("", TextoUtil.normalizar(null));
    }
}

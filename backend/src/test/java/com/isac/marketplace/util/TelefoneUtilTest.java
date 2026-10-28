package com.isac.marketplace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Utilitários de Telefone (TelefoneUtil)")
class TelefoneUtilTest {

    @Test
    @DisplayName("Deve normalizar telefone com DDI 55 e caracteres especiais")
    void deveNormalizarTelefoneComDdiECaracteres() {
        assertEquals("81999990001", TelefoneUtil.normalizar("+55 (81) 99999-0001"));
        assertEquals("81999990001", TelefoneUtil.normalizar("5581999990001"));
    }

    @Test
    @DisplayName("Deve normalizar telefone com sufixo do WhatsApp @s.whatsapp.net")
    void deveNormalizarTelefoneComSufixoWhatsApp() {
        assertEquals("81999990001", TelefoneUtil.normalizar("5581999990001@s.whatsapp.net"));
    }

    @Test
    @DisplayName("Deve retornar null se telefone informado for nulo")
    void deveRetornarNullParaTelefoneNulo() {
        assertNull(TelefoneUtil.normalizar(null));
        assertNull(TelefoneUtil.gerarHash(null));
    }

    @Test
    @DisplayName("Deve gerar hash SHA-256 consistente para LGPD")
    void deveGerarHashConsistente() {
        String hash1 = TelefoneUtil.gerarHash("81999990001");
        String hash2 = TelefoneUtil.gerarHash("+55 (81) 99999-0001");
        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Deve gerar link wa.me com mensagem pré-preenchida codificada")
    void deveGerarLinkWaMeComMensagem() {
        String link = TelefoneUtil.gerarLinkWaMe("81999990001", "Olá! Vi seu anúncio no ISAC.");
        assertTrue(link.startsWith("https://wa.me/5581999990001?text="));
        assertTrue(link.contains("ISAC"));
    }
}

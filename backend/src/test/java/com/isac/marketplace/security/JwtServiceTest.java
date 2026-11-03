package com.isac.marketplace.security;

import com.isac.marketplace.model.Papel;
import com.isac.marketplace.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    // 256-bit secret key for testing
    private final String secretKey = "1234567890123456789012345678901234567890123456789012345678901234";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secretKey, 3600000L); // 1 hora
    }

    @Test
    @DisplayName("Deve gerar token JWT válido com claims de papel e email")
    void deveGerarTokenValido() {
        Usuario usuario = new Usuario(1L, "admin@isac.org.br", "hash", Papel.ADMIN, null, true);

        String token = jwtService.gerarToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtService.isTokenValido(token));
        assertEquals("admin@isac.org.br", jwtService.extrairEmail(token));
        assertEquals("ROLE_ADMIN", jwtService.extrairPapel(token));
    }

    @Test
    @DisplayName("Deve invalidar token malformado ou alterado")
    void deveInvalidarTokenMalformado() {
        assertFalse(jwtService.isTokenValido("token.invalido.aqui"));
        assertFalse(jwtService.isTokenValido(null));
        assertFalse(jwtService.isTokenValido(""));
    }

    @Test
    @DisplayName("Deve invalidar token expirado")
    void deveInvalidarTokenExpirado() {
        JwtService jwtExpirado = new JwtService(secretKey, -1000L); // já expirado
        Usuario usuario = new Usuario(2L, "loja@isac.org.br", "hash", Papel.LOJA, null, true);

        String token = jwtExpirado.gerarToken(usuario);

        assertFalse(jwtExpirado.isTokenValido(token));
    }
}

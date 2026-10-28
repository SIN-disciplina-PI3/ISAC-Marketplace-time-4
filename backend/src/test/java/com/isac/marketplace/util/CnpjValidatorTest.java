package com.isac.marketplace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Validador de CNPJ")
class CnpjValidatorTest {

    @Test
    @DisplayName("Deve validar CNPJs válidos especificados no projeto")
    void deveValidarCnpjsValidos() {
        assertTrue(CnpjValidator.isValid("11.222.333/0001-81"));
        assertTrue(CnpjValidator.isValid("11222333000181"));
    }

    @Test
    @DisplayName("Deve validar CNPJ com primeiro dígito verificador igual a 0")
    void deveValidarCnpjComPrimeiroDvZero() {
        assertTrue(CnpjValidator.isValid("11.222.333/0005-05"));
        assertTrue(CnpjValidator.isValid("11222333000505"));
    }

    @Test
    @DisplayName("Deve validar CNPJ com segundo dígito verificador igual a 0")
    void deveValidarCnpjComSegundoDvZero() {
        assertTrue(CnpjValidator.isValid("11.222.333/0018-20"));
        assertTrue(CnpjValidator.isValid("11222333001820"));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com dígitos verificadores incorretos")
    void deveRejeitarCnpjComDigitosVerificadoresIncorretos() {
        assertFalse(CnpjValidator.isValid("11.222.333/0001-82"));
        assertFalse(CnpjValidator.isValid("11222333000182"));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com números repetidos")
    void deveRejeitarCnpjComNumerosRepetidos() {
        assertFalse(CnpjValidator.isValid("11.111.111/1111-11"));
        assertFalse(CnpjValidator.isValid("00000000000000"));
        assertFalse(CnpjValidator.isValid("99999999999999"));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ nulo ou vazio")
    void deveRejeitarCnpjNuloOuVazio() {
        assertFalse(CnpjValidator.isValid(null));
        assertFalse(CnpjValidator.isValid(""));
        assertFalse(CnpjValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve rejeitar CNPJ com tamanho inadequado")
    void deveRejeitarCnpjComTamanhoInadequado() {
        assertFalse(CnpjValidator.isValid("123"));
        assertFalse(CnpjValidator.isValid("1122233300018"));
        assertFalse(CnpjValidator.isValid("1122233300018199"));
    }
}

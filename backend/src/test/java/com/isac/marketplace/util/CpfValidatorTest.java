package com.isac.marketplace.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Validador de CPF")
class CpfValidatorTest {

    @Test
    @DisplayName("Deve validar CPF válido especificado no projeto")
    void deveValidarCpfValido() {
        assertTrue(CpfValidator.isValid("529.982.247-25"));
        assertTrue(CpfValidator.isValid("52998224725"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com dígitos verificadores incorretos")
    void deveRejeitarCpfComDigitosIncorretos() {
        assertFalse(CpfValidator.isValid("529.982.247-26"));
        assertFalse(CpfValidator.isValid("123.456.789-00"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com números repetidos")
    void deveRejeitarCpfComDigitosRepetidos() {
        assertFalse(CpfValidator.isValid("000.000.000-00"));
        assertFalse(CpfValidator.isValid("111.111.111-11"));
        assertFalse(CpfValidator.isValid("99999999999"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF nulo ou vazio")
    void deveRejeitarCpfNuloOuVazio() {
        assertFalse(CpfValidator.isValid(null));
        assertFalse(CpfValidator.isValid(""));
        assertFalse(CpfValidator.isValid("   "));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com tamanho menor ou maior que 11 dígitos")
    void deveRejeitarCpfComTamanhoInvalido() {
        assertFalse(CpfValidator.isValid("123456789"));
        assertFalse(CpfValidator.isValid("529982247250"));
    }
}

package com.isac.marketplace.util;

/**
 * Validador de CPF (Cadastro de Pessoas Físicas).
 * Implementa o algoritmo oficial dos dígitos verificadores (módulo 11 com pesos decrescentes).
 */
public final class CpfValidator {

    private CpfValidator() {
        // Utilitário estático
    }

    public static boolean isValid(String cpf) {
        if (cpf == null) {
            return false;
        }

        String digitos = cpf.replaceAll("\\D", "");

        if (digitos.length() != 11) {
            return false;
        }

        // Rejeita sequências com todos os dígitos repetidos (ex: 00000000000, 11111111111)
        if (digitos.matches("(\\d)\\1{10}")) {
            return false;
        }

        int dv1 = calcularDigito(digitos.substring(0, 9), 10);
        int dv2 = calcularDigito(digitos.substring(0, 9) + dv1, 11);

        return digitos.equals(digitos.substring(0, 9) + dv1 + dv2);
    }

    private static int calcularDigito(String trecho, int pesoInicial) {
        int soma = 0;
        int peso = pesoInicial;
        for (int i = 0; i < trecho.length(); i++) {
            soma += Character.getNumericValue(trecho.charAt(i)) * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : (11 - resto);
    }
}

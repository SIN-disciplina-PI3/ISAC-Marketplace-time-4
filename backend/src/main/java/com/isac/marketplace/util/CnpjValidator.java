package com.isac.marketplace.util;

/**
 * Validador de CNPJ (Cadastro Nacional da Pessoa Jurídica).
 * Implementa o algoritmo oficial dos dígitos verificadores (módulo 11 com pesos).
 */
public final class CnpjValidator {

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private CnpjValidator() {
        // Utilitário estático
    }

    public static boolean isValid(String cnpj) {
        if (cnpj == null) {
            return false;
        }

        // Remove pontuações e espaços, mantendo apenas dígitos
        String digitos = cnpj.replaceAll("\\D", "");

        if (digitos.length() != 14) {
            return false;
        }

        // Rejeita sequências com todos os dígitos repetidos (ex: 11111111111111)
        if (digitos.matches("(\\d)\\1{13}")) {
            return false;
        }

        int dv1 = calcularDigito(digitos.substring(0, 12), PESOS_DV1);
        int dv2 = calcularDigito(digitos.substring(0, 12) + dv1, PESOS_DV2);

        return digitos.equals(digitos.substring(0, 12) + dv1 + dv2);
    }

    private static int calcularDigito(String trecho, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < trecho.length(); i++) {
            soma += Character.getNumericValue(trecho.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : (11 - resto);
    }
}

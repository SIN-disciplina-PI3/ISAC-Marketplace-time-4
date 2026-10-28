package com.isac.marketplace.util;

import java.text.Normalizer;

/**
 * Utilitários para normalização e comparação de textos.
 */
public final class TextoUtil {

    private TextoUtil() {}

    /**
     * Remove acentos e converte para minúsculas, com trim.
     * Exemplo: 'Tapioca com Queijo Coalho' -> 'tapioca com queijo coalho'
     * Exemplo: 'São Francisco' -> 'sao francisco'
     */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String decomposto = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD);
        return decomposto.replaceAll("\\p{M}", "").toLowerCase();
    }

    /**
     * Verifica se o texto base contém o termo de busca, ignorando acentos e caixa.
     */
    public static boolean contemTermo(String base, String termo) {
        if (base == null || termo == null) {
            return false;
        }
        return normalizar(base).contains(normalizar(termo));
    }
}

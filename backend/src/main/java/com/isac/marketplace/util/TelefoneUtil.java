package com.isac.marketplace.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilitários para normalização, hash (LGPD) e geração de links wa.me para telefones.
 */
public final class TelefoneUtil {

    private TelefoneUtil() {}

    /**
     * Normaliza número recebido para o formato padrão do sistema (DDD + número, 10 ou 11 dígitos).
     * Exemplo: '5581999990001@s.whatsapp.net' -> '81999990001'
     * Exemplo: '+55 (81) 99999-0001' -> '81999990001'
     */
    public static String normalizar(String telefone) {
        if (telefone == null) {
            return null;
        }

        // Se contiver @, remove sufixos como @s.whatsapp.net
        int atIdx = telefone.indexOf('@');
        if (atIdx != -1) {
            telefone = telefone.substring(0, atIdx);
        }

        String digitos = telefone.replaceAll("\\D", "");

        // Remove DDI 55 do Brasil se presente e tiver 12 ou 13 dígitos
        if ((digitos.length() == 12 || digitos.length() == 13) && digitos.startsWith("55")) {
            digitos = digitos.substring(2);
        }

        return digitos;
    }

    /**
     * Gera hash SHA-256 do telefone normalizado para armazenamento seguro (LGPD).
     */
    public static String gerarHash(String telefone) {
        String normalizado = normalizar(telefone);
        if (normalizado == null || normalizado.isBlank()) {
            return null;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(normalizado.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular SHA-256", e);
        }
    }

    /**
     * Gera link wa.me para contato direto com o anunciante.
     */
    public static String gerarLinkWaMe(String telefone, String mensagemPrePreenchida) {
        String normalizado = normalizar(telefone);
        if (normalizado == null || normalizado.isBlank()) {
            return "";
        }
        String numeroCompleto = "55" + normalizado;
        if (mensagemPrePreenchida == null || mensagemPrePreenchida.isBlank()) {
            return "https://wa.me/" + numeroCompleto;
        }
        String textoCodificado = URLEncoder.encode(mensagemPrePreenchida, StandardCharsets.UTF_8);
        return "https://wa.me/" + numeroCompleto + "?text=" + textoCodificado;
    }
}

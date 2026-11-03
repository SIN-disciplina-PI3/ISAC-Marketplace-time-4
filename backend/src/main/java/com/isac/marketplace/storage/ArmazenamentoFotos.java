package com.isac.marketplace.storage;

import java.io.InputStream;

/**
 * Interface para armazenamento de fotos dos anúncios.
 * Abstrai o armazenamento local (disco) e em nuvem (Supabase Storage).
 */
public interface ArmazenamentoFotos {

    /**
     * Armazena uma foto e retorna sua URL pública acessível.
     * @param bytes Conteúdo binário da foto
     * @param nomeOriginal Nome original do arquivo enviado
     * @param contentType Tipo MIME do arquivo (ex: image/jpeg, image/png, image/webp)
     * @return URL pública do arquivo armazenado
     */
    String armazenar(byte[] bytes, String nomeOriginal, String contentType);

    /**
     * Valida se o contentType é permitido (jpg, jpeg, png, webp).
     */
    default boolean isTipoPermitido(String contentType) {
        if (contentType == null) return false;
        String mime = contentType.toLowerCase();
        return mime.equals("image/jpeg") || mime.equals("image/jpg") ||
               mime.equals("image/png") || mime.equals("image/webp");
    }
}

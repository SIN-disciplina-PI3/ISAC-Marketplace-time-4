package com.isac.marketplace.storage;

import com.isac.marketplace.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "storage.tipo", havingValue = "local", matchIfMissing = true)
public class ArmazenamentoLocalFotos implements ArmazenamentoFotos {

    private static final Logger log = LoggerFactory.getLogger(ArmazenamentoLocalFotos.class);
    public static final long TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024; // 5 MB

    private final Path diretorioDestino;

    public ArmazenamentoLocalFotos(@Value("${storage.local-dir:./uploads/anuncios}") String localDir) {
        this.diretorioDestino = Paths.get(localDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.diretorioDestino);
        } catch (IOException e) {
            log.error("Não foi possível criar o diretório de uploads: {}", this.diretorioDestino, e);
        }
    }

    @Override
    public String armazenar(byte[] bytes, String nomeOriginal, String contentType) {
        if (bytes == null || bytes.length == 0) {
            throw new RegraNegocioException("ARQUIVO_VAZIO", "O arquivo enviado está vazio.");
        }
        if (bytes.length > TAMANHO_MAXIMO_BYTES) {
            throw new RegraNegocioException("ARQUIVO_MUITO_GRANDE", "O arquivo excede o limite máximo permitido de 5 MB.");
        }
        if (!isTipoPermitido(contentType)) {
            throw new RegraNegocioException("TIPO_ARQUIVO_INVALIDO", "Apenas imagens nos formatos JPG, PNG ou WEBP são permitidas.");
        }

        String extensao = extrairExtensao(nomeOriginal, contentType);
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path caminhoCompleto = this.diretorioDestino.resolve(nomeArquivo);

        try {
            Files.write(caminhoCompleto, bytes);
            log.info("Foto salva em disco com sucesso: {}", caminhoCompleto);
            return "/uploads/anuncios/" + nomeArquivo;
        } catch (IOException e) {
            log.error("Erro ao gravar arquivo em disco: {}", caminhoCompleto, e);
            throw new RuntimeException("Erro ao armazenar arquivo no servidor.", e);
        }
    }

    private String extrairExtensao(String nomeOriginal, String contentType) {
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            return nomeOriginal.substring(nomeOriginal.lastIndexOf(".")).toLowerCase();
        }
        if ("image/png".equalsIgnoreCase(contentType)) return ".png";
        if ("image/webp".equalsIgnoreCase(contentType)) return ".webp";
        return ".jpg";
    }
}

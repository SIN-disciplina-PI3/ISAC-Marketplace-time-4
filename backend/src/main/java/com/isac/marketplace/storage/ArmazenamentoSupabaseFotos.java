package com.isac.marketplace.storage;

import com.isac.marketplace.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "storage.tipo", havingValue = "supabase")
public class ArmazenamentoSupabaseFotos implements ArmazenamentoFotos {

    private static final Logger log = LoggerFactory.getLogger(ArmazenamentoSupabaseFotos.class);
    public static final long TAMANHO_MAXIMO_BYTES = 5 * 1024 * 1024; // 5 MB

    private final String supabaseUrl;
    private final String supabaseKey;
    private final String bucket;
    private final HttpClient httpClient;

    public ArmazenamentoSupabaseFotos(
            @Value("${storage.supabase.url}") String supabaseUrl,
            @Value("${storage.supabase.key}") String supabaseKey,
            @Value("${storage.supabase.bucket:anuncios}") String bucket
    ) {
        this(supabaseUrl, supabaseKey, bucket, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build());
    }

    public ArmazenamentoSupabaseFotos(
            String supabaseUrl,
            String supabaseKey,
            String bucket,
            HttpClient httpClient
    ) {
        this.supabaseUrl = supabaseUrl != null ? supabaseUrl.replaceAll("/+$", "") : "";
        this.supabaseKey = supabaseKey;
        this.bucket = bucket;
        this.httpClient = httpClient;
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
        String endpoint = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, nomeArquivo);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", contentType)
                    .header("apikey", supabaseKey)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                String urlPublica = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, nomeArquivo);
                log.info("Foto enviada com sucesso ao Supabase Storage: {}", urlPublica);
                return urlPublica;
            } else {
                log.error("Erro no Supabase Storage: status {} - corpo: {}", response.statusCode(), response.body());
                throw new RuntimeException("Falha ao salvar foto no Supabase Storage: status " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Exceção ao comunicar com Supabase Storage: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao enviar foto para armazenamento remoto.", e);
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

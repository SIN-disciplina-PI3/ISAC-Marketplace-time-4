package com.isac.marketplace.whatsapp;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway de envio real para a Evolution API (v2).
 * Comunicação HTTP assíncrona/síncrona com tratamento robusto de falhas e timeouts.
 */
@Component
@ConditionalOnProperty(name = "whatsapp.evolution.enabled", havingValue = "true")
public class EvolutionApiGateway implements WhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(EvolutionApiGateway.class);

    private final String baseUrl;
    private final String apiKey;
    private final String instancia;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public EvolutionApiGateway(
            @Value("${whatsapp.evolution.base-url}") String baseUrl,
            @Value("${whatsapp.evolution.api-key}") String apiKey,
            @Value("${whatsapp.evolution.instancia:isac}") String instancia,
            ObjectMapper objectMapper
    ) {
        this(baseUrl, apiKey, instancia, HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build(), objectMapper);
    }

    public EvolutionApiGateway(
            String baseUrl,
            String apiKey,
            String instancia,
            HttpClient httpClient,
            ObjectMapper objectMapper
    ) {
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/+$", "") : "";
        this.apiKey = apiKey;
        this.instancia = instancia;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean enviarMensagem(String telefone, String texto) {
        if (telefone == null || texto == null || texto.isBlank()) {
            log.warn("Tentativa de envio com telefone ou texto inválido.");
            return false;
        }

        String numeroFormatado = normalizarNumeroParaEnvio(telefone);
        String endpoint = String.format("%s/message/sendText/%s", baseUrl, instancia);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("number", numeroFormatado);
            payload.put("text", texto);

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("apikey", apiKey)
                    .timeout(Duration.ofSeconds(8))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Mensagem enviada com sucesso para {} via Evolution API.", numeroFormatado);
                return true;
            } else {
                log.error("Falha no envio via Evolution API. Status: {} - Resposta: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("Exceção ao comunicar com a Evolution API: {}", e.getMessage(), e);
            return false;
        }
    }

    public static String normalizarNumeroParaEnvio(String telefone) {
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("55")) {
            return digitos;
        }
        return "55" + digitos;
    }
}

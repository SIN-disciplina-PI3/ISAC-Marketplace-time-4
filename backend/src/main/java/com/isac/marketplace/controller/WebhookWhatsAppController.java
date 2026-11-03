package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.service.ChatbotService;
import com.isac.marketplace.util.TelefoneUtil;
import com.isac.marketplace.whatsapp.WhatsAppGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhook/whatsapp")
public class WebhookWhatsAppController {

    private static final Logger log = LoggerFactory.getLogger(WebhookWhatsAppController.class);

    private final String webhookSecret;
    private final ChatbotService chatbotService;
    private final WhatsAppGateway whatsAppGateway;
    private final ObjectMapper objectMapper;

    public WebhookWhatsAppController(
            @Value("${whatsapp.webhook.token:isac-webhook-segredo-2026}") String webhookSecret,
            ChatbotService chatbotService,
            WhatsAppGateway whatsAppGateway,
            ObjectMapper objectMapper
    ) {
        this.webhookSecret = webhookSecret;
        this.chatbotService = chatbotService;
        this.whatsAppGateway = whatsAppGateway;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<String> receberEvento(
            @RequestHeader(value = "apikey", required = false) String apiKeyHeader,
            @RequestHeader(value = "x-webhook-secret", required = false) String secretHeader,
            @RequestBody String payloadJson
    ) {
        // Validação de segurança do webhook
        String tokenRecebido = (secretHeader != null && !secretHeader.isBlank()) ? secretHeader : apiKeyHeader;
        if (tokenRecebido == null || !tokenRecebido.equals(webhookSecret)) {
            log.warn("Tentativa de acesso ao webhook do WhatsApp com segredo inválido ou ausente.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Segredo do webhook inválido.");
        }

        try {
            JsonNode root = objectMapper.readTree(payloadJson);

            // Ignora eventos que não sejam mensagens recebidas
            String event = root.path("event").asText("");
            if (!event.isEmpty() && !event.contains("messages.upsert") && !event.contains("message")) {
                return ResponseEntity.ok("Evento ignorado (não é mensagem)");
            }

            JsonNode data = root.has("data") ? root.path("data") : root;
            JsonNode key = data.path("key");

            // Ignora mensagens enviadas pela própria instância
            if (key.path("fromMe").asBoolean(false)) {
                return ResponseEntity.ok("Mensagem ignorada (fromMe)");
            }

            // Ignora mensagens originárias de grupos (@g.us)
            String remoteJid = key.path("remoteJid").asText("");
            if (remoteJid.contains("@g.us")) {
                return ResponseEntity.ok("Mensagem ignorada (grupo)");
            }

            // Extrai o texto da mensagem
            JsonNode message = data.path("message");
            String texto = extrairTextoMensagem(message);

            if (texto.isBlank()) {
                return ResponseEntity.ok("Mensagem sem conteúdo textual processável.");
            }

            String telefone = TelefoneUtil.normalizar(remoteJid);
            if (telefone == null || telefone.isBlank()) {
                return ResponseEntity.ok("Remetente não identificado.");
            }

            // Processa na máquina de estados do chatbot
            MensagemChatbotResponse resposta = chatbotService.responderMensagem(telefone, texto);

            if (resposta != null && resposta.resposta() != null && !resposta.resposta().isBlank()) {
                whatsAppGateway.enviarMensagem(telefone, resposta.resposta());
            }

            return ResponseEntity.ok("Mensagem processada com sucesso");
        } catch (Exception e) {
            log.error("Erro ao processar webhook do WhatsApp: {}", e.getMessage(), e);
            // Retorna 200 para a Evolution API não ficar em retry loop
            return ResponseEntity.ok("Erro capturado e registrado.");
        }
    }

    private String extrairTextoMensagem(JsonNode message) {
        if (message == null || message.isMissingNode()) {
            return "";
        }
        if (message.has("conversation")) {
            return message.path("conversation").asText("");
        }
        if (message.has("extendedTextMessage")) {
            return message.path("extendedTextMessage").path("text").asText("");
        }
        if (message.has("imageMessage")) {
            return message.path("imageMessage").path("caption").asText("");
        }
        return "";
    }
}

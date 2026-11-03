package com.isac.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.service.ChatbotService;
import com.isac.marketplace.whatsapp.WhatsAppGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do WebhookWhatsAppController")
class WebhookWhatsAppControllerTest {

    @Mock
    private ChatbotService chatbotService;

    @Mock
    private WhatsAppGateway whatsAppGateway;

    private MockMvc mockMvc;
    private final String segredoValido = "segredo-teste-123";

    @BeforeEach
    void setUp() {
        WebhookWhatsAppController controller = new WebhookWhatsAppController(
                segredoValido, chatbotService, whatsAppGateway, new ObjectMapper()
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve rejeitar webhook sem segredo ou com segredo incorreto retornando 401")
    void deveRejeitarWebhookSemSegredo() throws Exception {
        mockMvc.perform(post("/api/webhook/whatsapp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"event\": \"messages.upsert\"}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/webhook/whatsapp")
                        .header("x-webhook-secret", "segredo-errado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"event\": \"messages.upsert\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(chatbotService);
        verifyNoInteractions(whatsAppGateway);
    }

    @Test
    @DisplayName("Deve processar mensagem válida da Evolution API e enviar resposta de volta via WhatsAppGateway")
    void deveProcessarMensagemValidaEEnviarResposta() throws Exception {
        String payload = """
                {
                  "event": "messages.upsert",
                  "data": {
                    "key": {
                      "remoteJid": "5581999990001@s.whatsapp.net",
                      "fromMe": false
                    },
                    "message": {
                      "conversation": "quero tapioca"
                    }
                  }
                }
                """;

        when(chatbotService.responderMensagem(eq("81999990001"), eq("quero tapioca")))
                .thenReturn(new MensagemChatbotResponse("Temos tapioca na Enseada!"));

        mockMvc.perform(post("/api/webhook/whatsapp")
                        .header("x-webhook-secret", segredoValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Mensagem processada com sucesso"));

        verify(whatsAppGateway).enviarMensagem("81999990001", "Temos tapioca na Enseada!");
    }

    @Test
    @DisplayName("Deve ignorar mensagem se fromMe for true (enviada pelo próprio bot)")
    void deveIgnorarMensagemFromMe() throws Exception {
        String payload = """
                {
                  "event": "messages.upsert",
                  "data": {
                    "key": {
                      "remoteJid": "5581999990001@s.whatsapp.net",
                      "fromMe": true
                    },
                    "message": {
                      "conversation": "Olá!"
                    }
                  }
                }
                """;

        mockMvc.perform(post("/api/webhook/whatsapp")
                        .header("apikey", segredoValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Mensagem ignorada (fromMe)"));

        verifyNoInteractions(chatbotService);
        verifyNoInteractions(whatsAppGateway);
    }

    @Test
    @DisplayName("Deve ignorar mensagem originária de grupo (@g.us)")
    void deveIgnorarMensagemDeGrupo() throws Exception {
        String payload = """
                {
                  "event": "messages.upsert",
                  "data": {
                    "key": {
                      "remoteJid": "12036304@g.us",
                      "fromMe": false
                    },
                    "message": {
                      "conversation": "Mensagem no grupo"
                    }
                  }
                }
                """;

        mockMvc.perform(post("/api/webhook/whatsapp")
                        .header("x-webhook-secret", segredoValido)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Mensagem ignorada (grupo)"));

        verifyNoInteractions(chatbotService);
        verifyNoInteractions(whatsAppGateway);
    }
}

package com.isac.marketplace.controller;

import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.service.ChatbotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ChatbotController (MockMvc standalone)")
class ChatbotControllerTest {

    @Mock
    private ChatbotService chatbotService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ChatbotController(chatbotService)).build();
    }

    @Test
    @DisplayName("POST /api/chatbot/mensagens - Deve responder com mensagem formatada e links")
    void deveResponderMensagemDoChatbot() throws Exception {
        MensagemChatbotResponse resposta = new MensagemChatbotResponse(
                "Encontrei tapioca!", List.of("https://wa.me/5581999990001")
        );

        when(chatbotService.responderMensagem(eq("81999990001"), eq("tapioca"))).thenReturn(resposta);

        mockMvc.perform(post("/api/chatbot/mensagens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefone\":\"81999990001\",\"texto\":\"tapioca\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta").value("Encontrei tapioca!"))
                .andExpect(jsonPath("$.links[0]").value("https://wa.me/5581999990001"));
    }
}

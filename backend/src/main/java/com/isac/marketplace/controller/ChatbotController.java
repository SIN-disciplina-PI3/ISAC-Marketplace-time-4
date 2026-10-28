package com.isac.marketplace.controller;

import com.isac.marketplace.dto.MensagemChatbotRequest;
import com.isac.marketplace.dto.MensagemChatbotResponse;
import com.isac.marketplace.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot/mensagens")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<MensagemChatbotResponse> receberMensagem(@Valid @RequestBody MensagemChatbotRequest request) {
        MensagemChatbotResponse resposta = chatbotService.responderMensagem(request.telefone(), request.texto());
        return ResponseEntity.ok(resposta);
    }
}

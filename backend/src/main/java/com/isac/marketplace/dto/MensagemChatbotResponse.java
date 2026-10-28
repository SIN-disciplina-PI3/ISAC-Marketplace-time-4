package com.isac.marketplace.dto;

import java.util.List;

public record MensagemChatbotResponse(
        String resposta,
        List<String> links
) {
    public MensagemChatbotResponse(String resposta) {
        this(resposta, List.of());
    }
}

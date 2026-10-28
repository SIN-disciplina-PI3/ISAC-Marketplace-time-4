package com.isac.marketplace.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record MensagemChatbotRequest(
        @NotBlank(message = "O telefone é obrigatório")
        String telefone,

        @NotBlank(message = "O texto da mensagem é obrigatório")
        String texto
) {}

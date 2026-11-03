package com.isac.marketplace.dto;

import com.isac.marketplace.model.PlanoLoja;
import com.isac.marketplace.model.StatusAssinatura;

import java.time.LocalDateTime;

public record AssinaturaDTO(
        PlanoLoja plano,
        StatusAssinatura statusAssinatura,
        LocalDateTime dataVencimento,
        long cotaUsada,
        int cotaLimite,
        boolean ilimitado
) {}

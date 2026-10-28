package com.isac.marketplace.dto;

import com.isac.marketplace.model.Papel;

public record LoginResponse(
        String token,
        String tipo,
        String email,
        Papel papel,
        Long lojaId,
        String nome
) {
    public LoginResponse(String token, String email, Papel papel, Long lojaId, String nome) {
        this(token, "Bearer", email, papel, lojaId, nome);
    }
}

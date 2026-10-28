package com.isac.marketplace.dto;

import com.isac.marketplace.model.TipoAnuncio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CriarAnuncioDTO(
        @NotBlank(message = "O título do anúncio é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
        BigDecimal preco,

        Long categoriaId,

        String bairro,

        TipoAnuncio tipo,

        String fotoUrl
) {}

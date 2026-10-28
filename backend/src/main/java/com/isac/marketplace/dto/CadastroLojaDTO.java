package com.isac.marketplace.dto;

import com.isac.marketplace.model.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroLojaDTO(
        @NotBlank(message = "O nome fantasia é obrigatório")
        @Size(max = 150, message = "O nome fantasia deve ter no máximo 150 caracteres")
        String nomeFantasia,

        String documento,

        TipoDocumento tipoDocumento,

        String categoria,

        @NotBlank(message = "O telefone de WhatsApp é obrigatório")
        String telefoneWhatsapp,

        @Email(message = "Formato de e-mail inválido")
        String email,

        @NotBlank(message = "O bairro é obrigatório")
        String bairro,

        String endereco,

        String senha
) {}

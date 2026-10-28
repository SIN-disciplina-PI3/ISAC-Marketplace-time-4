package com.isac.marketplace.dto;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.PlanoLoja;
import com.isac.marketplace.model.StatusAssinatura;
import com.isac.marketplace.model.TipoDocumento;

import java.time.LocalDateTime;

public record LojaDTO(
        Long id,
        String nomeFantasia,
        String documento,
        TipoDocumento tipoDocumento,
        String categoria,
        String telefoneWhatsapp,
        String email,
        String bairro,
        String endereco,
        PlanoLoja plano,
        StatusAssinatura statusAssinatura,
        LocalDateTime dataVencimentoAssinatura,
        LocalDateTime dataCadastro
) {
    public static LojaDTO fromEntity(Loja loja) {
        if (loja == null) return null;
        return new LojaDTO(
                loja.getId(),
                loja.getNomeFantasia(),
                loja.getDocumento(),
                loja.getTipoDocumento(),
                loja.getCategoria(),
                loja.getTelefoneWhatsapp(),
                loja.getEmail(),
                loja.getBairro(),
                loja.getEndereco(),
                loja.getPlano(),
                loja.getStatusAssinatura(),
                loja.getDataVencimentoAssinatura(),
                loja.getDataCadastro()
        );
    }
}

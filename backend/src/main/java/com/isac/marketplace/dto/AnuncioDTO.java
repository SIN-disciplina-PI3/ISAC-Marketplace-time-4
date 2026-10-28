package com.isac.marketplace.dto;

import com.isac.marketplace.model.Anuncio;
import com.isac.marketplace.model.StatusAnuncio;
import com.isac.marketplace.model.TipoAnuncio;
import com.isac.marketplace.util.TelefoneUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AnuncioDTO(
        Long id,
        Long lojaId,
        String nomeLoja,
        String telefoneLoja,
        String titulo,
        String descricao,
        BigDecimal preco,
        Long categoriaId,
        String nomeCategoria,
        String bairro,
        TipoAnuncio tipo,
        String fotoUrl,
        StatusAnuncio status,
        Boolean disponivel,
        Boolean destaque,
        LocalDateTime dataPublicacao,
        LocalDateTime dataExpiracao,
        String linkWaMe
) {
    public static AnuncioDTO fromEntity(Anuncio anuncio) {
        if (anuncio == null) return null;

        String nomeLoja = anuncio.getLoja() != null ? anuncio.getLoja().getNomeFantasia() : null;
        String telLoja = anuncio.getLoja() != null ? anuncio.getLoja().getTelefoneWhatsapp() : null;
        Long catId = anuncio.getCategoria() != null ? anuncio.getCategoria().getId() : null;
        String catNome = anuncio.getCategoria() != null ? anuncio.getCategoria().getNome() : null;

        String msgWa = "Olá! Vi seu anúncio *" + anuncio.getTitulo() + "* no ISAC Marketplace.";
        String link = telLoja != null ? TelefoneUtil.gerarLinkWaMe(telLoja, msgWa) : "";

        return new AnuncioDTO(
                anuncio.getId(),
                anuncio.getLoja() != null ? anuncio.getLoja().getId() : null,
                nomeLoja,
                telLoja,
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getPreco(),
                catId,
                catNome,
                anuncio.getBairro(),
                anuncio.getTipo(),
                anuncio.getFotoUrl(),
                anuncio.getStatus(),
                anuncio.getDisponivel(),
                anuncio.getDestaque(),
                anuncio.getDataPublicacao(),
                anuncio.getDataExpiracao(),
                link
        );
    }
}

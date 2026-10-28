package com.isac.marketplace.dto;

import java.util.List;

public record ResultadoBuscaDTO(
        List<AnuncioDTO> anuncios,
        long total,
        int pagina,
        int totalPaginas
) {}

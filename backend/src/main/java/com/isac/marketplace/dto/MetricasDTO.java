package com.isac.marketplace.dto;

import java.util.List;

public record MetricasDTO(
        long totalAnunciosAtivos,
        long totalLojasAtivas,
        long totalLojasPendentes,
        long totalLojasInativas,
        long totalBuscas,
        List<TermoMetricaDTO> termosMaisBuscados,
        List<TermoMetricaDTO> buscasSemResultado
) {}

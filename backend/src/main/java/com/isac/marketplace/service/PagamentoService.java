package com.isac.marketplace.service;

import com.isac.marketplace.model.Loja;
import com.isac.marketplace.model.Pagamento;

import java.math.BigDecimal;

/**
 * Interface para gestão e confirmação de pagamentos das assinaturas.
 */
public interface PagamentoService {

    /**
     * Registra intenção de pagamento / cobrança para o ciclo da loja.
     */
    Pagamento criarCobranca(Loja loja, BigDecimal valor, String referenciaCiclo);

    /**
     * Confirmação manual de pagamento realizada pelo administrador.
     */
    Pagamento confirmarPagamentoManual(Long pagamentoId, String confirmadoPor);

    /**
     * Cancela cobrança pendente.
     */
    Pagamento cancelarCobranca(Long pagamentoId);
}

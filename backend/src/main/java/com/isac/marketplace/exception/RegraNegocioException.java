package com.isac.marketplace.exception;

public class RegraNegocioException extends RuntimeException {

    private final String codigo;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
        this.codigo = "REGRA_NEGOCIO";
    }

    public RegraNegocioException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

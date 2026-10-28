package com.isac.marketplace.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    private final String codigo;

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
        this.codigo = "RECURSO_NAO_ENCONTRADO";
    }

    public RecursoNaoEncontradoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

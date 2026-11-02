package com.banca.digital.domain.exception;

/** Recurso inexistente (HTTP 404). */
public class NoEncontradoException extends DominioException {

    public NoEncontradoException(String mensaje) {
        super("NO_ENCONTRADO", mensaje);
    }
}

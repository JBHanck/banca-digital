package com.banca.digital.domain.exception;

/** El recurso ya existe o está en conflicto (HTTP 409). */
public class ConflictoException extends DominioException {

    public ConflictoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}

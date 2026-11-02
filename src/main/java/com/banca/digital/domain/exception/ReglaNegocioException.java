package com.banca.digital.domain.exception;

/** Una política de negocio impide completar la operación (HTTP 422). */
public class ReglaNegocioException extends DominioException {

    public ReglaNegocioException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}

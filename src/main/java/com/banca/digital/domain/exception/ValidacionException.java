package com.banca.digital.domain.exception;

/** Datos de entrada con formato o valor inválido (HTTP 400). */
public class ValidacionException extends DominioException {

    public ValidacionException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}

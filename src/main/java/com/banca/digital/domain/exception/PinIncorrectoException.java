package com.banca.digital.domain.exception;

public class PinIncorrectoException extends ReglaNegocioException {

    public PinIncorrectoException() {
        super("PIN_INCORRECTO", "PIN incorrecto. El retiro no fue procesado");
    }
}

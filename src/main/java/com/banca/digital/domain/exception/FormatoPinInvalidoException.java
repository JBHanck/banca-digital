package com.banca.digital.domain.exception;

public class FormatoPinInvalidoException extends ValidacionException {

    public FormatoPinInvalidoException() {
        super("PIN_FORMATO_INVALIDO", "El PIN debe tener exactamente 4 dígitos numéricos");
    }
}

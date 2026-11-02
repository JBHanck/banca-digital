package com.banca.digital.domain.exception;

public class MontoInvalidoException extends ValidacionException {

    public MontoInvalidoException(String mensaje) {
        super("MONTO_INVALIDO", mensaje);
    }
}

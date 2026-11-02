package com.banca.digital.domain.exception;

public class ClaveActualIncorrectaException extends ReglaNegocioException {

    public ClaveActualIncorrectaException() {
        super("CLAVE_ACTUAL_INCORRECTA", "La clave actual es incorrecta");
    }
}

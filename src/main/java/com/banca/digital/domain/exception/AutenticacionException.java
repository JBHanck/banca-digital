package com.banca.digital.domain.exception;

/** Credenciales inválidas al iniciar sesión (HTTP 401). */
public class AutenticacionException extends DominioException {

    public AutenticacionException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}

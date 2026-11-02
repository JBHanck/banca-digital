package com.banca.digital.domain.exception;

/**
 * Raíz de todas las excepciones controladas del dominio.
 * Cada una lleva un código estable (para el frontend) y un mensaje legible para el usuario.
 */
public abstract class DominioException extends RuntimeException {

    private final String codigo;

    protected DominioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

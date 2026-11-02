package com.banca.digital.domain.service;

import com.banca.digital.domain.exception.ValidacionException;

public final class ReglasCredenciales {

    public static final int LONGITUD_MINIMA_CLAVE = 8;

    private ReglasCredenciales() {
    }

    public static void validarClave(String clave) {
        if (clave == null || clave.length() < LONGITUD_MINIMA_CLAVE) {
            throw new ValidacionException("CLAVE_INVALIDA",
                    "La clave debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres");
        }
    }
}

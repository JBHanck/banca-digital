package com.banca.digital.domain.service;

import java.math.BigDecimal;
import java.util.Locale;

/** Formatea pesos colombianos con punto como separador de miles: $1.000.000 */
public final class FormatoMoneda {

    private FormatoMoneda() {
    }

    public static String pesos(BigDecimal valor) {
        long entero = valor.toBigInteger().longValue();
        return "$" + String.format(Locale.GERMANY, "%,d", entero);
    }
}

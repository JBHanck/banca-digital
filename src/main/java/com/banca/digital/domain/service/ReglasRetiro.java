package com.banca.digital.domain.service;

import com.banca.digital.domain.exception.FormatoPinInvalidoException;
import com.banca.digital.domain.exception.MontoInvalidoException;
import com.banca.digital.domain.exception.MontoMinimoException;
import com.banca.digital.domain.exception.MontoNoFraccionableException;
import com.banca.digital.domain.exception.TopeDiarioExcedidoException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/** Políticas del cajero automático. Java puro: sin dependencias de frameworks. */
public final class ReglasRetiro {

    public static final BigDecimal MONTO_MINIMO = BigDecimal.valueOf(10_000);
    public static final BigDecimal TOPE_DIARIO = BigDecimal.valueOf(1_000_000);

    private static final Pattern PIN_4_DIGITOS = Pattern.compile("\\d{4}");

    private ReglasRetiro() {
    }

    /**
     * Valida el monto solicitado: positivo, mínimo $10.000 y múltiplo de $10.000
     * (la menor denominación disponible; cualquier múltiplo es dispensable exactamente).
     */
    public static void validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new MontoInvalidoException("El monto del retiro debe ser mayor a cero");
        }
        if (monto.compareTo(MONTO_MINIMO) < 0) {
            throw new MontoMinimoException();
        }
        if (monto.remainder(MONTO_MINIMO).signum() != 0) {
            throw new MontoNoFraccionableException(monto);
        }
    }

    /** El PIN debe ser exactamente 4 dígitos numéricos. */
    public static void validarFormatoPin(String pin) {
        if (pin == null || !PIN_4_DIGITOS.matcher(pin).matches()) {
            throw new FormatoPinInvalidoException();
        }
    }

    /** Rechaza si lo retirado hoy + el monto solicitado supera el tope diario. */
    public static void validarTope(BigDecimal acumuladoHoy, BigDecimal monto) {
        if (acumuladoHoy.add(monto).compareTo(TOPE_DIARIO) > 0) {
            BigDecimal disponible = TOPE_DIARIO.subtract(acumuladoHoy).max(BigDecimal.ZERO);
            throw new TopeDiarioExcedidoException(disponible);
        }
    }
}

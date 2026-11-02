package com.banca.digital.domain.service;

import com.banca.digital.domain.exception.MontoNoFraccionableException;
import com.banca.digital.domain.model.Denominacion;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Algoritmo de dispensación: entrega el monto con la MENOR cantidad de billetes.
 *
 * <p>Se usa el método voraz (greedy) de mayor a menor denominación. Es óptimo porque el sistema
 * {100k, 50k, 20k, 10k} equivale a {10, 5, 2, 1} (sistema canónico). Complejidad O(d), con d = 4.
 */
public class Dispensador {

    public Map<Denominacion, Integer> calcular(BigDecimal monto) {
        ReglasRetiro.validarMonto(monto);

        long restante = monto.longValueExact();
        Map<Denominacion, Integer> billetes = new EnumMap<>(Denominacion.class);

        for (Denominacion denominacion : Denominacion.values()) {
            long cantidad = restante / denominacion.valor();
            if (cantidad > 0) {
                billetes.put(denominacion, (int) cantidad);
                restante -= cantidad * denominacion.valor();
            }
        }

        if (restante != 0) {
            throw new MontoNoFraccionableException(monto);
        }
        return billetes;
    }
}

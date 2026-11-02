package com.banca.digital.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.banca.digital.domain.exception.MontoInvalidoException;
import com.banca.digital.domain.exception.MontoMinimoException;
import com.banca.digital.domain.exception.MontoNoFraccionableException;
import com.banca.digital.domain.model.Denominacion;
import com.banca.digital.domain.service.Dispensador;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DispensadorTest {

    private final Dispensador dispensador = new Dispensador();

    @ParameterizedTest(name = "{0} -> {1}x100k {2}x50k {3}x20k {4}x10k")
    @CsvSource({
            "10000,   0, 0, 0, 1",
            "30000,   0, 0, 1, 1",
            "60000,   0, 1, 0, 1",
            "90000,   0, 1, 2, 0",
            "180000,  1, 1, 1, 1",
            "350000,  3, 1, 0, 0",
            "1000000, 10, 0, 0, 0"
    })
    void calculaMenorCantidadDeBilletes(long monto, int cien, int cincuenta, int veinte, int diez) {
        Map<Denominacion, Integer> r = dispensador.calcular(BigDecimal.valueOf(monto));

        assertEquals(cien, r.getOrDefault(Denominacion.CIEN_MIL, 0));
        assertEquals(cincuenta, r.getOrDefault(Denominacion.CINCUENTA_MIL, 0));
        assertEquals(veinte, r.getOrDefault(Denominacion.VEINTE_MIL, 0));
        assertEquals(diez, r.getOrDefault(Denominacion.DIEZ_MIL, 0));
    }

    @Test
    void todoMultiploDeDiezMilSumaExactoYUsaElMinimoDeBilletes() {
        for (int unidades = 1; unidades <= 500; unidades++) {
            long monto = unidades * 10_000L;
            Map<Denominacion, Integer> r = dispensador.calcular(BigDecimal.valueOf(monto));

            long suma = r.entrySet().stream().mapToLong(e -> (long) e.getKey().valor() * e.getValue()).sum();
            int billetes = r.values().stream().mapToInt(Integer::intValue).sum();

            assertEquals(monto, suma, "La suma debe ser exacta para " + monto);
            assertEquals(minimoBilletes(unidades), billetes, "Cantidad mínima para " + monto);
        }
    }

    @Test
    void montoNoMultiploDeDiezMilNoSePuedeFraccionar() {
        var ex = assertThrows(MontoNoFraccionableException.class,
                () -> dispensador.calcular(BigDecimal.valueOf(25_000)));
        assertEquals("MONTO_NO_FRACCIONABLE", ex.getCodigo());
        assertTrue(ex.getMessage().contains("$25.000"));
    }

    @Test
    void montoMenorAlMinimoSeRechaza() {
        assertThrows(MontoMinimoException.class, () -> dispensador.calcular(BigDecimal.valueOf(5_000)));
    }

    @Test
    void montoCeroNegativoONuloEsInvalido() {
        assertThrows(MontoInvalidoException.class, () -> dispensador.calcular(BigDecimal.ZERO));
        assertThrows(MontoInvalidoException.class, () -> dispensador.calcular(BigDecimal.valueOf(-10_000)));
        assertThrows(MontoInvalidoException.class, () -> dispensador.calcular(null));
    }

    /** Referencia independiente (programación dinámica) en unidades de $10.000: monedas {10, 5, 2, 1}. */
    private static int minimoBilletes(int unidades) {
        int[] dp = new int[unidades + 1];
        for (int i = 1; i <= unidades; i++) {
            dp[i] = Integer.MAX_VALUE;
            for (int moneda : new int[]{10, 5, 2, 1}) {
                if (i >= moneda && dp[i - moneda] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[i - moneda] + 1);
                }
            }
        }
        return dp[unidades];
    }
}

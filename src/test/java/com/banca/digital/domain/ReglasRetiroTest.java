package com.banca.digital.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banca.digital.domain.exception.FormatoPinInvalidoException;
import com.banca.digital.domain.exception.TopeDiarioExcedidoException;
import com.banca.digital.domain.service.ReglasRetiro;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReglasRetiroTest {

    @ParameterizedTest
    @ValueSource(strings = {"1234", "0000", "9999"})
    void aceptaPinDeExactamenteCuatroDigitos(String pin) {
        assertDoesNotThrow(() -> ReglasRetiro.validarFormatoPin(pin));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "1", "123", "12345", "abcd", "12a4", "12 4", "-123"})
    @NullSource
    void rechazaPinConFormatoInvalido(String pin) {
        assertThrows(FormatoPinInvalidoException.class, () -> ReglasRetiro.validarFormatoPin(pin));
    }

    @Test
    void permiteLlegarJustoAlTopeDiario() {
        assertDoesNotThrow(() -> ReglasRetiro.validarTope(BigDecimal.valueOf(800_000), BigDecimal.valueOf(200_000)));
        assertDoesNotThrow(() -> ReglasRetiro.validarTope(BigDecimal.ZERO, BigDecimal.valueOf(1_000_000)));
    }

    @Test
    void rechazaSiSuperaElTopeDiarioIndicandoElDisponible() {
        var ex = assertThrows(TopeDiarioExcedidoException.class,
                () -> ReglasRetiro.validarTope(BigDecimal.valueOf(800_000), BigDecimal.valueOf(300_000)));

        assertEquals("TOPE_DIARIO_EXCEDIDO", ex.getCodigo());
        assertEquals("Supera el tope diario de $1.000.000. Disponible hoy: $200.000", ex.getMessage());
    }
}

package com.banca.digital.domain.exception;

import com.banca.digital.domain.service.FormatoMoneda;
import java.math.BigDecimal;

public class SaldoInsuficienteException extends ReglaNegocioException {

    public SaldoInsuficienteException(BigDecimal saldoActual) {
        super("SALDO_INSUFICIENTE",
                "Saldo insuficiente. Su saldo disponible es " + FormatoMoneda.pesos(saldoActual));
    }
}

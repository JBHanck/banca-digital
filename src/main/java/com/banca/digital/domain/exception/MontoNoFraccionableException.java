package com.banca.digital.domain.exception;

import com.banca.digital.domain.service.FormatoMoneda;
import com.banca.digital.domain.service.ReglasRetiro;
import java.math.BigDecimal;

public class MontoNoFraccionableException extends ReglaNegocioException {

    public MontoNoFraccionableException(BigDecimal monto) {
        super("MONTO_NO_FRACCIONABLE",
                "El monto " + FormatoMoneda.pesos(monto) + " no puede dispensarse con billetes de "
                        + "$100.000, $50.000, $20.000 y $10.000. Debe ser múltiplo de "
                        + FormatoMoneda.pesos(ReglasRetiro.MONTO_MINIMO));
    }
}

package com.banca.digital.domain.exception;

import com.banca.digital.domain.service.FormatoMoneda;
import com.banca.digital.domain.service.ReglasRetiro;

public class MontoMinimoException extends ReglaNegocioException {

    public MontoMinimoException() {
        super("MONTO_MINIMO",
                "El monto mínimo de retiro es " + FormatoMoneda.pesos(ReglasRetiro.MONTO_MINIMO));
    }
}

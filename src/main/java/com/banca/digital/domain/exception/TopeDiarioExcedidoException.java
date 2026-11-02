package com.banca.digital.domain.exception;

import com.banca.digital.domain.service.FormatoMoneda;
import com.banca.digital.domain.service.ReglasRetiro;
import java.math.BigDecimal;

public class TopeDiarioExcedidoException extends ReglaNegocioException {

    public TopeDiarioExcedidoException(BigDecimal disponibleHoy) {
        super("TOPE_DIARIO_EXCEDIDO",
                "Supera el tope diario de " + FormatoMoneda.pesos(ReglasRetiro.TOPE_DIARIO)
                        + ". Disponible hoy: " + FormatoMoneda.pesos(disponibleHoy));
    }
}

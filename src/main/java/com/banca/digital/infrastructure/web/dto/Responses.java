package com.banca.digital.infrastructure.web.dto;

import com.banca.digital.application.dto.Resultados.RetiroResultado;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class Responses {

    private Responses() {
    }

    public record ErrorResponse(String codigo, String mensaje, int status, LocalDateTime timestamp) {
    }

    public record MensajeResponse(String mensaje) {
    }

    public record BilleteResponse(int denominacion, int cantidad) {
    }

    public record RetiroResponse(BigDecimal montoRetirado,
                                 List<BilleteResponse> billetes,
                                 int totalBilletes,
                                 BigDecimal nuevoSaldo,
                                 BigDecimal acumuladoHoy,
                                 BigDecimal disponibleHoy) {

        public static RetiroResponse desde(RetiroResultado r) {
            List<BilleteResponse> billetes = r.billetes().entrySet().stream()
                    .map(e -> new BilleteResponse(e.getKey().valor(), e.getValue()))
                    .toList();
            return new RetiroResponse(r.montoRetirado(), billetes, r.totalBilletes(),
                    r.nuevoSaldo(), r.acumuladoHoy(), r.disponibleHoy());
        }
    }
}

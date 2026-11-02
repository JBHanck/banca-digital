package com.banca.digital.application.dto;

import com.banca.digital.domain.model.Denominacion;
import java.math.BigDecimal;
import java.util.Map;

/** Datos de salida de los casos de uso. */
public final class Resultados {

    private Resultados() {
    }

    public record RegistroResultado(Long usuarioId, String nombre, String numeroCuenta) {
    }

    public record SesionResultado(String token, Long usuarioId, String nombre) {
    }

    public record SaldoResultado(String numeroCuenta, BigDecimal saldo) {
    }

    public record PerfilResultado(Long id, String nombre, String correo, String telefono,
                                  String direccion) {
    }

    public record RetiroResultado(BigDecimal montoRetirado,
                                  Map<Denominacion, Integer> billetes,
                                  int totalBilletes,
                                  BigDecimal nuevoSaldo,
                                  BigDecimal acumuladoHoy,
                                  BigDecimal disponibleHoy) {
    }
}

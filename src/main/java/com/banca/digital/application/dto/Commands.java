package com.banca.digital.application.dto;

import java.math.BigDecimal;

/** Datos de entrada de los casos de uso. */
public final class Commands {

    private Commands() {
    }

    public record RegistroCommand(String nombre, String correo, String telefono, String direccion,
                                  String clave, String pin) {
    }

    public record LoginCommand(String correo, String clave) {
    }

    public record DepositoCommand(Long usuarioId, BigDecimal monto) {
    }

    public record ActualizarDatosCommand(Long usuarioId, String correo, String telefono,
                                         String direccion) {
    }

    public record CambiarClaveCommand(Long usuarioId, String claveActual, String claveNueva) {
    }

    public record RetiroCommand(Long usuarioId, BigDecimal monto, String pin) {
    }
}

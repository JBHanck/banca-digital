package com.banca.digital.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Cuerpos de las peticiones REST. Las reglas de negocio se validan en el dominio. */
public final class Requests {

    private Requests() {
    }

    public record RegistroRequest(
            @NotBlank(message = "es obligatorio") String nombre,
            @NotBlank(message = "es obligatorio") String correo,
            @NotBlank(message = "es obligatorio") String telefono,
            @NotBlank(message = "es obligatorio") String direccion,
            @NotBlank(message = "es obligatoria") String clave,
            @NotBlank(message = "es obligatorio") String pin) {
    }

    public record LoginRequest(
            @NotBlank(message = "es obligatorio") String correo,
            @NotBlank(message = "es obligatoria") String clave) {
    }

    public record DepositoRequest(
            @NotNull(message = "es obligatorio") BigDecimal monto) {
    }

    public record ActualizarDatosRequest(String correo, String telefono, String direccion) {
    }

    public record CambiarClaveRequest(
            @NotBlank(message = "es obligatoria") String claveActual,
            @NotBlank(message = "es obligatoria") String claveNueva) {
    }

    public record RetiroRequest(
            @NotNull(message = "es obligatorio") BigDecimal monto,
            @NotBlank(message = "es obligatorio") String pin) {
    }
}

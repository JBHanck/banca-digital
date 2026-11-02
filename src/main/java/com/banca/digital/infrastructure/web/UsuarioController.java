package com.banca.digital.infrastructure.web;

import com.banca.digital.application.dto.Commands.ActualizarDatosCommand;
import com.banca.digital.application.dto.Commands.CambiarClaveCommand;
import com.banca.digital.application.dto.Resultados.PerfilResultado;
import com.banca.digital.application.usecase.ActualizarDatosUseCase;
import com.banca.digital.application.usecase.CambiarClaveUseCase;
import com.banca.digital.application.usecase.ConsultarPerfilUseCase;
import com.banca.digital.infrastructure.web.dto.Requests.ActualizarDatosRequest;
import com.banca.digital.infrastructure.web.dto.Requests.CambiarClaveRequest;
import com.banca.digital.infrastructure.web.dto.Responses.MensajeResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final ConsultarPerfilUseCase consultarPerfil;
    private final ActualizarDatosUseCase actualizarDatos;
    private final CambiarClaveUseCase cambiarClave;

    public UsuarioController(ConsultarPerfilUseCase consultarPerfil,
                             ActualizarDatosUseCase actualizarDatos,
                             CambiarClaveUseCase cambiarClave) {
        this.consultarPerfil = consultarPerfil;
        this.actualizarDatos = actualizarDatos;
        this.cambiarClave = cambiarClave;
    }

    @GetMapping("/perfil")
    public PerfilResultado perfil(@AuthenticationPrincipal Long usuarioId) {
        return consultarPerfil.ejecutar(usuarioId);
    }

    @PutMapping("/perfil")
    public PerfilResultado actualizarPerfil(@AuthenticationPrincipal Long usuarioId,
                                            @RequestBody ActualizarDatosRequest req) {
        return actualizarDatos.ejecutar(new ActualizarDatosCommand(
                usuarioId, req.correo(), req.telefono(), req.direccion()));
    }

    @PutMapping("/clave")
    public MensajeResponse cambiarClave(@AuthenticationPrincipal Long usuarioId,
                                        @Valid @RequestBody CambiarClaveRequest req) {
        cambiarClave.ejecutar(new CambiarClaveCommand(usuarioId, req.claveActual(), req.claveNueva()));
        return new MensajeResponse("Clave actualizada correctamente");
    }
}

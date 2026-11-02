package com.banca.digital.infrastructure.web;

import com.banca.digital.application.dto.Commands.LoginCommand;
import com.banca.digital.application.dto.Commands.RegistroCommand;
import com.banca.digital.application.dto.Resultados.RegistroResultado;
import com.banca.digital.application.dto.Resultados.SesionResultado;
import com.banca.digital.application.usecase.LoginUseCase;
import com.banca.digital.application.usecase.RegistrarUsuarioUseCase;
import com.banca.digital.infrastructure.web.dto.Requests.LoginRequest;
import com.banca.digital.infrastructure.web.dto.Requests.RegistroRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrar;
    private final LoginUseCase login;

    public AuthController(RegistrarUsuarioUseCase registrar, LoginUseCase login) {
        this.registrar = registrar;
        this.login = login;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResultado registro(@Valid @RequestBody RegistroRequest req) {
        return registrar.ejecutar(new RegistroCommand(req.nombre(), req.correo(), req.telefono(),
                req.direccion(), req.clave(), req.pin()));
    }

    @PostMapping("/login")
    public SesionResultado login(@Valid @RequestBody LoginRequest req) {
        return login.ejecutar(new LoginCommand(req.correo(), req.clave()));
    }
}

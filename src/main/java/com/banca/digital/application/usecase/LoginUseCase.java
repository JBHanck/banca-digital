package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.LoginCommand;
import com.banca.digital.application.dto.Resultados.SesionResultado;
import com.banca.digital.application.port.out.PasswordHasher;
import com.banca.digital.application.port.out.TokenGenerator;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.exception.AutenticacionException;
import com.banca.digital.domain.model.Usuario;

public class LoginUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher hasher;
    private final TokenGenerator tokenGenerator;

    public LoginUseCase(UsuarioRepository usuarioRepository, PasswordHasher hasher,
                        TokenGenerator tokenGenerator) {
        this.usuarioRepository = usuarioRepository;
        this.hasher = hasher;
        this.tokenGenerator = tokenGenerator;
    }

    public SesionResultado ejecutar(LoginCommand cmd) {
        Usuario usuario = usuarioRepository
                .buscarPorCorreo(Usuario.normalizarCorreo(cmd.correo()))
                .filter(u -> hasher.coincide(cmd.clave(), u.getHashClave()))
                .orElseThrow(() -> new AutenticacionException("CREDENCIALES_INVALIDAS", "Credenciales inválidas"));

        String token = tokenGenerator.generar(usuario.getId(), usuario.getNombre());
        return new SesionResultado(token, usuario.getId(), usuario.getNombre());
    }
}

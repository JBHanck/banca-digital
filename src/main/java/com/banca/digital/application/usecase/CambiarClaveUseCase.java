package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.CambiarClaveCommand;
import com.banca.digital.application.port.out.PasswordHasher;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.exception.ClaveActualIncorrectaException;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.exception.ValidacionException;
import com.banca.digital.domain.model.Usuario;
import com.banca.digital.domain.service.ReglasCredenciales;

public class CambiarClaveUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher hasher;
    private final UnidadDeTrabajo unidadDeTrabajo;

    public CambiarClaveUseCase(UsuarioRepository usuarioRepository, PasswordHasher hasher,
                               UnidadDeTrabajo unidadDeTrabajo) {
        this.usuarioRepository = usuarioRepository;
        this.hasher = hasher;
        this.unidadDeTrabajo = unidadDeTrabajo;
    }

    public void ejecutar(CambiarClaveCommand cmd) {
        unidadDeTrabajo.ejecutar(() -> {
            Usuario usuario = usuarioRepository.buscarPorId(cmd.usuarioId())
                    .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));

            if (!hasher.coincide(cmd.claveActual(), usuario.getHashClave())) {
                throw new ClaveActualIncorrectaException();
            }
            ReglasCredenciales.validarClave(cmd.claveNueva());
            if (hasher.coincide(cmd.claveNueva(), usuario.getHashClave())) {
                throw new ValidacionException("CLAVE_REPETIDA", "La nueva clave debe ser distinta a la actual");
            }

            usuario.cambiarClave(hasher.encriptar(cmd.claveNueva()));
            usuarioRepository.guardar(usuario);
            return null;
        });
    }
}

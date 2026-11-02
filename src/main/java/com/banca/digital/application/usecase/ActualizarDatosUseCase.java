package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.ActualizarDatosCommand;
import com.banca.digital.application.dto.Resultados.PerfilResultado;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.exception.ConflictoException;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.model.Usuario;

public class ActualizarDatosUseCase {

    private final UsuarioRepository usuarioRepository;
    private final UnidadDeTrabajo unidadDeTrabajo;

    public ActualizarDatosUseCase(UsuarioRepository usuarioRepository, UnidadDeTrabajo unidadDeTrabajo) {
        this.usuarioRepository = usuarioRepository;
        this.unidadDeTrabajo = unidadDeTrabajo;
    }

    public PerfilResultado ejecutar(ActualizarDatosCommand cmd) {
        return unidadDeTrabajo.ejecutar(() -> {
            Usuario usuario = usuarioRepository.buscarPorId(cmd.usuarioId())
                    .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));

            usuario.actualizarDatos(cmd.correo(), cmd.telefono(), cmd.direccion());

            usuarioRepository.buscarPorCorreo(usuario.getCorreo())
                    .filter(otro -> !otro.getId().equals(usuario.getId()))
                    .ifPresent(otro -> {
                        throw new ConflictoException("CORREO_YA_REGISTRADO",
                                "Ya existe un usuario registrado con ese correo");
                    });

            Usuario guardado = usuarioRepository.guardar(usuario);
            return new PerfilResultado(guardado.getId(), guardado.getNombre(), guardado.getCorreo(),
                    guardado.getTelefono(), guardado.getDireccion());
        });
    }
}

package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Resultados.PerfilResultado;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.model.Usuario;

public class ConsultarPerfilUseCase {

    private final UsuarioRepository usuarioRepository;

    public ConsultarPerfilUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public PerfilResultado ejecutar(Long usuarioId) {
        Usuario u = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new NoEncontradoException("Usuario no encontrado"));
        return new PerfilResultado(u.getId(), u.getNombre(), u.getCorreo(), u.getTelefono(), u.getDireccion());
    }
}

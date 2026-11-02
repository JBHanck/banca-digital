package com.banca.digital.application.port.out;

import com.banca.digital.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorCorreo(String correo);

    Usuario guardar(Usuario usuario);
}

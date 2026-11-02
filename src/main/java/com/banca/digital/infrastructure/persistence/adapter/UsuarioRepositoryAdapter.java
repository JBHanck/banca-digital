package com.banca.digital.infrastructure.persistence.adapter;

import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.model.Usuario;
import com.banca.digital.infrastructure.persistence.entity.UsuarioEntity;
import com.banca.digital.infrastructure.persistence.repository.UsuarioJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpa;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jpa.findByCorreo(correo).map(this::aDominio);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioEntity entidad = usuario.getId() == null
                ? new UsuarioEntity()
                : jpa.findById(usuario.getId()).orElseGet(UsuarioEntity::new);
        entidad.setNombre(usuario.getNombre());
        entidad.setCorreo(usuario.getCorreo());
        entidad.setTelefono(usuario.getTelefono());
        entidad.setDireccion(usuario.getDireccion());
        entidad.setHashClave(usuario.getHashClave());
        return aDominio(jpa.saveAndFlush(entidad));
    }

    private Usuario aDominio(UsuarioEntity e) {
        return new Usuario(e.getId(), e.getNombre(), e.getCorreo(), e.getTelefono(),
                e.getDireccion(), e.getHashClave());
    }
}

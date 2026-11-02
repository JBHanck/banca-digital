package com.banca.digital.infrastructure.persistence.repository;

import com.banca.digital.infrastructure.persistence.entity.UsuarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByCorreo(String correo);
}

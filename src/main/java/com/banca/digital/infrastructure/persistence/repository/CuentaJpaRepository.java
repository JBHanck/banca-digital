package com.banca.digital.infrastructure.persistence.repository;

import com.banca.digital.infrastructure.persistence.entity.CuentaEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByUsuarioId(Long usuarioId);

    boolean existsByNumero(String numero);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CuentaEntity c where c.usuarioId = :usuarioId")
    Optional<CuentaEntity> buscarParaActualizar(@Param("usuarioId") Long usuarioId);
}

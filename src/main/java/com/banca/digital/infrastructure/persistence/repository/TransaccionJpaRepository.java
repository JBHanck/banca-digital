package com.banca.digital.infrastructure.persistence.repository;

import com.banca.digital.domain.model.TipoTransaccion;
import com.banca.digital.infrastructure.persistence.entity.TransaccionEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransaccionJpaRepository extends JpaRepository<TransaccionEntity, Long> {

    /** Devuelve null si no hay filas; el adaptador lo convierte en cero. */
    @Query("select sum(t.monto) from TransaccionEntity t "
            + "where t.cuentaId = :cuentaId and t.tipo = :tipo "
            + "and t.fecha >= :desde and t.fecha < :hasta")
    BigDecimal sumarMontos(@Param("cuentaId") Long cuentaId,
                           @Param("tipo") TipoTransaccion tipo,
                           @Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta);
}

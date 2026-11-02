package com.banca.digital.infrastructure.persistence.adapter;

import com.banca.digital.application.port.out.TransaccionRepository;
import com.banca.digital.domain.model.TipoTransaccion;
import com.banca.digital.domain.model.Transaccion;
import com.banca.digital.infrastructure.persistence.entity.TransaccionEntity;
import com.banca.digital.infrastructure.persistence.repository.TransaccionJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TransaccionRepositoryAdapter implements TransaccionRepository {

    private final TransaccionJpaRepository jpa;

    public TransaccionRepositoryAdapter(TransaccionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Transaccion guardar(Transaccion t) {
        TransaccionEntity e = new TransaccionEntity();
        e.setCuentaId(t.getCuentaId());
        e.setTipo(t.getTipo());
        e.setMonto(t.getMonto());
        e.setFecha(t.getFecha());
        e.setSaldoPosterior(t.getSaldoPosterior());
        TransaccionEntity guardada = jpa.saveAndFlush(e);
        return new Transaccion(guardada.getId(), guardada.getCuentaId(), guardada.getTipo(),
                guardada.getMonto(), guardada.getFecha(), guardada.getSaldoPosterior());
    }

    @Override
    public BigDecimal sumarRetirosEntre(Long cuentaId, LocalDateTime desde, LocalDateTime hasta) {
        BigDecimal suma = jpa.sumarMontos(cuentaId, TipoTransaccion.RETIRO, desde, hasta);
        return suma == null ? BigDecimal.ZERO : suma;
    }
}

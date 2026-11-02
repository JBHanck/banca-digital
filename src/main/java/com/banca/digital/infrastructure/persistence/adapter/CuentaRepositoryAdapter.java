package com.banca.digital.infrastructure.persistence.adapter;

import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.domain.model.Cuenta;
import com.banca.digital.infrastructure.persistence.entity.CuentaEntity;
import com.banca.digital.infrastructure.persistence.repository.CuentaJpaRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class CuentaRepositoryAdapter implements CuentaRepository {

    private final CuentaJpaRepository jpa;

    public CuentaRepositoryAdapter(CuentaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Cuenta> buscarPorUsuarioId(Long usuarioId) {
        return jpa.findByUsuarioId(usuarioId).map(this::aDominio);
    }

    @Override
    public Optional<Cuenta> buscarPorUsuarioIdParaActualizar(Long usuarioId) {
        return jpa.buscarParaActualizar(usuarioId).map(this::aDominio);
    }

    @Override
    public boolean existePorNumero(String numero) {
        return jpa.existsByNumero(numero);
    }

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        CuentaEntity entidad = cuenta.getId() == null
                ? new CuentaEntity()
                : jpa.findById(cuenta.getId()).orElseGet(CuentaEntity::new);
        entidad.setNumero(cuenta.getNumero());
        entidad.setUsuarioId(cuenta.getUsuarioId());
        entidad.setSaldo(cuenta.getSaldo());
        entidad.setHashPin(cuenta.getHashPin());
        return aDominio(jpa.saveAndFlush(entidad));
    }

    private Cuenta aDominio(CuentaEntity e) {
        return new Cuenta(e.getId(), e.getNumero(), e.getUsuarioId(), e.getSaldo(), e.getHashPin());
    }
}

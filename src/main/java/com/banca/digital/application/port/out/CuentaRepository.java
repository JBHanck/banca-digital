package com.banca.digital.application.port.out;

import com.banca.digital.domain.model.Cuenta;
import java.util.Optional;

public interface CuentaRepository {

    Optional<Cuenta> buscarPorUsuarioId(Long usuarioId);

    /** Igual que la anterior pero bloquea la fila hasta terminar la transacción (evita carreras). */
    Optional<Cuenta> buscarPorUsuarioIdParaActualizar(Long usuarioId);

    boolean existePorNumero(String numero);

    Cuenta guardar(Cuenta cuenta);
}

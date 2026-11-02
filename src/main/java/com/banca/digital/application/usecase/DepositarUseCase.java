package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.DepositoCommand;
import com.banca.digital.application.dto.Resultados.SaldoResultado;
import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.application.port.out.TransaccionRepository;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.model.Cuenta;
import com.banca.digital.domain.model.Transaccion;
import java.time.Clock;
import java.time.LocalDateTime;

public class DepositarUseCase {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final UnidadDeTrabajo unidadDeTrabajo;
    private final Clock reloj;

    public DepositarUseCase(CuentaRepository cuentaRepository,
                            TransaccionRepository transaccionRepository,
                            UnidadDeTrabajo unidadDeTrabajo, Clock reloj) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    public SaldoResultado ejecutar(DepositoCommand cmd) {
        return unidadDeTrabajo.ejecutar(() -> {
            Cuenta cuenta = cuentaRepository.buscarPorUsuarioIdParaActualizar(cmd.usuarioId())
                    .orElseThrow(() -> new NoEncontradoException("No se encontró la cuenta del usuario"));

            cuenta.depositar(cmd.monto());
            cuentaRepository.guardar(cuenta);
            transaccionRepository.guardar(Transaccion.deposito(
                    cuenta.getId(), cmd.monto(), LocalDateTime.now(reloj), cuenta.getSaldo()));

            return new SaldoResultado(cuenta.getNumero(), cuenta.getSaldo());
        });
    }
}

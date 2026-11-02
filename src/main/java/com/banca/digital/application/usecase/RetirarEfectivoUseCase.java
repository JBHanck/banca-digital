package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.RetiroCommand;
import com.banca.digital.application.dto.Resultados.RetiroResultado;
import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.application.port.out.PasswordHasher;
import com.banca.digital.application.port.out.TransaccionRepository;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.exception.PinIncorrectoException;
import com.banca.digital.domain.exception.SaldoInsuficienteException;
import com.banca.digital.domain.model.Cuenta;
import com.banca.digital.domain.model.Denominacion;
import com.banca.digital.domain.model.Transaccion;
import com.banca.digital.domain.service.Dispensador;
import com.banca.digital.domain.service.ReglasRetiro;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Módulo crítico: retiro de efectivo en el cajero inteligente.
 *
 * <p>Orden de validaciones (se informa el PRIMER incumplimiento):
 * monto mínimo → monto múltiplo de $10.000 → formato de PIN → PIN correcto → saldo → tope diario.
 * Luego se calcula la dispensación, se descuenta el saldo y se registra la transacción,
 * todo dentro de una única transacción con la cuenta bloqueada.
 */
public class RetirarEfectivoUseCase {

    private final CuentaRepository cuentaRepository;
    private final TransaccionRepository transaccionRepository;
    private final PasswordHasher hasher;
    private final Dispensador dispensador;
    private final UnidadDeTrabajo unidadDeTrabajo;
    private final Clock reloj;

    public RetirarEfectivoUseCase(CuentaRepository cuentaRepository,
                                  TransaccionRepository transaccionRepository,
                                  PasswordHasher hasher, Dispensador dispensador,
                                  UnidadDeTrabajo unidadDeTrabajo, Clock reloj) {
        this.cuentaRepository = cuentaRepository;
        this.transaccionRepository = transaccionRepository;
        this.hasher = hasher;
        this.dispensador = dispensador;
        this.unidadDeTrabajo = unidadDeTrabajo;
        this.reloj = reloj;
    }

    public RetiroResultado ejecutar(RetiroCommand cmd) {
        BigDecimal monto = cmd.monto();

        // Validaciones que no requieren acceso a datos
        ReglasRetiro.validarMonto(monto);
        ReglasRetiro.validarFormatoPin(cmd.pin());

        return unidadDeTrabajo.ejecutar(() -> {
            Cuenta cuenta = cuentaRepository.buscarPorUsuarioIdParaActualizar(cmd.usuarioId())
                    .orElseThrow(() -> new NoEncontradoException("No se encontró la cuenta del usuario"));

            if (!hasher.coincide(cmd.pin(), cuenta.getHashPin())) {
                throw new PinIncorrectoException();
            }
            if (!cuenta.tieneSaldoPara(monto)) {
                throw new SaldoInsuficienteException(cuenta.getSaldo());
            }

            LocalDate hoy = LocalDate.now(reloj);
            BigDecimal acumuladoHoy = transaccionRepository.sumarRetirosEntre(
                    cuenta.getId(), hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
            ReglasRetiro.validarTope(acumuladoHoy, monto);

            Map<Denominacion, Integer> billetes = dispensador.calcular(monto);

            cuenta.retirar(monto);
            cuentaRepository.guardar(cuenta);
            transaccionRepository.guardar(Transaccion.retiro(
                    cuenta.getId(), monto, LocalDateTime.now(reloj), cuenta.getSaldo()));

            BigDecimal nuevoAcumulado = acumuladoHoy.add(monto);
            int totalBilletes = billetes.values().stream().mapToInt(Integer::intValue).sum();

            return new RetiroResultado(monto, billetes, totalBilletes, cuenta.getSaldo(),
                    nuevoAcumulado, ReglasRetiro.TOPE_DIARIO.subtract(nuevoAcumulado));
        });
    }
}

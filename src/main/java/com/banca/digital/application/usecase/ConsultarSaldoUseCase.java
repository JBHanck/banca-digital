package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Resultados.SaldoResultado;
import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.model.Cuenta;

public class ConsultarSaldoUseCase {

    private final CuentaRepository cuentaRepository;

    public ConsultarSaldoUseCase(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public SaldoResultado ejecutar(Long usuarioId) {
        Cuenta cuenta = cuentaRepository.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new NoEncontradoException("No se encontró la cuenta del usuario"));
        return new SaldoResultado(cuenta.getNumero(), cuenta.getSaldo());
    }
}

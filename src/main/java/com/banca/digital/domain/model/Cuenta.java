package com.banca.digital.domain.model;

import com.banca.digital.domain.exception.MontoInvalidoException;
import com.banca.digital.domain.exception.SaldoInsuficienteException;
import java.math.BigDecimal;

public class Cuenta {

    private Long id;
    private final String numero;
    private final Long usuarioId;
    private BigDecimal saldo;
    private final String hashPin;

    public Cuenta(Long id, String numero, Long usuarioId, BigDecimal saldo, String hashPin) {
        this.id = id;
        this.numero = numero;
        this.usuarioId = usuarioId;
        this.saldo = saldo;
        this.hashPin = hashPin;
    }

    public static Cuenta nueva(String numero, Long usuarioId, String hashPin) {
        return new Cuenta(null, numero, usuarioId, BigDecimal.ZERO, hashPin);
    }

    public void depositar(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new MontoInvalidoException("El monto del depósito debe ser mayor a cero");
        }
        if (monto.stripTrailingZeros().scale() > 0) {
            throw new MontoInvalidoException("El monto debe ser un valor entero en pesos");
        }
        saldo = saldo.add(monto);
    }

    public void retirar(BigDecimal monto) {
        if (!tieneSaldoPara(monto)) {
            throw new SaldoInsuficienteException(saldo);
        }
        saldo = saldo.subtract(monto);
    }

    public boolean tieneSaldoPara(BigDecimal monto) {
        return saldo.compareTo(monto) >= 0;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public Long getUsuarioId() { return usuarioId; }
    public BigDecimal getSaldo() { return saldo; }
    public String getHashPin() { return hashPin; }
}

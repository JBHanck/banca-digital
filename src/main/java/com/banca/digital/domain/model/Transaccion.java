package com.banca.digital.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {

    private Long id;
    private final Long cuentaId;
    private final TipoTransaccion tipo;
    private final BigDecimal monto;
    private final LocalDateTime fecha;
    private final BigDecimal saldoPosterior;

    public Transaccion(Long id, Long cuentaId, TipoTransaccion tipo, BigDecimal monto,
                       LocalDateTime fecha, BigDecimal saldoPosterior) {
        this.id = id;
        this.cuentaId = cuentaId;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.saldoPosterior = saldoPosterior;
    }

    public static Transaccion deposito(Long cuentaId, BigDecimal monto, LocalDateTime fecha,
                                       BigDecimal saldoPosterior) {
        return new Transaccion(null, cuentaId, TipoTransaccion.DEPOSITO, monto, fecha, saldoPosterior);
    }

    public static Transaccion retiro(Long cuentaId, BigDecimal monto, LocalDateTime fecha,
                                     BigDecimal saldoPosterior) {
        return new Transaccion(null, cuentaId, TipoTransaccion.RETIRO, monto, fecha, saldoPosterior);
    }

    public Long getId() { return id; }
    public Long getCuentaId() { return cuentaId; }
    public TipoTransaccion getTipo() { return tipo; }
    public BigDecimal getMonto() { return monto; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getSaldoPosterior() { return saldoPosterior; }
}

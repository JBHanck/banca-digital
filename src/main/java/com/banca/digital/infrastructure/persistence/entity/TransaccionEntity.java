package com.banca.digital.infrastructure.persistence.entity;

import com.banca.digital.domain.model.TipoTransaccion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones",
        indexes = @Index(name = "idx_transacciones_cuenta_fecha", columnList = "cuenta_id, fecha"))
public class TransaccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuenta_id", nullable = false)
    private Long cuentaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTransaccion tipo;

    @Column(nullable = false, precision = 19, scale = 0)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "saldo_posterior", nullable = false, precision = 19, scale = 0)
    private BigDecimal saldoPosterior;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCuentaId() { return cuentaId; }
    public void setCuentaId(Long cuentaId) { this.cuentaId = cuentaId; }
    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public BigDecimal getSaldoPosterior() { return saldoPosterior; }
    public void setSaldoPosterior(BigDecimal saldoPosterior) { this.saldoPosterior = saldoPosterior; }
}

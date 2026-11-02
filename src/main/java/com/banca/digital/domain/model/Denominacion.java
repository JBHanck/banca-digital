package com.banca.digital.domain.model;

/** Billetes físicos del cajero, declarados de mayor a menor valor. */
public enum Denominacion {
    CIEN_MIL(100_000),
    CINCUENTA_MIL(50_000),
    VEINTE_MIL(20_000),
    DIEZ_MIL(10_000);

    private final int valor;

    Denominacion(int valor) {
        this.valor = valor;
    }

    public int valor() {
        return valor;
    }
}

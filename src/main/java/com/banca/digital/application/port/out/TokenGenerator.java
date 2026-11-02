package com.banca.digital.application.port.out;

public interface TokenGenerator {

    String generar(Long usuarioId, String nombre);
}

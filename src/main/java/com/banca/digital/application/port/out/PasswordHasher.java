package com.banca.digital.application.port.out;

public interface PasswordHasher {

    String encriptar(String textoPlano);

    boolean coincide(String textoPlano, String hash);
}

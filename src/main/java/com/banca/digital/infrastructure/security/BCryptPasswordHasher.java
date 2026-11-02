package com.banca.digital.infrastructure.security;

import com.banca.digital.application.port.out.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encriptar(String textoPlano) {
        return encoder.encode(textoPlano);
    }

    @Override
    public boolean coincide(String textoPlano, String hash) {
        return textoPlano != null && hash != null && encoder.matches(textoPlano, hash);
    }
}

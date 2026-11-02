package com.banca.digital.infrastructure.security;

import com.banca.digital.application.port.out.TokenGenerator;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService implements TokenGenerator {

    private final SecretKey clave;
    private final Duration expiracion;

    public JwtService(@Value("${banca.jwt.secret}") String secreto,
                      @Value("${banca.jwt.expiracion-minutos}") long minutos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracion = Duration.ofMinutes(minutos);
    }

    @Override
    public String generar(Long usuarioId, String nombre) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .claim("nombre", nombre)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(expiracion)))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el id del usuario si el token es válido y no ha expirado. */
    public Optional<Long> extraerUsuarioId(String token) {
        try {
            String sujeto = Jwts.parser().verifyWith(clave).build()
                    .parseSignedClaims(token).getPayload().getSubject();
            return Optional.of(Long.valueOf(sujeto));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}

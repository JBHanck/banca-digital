package com.banca.digital.infrastructure.config;

import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.application.port.out.PasswordHasher;
import com.banca.digital.application.port.out.TokenGenerator;
import com.banca.digital.application.port.out.TransaccionRepository;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.application.usecase.ActualizarDatosUseCase;
import com.banca.digital.application.usecase.CambiarClaveUseCase;
import com.banca.digital.application.usecase.ConsultarPerfilUseCase;
import com.banca.digital.application.usecase.ConsultarSaldoUseCase;
import com.banca.digital.application.usecase.DepositarUseCase;
import com.banca.digital.application.usecase.LoginUseCase;
import com.banca.digital.application.usecase.RegistrarUsuarioUseCase;
import com.banca.digital.application.usecase.RetirarEfectivoUseCase;
import com.banca.digital.domain.service.Dispensador;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Raíz de composición: aquí (y solo aquí) se conectan casos de uso con sus adaptadores.
 * Los casos de uso y el dominio no conocen Spring.
 */
@Configuration
public class BeanConfig {

    @Bean
    Clock reloj(@Value("${banca.zona-horaria}") String zona) {
        return Clock.system(ZoneId.of(zona));
    }

    @Bean
    Dispensador dispensador() {
        return new Dispensador();
    }

    @Bean
    RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarios, CuentaRepository cuentas,
                                                    PasswordHasher hasher, UnidadDeTrabajo uow) {
        return new RegistrarUsuarioUseCase(usuarios, cuentas, hasher, uow);
    }

    @Bean
    LoginUseCase loginUseCase(UsuarioRepository usuarios, PasswordHasher hasher, TokenGenerator tokens) {
        return new LoginUseCase(usuarios, hasher, tokens);
    }

    @Bean
    ConsultarSaldoUseCase consultarSaldoUseCase(CuentaRepository cuentas) {
        return new ConsultarSaldoUseCase(cuentas);
    }

    @Bean
    DepositarUseCase depositarUseCase(CuentaRepository cuentas, TransaccionRepository transacciones,
                                      UnidadDeTrabajo uow, Clock reloj) {
        return new DepositarUseCase(cuentas, transacciones, uow, reloj);
    }

    @Bean
    ConsultarPerfilUseCase consultarPerfilUseCase(UsuarioRepository usuarios) {
        return new ConsultarPerfilUseCase(usuarios);
    }

    @Bean
    ActualizarDatosUseCase actualizarDatosUseCase(UsuarioRepository usuarios, UnidadDeTrabajo uow) {
        return new ActualizarDatosUseCase(usuarios, uow);
    }

    @Bean
    CambiarClaveUseCase cambiarClaveUseCase(UsuarioRepository usuarios, PasswordHasher hasher,
                                            UnidadDeTrabajo uow) {
        return new CambiarClaveUseCase(usuarios, hasher, uow);
    }

    @Bean
    RetirarEfectivoUseCase retirarEfectivoUseCase(CuentaRepository cuentas, TransaccionRepository transacciones,
                                                  PasswordHasher hasher, Dispensador dispensador,
                                                  UnidadDeTrabajo uow, Clock reloj) {
        return new RetirarEfectivoUseCase(cuentas, transacciones, hasher, dispensador, uow, reloj);
    }
}

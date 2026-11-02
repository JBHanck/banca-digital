package com.banca.digital.infrastructure.config;

import com.banca.digital.application.dto.Commands.DepositoCommand;
import com.banca.digital.application.dto.Commands.RegistroCommand;
import com.banca.digital.application.dto.Resultados.RegistroResultado;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.application.usecase.DepositarUseCase;
import com.banca.digital.application.usecase.RegistrarUsuarioUseCase;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Crea un usuario de demostración la primera vez que arranca la aplicación. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    public static final String CORREO_DEMO = "demo@banca.com";
    public static final String CLAVE_DEMO = "Demo12345";
    public static final String PIN_DEMO = "1234";

    private final UsuarioRepository usuarios;
    private final RegistrarUsuarioUseCase registrar;
    private final DepositarUseCase depositar;

    public DataSeeder(UsuarioRepository usuarios, RegistrarUsuarioUseCase registrar,
                      DepositarUseCase depositar) {
        this.usuarios = usuarios;
        this.registrar = registrar;
        this.depositar = depositar;
    }

    @Override
    public void run(String... args) {
        if (usuarios.buscarPorCorreo(CORREO_DEMO).isPresent()) {
            return;
        }
        RegistroResultado demo = registrar.ejecutar(new RegistroCommand(
                "Usuario Demo", CORREO_DEMO, "3001234567", "Calle 1 # 2-3, Bucaramanga",
                CLAVE_DEMO, PIN_DEMO));
        depositar.ejecutar(new DepositoCommand(demo.usuarioId(), BigDecimal.valueOf(2_000_000)));
        log.info("Usuario demo creado -> correo: {} | clave: {} | PIN: {} | saldo: $2.000.000",
                CORREO_DEMO, CLAVE_DEMO, PIN_DEMO);
    }
}

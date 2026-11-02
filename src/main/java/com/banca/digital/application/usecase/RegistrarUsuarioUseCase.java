package com.banca.digital.application.usecase;

import com.banca.digital.application.dto.Commands.RegistroCommand;
import com.banca.digital.application.dto.Resultados.RegistroResultado;
import com.banca.digital.application.port.out.CuentaRepository;
import com.banca.digital.application.port.out.PasswordHasher;
import com.banca.digital.application.port.out.UnidadDeTrabajo;
import com.banca.digital.application.port.out.UsuarioRepository;
import com.banca.digital.domain.exception.ConflictoException;
import com.banca.digital.domain.model.Cuenta;
import com.banca.digital.domain.model.Usuario;
import com.banca.digital.domain.service.ReglasCredenciales;
import com.banca.digital.domain.service.ReglasRetiro;
import java.util.concurrent.ThreadLocalRandom;

public class RegistrarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CuentaRepository cuentaRepository;
    private final PasswordHasher hasher;
    private final UnidadDeTrabajo unidadDeTrabajo;

    public RegistrarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                   CuentaRepository cuentaRepository,
                                   PasswordHasher hasher,
                                   UnidadDeTrabajo unidadDeTrabajo) {
        this.usuarioRepository = usuarioRepository;
        this.cuentaRepository = cuentaRepository;
        this.hasher = hasher;
        this.unidadDeTrabajo = unidadDeTrabajo;
    }

    public RegistroResultado ejecutar(RegistroCommand cmd) {
        ReglasCredenciales.validarClave(cmd.clave());
        ReglasRetiro.validarFormatoPin(cmd.pin());
        Usuario nuevo = Usuario.nuevo(cmd.nombre(), cmd.correo(), cmd.telefono(), cmd.direccion(),
                hasher.encriptar(cmd.clave()));
        String hashPin = hasher.encriptar(cmd.pin());

        return unidadDeTrabajo.ejecutar(() -> {
            if (usuarioRepository.buscarPorCorreo(nuevo.getCorreo()).isPresent()) {
                throw new ConflictoException("CORREO_YA_REGISTRADO",
                        "Ya existe un usuario registrado con ese correo");
            }
            Usuario usuario = usuarioRepository.guardar(nuevo);
            Cuenta cuenta = cuentaRepository.guardar(
                    Cuenta.nueva(generarNumeroCuenta(), usuario.getId(), hashPin));
            return new RegistroResultado(usuario.getId(), usuario.getNombre(), cuenta.getNumero());
        });
    }

    private String generarNumeroCuenta() {
        String numero;
        do {
            numero = String.format("%010d", ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
        } while (cuentaRepository.existePorNumero(numero));
        return numero;
    }
}

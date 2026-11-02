package com.banca.digital.domain.model;

import com.banca.digital.domain.exception.ValidacionException;
import java.util.regex.Pattern;

public class Usuario {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?[0-9]{7,15}$");

    private Long id;
    private String nombre;
    private String correo;
    private String telefono;
    private String direccion;
    private String hashClave;

    public Usuario(Long id, String nombre, String correo, String telefono, String direccion,
                   String hashClave) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.hashClave = hashClave;
    }

    /** Crea un usuario nuevo validando todos sus datos. */
    public static Usuario nuevo(String nombre, String correo, String telefono, String direccion,
                                String hashClave) {
        exigirTexto(nombre, "nombre");
        exigirTexto(direccion, "dirección");
        return new Usuario(null, nombre.trim(), validarCorreo(correo), validarTelefono(telefono),
                direccion.trim(), hashClave);
    }

    /** Actualiza solo los campos enviados (los nulos se conservan). */
    public void actualizarDatos(String correo, String telefono, String direccion) {
        if (correo != null) {
            this.correo = validarCorreo(correo);
        }
        if (telefono != null) {
            this.telefono = validarTelefono(telefono);
        }
        if (direccion != null) {
            exigirTexto(direccion, "dirección");
            this.direccion = direccion.trim();
        }
    }

    public void cambiarClave(String nuevoHash) {
        this.hashClave = nuevoHash;
    }

    public static String normalizarCorreo(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }

    public static String validarCorreo(String correo) {
        String normalizado = normalizarCorreo(correo);
        if (normalizado == null || !CORREO.matcher(normalizado).matches()) {
            throw new ValidacionException("CORREO_INVALIDO", "El correo electrónico no tiene un formato válido");
        }
        return normalizado;
    }

    public static String validarTelefono(String telefono) {
        String limpio = telefono == null ? null : telefono.trim();
        if (limpio == null || !TELEFONO.matcher(limpio).matches()) {
            throw new ValidacionException("TELEFONO_INVALIDO",
                    "El teléfono debe tener entre 7 y 15 dígitos (puede iniciar con +)");
        }
        return limpio;
    }

    private static void exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ValidacionException("DATOS_INVALIDOS", "El campo " + campo + " no puede estar vacío");
        }
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getHashClave() { return hashClave; }
}

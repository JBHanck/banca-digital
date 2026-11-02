package com.banca.digital.application.port.out;

import java.util.function.Supplier;

/**
 * Ejecuta una acción de forma atómica (todo o nada). Si la acción lanza una excepción,
 * se revierten todos los cambios. Mantiene la capa de aplicación libre de Spring.
 */
public interface UnidadDeTrabajo {

    <T> T ejecutar(Supplier<T> accion);
}

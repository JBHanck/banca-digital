package com.banca.digital.application.port.out;

import com.banca.digital.domain.model.Transaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransaccionRepository {

    Transaccion guardar(Transaccion transaccion);

    /** Suma de retiros en el rango [desde, hasta). Devuelve cero si no hay ninguno. */
    BigDecimal sumarRetirosEntre(Long cuentaId, LocalDateTime desde, LocalDateTime hasta);
}

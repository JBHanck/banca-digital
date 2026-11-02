package com.banca.digital.infrastructure.config;

import com.banca.digital.application.port.out.UnidadDeTrabajo;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** Implementa la transacción atómica con Spring, sin contaminar los casos de uso. */
@Component
public class UnidadDeTrabajoAdapter implements UnidadDeTrabajo {

    private final TransactionTemplate plantilla;

    public UnidadDeTrabajoAdapter(PlatformTransactionManager transactionManager) {
        this.plantilla = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T ejecutar(Supplier<T> accion) {
        return plantilla.execute(status -> accion.get());
    }
}

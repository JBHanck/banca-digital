package com.banca.digital.infrastructure.web;

import com.banca.digital.application.dto.Commands.RetiroCommand;
import com.banca.digital.application.usecase.RetirarEfectivoUseCase;
import com.banca.digital.infrastructure.web.dto.Requests.RetiroRequest;
import com.banca.digital.infrastructure.web.dto.Responses.RetiroResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cajero")
public class CajeroController {

    private final RetirarEfectivoUseCase retirarEfectivo;

    public CajeroController(RetirarEfectivoUseCase retirarEfectivo) {
        this.retirarEfectivo = retirarEfectivo;
    }

    @PostMapping("/retiros")
    public RetiroResponse retirar(@AuthenticationPrincipal Long usuarioId,
                                  @Valid @RequestBody RetiroRequest req) {
        return RetiroResponse.desde(
                retirarEfectivo.ejecutar(new RetiroCommand(usuarioId, req.monto(), req.pin())));
    }
}

package com.banca.digital.infrastructure.web;

import com.banca.digital.application.dto.Commands.DepositoCommand;
import com.banca.digital.application.dto.Resultados.SaldoResultado;
import com.banca.digital.application.usecase.ConsultarSaldoUseCase;
import com.banca.digital.application.usecase.DepositarUseCase;
import com.banca.digital.infrastructure.web.dto.Requests.DepositoRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final ConsultarSaldoUseCase consultarSaldo;
    private final DepositarUseCase depositar;

    public CuentaController(ConsultarSaldoUseCase consultarSaldo, DepositarUseCase depositar) {
        this.consultarSaldo = consultarSaldo;
        this.depositar = depositar;
    }

    @GetMapping("/saldo")
    public SaldoResultado saldo(@AuthenticationPrincipal Long usuarioId) {
        return consultarSaldo.ejecutar(usuarioId);
    }

    @PostMapping("/depositos")
    public SaldoResultado depositar(@AuthenticationPrincipal Long usuarioId,
                                    @Valid @RequestBody DepositoRequest req) {
        return depositar.ejecutar(new DepositoCommand(usuarioId, req.monto()));
    }
}

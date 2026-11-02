package com.banca.digital.infrastructure.web;

import com.banca.digital.domain.exception.AutenticacionException;
import com.banca.digital.domain.exception.ConflictoException;
import com.banca.digital.domain.exception.DominioException;
import com.banca.digital.domain.exception.NoEncontradoException;
import com.banca.digital.domain.exception.ReglaNegocioException;
import com.banca.digital.domain.exception.ValidacionException;
import com.banca.digital.infrastructure.web.dto.Responses.ErrorResponse;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce toda excepción a una respuesta JSON uniforme: {codigo, mensaje, status, timestamp}. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<ErrorResponse> manejarDominio(DominioException ex) {
        HttpStatus status = switch (ex) {
            case ValidacionException e -> HttpStatus.BAD_REQUEST;
            case ReglaNegocioException e -> HttpStatus.UNPROCESSABLE_ENTITY;
            case AutenticacionException e -> HttpStatus.UNAUTHORIZED;
            case ConflictoException e -> HttpStatus.CONFLICT;
            case NoEncontradoException e -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.BAD_REQUEST;
        };
        return responder(status, ex.getCodigo(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> "El campo " + f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return responder(HttpStatus.BAD_REQUEST, "SOLICITUD_INVALIDA", detalle);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoIlegible(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO",
                "El cuerpo de la petición es inválido o tiene valores con formato incorrecto");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        return responder(HttpStatus.CONFLICT, "CONFLICTO_DATOS",
                "La operación entra en conflicto con datos existentes (por ejemplo, un correo ya registrado)");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(Exception ex) {
        log.error("Error inesperado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error inesperado. Intente nuevamente");
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String codigo, String mensaje) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(codigo, mensaje, status.value(), LocalDateTime.now()));
    }
}

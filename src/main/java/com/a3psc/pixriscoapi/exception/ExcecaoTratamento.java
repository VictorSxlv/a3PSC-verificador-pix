package com.a3psc.pixriscoapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExcecaoTratamento {

    @ExceptionHandler(ExcecaoRegrasNegocio.class)
    public ResponseEntity<Object> handleExcecaoRegrasNegocio(ExcecaoRegrasNegocio ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Regra de negócio violada");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }

        @ExceptionHandler(ExcecaoNaoEncontrado.class)
        public ResponseEntity<Object> handleExcecaoNaoEncontrado(ExcecaoNaoEncontrado ex, HttpServletRequest request) {
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.NOT_FOUND.value());
            body.put("error", "Recurso não encontrado");
            body.put("message", ex.getMessage());
            body.put("path", request.getRequestURI());

            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);

    }
}
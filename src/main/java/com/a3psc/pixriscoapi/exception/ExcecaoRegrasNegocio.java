package com.a3psc.pixriscoapi.exception;

import org.springframework.http.HttpStatus; //Biblioteca com os cósigos HTTP
import org.springframework.web.bind.annotation.ResponseStatus; //Ferramenta pra tratar erros do Spring

@ResponseStatus(HttpStatus.CONFLICT) //estou usando o erro 409 pra indicar erros de negócio
public class ExcecaoRegrasNegocio extends RuntimeException {
    public ExcecaoRegrasNegocio(String message) {
        super(message);
    }
}

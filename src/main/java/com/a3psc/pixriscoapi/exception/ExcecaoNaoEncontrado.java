package com.a3psc.pixriscoapi.exception;

import org.springframework.http.HttpStatus; //Biblioteca com os cósigos HTTP

public class ExcecaoNaoEncontrado extends RuntimeException {
    public ExcecaoNaoEncontrado(String message) {
        super(message);
    }
}
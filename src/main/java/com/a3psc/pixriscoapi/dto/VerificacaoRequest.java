package com.a3psc.pixriscoapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificacaoRequest {
    private String chavePix;
    private double valorTransferencia;
}

package com.a3psc.pixriscoapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder

public class VerificacaoResponse {
    private String chaveConsultada;
    private String nivelRisco; 
    private Integer tempoBloqueioHoras;
    private Integer quantidadeDenuncias;
    private String nomeDestinatario;
    private String bancoDestinatario;
    private String cpfDestinatario;
    private Double valorTransferencia;
    private LocalDateTime dataHoraConsulta;
    private String tipoContaDestinatario;
}

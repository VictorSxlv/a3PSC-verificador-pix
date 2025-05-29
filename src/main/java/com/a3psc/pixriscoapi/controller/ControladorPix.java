package com.a3psc.pixriscoapi.controller;

import com.a3psc.pixriscoapi.dto.VerificacaoRequest;
import com.a3psc.pixriscoapi.dto.VerificacaoResponse;
import com.a3psc.pixriscoapi.service.PixRiscoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Classe padrão do Spring onde a resposta da requisição fica armazenada
import org.springframework.web.bind.annotation.PostMapping; // Diz que a requisição HTTP é Post e dita oq vai aconecer
import org.springframework.web.bind.annotation.RequestBody; //Indica que deve usar o Request(q vem do front) no método
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/verificadorpix")

public class ControladorPix {

    @Autowired
    private PixRiscoServico pixRiscoServico;

    // Endpoint /verificar-chavepix
    @PostMapping("/verificar-chavepix")
    public ResponseEntity<VerificacaoResponse> verificarChavePix(@RequestBody VerificacaoRequest request) {

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        // O sistema bloqueia a transação temporariamente caso a API retorne um alto risco de fraude.
        String risco = response.getNivelRisco();
        int tempo = response.getTempoBloqueioHoras();

        if (risco != null && risco.equals("RISCO_ALTO") && tempo > 0) {
            String chave = request.getChavePix();
            pixRiscoServico.aplicarBloqueioConta(chave, tempo);
        }

        return ResponseEntity.ok(response);
    }
}
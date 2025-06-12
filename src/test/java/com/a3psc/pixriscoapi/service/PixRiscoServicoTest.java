package com.a3psc.pixriscoapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

import com.a3psc.pixriscoapi.dto.VerificacaoRequest;
import com.a3psc.pixriscoapi.dto.VerificacaoResponse;
import com.a3psc.pixriscoapi.exception.ExcecaoNaoEncontrado;
import com.a3psc.pixriscoapi.exception.ExcecaoRegrasNegocio;
import com.a3psc.pixriscoapi.repository.ContaRepository;
import com.a3psc.pixriscoapi.model.Conta;

import java.time.LocalDateTime;

@SpringBootTest // Carrega o contexto completo da aplicação Spring para o teste
@ActiveProfiles("test") // Ativa o application-test.properties, forçando o uso do H2
class PixRiscoServicoTest {

    @Autowired
    private PixRiscoServico pixRiscoServico;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @DisplayName("Deve retornar RISCO_BAIXO para uma chave segura e antiga")
    void TesteRiscoBaixoChaveSegura() {

        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("111.111.111-11"); // Chave segura
        request.setValorTransferencia(500.0);

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        assertNotNull(response);
        assertEquals("RISCO_BAIXO", response.getNivelRisco());
        assertEquals(0, response.getTempoBloqueioHoras());
    }
    @Test
    @DisplayName("Deve retornar RISCO_MEDIO para chave com conta e chave recentes")
    void TesteRiscoMedioChaveContaRecente() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("maria.oliveira@email.com"); // Chave de risco médio
        request.setValorTransferencia(2500.0);

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        assertNotNull(response);
        assertEquals("RISCO_MEDIO", response.getNivelRisco());
    }

    @Test
    @DisplayName("Deve retornar RISCO_ALTO quando o score for maior ou igual a 13")
    void TesteRiscoAltoChaveScoreElevado() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("a1b2c3d4-e5f6-7777-8888-9999abcdeff0"); // Chave de risco alto (score)
        request.setValorTransferencia(5001.0);

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        assertNotNull(response);
        assertEquals("RISCO_ALTO", response.getNivelRisco());
        assertTrue(response.getTempoBloqueioHoras() > 0);
    }

    @Test
    @DisplayName("Deve retornar RISCO_ALTO para chave com 3 ou mais denúncias")
    void TesteRiscoAltoChaveMuitasDenuncias() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("+5521988887777"); // Chave de risco algo (3 denúncias)
        request.setValorTransferencia(100.0);

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        assertNotNull(response);
        assertEquals("RISCO_ALTO", response.getNivelRisco());
        assertEquals(3, response.getQuantidadeDenuncias());
    }

    @Test
    @DisplayName("Deve retornar RISCO_ALTO para chave com denúncias de alta confiança")
    void TesteRiscoAltoChaveDenunciasConfiaveis() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("beatriz.lima@email.com"); // Chave com 2 denúncias de confiança alta
        request.setValorTransferencia(100.0);

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        assertNotNull(response);
        assertEquals("RISCO_ALTO", response.getNivelRisco());
        assertEquals(2, response.getQuantidadeDenuncias());
    }

    @Test
    @DisplayName("Deve lançar ExcecaoNaoEncontrado para uma chave PIX que não existe")
    void TesteExcecaoChaveInexistente() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("chave.inexistente@dominio.com");
        request.setValorTransferencia(100.0);

        assertThrows(ExcecaoNaoEncontrado.class, () -> {
            pixRiscoServico.verificarRiscoChavePix(request);
        });
    }

    @Test
    @DisplayName("Deve lançar ExcecaoRegrasNegocio para uma conta que já está bloqueada")
    void TesteExcecaoContaJaBloqueada() {
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix("+5521977778888"); // Chave cuja conta está bloqueada no data.sql
        request.setValorTransferencia(100.0);

        ExcecaoRegrasNegocio exception = assertThrows(ExcecaoRegrasNegocio.class, () -> {
            pixRiscoServico.verificarRiscoChavePix(request);
        });

        assertTrue(exception.getMessage().contains("Conta associada à chave PIX está temporariamente bloqueada"));
    }

    @Test
    @DisplayName("Deve aplicar o bloqueio na conta quando o risco alto for por score")
    void TesteBloqueioContaRiscoAlto() {
        String chaveDeRiscoAlto = "joao.nogueira@email.com";
        VerificacaoRequest request = new VerificacaoRequest();
        request.setChavePix(chaveDeRiscoAlto);
        request.setValorTransferencia(10000.0);

        // pra garatir que a conta não está bloqueada antes do teste
        Conta contaAntes = contaRepository.findById(7).orElseThrow();
        assertNull(contaAntes.getBloqueadaate());

        VerificacaoResponse response = pixRiscoServico.verificarRiscoChavePix(request);

        if ("RISCO_ALTO".equals(response.getNivelRisco())) {
            pixRiscoServico.aplicarBloqueioConta(chaveDeRiscoAlto, response.getTempoBloqueioHoras());
        }

        Conta contaDepois = contaRepository.findById(7).orElseThrow();
        assertEquals("RISCO_ALTO", response.getNivelRisco());
        assertNotNull(contaDepois.getBloqueadaate(), "A data de bloqueio não deveria ser nula após a operação.");
        assertTrue(contaDepois.getBloqueadaate().isAfter(LocalDateTime.now()), "A data de bloqueio deveria ser no futuro.");
    }
}
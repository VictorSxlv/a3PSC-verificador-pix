package com.a3psc.pixriscoapi.service;

//Importando classes do DTO(que vai armazenar os parâmetros enviados/recebidos pela API)
import com.a3psc.pixriscoapi.dto.VerificacaoRequest;
import com.a3psc.pixriscoapi.dto.VerificacaoResponse;

//Importando tratamento de excessões
import com.a3psc.pixriscoapi.exception.ExcecaoRegrasNegocio;
import com.a3psc.pixriscoapi.exception.ExcecaoNaoEncontrado;

//Importando todas as classes do model
import com.a3psc.pixriscoapi.model.*;

//Importando repositórios (com os métodos da API)
import com.a3psc.pixriscoapi.repository.ChaveRepository;
import com.a3psc.pixriscoapi.repository.DenunciaRepository;
import com.a3psc.pixriscoapi.repository.ContaRepository;

import org.springframework.beans.factory.annotation.Autowired; //Autoriza o spring a criar objetos automaticamente por injeção de dependência
import org.springframework.stereotype.Service; //Indica pro Spring que a classe contém as regras de negócio
import org.springframework.transaction.annotation.Transactional; // Garante que as transações serão feitas juntas

//Importando bibliotecas java
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PixRiscoServico {

    @Autowired
    private ChaveRepository chaveRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private DenunciaRepository denunciaRepository;

    // Constantes para os níveis de risco
    private static final String RISCO_BAIXO = "BAIXO";
    private static final String RISCO_MEDIO = "MEDIO";
    private static final String RISCO_ALTO = "ALTO";

    @Transactional(readOnly = true) // li no stack overflow que "readOnly" melhora a performance
    public VerificacaoResponse verificarRiscoChavePix(VerificacaoRequest request) {
        // Verifica se a chave existe e não está bloqueada
        Chave chave = chaveRepository.findBychavepix(request.getChavePix()).orElseThrow(() -> new ExcecaoNaoEncontrado("Chave PIX não encontrada:"));

        Conta conta = chave.getConta();

        if (conta.getBloqueadaate() != null && conta.getBloqueadaate().isAfter(LocalDateTime.now())) {
            throw new ExcecaoRegrasNegocio("Chave PIX temporariamente bloqueada para transações até " + conta.getBloqueadaate());
        }

        Conta contaDestino = chave.getConta();
        Usuario usuarioDestino = contaDestino.getUsuario();

        // Calculando o risco
        String nivelRiscoFinal = "RISCO_BAIXO"; // Começa como baixo por padrão
        int score = 0;
        int quantidadeDenuncias = 0;
        int tempoBloqueioHoras = 0;

        // Verifica denúncias
        List<Denuncia> denuncias = denunciaRepository.findBychave(chave);
        quantidadeDenuncias = denuncias.size();

        boolean altoRiscoPorDenuncia = false;

        if (quantidadeDenuncias > 2) {
            nivelRiscoFinal = "RISCO_ALTO";
            altoRiscoPorDenuncia = true;
        } else if (quantidadeDenuncias >= 1) {

            double somaConfianca = 0.0;

            for (Denuncia denuncia : denuncias) {
                somaConfianca += denuncia.getConfiab();
            }

            double mediaConfianca = somaConfianca / quantidadeDenuncias;
            if (mediaConfianca > 0.50) { // Confiança geral > 50%
                nivelRiscoFinal = "RISCO_ALTO";
                altoRiscoPorDenuncia = true;
            }
        }

        // Calcula score se não for alto risco por denúncia
        if (!altoRiscoPorDenuncia) {

            // Valor da transferência
            if (request.getValorTransferencia() > 5000) {
                score += 2;
            } else if (request.getValorTransferencia() > 1000) {
                score += 1;
            }

            // Meses desde a abertura da conta
            LocalDateTime dataCriacaoconta = contaDestino.getDatacriacao();
            LocalDateTime agoraconta = LocalDateTime.now();

            int anoCriacao = dataCriacaoconta.getYear();
            int mesCriacao = dataCriacaoconta.getMonthValue();

            int anoAtual = agoraconta.getYear();
            int mesAtual = agoraconta.getMonthValue();

            int mesesConta = (anoAtual - anoCriacao) * 12 + (mesAtual - mesCriacao);

            if (mesesConta <= 3) {
                score += 2;
            } else if (mesesConta <= 12) {
                score += 1;
            }

            // Dias desde a criação da chave
            LocalDateTime dataCriacaochave = chave.getDatacriacao();
            LocalDateTime agorachave = LocalDateTime.now();
            long diffMillis = java.time.Duration.between(dataCriacaochave, agorachave).toMillis();
            long diasChave = diffMillis / (1000 * 60 * 60 * 24);

            if (diasChave <= 7) {
                score += 3;
            } else if (diasChave <= 30) {
                score += 2;
            } else if (diasChave <= 180) {
                score += 1;
            }

            // Média de transações semanais
            int mediaTransacoesSemanais;
            if (contaDestino.getMediatransacoessemanais() != null) {
                mediaTransacoesSemanais = contaDestino.getMediatransacoessemanais();
            } else {
                mediaTransacoesSemanais = 0;
            }

            if (mediaTransacoesSemanais <= 1) {
                score += 3;
            } else if (mediaTransacoesSemanais <= 5) {
                score += 2;
            } else if (mediaTransacoesSemanais <= 10) {
                score += 1;
            }

            // Movimentações no último mês
            String movimentacao = contaDestino.getPadraomovimentacoesultimomes();

            if (movimentacao != null) {
                if (movimentacao.equals("APENAS_1_OU_2_ALTAS_IRREGULARES")) {
                    score = score + 3;
                } else if (movimentacao.equals("VOLUME_BAIXO_INCONSISTENTE")) {
                    score = score + 2;
                } else if (movimentacao.equals("VOLUME_MEDIO_RAZOAVEL")) {
                    score = score + 1;
                }
            }


            // Tipo de chave
            switch (chave.getTipo()) {
                case ALEATORIA:
                    score += 3;
                    break;
                case EMAIL:
                    score += 2;
                    break;
                case TELEFONE:
                    score += 1;
                    break;
                case CPF:
                    // Não soma pontos
                    break;
            }

            // Determinar nível de risco com base no score
            if (score >= 13) {
                nivelRiscoFinal = "RISCO_ALTO";
            } else if (score >= 7) {
                nivelRiscoFinal = "RISCO_MEDIO";
            } else { // score <= 6
                nivelRiscoFinal = "RISCO_BAIXO";
            }
        }


        // Calcular tempo de bloqueio para ALTO RISCO
        if (nivelRiscoFinal.equals("RISCO_ALTO")) {
            tempoBloqueioHoras = tempoBloqueioHoras + quantidadeDenuncias;
        }

            // +1 hora para cada ponto acima do 6º ponto de score
            if (score > 6) {
                tempoBloqueioHoras += (score - 6);
            }

            // Limitando a 24 horas
            if (tempoBloqueioHoras > 24) {
                tempoBloqueioHoras = 24;
            }

        // Fabricação da resposta JSON

        int horasBloqueio = 0;
        if (nivelRiscoFinal.equals("RISCO_ALTO")) {
            horasBloqueio = tempoBloqueioHoras;
        }

        return VerificacaoResponse.builder()
                .chaveConsultada(chave.getChavepix())
                .nivelRisco(nivelRiscoFinal)
                .tempoBloqueioHoras(horasBloqueio)
                .quantidadeDenuncias(quantidadeDenuncias)
                .nomeDestinatario(usuarioDestino.getNome())
                .bancoDestinatario(contaDestino.getInstituicao())
                .cpfDestinatario(mascararCpf(usuarioDestino.getCpf()))
                .valorTransferencia(request.getValorTransferencia())
                .dataHoraConsulta(LocalDateTime.now())
                .tipoContaDestinatario(contaDestino.getTipo())
                .build();
    }


    // Bloqueio temporário da transação para a chave pix de destino (chamada no controller caso o risco for alto)
    @Transactional
    public void aplicarBloqueioConta(String chavePix, int horasBloqueio) {
        if (horasBloqueio <= 0) return;

        Chave chave = chaveRepository.findBychavepix(chavePix)
                .orElseThrow(() -> new ExcecaoNaoEncontrado("Chave PIX não encontrada para aplicar bloqueio: "));

        Conta conta = chave.getConta();

        conta.setBloqueadaate(LocalDateTime.now().plusHours(horasBloqueio));
        contaRepository.save(conta);
    }

//Aplicação de máscara no CPF
    private String mascararCpf(String cpf) {
       if (cpf != null && cpf.length() == 14 ) {
            return "***." + cpf.substring(3,6) + ".***-" + cpf.substring(9);
        }
        return "CPF Inválido";
    }
}
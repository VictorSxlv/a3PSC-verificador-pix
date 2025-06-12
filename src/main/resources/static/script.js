document.addEventListener('DOMContentLoaded', () => {

    const chavePixInput = document.getElementById('chavePixInput');
    const valorInput = document.getElementById('valorInput');
    const tela1Erro = document.getElementById('tela1-erro');

    const btnContinuarTela1 = document.getElementById('btnContinuarTela1');
    const btnVoltarTela2 = document.getElementById('btnVoltarTela2');
    const btnAjudaTela2 = document.getElementById('btnAjudaTela2');
    const btnVoltarTela3 = document.getElementById('btnVoltarTela3');
    const btnVoltarTela4 = document.getElementById('btnVoltarTela4');
    const btnFinalizarTela4 = document.getElementById('btnFinalizarTela4');
    const btnFecharTela5 = document.getElementById('btnFecharTela5');
    const checkboxRisco = document.getElementById('checkboxRisco');
    const btnFinalizarTela6 = document.getElementById('btnFinalizarTela6');
    const btnFecharTela6 = document.getElementById('btnFecharTela6');
    const btnFecharTela7 = document.getElementById('btnFecharTela7');

    const API_BASE_URL = '';
    const API_ENDPOINT = `${API_BASE_URL}/api/verificadorpix/verificar-chavepix`;

    let apiResponseData = null;

    // === Funções Utilitárias ===
    function mostrarTela(idTela) {
        document.querySelectorAll('.tela').forEach(tela => tela.classList.remove('ativa'));
        const telaAtiva = document.getElementById(idTela);
        if (telaAtiva) {
            telaAtiva.classList.add('ativa');
        }
        if (idTela === 'tela1') {
            tela1Erro.textContent = '';
            chavePixInput.value = '';
            valorInput.value = '';
            apiResponseData = null;
        }
        if (idTela === 'tela6') {
            checkboxRisco.checked = false;
            btnFinalizarTela6.disabled = true;
        }
    }

    function formatCurrency(value) {
        if (typeof value !== 'number') return '0,00';
        return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }).replace('R$', '').trim();
    }

    function formatDateTime(isoString) {
        if (!isoString) return '--/--/---- --:--';
        try {
            const date = new Date(isoString);
            if (isNaN(date.getTime())) return '--/--/---- --:--';
            return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        } catch (e) {
            return '--/--/---- --:--';
        }
    }

    function formatDate(isoString) {
        if (!isoString) return '--/--/----';
        try {
            const date = new Date(isoString);
            if (isNaN(date.getTime())) return '--/--/----';
            return date.toLocaleDateString('pt-BR');
        } catch (e) {
            return '--/--/----';
        }
    }

    function formatDateTimeFromErrorMessage(message) {
        const match = message.match(/até\s(.+)/);
        if (match && match[1]) {
            return formatDateTime(match[1]);
        }
        return 'Data indisponível';
    }

    function setButtonLoading(button, isLoading, defaultText = "Continuar") {
        if (isLoading) {
            button.disabled = true;
            button.textContent = 'Aguarde...';
        } else {
            button.disabled = false;
            button.textContent = defaultText;
        }
    }


    btnContinuarTela1.addEventListener('click', async () => {
        const chavePix = chavePixInput.value.trim();
        const valorTransferencia = parseFloat(valorInput.value);

        tela1Erro.textContent = '';

        if (!chavePix) {
            tela1Erro.textContent = 'Por favor, insira a Chave PIX.';
            return;
        }
        if (isNaN(valorTransferencia) || valorTransferencia <= 0) {
            tela1Erro.textContent = 'Por favor, insira um valor de transferência válido.';
            return;
        }

        setButtonLoading(btnContinuarTela1, true);

        try {
            const response = await fetch(API_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ chavePix, valorTransferencia }),
            });

            if (response.ok) {
                apiResponseData = await response.json();
                document.getElementById('tela4-nome').textContent = apiResponseData.nomeDestinatario || 'N/A';
                document.getElementById('tela4-cpf').textContent = apiResponseData.cpfDestinatario || 'N/A';
                document.getElementById('tela4-banco').textContent = apiResponseData.bancoDestinatario || 'N/A';
                document.getElementById('tela4-chave').textContent = apiResponseData.chaveConsultada || 'N/A';
                document.getElementById('tela4-tipoConta').textContent = apiResponseData.tipoContaDestinatario || 'N/A';
                document.getElementById('tela4-valor').textContent = formatCurrency(apiResponseData.valorTransferencia);
                document.getElementById('tela4-data').textContent = formatDate(apiResponseData.dataHoraConsulta);
                mostrarTela('tela4');
            } else if (response.status === 404) {
                const errorData = await response.json();
                document.getElementById('tela2-mensagem').textContent = errorData.message || "A Chave PIX informada não foi encontrada.";
                mostrarTela('tela2');
            } else if (response.status === 400) {
                const errorData = await response.json();
                const chaveInformada = chavePixInput.value.trim(); // Pega o valor atual do input
                document.getElementById('tela3-mensagem-principal').innerHTML = `Por questões de segurança, a chave PIX <strong>${chaveInformada}</strong> está temporariamente bloqueada para receber transações suas.`;
                document.getElementById('tela3-dataBloqueio').textContent = `Bloqueio vigente até: ${formatDateTimeFromErrorMessage(errorData.message)}`;
                mostrarTela('tela3');
            } else {
                const errorData = await response.json();
                tela1Erro.textContent = `Erro: ${errorData.message || response.statusText || 'Não foi possível processar sua solicitação.'}`;
            }
        } catch (error) {
            console.error('Erro na requisição:', error);
            tela1Erro.textContent = 'Erro de conexão. Verifique sua rede e tente novamente.';
        } finally {
            setButtonLoading(btnContinuarTela1, false, "Continuar");
        }
    });

    btnVoltarTela2.addEventListener('click', () => mostrarTela('tela1'));
    btnAjudaTela2.addEventListener('click', () => {
        alert("Funcionalidade 'Preciso de ajuda' ainda não implementada.\nPor favor, verifique os dados ou contate o suporte do Bradesco.");
    });

    btnVoltarTela3.addEventListener('click', () => mostrarTela('tela1'));
    btnVoltarTela4.addEventListener('click', () => mostrarTela('tela1'));

    btnFinalizarTela4.addEventListener('click', () => {
        if (!apiResponseData) {
            alert('Erro: Dados da API não encontrados. Por favor, inicie novamente.');
            mostrarTela('tela1');
            return;
        }

        console.log("Dados completos recebidos da API (Tela 4):", apiResponseData);
        const nivelRiscoRecebido = apiResponseData.nivelRisco;
        console.log("Valor exato de 'nivelRisco' recebido (Tela 4):", nivelRiscoRecebido);
        console.log("Tipo de 'nivelRisco':", typeof nivelRiscoRecebido);

        const nivelRisco = typeof nivelRiscoRecebido === 'string' ? nivelRiscoRecebido.trim().toUpperCase() : null;
        console.log("Nivel de Risco Normalizado para Comparação:", nivelRisco);


        if (nivelRisco === 'BAIXO' || nivelRisco === 'RISCO_BAIXO') {
            document.getElementById('tela5-nome').textContent = apiResponseData.nomeDestinatario || 'N/A';
            document.getElementById('tela5-cpf').textContent = apiResponseData.cpfDestinatario || 'N/A';
            document.getElementById('tela5-banco').textContent = apiResponseData.bancoDestinatario || 'N/A';
            document.getElementById('tela5-chavePix').textContent = apiResponseData.chaveConsultada || 'N/A';
            document.getElementById('tela5-tipoConta').textContent = apiResponseData.tipoContaDestinatario || 'N/A';
            document.getElementById('tela5-valor').textContent = formatCurrency(apiResponseData.valorTransferencia);
            document.getElementById('tela5-dataHora').textContent = formatDateTime(apiResponseData.dataHoraConsulta);
            document.getElementById('tela5-id').textContent = `PIX-${Date.now().toString().slice(-4)}-${Math.random().toString(36).substring(2, 6).toUpperCase()}-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
            mostrarTela('tela5');
        } else if (nivelRisco === 'MEDIO' || nivelRisco === 'RISCO_MEDIO') {
            mostrarTela('tela6');
        } else if (nivelRisco === 'ALTO' || nivelRisco === 'RISCO_ALTO') {
            let motivoMsg = "Por motivos de segurança, sua transferência para este destinatário foi bloqueada devido ao alto risco detectado.";
            document.getElementById('tela7-motivo').textContent = motivoMsg;

            if (apiResponseData.quantidadeDenuncias > 0) {
                document.getElementById('tela7-denuncias').textContent = `Quantidade de denúncias anteriores para esta chave: ${apiResponseData.quantidadeDenuncias}.`;
            } else {
                document.getElementById('tela7-denuncias').textContent = '';
            }

            if (apiResponseData.tempoBloqueioHoras > 0) {
                const dataConsulta = new Date(apiResponseData.dataHoraConsulta);
                const dataFimBloqueio = new Date(dataConsulta.getTime() + apiResponseData.tempoBloqueioHoras * 60 * 60 * 1000);
                document.getElementById('tela7-tempoBloqueio').textContent = `Devido a esta tentativa, a chave ficará bloqueada para novas transações até: ${formatDateTime(dataFimBloqueio.toISOString())}.`;
            } else {
                document.getElementById('tela7-tempoBloqueio').textContent = "A transação foi bloqueada, mas não foi especificado um novo período de bloqueio adicional para a chave.";
            }
            mostrarTela('tela7');
        } else {
            alert(`Nível de risco não reconhecido: "${nivelRiscoRecebido}". Verifique o console para mais detalhes. Voltando ao início.`);
            console.error("Valor não esperado para nivelRisco após tentativa de normalização:", nivelRiscoRecebido, "(Normalizado:", nivelRisco + ")");
            mostrarTela('tela1');
        }
    });


    btnFecharTela5.addEventListener('click', () => mostrarTela('tela1'));

    checkboxRisco.addEventListener('change', () => {
        btnFinalizarTela6.disabled = !checkboxRisco.checked;
    });

    btnFecharTela6.addEventListener('click', () => mostrarTela('tela1'));

    btnFinalizarTela6.addEventListener('click', () => {
        if (!checkboxRisco.checked) {
            alert("Você precisa confirmar que entende os riscos para prosseguir.");
            return;
        }
        if (!apiResponseData) {
            alert('Erro: Dados da API não encontrados. Por favor, inicie novamente.');
            mostrarTela('tela1');
            return;
        }
        document.getElementById('tela5-nome').textContent = apiResponseData.nomeDestinatario || 'N/A';
        document.getElementById('tela5-cpf').textContent = apiResponseData.cpfDestinatario || 'N/A';
        document.getElementById('tela5-banco').textContent = apiResponseData.bancoDestinatario || 'N/A';
        document.getElementById('tela5-chavePix').textContent = apiResponseData.chaveConsultada || 'N/A';
        document.getElementById('tela5-tipoConta').textContent = apiResponseData.tipoContaDestinatario || 'N/A';
        document.getElementById('tela5-valor').textContent = formatCurrency(apiResponseData.valorTransferencia);
        document.getElementById('tela5-dataHora').textContent = formatDateTime(apiResponseData.dataHoraConsulta);
        document.getElementById('tela5-id').textContent = `PIX-${Date.now().toString().slice(-4)}-${Math.random().toString(36).substring(2, 6).toUpperCase()}-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
        mostrarTela('tela5');
    });

    btnFecharTela7.addEventListener('click', () => mostrarTela('tela1'));

    mostrarTela('tela1');
});
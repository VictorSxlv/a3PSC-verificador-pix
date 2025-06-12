-- USUÁRIOS:
INSERT INTO Usuario (ID, Nome, CPF) VALUES
    (1, 'João Silva (Seguro)', '11111111111'),
    (2, 'Maria Oliveira (Médio Risco)', '22222222222'),
    (3, 'Carlos Pereira (Alto Risco por Score)', '33333333333'),
    (4, 'Ana Costa (Muitas Denúncias)', '44444444444'),
    (5, 'Beatriz Lima (Denúncias Confiáveis)', '55555555555'),
    (6, 'Lucas Mendes (Conta Bloqueada)', '66666666666'),
    (7, 'Roberta Farias (Teste de Bloqueio)', '77777777777');


--CONTAS:
-- Conta antiga, transações regulares - BAIXO RISCO.
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (1, 'CORRENTE', 'Banco Alfa', '0001', '11111-1', '2021-01-15 10:00:00', 1, 20, 'ALTA_REGULAR', NULL);

-- Conta recente, poucas transações, padrão inconsistente - MÉDIO RISCO.
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (2, 'POUPANCA', 'Banco Beta', '0002', '22222-2', '2025-01-10 14:00:00', 2, 3, 'BAIXA_INCONSISTENTE', NULL);

-- Conta muito recente, pouquíssimas transações, padrão de movimentação suspeito - ALTO RISCO (SCORE)
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimomes, bloqueadaate) VALUES
    (3, 'PAGAMENTO', 'Fintech Gama', '0003', '33333-3', '2025-05-20 09:00:00', 3, 1, 'ALTA_IRREGULAR', NULL);

-- A conta em si é normal, porém com alta qtd de den´uncias - ALTO RISCO (DENÚNCIAS)
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (4, 'SALARIO', 'Banco Delta', '0004', '44444-4', '2022-06-01 10:00:00', 4, 15, 'MEDIO_RAZOAVEL', NULL);

-- Conta 5: CONTA COM CHAVE COM DENÚNCIAS CONFIÁVEIS
-- A conta em si é normal, mas a chave associada tem 2 denúncias com alta confiança - ALTO RISCO (DENÚNCIAS)
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (5, 'CORRENTE', 'Banco Epsilon', '0005', '55555-5', '2023-01-01 10:00:00', 5, 10, 'MEDIO_RAZOAVEL', NULL);

-- Conta que já possui uma data de bloqueio futura - ALTO RISCO (já bloqueada)
INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (6, 'POUPANCA', 'Banco Zeta', '0006', '66666-6', '2023-02-01 10:00:00', 6, 5, 'BAIXA_INCONSISTENTE', '2099-12-31 23:59:59');

INSERT INTO Conta (ID, Tipo, Instituicao, Agencia, NumeroConta, DataCriacao, UsuarioID, MediaTransacoesSemanais, PadraoMovimentacoesUltimoMes, bloqueadaate) VALUES
    (7, 'PAGAMENTO', 'Banco de Teste G', '0007', '77777-7', '2025-06-10 09:00:00', 7, 0, 'ALTA_IRREGULAR', NULL);

-- CHAVES PIX:
INSERT INTO Chave (ID, ChavePIX, Tipo, DataCriacao, ContaID) VALUES
    (1, '111.111.111-11', 'CPF', '2021-02-01 11:00:00', 1),
    (2, 'maria.oliveira@email.com', 'EMAIL', '2025-05-15 15:00:00', 2),
    (3, 'a1b2c3d4-e5f6-7777-8888-9999abcdeff0', 'ALEATORIA', '2025-06-10 12:00:00', 3),
    (4, '+5521988887777', 'TELEFONE', '2023-03-01 10:00:00', 4),
    (5, 'beatriz.lima@email.com', 'EMAIL', '2023-04-01 10:00:00', 5),
    (6, '+5521977778888', 'TELEFONE', '2023-05-01 10:00:00', 6),
    (7, 'joao.nogueira@email.com', 'EMAIL', '2025-06-11 12:00:00', 7);

-- DENÚNCIAS:
-- 3 Denúncias para a Chave ID 4 (Alto Risco por quantidade)
INSERT INTO Denuncia (ID, ChaveID, DataHora, ConfiaB) VALUES
    (1, 4, '2025-01-01 10:00:00', 0.8),
    (2, 4, '2025-02-15 11:00:00', 0.6),
    (3, 4, '2025-03-20 12:00:00', 0.9);

-- 2 Denúncias para a Chave ID 5 (Alto Risco por confiança > 50%)
INSERT INTO Denuncia (ID, ChaveID, DataHora, ConfiaB) VALUES
    (4, 5, '2025-04-01 14:00:00',0.75),
    (5, 5, '2025-04-05 15:00:00', 0.85); -- Média de confiança: 80%
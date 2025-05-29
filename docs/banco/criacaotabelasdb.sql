CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS conta (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(50),
    instituicao VARCHAR(255) NOT NULL,
    agencia VARCHAR(20),
    numeroconta VARCHAR(30),
    datacriacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuarioid INTEGER NOT NULL,
    mediatransacoessemanais INTEGER,
    padraomovimentacoesultimomes VARCHAR(100),
    bloqueadaate TIMESTAMP,
    CONSTRAINT fk_usuario
        FOREIGN KEY(usuarioid)
        REFERENCES usuario(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS chave (
    id SERIAL PRIMARY KEY,
    chavepix VARCHAR(255) NOT NULL UNIQUE,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('EMAIL', 'CPF', 'TELEFONE', 'ALEATORIA')),
    datacriacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    contaid INTEGER NOT NULL,
    CONSTRAINT fk_conta
        FOREIGN KEY(contaid)
        REFERENCES conta(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS denuncia (
    id SERIAL PRIMARY KEY,
    chaveid INTEGER NOT NULL,
    datahora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confiab DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_chave
        FOREIGN KEY(chaveid)
        REFERENCES chave(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_conta_usuarioid ON conta(usuarioid);
CREATE INDEX IF NOT EXISTS idx_chave_contaid ON chave(contaid);
CREATE INDEX IF NOT EXISTS idx_chave_chavepix ON chave(chavepix);
CREATE INDEX IF NOT EXISTS idx_denuncia_chaveid ON denuncia(chaveid);




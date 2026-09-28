CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    ad_object_guid VARCHAR(100) UNIQUE NOT NULL,
    login VARCHAR(100) NOT NULL,
    nome VARCHAR(200),
    email VARCHAR(200),
    perfil VARCHAR(30) DEFAULT 'ALUNO',
    ativo BOOLEAN DEFAULT TRUE,
    ultimo_login TIMESTAMP
);

-- Keep the default consistent for databases created by previous versions.
ALTER TABLE usuarios ALTER COLUMN perfil SET DEFAULT 'ALUNO';
UPDATE usuarios SET perfil = 'ALUNO' WHERE perfil = 'USUARIO';

CREATE TABLE IF NOT EXISTS tutoriais (
    id BIGSERIAL PRIMARY KEY,
    titulo TEXT NOT NULL,
    descricao TEXT,
    conteudo TEXT NOT NULL,
    categoria TEXT,
    nivel TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS trilhas (
    id BIGSERIAL PRIMARY KEY,
    titulo TEXT NOT NULL,
    descricao TEXT,
    nivel TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS trilha_tutoriais (
    trilha_id BIGINT NOT NULL REFERENCES trilhas(id),
    tutorial_id BIGINT NOT NULL REFERENCES tutoriais(id),
    ordem INTEGER NOT NULL CHECK (ordem > 0),
    PRIMARY KEY (trilha_id, tutorial_id),
    UNIQUE (trilha_id, ordem)
);
CREATE TABLE IF NOT EXISTS dicas (
    id BIGSERIAL PRIMARY KEY,
    titulo TEXT NOT NULL,
    conteudo TEXT NOT NULL,
    categoria TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS glossario (
    id BIGSERIAL PRIMARY KEY,
    termo TEXT NOT NULL,
    definicao TEXT NOT NULL,
    categoria TEXT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS exercicios (
    id BIGSERIAL PRIMARY KEY,
    tutorial_id BIGINT NOT NULL REFERENCES tutoriais(id),
    pergunta TEXT NOT NULL,
    alternativa_a TEXT NOT NULL,
    alternativa_b TEXT NOT NULL,
    alternativa_c TEXT NOT NULL,
    alternativa_d TEXT NOT NULL,
    resposta_correta VARCHAR(1) NOT NULL CHECK (resposta_correta IN ('A', 'B', 'C', 'D')),
    explicacao TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS progresso_tutoriais (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    tutorial_id BIGINT NOT NULL REFERENCES tutoriais(id),
    status VARCHAR(20) NOT NULL CHECK (status IN ('EM_ANDAMENTO', 'CONCLUIDO')),
    iniciado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    concluido_em TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, tutorial_id)
);
CREATE TABLE IF NOT EXISTS respostas_exercicios (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    exercicio_id BIGINT NOT NULL REFERENCES exercicios(id),
    resposta VARCHAR(1) NOT NULL CHECK (resposta IN ('A', 'B', 'C', 'D')),
    correto BOOLEAN NOT NULL,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, exercicio_id)
);
CREATE INDEX IF NOT EXISTS idx_exercicios_tutorial ON exercicios(tutorial_id);
CREATE INDEX IF NOT EXISTS idx_trilha_tutoriais_tutorial ON trilha_tutoriais(tutorial_id);

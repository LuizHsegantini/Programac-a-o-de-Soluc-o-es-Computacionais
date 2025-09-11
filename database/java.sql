-- Script SQL para Sistema de Gestão de Projetos e Equipes
-- Execute este script no MySQL para criar o banco de dados

CREATE DATABASE IF NOT EXISTS gestao_projetos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestao_projetos;

-- Tabela de Usuários
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    perfil ENUM('ADMINISTRADOR', 'GERENTE', 'COLABORADOR') NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabela de Projetos
CREATE TABLE projetos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    data_inicio DATE NOT NULL,
    data_prevista_termino DATE NOT NULL,
    data_termino_real DATE NULL,
    status ENUM('PLANEJADO', 'EM_ANDAMENTO', 'CONCLUIDO', 'CANCELADO') DEFAULT 'PLANEJADO',
    gerente_id INT NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (gerente_id) REFERENCES usuarios(id)
);

-- Tabela de Tarefas
CREATE TABLE tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    projeto_id INT NOT NULL,
    responsavel_id INT NOT NULL,
    status ENUM('PENDENTE', 'EM_EXECUCAO', 'CONCLUIDA', 'CANCELADA') DEFAULT 'PENDENTE',
    prioridade ENUM('BAIXA', 'MEDIA', 'ALTA') DEFAULT 'MEDIA',
    data_prevista_conclusao DATE,
    data_conclusao_real DATE NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    FOREIGN KEY (responsavel_id) REFERENCES usuarios(id)
);

-- Tabela de Relacionamento Usuário-Projeto (para usuários que participam de projetos)
CREATE TABLE usuario_projeto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    projeto_id INT NOT NULL,
    data_vinculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (projeto_id) REFERENCES projetos(id),
    UNIQUE KEY uk_usuario_projeto (usuario_id, projeto_id)
);

-- Inserção de dados iniciais
-- Senha padrão: "123456" (você deve implementar hash no código Java)
INSERT INTO usuarios (nome, email, login, senha, perfil) VALUES
('Administrador Sistema', 'admin@empresa.com', 'admin', '123456', 'ADMINISTRADOR'),
('João Silva', 'joao.silva@empresa.com', 'joao', '123456', 'GERENTE'),
('Maria Santos', 'maria.santos@empresa.com', 'maria', '123456', 'GERENTE'),
('Pedro Oliveira', 'pedro.oliveira@empresa.com', 'pedro', '123456', 'COLABORADOR'),
('Ana Costa', 'ana.costa@empresa.com', 'ana', '123456', 'COLABORADOR');

-- Inserção de projetos de exemplo
INSERT INTO projetos (nome, descricao, data_inicio, data_prevista_termino, status, gerente_id) VALUES
('Sistema Web Vendas', 'Desenvolvimento de sistema web para controle de vendas', '2025-01-15', '2025-06-30', 'EM_ANDAMENTO', 2),
('App Mobile Cliente', 'Aplicativo móvel para atendimento ao cliente', '2025-02-01', '2025-08-15', 'PLANEJADO', 3),
('Migração Banco Dados', 'Migração do banco de dados legado para nova arquitetura', '2025-01-10', '2025-04-30', 'EM_ANDAMENTO', 2);

-- Inserção de tarefas de exemplo
INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id, status, prioridade, data_prevista_conclusao) VALUES
('Análise de Requisitos', 'Levantamento e documentação dos requisitos do sistema', 1, 4, 'CONCLUIDA', 'ALTA', '2025-02-01'),
('Design da Interface', 'Criação do design das telas principais', 1, 5, 'EM_EXECUCAO', 'ALTA', '2025-02-15'),
('Desenvolvimento Backend', 'Implementação da API REST', 1, 4, 'PENDENTE', 'ALTA', '2025-04-01'),
('Testes Unitários', 'Criação e execução de testes unitários', 1, 5, 'PENDENTE', 'MEDIA', '2025-05-15'),
('Prototipação App', 'Criação de protótipos navegáveis', 2, 4, 'PENDENTE', 'ALTA', '2025-03-01'),
('Backup Dados Atuais', 'Backup completo dos dados antes da migração', 3, 5, 'CONCLUIDA', 'ALTA', '2025-01-20');

-- Vinculação de usuários aos projetos
INSERT INTO usuario_projeto (usuario_id, projeto_id) VALUES
(2, 1), -- João no projeto Sistema Web Vendas
(4, 1), -- Pedro no projeto Sistema Web Vendas  
(5, 1), -- Ana no projeto Sistema Web Vendas
(3, 2), -- Maria no projeto App Mobile Cliente
(4, 2), -- Pedro no projeto App Mobile Cliente
(2, 3), -- João no projeto Migração Banco Dados
(5, 3); -- Ana no projeto Migração Banco Dados

-- Índices para melhor performance
CREATE INDEX idx_usuarios_login ON usuarios(login);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_projetos_gerente ON projetos(gerente_id);
CREATE INDEX idx_projetos_status ON projetos(status);
CREATE INDEX idx_tarefas_projeto ON tarefas(projeto_id);
CREATE INDEX idx_tarefas_responsavel ON tarefas(responsavel_id);
CREATE INDEX idx_tarefas_status ON tarefas(status);

-- Views úteis para relatórios
CREATE VIEW vw_projetos_resumo AS
SELECT 
    p.id,
    p.nome,
    p.status,
    u.nome as gerente_nome,
    COUNT(t.id) as total_tarefas,
    SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas,
    ROUND(
        (SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) * 100.0) / 
        NULLIF(COUNT(t.id), 0), 2
    ) as percentual_conclusao
FROM projetos p
LEFT JOIN usuarios u ON p.gerente_id = u.id
LEFT JOIN tarefas t ON p.id = t.projeto_id AND t.ativo = TRUE
WHERE p.ativo = TRUE
GROUP BY p.id, p.nome, p.status, u.nome;

CREATE VIEW vw_tarefas_por_usuario AS
SELECT 
    u.id as usuario_id,
    u.nome as usuario_nome,
    u.perfil,
    COUNT(t.id) as total_tarefas,
    SUM(CASE WHEN t.status = 'PENDENTE' THEN 1 ELSE 0 END) as tarefas_pendentes,
    SUM(CASE WHEN t.status = 'EM_EXECUCAO' THEN 1 ELSE 0 END) as tarefas_em_execucao,
    SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas
FROM usuarios u
LEFT JOIN tarefas t ON u.id = t.responsavel_id AND t.ativo = TRUE
WHERE u.ativo = TRUE
GROUP BY u.id, u.nome, u.perfil;
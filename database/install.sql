-- Script de Instalação - Sistema de Gestão de Projetos
-- Execute este script como administrador do MySQL

-- 1. Criar banco de dados
DROP DATABASE IF EXISTS gestao_projetos;
CREATE DATABASE gestao_projetos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestao_projetos;

-- 2. Criar usuário específico (opcional - para produção)
-- CREATE USER 'gestao_user'@'localhost' IDENTIFIED BY 'senha_segura_aqui';
-- GRANT ALL PRIVILEGES ON gestao_projetos.* TO 'gestao_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 3. Executar o script principal de criação das tabelas
SOURCE database_script.sql;

-- 4. Verificar instalação
SELECT 'Instalação concluída com sucesso!' as status;
SELECT COUNT(*) as total_usuarios FROM usuarios;
SELECT COUNT(*) as total_projetos FROM projetos;
SELECT COUNT(*) as total_tarefas FROM tarefas;

SHOW TABLES;
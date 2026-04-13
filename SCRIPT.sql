-- 1. Criação do Banco de Dados
-- (Execute este comando no pgAdmin antes de rodar a aplicação Spring Boot)
CREATE DATABASE comercio_sa;

-- =========================================================================
-- ATENÇÃO AVALIADOR:
-- As tabelas (cliente e contato) serão criadas automaticamente pelo
-- Hibernate (ddl-auto=update) ao rodar a aplicação Spring Boot pela primeira vez.
-- Após a aplicação subir e criar as tabelas, execute os INSERTS abaixo
-- para popular o banco com dados de teste.
-- =========================================================================

-- 2. População de Dados (Carga Inicial)

-- Inserindo Clientes
INSERT INTO cliente (id, nome, cpf, data_nascimento)
VALUES
(nextval('cliente_sequence'), 'Carlos Silva', '59126654024', '1985-04-15'),
(nextval('cliente_sequence'), 'Maria Oliveira', '36347562090', '1992-08-20'),
(nextval('cliente_sequence'), 'João Souza', '92212247087', '1978-11-05');

-- Inserindo Contatos (Certifique-se de que os IDs dos clientes correspondam)
INSERT INTO contato (id, tipo, valor, observacao, cliente_id)
VALUES
(nextval('contato_sequence'), 'Telefone', '(11) 98765-4321', 'Celular principal', 1),
(nextval('contato_sequence'), 'E-mail', 'carlos.silva@email.com', 'E-mail pessoal', 1),
(nextval('contato_sequence'), 'Telefone', '(21) 99999-8888', 'WhatsApp', 2),
(nextval('contato_sequence'), 'E-mail', 'joao.souza@empresa.com', 'E-mail corporativo', 3);

CREATE TABLE IF NOT EXISTS users(
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100),
    role VARCHAR(15) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS boleto_data(
    id VARCHAR(36) PRIMARY KEY,
    nome_empresa VARCHAR(100),
    cpf_cnpj VARCHAR(18),
    endereco VARCHAR(255),
    descricao_referencia VARCHAR(255),
    valor NUMERIC(10,2),
    vencimento DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS faturamento(
    id VARCHAR(36) PRIMARY KEY,
    equipamento VARCHAR(255) NOT NULL,
    total_equipamento NUMERIC(10,2) NOT NULL,
    media_alugados NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS precos(
    id VARCHAR(36) PRIMARY KEY,
    equipamento VARCHAR(255) NOT NULL,
    investimento NUMERIC(10,2) NOT NULL,
    residual NUMERIC(10,2) NOT NULL,
    depreciacao_meses INT NOT NULL,
    preco_atual_mensal NUMERIC(10,2) NOT NULL,
    margem NUMERIC(10,2) NOT NULL,
    manutencao_atual NUMERIC(10,2) NOT NULL,
    faturamento_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (faturamento_id) REFERENCES faturamento(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS custos(
    id VARCHAR(36) PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    tipo_custo VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    user_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO users (id, name, email, password, role)
VALUES
('92056f79-327d-4792-9543-ccf68f8597a8','User Admin','user.admin@gmail.com','$2a$14$jVZnYnz2GY9xdJ7ZLmi/Qum.EMA.C/LumRdiwWxlJWAUmCF.P7WZy','admin'), -- senha: Senha@123
('92056f79-327d-4792-9543-ccf68f8597a9','User Test','user.test@gmail.com','$2a$14$jVZnYnz2GY9xdJ7ZLmi/Qum.EMA.C/LumRdiwWxlJWAUmCF.P7WZy','user'), -- senha: Senha@123
('92056f79-327d-4792-9543-ccf68f8597aa','User Test 2','user.test2@gmail.com','$2a$14$jVZnYnz2GY9xdJ7ZLmi/Qum.EMA.C/LumRdiwWxlJWAUmCF.P7WZy','user'); -- senha: Senha@123

INSERT INTO boleto_data (id, nome_empresa, cpf_cnpj, endereco, descricao_referencia, valor, vencimento, user_id)
VALUES
('43cbe748-32f5-48fe-86d1-a8a591f28b35','Empresa Teste','12345678901234','Rua Teste, 123','Referência Teste',100.00,'2024-05-31','92056f79-327d-4792-9543-ccf68f8597a9'),
('43cbe748-32f5-48fe-86d1-a8a591f28b36','Empresa Teste 2','12345678901235','Rua Teste 2, 123','Referência Teste 2',200.00,'2024-05-31','92056f79-327d-4792-9543-ccf68f8597aa');

INSERT INTO faturamento (id, equipamento, total_equipamento, media_alugados, user_id)
VALUES
('ft001','Computador i5',1500.00,250.00,'92056f79-327d-4792-9543-ccf68f8597a9'),
('ft002','Notebook Dell',2000.00,400.00,'92056f79-327d-4792-9543-ccf68f8597a9'),
('ft003','Monitor 24"',800.00,150.00,'92056f79-327d-4792-9543-ccf68f8597a9');

INSERT INTO precos (id, equipamento, investimento, residual, depreciacao_meses, preco_atual_mensal, margem, manutencao_atual, faturamento_id, user_id)
VALUES
('pr001','Computador i5',5000.00,500.00,24,180.50,15.50,50.00,'ft001','92056f79-327d-4792-9543-ccf68f8597a9'),
('pr002','Notebook Dell',3500.00,350.00,36,85.50,10.50,75.00,'ft002','92056f79-327d-4792-9543-ccf68f8597a9'),
('pr003','Monitor 24"',1200.00,120.00,48,22.50,8.50,25.00,'ft003','92056f79-327d-4792-9543-ccf68f8597a9');

INSERT INTO custos (id, descricao, valor, tipo_custo, user_id)
VALUES
('ct001','Aluguel de escritório',1500.00,'Aluguel','92056f79-327d-4792-9543-ccf68f8597a9'),
('ct002','Material de escritório',350.75,'Material','92056f79-327d-4792-9543-ccf68f8597a9'),
('ct003','Serviços de limpeza',200.00,'Serviço','92056f79-327d-4792-9543-ccf68f8597a9'),
('ct004','Internet e telefonia',180.50,'Serviço','92056f79-327d-4792-9543-ccf68f8597a9'),
('ct005','Equipamentos de TI',800.00,'Equipamento','92056f79-327d-4792-9543-ccf68f8597a9');
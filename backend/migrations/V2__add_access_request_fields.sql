-- Migration V2: Adicionar campos para controle de acesso com motivos pré-definidos

-- Remover coluna antiga se existir (para evitar conflitos)
DROP TABLE IF EXISTS access_requests_temp;

-- Adicionar coluna last_access_request se não existir
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_access_request TIMESTAMP;

-- Atualizar usuários existentes para first_login = FALSE (já têm senha definida)
UPDATE users SET first_login = FALSE WHERE first_login IS NULL OR first_login = TRUE;

-- Adicionar índice para performance na verificação de solicitações diárias
CREATE INDEX IF NOT EXISTS idx_users_last_access_request ON users(last_access_request);

-- Adicionar comentários para documentação
COMMENT ON COLUMN users.first_login IS 'Indica se usuário precisa definir senha no primeiro acesso (TRUE=precisa definir, FALSE=senha já definida)';
COMMENT ON COLUMN users.last_access_request IS 'Data/hora da última solicitação de acesso (usado para controle de limite diário)';

-- Inserir dados de teste para verificação
INSERT INTO users (id, name, email, password, role, first_login, last_access_request, created_at, deleted)
VALUES (
    'test-access-request-001',
    'Teste Solicitação',
    'test@example.com',
    '$2a$14$jVZnYnz2GY9xdJ7ZLmi/Qum.EMA.C/LumRdiwWxlJWAUmCF.P7WZy',
    'USER',
    TRUE,
    NOW(),
    NOW(),
    FALSE
) ON CONFLICT (id) DO NOTHING;

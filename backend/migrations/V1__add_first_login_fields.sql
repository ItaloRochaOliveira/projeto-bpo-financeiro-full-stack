-- Migration V1: Adicionar campos para controle de primeiro acesso e limite diário de solicitações

-- Adicionar coluna first_login para controlar primeiro acesso do usuário
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_login BOOLEAN DEFAULT TRUE;

-- Adicionar coluna last_access_request para controlar limite diário de solicitações
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_access_request TIMESTAMP;

-- Atualizar usuários existentes para first_login = FALSE (já têm senha definida)
-- Isso evita que usuários existentes precisem redefinir senha
UPDATE users SET first_login = FALSE WHERE first_login IS NULL OR first_login = TRUE;

-- Adicionar índice para performance na verificação de solicitações diárias
CREATE INDEX IF NOT EXISTS idx_users_last_access_request ON users(last_access_request);

-- Adicionar comentários para documentação
COMMENT ON COLUMN users.first_login IS 'Indica se usuário precisa definir senha no primeiro acesso (TRUE=precisa definir, FALSE=senha já definida)';
COMMENT ON COLUMN users.last_access_request IS 'Data/hora da última solicitação de acesso (usado para controle de limite diário)';

# Migrations do Banco de Dados

Este diretório contém as migrations SQL para o PostgreSQL.

## 📁 Estrutura

- `V1__add_first_login_fields.sql` - Adiciona colunas `first_login` e `last_access_request`
- `V2__add_access_request_fields.sql` - Atualiza estrutura para suportar motivos pré-definidos

## 🚀 Como Executar

### Opção 1: Via Docker Compose (Recomendado)

```bash
# Executar migrations
make migrate

# Ou diretamente
docker-compose run --rm migration
```

### Opção 2: Localmente (Windows)

```bash
# No diretório backend
cd backend
scripts\run-migrations.bat
```

### Opção 3: Localmente (Linux/Mac)

```bash
# No diretório backend
cd backend
chmod +x scripts/run-migrations.sh
./scripts/run-migrations.sh
```

### Opção 4: Manualmente

```bash
# Conectar ao banco
psql -h localhost -p 5432 -U italo -d consulta_sql

# Executar migration manualmente
\i migrations/V1__add_first_login_fields.sql
\i migrations/V2__add_access_request_fields.sql
```

## 📋 Comandos Úteis

```bash
# Verificar se as colunas existem
\c consulta_sql
\d users

# Verificar dados
SELECT id, name, email, first_login, last_access_request FROM users;

# Verificar índices
\di idx_users_last_access_request
```

## 🔧 Setup Completo

```bash
# 1. Construir e iniciar com migrations
make setup

# 2. Ou passo a passo:
make build
make migrate
make up
```

## ⚠️ Importante

- As migrations são idempotentes (usam `IF NOT EXISTS`)
- Execute sempre em ordem (V1, V2, etc.)
- O serviço de migration no Docker Compose espera o banco estar saudável antes de executar
- Em caso de erro, verifique os logs: `docker-compose logs migration`

## 🐛 Troubleshooting

### Erro: "column does not exist"
- Execute as migrations: `make migrate`
- Verifique se o banco está atualizado

### Erro: "Connection refused"
- Verifique se o PostgreSQL está rodando: `docker-compose ps db`
- Aguarde o healthcheck: `docker-compose logs db`

### Erro de permissão (Linux/Mac)
```bash
chmod +x scripts/run-migrations.sh
```

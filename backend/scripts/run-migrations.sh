#!/bin/bash

# Script para executar migrations no PostgreSQL
# Uso: ./run-migrations.sh

echo "🔄 Executando migrations do banco de dados..."

# Configurações do banco
DB_HOST="localhost"
DB_PORT="5432"
DB_USER="italo"
DB_PASSWORD="Senha@123"
DB_NAME="consulta_sql"

# Diretório das migrations
MIGRATIONS_DIR="./migrations"

# Verificar se o diretório de migrations existe
if [ ! -d "$MIGRATIONS_DIR" ]; then
    echo "❌ Diretório de migrations não encontrado: $MIGRATIONS_DIR"
    exit 1
fi

# Executar cada arquivo de migration em ordem
for migration_file in $MIGRATIONS_DIR/*.sql; do
    if [ -f "$migration_file" ]; then
        echo "📄 Executando migration: $(basename "$migration_file")"
        
        # Executar a migration
        PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$migration_file"
        
        if [ $? -eq 0 ]; then
            echo "✅ Migration $(basename "$migration_file") executada com sucesso"
        else
            echo "❌ Erro ao executar migration $(basename "$migration_file")"
            exit 1
        fi
    fi
done

echo "🎉 Todas as migrations foram executadas com sucesso!"

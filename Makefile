# Makefile para gerenciar o projeto BPO Financeiro

.PHONY: help build up down logs migrate migrate-local clean test

# Comandos de ajuda
help:
	@echo "Comandos disponíveis:"
	@echo "  make build       - Constrói todas as imagens Docker"
	@echo "  make up          - Inicia todos os serviços"
	@echo "  make down        - Para todos os serviços"
	@echo "  make logs        - Mostra logs dos serviços"
	@echo "  make migrate     - Executa migrations via Docker"
	@echo "  make migrate-local - Executa migrations localmente"
	@echo "  make clean       - Limpa containers e imagens"
	@echo "  make test        - Executa testes"

# Construir imagens
build:
	@echo "🔨 Construindo imagens Docker..."
	docker-compose build

# Iniciar serviços
up:
	@echo "🚀 Iniciando serviços..."
	docker-compose up -d

# Parar serviços
down:
	@echo "🛑 Parando serviços..."
	docker-compose down

# Mostrar logs
logs:
	@echo "📋 Mostrando logs..."
	docker-compose logs -f

# Executar migrations via Docker
migrate:
	@echo "🔄 Executando migrations via Docker..."
	docker-compose run --rm migration

# Executar migrations localmente (Windows)
migrate-local:
	@echo "🔄 Executando migrations localmente..."
	cd backend && scripts\run-migrations.bat

# Executar migrations localmente (Linux/Mac)
migrate-local-sh:
	@echo "🔄 Executando migrations localmente..."
	cd backend && chmod +x scripts/run-migrations.sh && ./scripts/run-migrations.sh

# Limpar
clean:
	@echo "🧹 Limpando containers e imagens..."
	docker-compose down -v --rmi all

# Executar testes
test:
	@echo "🧪 Executando testes..."
	cd backend && mvn test

# Reiniciar serviços
restart: down up

# Setup completo (build + migrate + up)
setup: build migrate up
	@echo "✅ Setup completo finalizado!"

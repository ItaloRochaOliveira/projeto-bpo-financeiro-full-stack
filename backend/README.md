# 🏦 Gerador de Boletos Bancários - Backend Java

Backend em Java Spring Boot para geração e gerenciamento de boletos bancários.

## 🛠️ Stack Tecnológica

- **Java 17** com **Spring Boot 3.2.0**
- **Spring Security** com autenticação JWT
- **Spring Data JPA** com PostgreSQL
- **iText 7** para geração de PDFs
- **Maven** para gerenciamento de dependências
- **Docker** para containerização

## 📁 Estrutura do Projeto

```
backend-java/
├── src/main/java/com/italo/geradorboleto/
│   ├── GeradorBoletoApplication.java    # Classe principal
│   ├── config/                          # Configurações
│   │   └── SecurityConfig.java          # Configuração de segurança
│   ├── controller/                      # Controllers REST
│   │   ├── AuthController.java          # Autenticação
│   │   ├── BoletoController.java       # Boletos
│   │   └── HealthController.java       # Health check
│   ├── dto/                            # Data Transfer Objects
│   │   ├── AuthResponse.java
│   │   ├── LoginRequest.java
│   │   ├── SignupRequest.java
│   │   ├── BoletoRequest.java
│   │   └── BoletoResponse.java
│   ├── entity/                         # Entidades JPA
│   │   ├── User.java
│   │   └── Boleto.java
│   ├── repository/                     # Repositórios JPA
│   │   ├── UserRepository.java
│   │   └── BoletoRepository.java
│   ├── security/                       # Segurança JWT
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   └── service/                        # Services de negócio
│       ├── AuthService.java
│       ├── BoletoService.java
│       └── PdfService.java
├── src/main/resources/
│   └── application.properties           # Configurações
├── Dockerfile                          # Configuração Docker
├── pom.xml                            # Dependências Maven
└── README.md                          # Este arquivo
```

## 🚀 Como Executar

### Pré-requisitos

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Docker** (opcional)

### Configuração do Banco de Dados

1. Criar banco de dados PostgreSQL:
```sql
CREATE DATABASE projeto_boletos;
```

2. Configurar variáveis de ambiente em `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/projeto_boletos
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
jwt.secret=seu_secret_super_secreto_aqui_pelo_menos_256_bits
```

### Execução Local

1. **Clonar e compilar**:
```bash
cd backend-java
mvn clean install
```

2. **Executar aplicação**:
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: http://localhost:3006

### Docker (Recomendado)

1. **Construir imagem**:
```bash
docker build -t gerador-boleto-backend:1.0.0 .
```

2. **Executar container**:
```bash
docker run -p 3006:3006 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/projeto_boletos \
  -e SPRING_DATASOURCE_USERNAME=seu_usuario \
  -e SPRING_DATASOURCE_PASSWORD=sua_senha \
  -e JWT_SECRET=seu_secret_super_secreto \
  gerador-boleto-backend:1.0.0
```

## 📊 Endpoints da API

### Autenticação
- `POST /api/auth/login` - Fazer login
- `POST /api/auth/signup` - Criar conta

### Boletos
- `GET /api/boleto` - Listar boletos do usuário
- `POST /api/boleto/create` - Criar novo boleto
- `GET /api/boleto/{id}` - Obter boleto específico
- `GET /api/boleto/{id}/pdf` - Gerar PDF do boleto

### Health Check
- `GET /api/health` - Verificar status da API

## 🔒 Segurança

- **Tokens JWT** com expiração de 24h
- **Password hashing** com BCrypt
- **CORS** configurado para frontend
- **Validação de entrada** com Bean Validation
- **Proteção contra ataques CSRF**

## 📝 Exemplos de Uso

### Login
```bash
curl -X POST http://localhost:3006/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@exemplo.com",
    "password": "senha123"
  }'
```

### Criar Boleto
```bash
curl -X POST http://localhost:3006/api/boleto/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_JWT" \
  -d '{
    "nomeEmpresa": "Empresa Exemplo LTDA",
    "cpfCnpj": "12.345.678/0001-23",
    "endereco": "Rua das Flores, 123 - São Paulo, SP",
    "descricaoReferencia": "Pagamento de serviços",
    "valor": "150.00",
    "vencimento": "2024-12-31"
  }'
```

## 🐛 Troubleshooting

### Problemas Comuns

#### Aplicação não inicia
```bash
# Verificar variáveis de ambiente
cat src/main/resources/application.properties

# Verificar logs
mvn spring-boot:run --debug
```

#### Erro de conexão com banco
```bash
# Testar conexão PostgreSQL
psql -h localhost -U seu_usuario -d projeto_boletos

# Verificar se o banco está rodando
docker ps | grep postgres
```

#### PDF não gera
```bash
# Verificar dependências iText
mvn dependency:tree | grep itext

# Verificar logs da aplicação
tail -f logs/application.log
```

## 🧪 Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com coverage
mvn jacoco:report

# Executar testes de integração
mvn test -P integration-test
```

## 📈 Monitoramento

- **Actuator endpoints** em `/actuator`
- **Health check** em `/api/health`
- **Métricas** disponíveis via Actuator
- **Logs** configurados para debug

## 🔄 CI/CD

O projeto está preparado para integração contínua com:
- **GitHub Actions** (ver `.github/workflows/`)
- **Docker Hub** para builds automatizados
- **Testes automatizados** em cada commit

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.

---

**Desenvolvido com as melhores práticas de engenharia de software Java!** 🚀

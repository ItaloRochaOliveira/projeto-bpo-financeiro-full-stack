# 🏦 Sistema Financeiro BPO Full Stack

Uma aplicação web completa para gestão financeira empresarial, desenvolvida com **Next.js 14+**, **TypeScript**, **Tailwind CSS** e **Spring Boot Java**.

## ✨ Funcionalidades

### 🔐 Autenticação
- ✅ Login e Signup com validação de formulários
- ✅ Tokens JWT com armazenamento seguro
- ✅ Middleware de proteção de rotas
- ✅ Logout com limpeza de localStorage
- ✅ Sistema de roles (admin/user)

### 📄 Gerenciamento de Boletos
- ✅ Criação de boletos com dados completos
- ✅ Listagem de boletos por usuário
- ✅ Geração de PDF profissional em nova aba
- ✅ Design padrão brasileiro com marca d'água
- ✅ Validação de dados com Zod

### 💰 Sistema Financeiro
- ✅ **Preços**: Gestão de preços de equipamentos com depreciação
- ✅ **Faturamento**: Controle de faturamento e médias de aluguel
- ✅ **Custos**: Registro de custos por tipo (Material, Serviço, etc.)
- ✅ **Soft Delete**: Proteção de dados com exclusão lógica
- ✅ **Hard Delete**: Exclusão permanente apenas para admins

### 🎨 Design
- ✅ Interface moderna e responsiva
- ✅ Paleta de cores institucional (azul médico)
- ✅ Componentes shadcn/ui
- ✅ Feedback visual de erros e sucessos
- ✅ Dashboard intuitivo

## 🛠️ Stack Tecnológica

### Frontend
- **Next.js 14+** com App Router
- **TypeScript** para tipagem segura
- **Tailwind CSS** para estilização
- **shadcn/ui** para componentes
- **React Hook Form** + **Zod** para formulários
- **Axios** para comunicação API
- **Sonner** para notificações toast
- **Lucide React** para ícones

### Backend
- **Spring Boot 3+** com Java 17+
- **TypeScript** para tipagem
- **PostgreSQL** com **JPA/Hibernate**
- **JWT** para autenticação
- **Lombok** para boilerplate reduction
- **Spring Security** para proteção
- **GlobalExceptionHandler** para tratamento centralizado de erros

### Infraestrutura
- **Docker** com multi-stage builds
- **Docker Compose** para orquestração
- **pnpm** para gerenciamento de pacotes

## Estrutura do Projeto

```
projeto-trabalho/
├── backend/                    # API Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/italo/geradorboleto/
│   │   │   │   ├── controller/         # Controladores REST
│   │   │   │   ├── service/            # Lógica de negócio
│   │   │   │   ├── repository/         # Camada de dados
│   │   │   │   ├── dto/               # Data Transfer Objects
│   │   │   │   ├── model/             # Entidades JPA
│   │   │   │   ├── exception/         # Tratamento de erros
│   │   │   │   └── config/            # Configurações
│   │   │   └── resources/
│   │   │       └── init.sql            # Schema inicial
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                   # Aplicação Next.js
│   ├── app/                    # App Router
│   │   ├── auth/              # Páginas de autenticação
│   │   ├── pdf/               # Gestão de boletos
│   │   ├── dashboard/          # Dashboard financeiro
│   │   └── api/               # API Routes
│   ├── components/              # Componentes React
│   │   ├── ui/                 # shadcn/ui
│   │   ├── forms/              # Formulários
│   │   └── dashboard/          # Dashboard
│   ├── hooks/                   # Hooks personalizados
│   ├── lib/                    # Utilitários
│   ├── utils/                  # Funções auxiliares
│   └── types/                  # Tipos TypeScript
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml            # Orquestração dos serviços
└── README.md                   # Este arquivo
```

## Como Executar

### Pré-requisitos
- **Java 17+**
- **Maven 3.6+**
- **Node.js 18+**
- **pnpm** (recomendado) ou npm
- **Docker** e **Docker Compose**

### Desenvolvimento Local

1. **Clonar o repositório**
```bash
git clone <repositório>
cd projeto-trabalho
```

2. **Instalar dependências**
```bash
# Backend
cd backend
mvn clean install

# Frontend
cd frontend
pnpm install
```

3. **Configurar ambiente**
```bash
# Backend - copiar .env.example para .env
cp backend/.env.example backend/.env

# Frontend - configurar variáveis
echo "NEXT_PUBLIC_API_URL=http://localhost:8080" > frontend/.env.local
```

4. **Executar aplicação**
```bash
# Backend (terminal 1)
cd backend
mvn spring-boot:run

# Frontend (terminal 2)
cd frontend
pnpm run dev
```

### Docker (Recomendado)

1. **Subir todos os serviços**
```bash
docker-compose up -d --build
```

2. **Acessar aplicações**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Database: localhost:5432

## Funcionalidades Detalhadas

### Fluxo de Autenticação
1. **Signup**: Usuário cria conta com email e senha
2. **Login**: Autenticação com token JWT
3. **Middleware**: Proteção automática de rotas
4. **Session**: Token armazenado em localStorage
5. **Logout**: Limpeza segura dos dados

### 📄 Gestão de Boletos
1. **Criação**: Formulário com validação em tempo real
2. **Listagem**: Visualização dos boletos do usuário
3. **PDF**: Geração profissional com marca d'água
4. **Download**: Abertura em nova aba para visualização

### 🎨 Design do PDF
- **Layout padrão FEBRABAN**
- **Cores institucionais**
- **Marca d'água de segurança**
- **Informações completas do beneficiário**
- **Código de barras simulado**
- **Linha digitável**

## 🔧 Configuração

### Variáveis de Ambiente

#### Backend (.env)
```env
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=seu_usuario
POSTGRES_PASSWORD=sua_senha
POSTGRES_DB=projeto_boletos

# JWT
JWT_SECRET=seu_secret_super_secreto

# Server
PORT=3006
ENV=dev
```

#### Frontend (.env.local)
```env
NEXT_PUBLIC_API_URL=http://localhost:3006
```

## 📱 Endpoints da API

### Autenticação
- `POST /api/auth/signup` - Criar usuário
- `POST /api/auth/login` - Fazer login

### Boletos
- `GET /api/boleto` - Listar boletos do usuário
- `POST /api/boleto/create` - Criar novo boleto
- `GET /api/boleto/:id` - Obter boleto específico
- `GET /api/boleto/:id/pdf` - Gerar PDF do boleto

### Preços
- `GET /api/precos` - Listar preços
- `POST /api/precos` - Criar preço
- `GET /api/precos/:id` - Obter preço específico
- `PUT /api/precos/:id` - Atualizar preço
- `DELETE /api/precos/:id` - Soft delete
- `DELETE /api/precos/:id/hard` - Hard delete (admin)
- `GET /api/precos/deleted` - Listar deletados

### Faturamento
- `GET /api/faturamento` - Listar faturamentos
- `POST /api/faturamento` - Criar faturamento
- `GET /api/faturamento/:id` - Obter faturamento específico
- `PUT /api/faturamento/:id` - Atualizar faturamento
- `DELETE /api/faturamento/:id` - Soft delete
- `DELETE /api/faturamento/:id/hard` - Hard delete (admin)
- `GET /api/faturamento/deleted` - Listar deletados

### Custos
- `GET /api/custos` - Listar custos
- `POST /api/custos` - Criar custo
- `GET /api/custos/:id` - Obter custo específico
- `GET /api/custos/tipo/:tipoCusto` - Listar por tipo
- `PUT /api/custos/:id` - Atualizar custo
- `DELETE /api/custos/:id` - Soft delete
- `DELETE /api/custos/:id/hard` - Hard delete (admin)
- `GET /api/custos/deleted` - Listar deletados

## 🎯 Exemplos de Uso

### Criar Preço
```json
{
  "equipamento": "Computador i5",
  "investimento": 5000.00,
  "residual": 500.00,
  "depreciacaoMeses": 24,
  "precoAtualMensal": 180.50,
  "margem": 15.50,
  "manutencaoAtual": 50.00,
  "faturamentoId": "ft001"
}
```

### Criar Faturamento
```json
{
  "equipamento": "Computador i5",
  "totalEquipamento": 1500.00,
  "mediaAlugados": 250.00
}
```

### Criar Custo
```json
{
  "descricao": "Aluguel de escritório",
  "valor": 1500.00,
  "tipoCusto": "Aluguel"
}
```

### Resposta da API - Preço
```json
{
  "id": "uuid-generated",
  "equipamento": "Computador i5",
  "investimento": 5000.00,
  "residual": 500.00,
  "depreciacaoMeses": 24,
  "precoAtualMensal": 180.50,
  "margem": 15.50,
  "manutencaoAtual": 50.00,
  "faturamentoId": "ft001",
  "createdAt": "2024-01-01T00:00:00.000Z",
  "updatedAt": "2024-01-01T00:00:00.000Z",
  "deleted": false,
  "deletedAt": null,
  "userId": "uuid-user"
}
```

## 🔒 Segurança

### Implementações
- ✅ **Tokens JWT** com expiração configurável
- ✅ **Middleware** de proteção de rotas
- ✅ **Validação de entrada** com Bean Validation
- ✅ **Hash de senhas** com bcrypt
- ✅ **CORS** configurado para frontend
- ✅ **Soft Delete** para proteção de dados
- ✅ **Role-based access** para operações críticas
- ✅ **GlobalExceptionHandler** para tratamento centralizado de erros
- ✅ **DTOs** para validação de entrada
- ✅ **Soft Delete** para preservação de dados

### Boas Práticas
- ✅ **Tipagem forte** com TypeScript
- ✅ **Separação de responsabilidades** (Clean Architecture)
- ✅ **Injeção de dependências**
- ✅ **Tratamento de erros** centralizado
- ✅ **Logs** para debugging e auditoria

## 🐛 Troubleshooting

### Problemas Comuns

#### Frontend não carrega
```bash
# Verificar se o backend está rodando
curl http://localhost:3006/health

# Verificar variáveis de ambiente
cat frontend/.env.local
```

#### Erro de conexão com API
```bash
# Verificar configuração CORS
# Verificar se NEXT_PUBLIC_API_URL está correta
```

#### PDF não gera
```bash
# Verificar logs do backend
docker-compose logs backend

# Verificar se jsPDF está instalado
cd backend && npm list jspdf
```

## 📈 Melhorias Futuras

- [ ] **Upload de logo** personalizado da empresa
- [ ] **Geração de lote** de boletos
- [ ] **Integração com APIs** bancárias reais
- [ ] **Templates diferentes** de boletos
- [ ] **Dashboard analytics** com gráficos
- [ ] **Notificações por email** de vencimento
- [ ] **Exportação** em Excel/CSV
- [ ] **Histórico** de pagamentos
- [ ] **Webhooks** para notificações externas

## 🤝 Como Contribuir

1. **Fork** o repositório
2. **Criar branch** de feature (`git checkout -b feature/nova-funcionalidade`)
3. **Commit** das mudanças (`git commit -m "Adiciona nova funcionalidade"`)
4. **Push** para o branch (`git push origin feature/nova-funcionalidade`)
5. **Abrir Pull Request** descrevendo as mudanças

## 📄 Licença

Este projeto está licenciado sob a **MIT License**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

### 📞 Contato

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:3006](http://localhost:3006)
- **Documentação**: Este README

### Informações Pessoais

- Italo Rocha Oliveira
- [Linkedin](https://www.linkedin.com/in/italorochaoliveira/).
- [Github](https://github.com/ItaloRochaOliveira).
- Email: italo.rocha.de.oliveira@gmail.com

**Desenvolvido com as melhores práticas de engenharia de software!** 🚀
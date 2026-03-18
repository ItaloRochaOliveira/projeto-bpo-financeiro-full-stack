

# 🏦 Gerador de Boletos Bancários

Uma aplicação web completa para geração e gerenciamento de boletos bancários, desenvolvida com **Next.js 14+**, **TypeScript**, **Tailwind CSS** e **Node.js/Express**.

## ✨ Funcionalidades

### 🔐 Autenticação
- ✅ Login e Signup com validação de formulários
- ✅ Tokens JWT com armazenamento seguro
- ✅ Middleware de proteção de rotas
- ✅ Logout com limpeza de localStorage

### 📄 Gerenciamento de Boletos
- ✅ Criação de boletos com dados completos
- ✅ Listagem de boletos por usuário
- ✅ Geração de PDF profissional em nova aba
- ✅ Design padrão brasileiro com marca d'água
- ✅ Validação de dados com Zod

### 🎨 Design
- ✅ Interface moderna e responsiva
- ✅ Paleta de cores institucional (azul médico)
- ✅ Componentes shadcn/ui
- ✅ Feedback visual de erros e sucessos

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
- **Node.js** com **Express**
- **TypeScript** para tipagem
- **PostgreSQL** com **TypeORM**
- **JWT** para autenticação
- **jsPDF** para geração de PDFs
- **Zod** para validação

### Infraestrutura
- **Docker** com multi-stage builds
- **Docker Compose** para orquestração
- **pnpm** para gerenciamento de pacotes

## 📁 Estrutura do Projeto

```
projeto-trabalho/
├── backend/                    # API Node.js/Express
│   ├── src/
│   │   ├── controller/         # Controladores de API
│   │   ├── service/            # Lógica de negócio
│   │   ├── repository/         # Camada de dados
│   │   ├── middleware/         # Middlewares
│   │   ├── utils/              # Utilitários (PDF, etc)
│   │   └── db/                # Configuração DB
│   ├── Dockerfile
│   └── package.json
├── frontend/                   # Aplicação Next.js
│   ├── app/                    # App Router
│   │   ├── auth/              # Páginas de autenticação
│   │   ├── pdf/               # Gestão de boletos
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

## 🚀 Como Executar

### Pré-requisitos
- **Node.js 18+**
- **pnpm** (recomendado) ou npm
- **Docker** e **Docker Compose**

### Desenvolvimento Local

1. **Clonar o repositório**
```bash
git clone <repositorio>
cd projeto-trabalho
```

2. **Instalar dependências**
```bash
# Backend
cd backend
pnpm install

# Frontend
cd frontend
pnpm install
```

3. **Configurar ambiente**
```bash
# Backend - copiar .env.example para .env
cp backend/.env.example backend/.env

# Frontend - configurar variáveis
echo "NEXT_PUBLIC_API_URL=http://localhost:3006" > frontend/.env.local
```

4. **Executar aplicação**
```bash
# Backend (terminal 1)
cd backend
pnpm run dev

# Frontend (terminal 2)
cd frontend
pnpm run dev
```

### Docker (Recomendado)

1. **Subir todos os serviços**
```bash
docker build -t back_end_gerador_pdf:1.0.0 ./ && docker compose up -d && docker compose up --build frontend
```

2. **Acessar aplicações**
- Frontend: http://localhost:3000
- Backend: http://localhost:3006
- Database: localhost:5432

## 📊 Funcionalidades Detalhadas

### 🔐 Fluxo de Autenticação
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
- `POST /auth/signup` - Criar usuário
- `POST /auth/login` - Fazer login

### Boletos
- `GET /boleto` - Listar boletos do usuário
- `POST /boleto/create` - Criar novo boleto
- `GET /boleto/:id` - Obter boleto específico
- `GET /boleto/:id/pdf` - Gerar PDF do boleto

## 🎯 Exemplos de Uso

### Criar Boleto
```typescript
const boletoData = {
  nomeEmpresa: "Empresa Exemplo LTDA",
  cpfCnpj: "12.345.678/0001-23",
  endereco: "Rua das Flores, 123 - São Paulo, SP",
  descricaoReferencia: "Pagamento de serviços",
  valor: 150.00,
  vencimento: "2024-12-31"
};
```

### Resposta da API
```json
{
  "id": "uuid-generated",
  "nomeEmpresa": "Empresa Exemplo LTDA",
  "cpfCnpj": "12.345.678/0001-23",
  "endereco": "Rua das Flores, 123 - São Paulo, SP",
  "descricaoReferencia": "Pagamento de serviços",
  "valor": 150.00,
  "vencimento": "2024-12-31",
  "createdAt": "2024-01-01T00:00:00.000Z",
  "updatedAt": "2024-01-01T00:00:00.000Z"
}
```

## 🔒 Segurança

### Implementações
- ✅ **Tokens JWT** com expiração configurável
- ✅ **Middleware** de proteção de rotas
- ✅ **Validação de entrada** com Zod schemas
- ✅ **Hash de senhas** com bcrypt
- ✅ **CORS** configurado para frontend
- ✅ **Marca d'água** nos PDFs gerados

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
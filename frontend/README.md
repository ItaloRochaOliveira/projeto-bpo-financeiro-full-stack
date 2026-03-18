# PDF Generator Medical Frontend

Frontend em Next.js 14+ para o sistema de geração de PDFs médicos, desenvolvido seguindo as melhores práticas do teste técnico RNF02.

## Tecnologias

- **Next.js 14+** com App Router
- **TypeScript** - Sem arquivos .js
- **Tailwind CSS** - Para estilização
- **shadcn/ui** - Biblioteca de componentes
- **React Hook Form** - Gerenciamento de formulários
- **Zod** - Validação de schemas
- **Sonner** - Notificações toast
- **Lucide React** - Ícones
- **Axios** - Cliente HTTP

## Funcionalidades Implementadas

### ✅ Requisitos do Teste Técnico RNF02

- **Server Components vs Client Components**: Separação correta dos componentes
- **Validação de formulários**: Feedback visual inline por campo com React Hook Form + Zod
- **Estados de loading**: Tratamento adequado durante chamadas à API
- **Tratamento de erros**: Exibição clara de erros da API com toast notifications

### 🎨 Funcionalidades do Sistema

- ✅ Autenticação (login/signup) com validação
- ✅ Verificação automática de token via middleware
- ✅ Dashboard com módulos (apenas PDF ativo)
- ✅ Gerador de PDF com formulário validado
- ✅ Lista de documentos com estados de loading
- ✅ Download de PDFs
- ✅ Layout responsivo baseado no Zscan EVO
- ✅ Sistema de notificações (toast)

## Arquitetura do Projeto

### 📁 Estrutura de Arquivos

```
frontend/
├── app/                          # Server Components (App Router)
│   ├── auth/
│   │   └── page.tsx             # Página de autenticação (Client Component)
│   ├── globals.css               # Estilos globais
│   ├── layout.tsx               # Layout principal (Server Component)
│   ├── middleware.ts             # Middleware de autenticação
│   └── page.tsx                 # Dashboard (Client Component)
├── components/
│   ├── dashboard/               # Componentes do dashboard
│   │   ├── dashboard-header.tsx
│   │   ├── documents-list.tsx
│   │   └── modules-grid.tsx
│   ├── forms/                  # Formulários reutilizáveis
│   │   ├── auth-form.tsx
│   │   └── boleto-form.tsx
│   └── ui/                     # Componentes UI (shadcn/ui)
│       ├── button.tsx
│       ├── card.tsx
│       ├── form.tsx
│       ├── input.tsx
│       ├── label.tsx
│       ├── sonner.tsx
│       ├── textarea.tsx
│       └── toaster.tsx
├── hooks/                       # Hooks personalizados
│   ├── use-auth.ts
│   └── use-boletos.ts
├── lib/
│   ├── api.ts                   # Configuração da API
│   ├── utils.ts                 # Utilitários
│   └── validations.ts            # Schemas de validação Zod
├── Dockerfile                   # Configuração Docker
├── package.json                 # Dependências
├── tailwind.config.ts           # Configuração Tailwind
└── tsconfig.json               # Configuração TypeScript
```

### 🏗️ Server vs Client Components

**Server Components:**
- `app/layout.tsx` - Layout principal
- Middleware de autenticação

**Client Components:**
- Páginas que usam hooks (`'use client'`)
- Componentes de formulários
- Componentes interativos

### 🔧 Hooks Personalizados

#### `useAuth`
- Gerencia estado de autenticação
- Fornece funções login, signup, logout
- Tratamento automático de erros com toast

#### `useBoletos`
- Gerencia estado dos documentos
- Fornece funções CRUD
- Estados de loading integrados

### ✅ Validação de Formulários

**Schema Zod:**
```typescript
// Login: email + senha (mín 6 caracteres)
// Signup: nome + email + senha (complexa)
// Boleto: nome + valor + data (validações específicas)
```

**React Hook Form:**
- Validação em tempo real
- Feedback visual inline
- Estados de loading por campo

### 🔄 Estados de Loading

- **Loading global**: Durante verificação de autenticação
- **Loading de formulários**: Durante submissão
- **Loading de lista**: Durante carregamento de documentos
- **Loading de ações**: Durante download/criação

### 🚨 Tratamento de Erros

- **Erros de API**: Capturados e exibidos via toast
- **Erros de validação**: Feedback inline nos campos
- **Erros de rede**: Mensagens amigáveis
- **Redirecionamento automático**: Para login em caso de 401

## Como Executar

### Desenvolvimento

```bash
# Instalar dependências
npm install

# Iniciar servidor de desenvolvimento
npm run dev
```

Acesse `http://localhost:3000`

### Produção com Docker

```bash
# Construir imagem
docker build -t frontend-pdf-generator .

# Executar container
docker run -p 8080:8080 frontend-pdf-generator
```

### Docker Compose

O projeto está configurado para rodar com o backend:

```bash
docker-compose up --build
```

## Variáveis de Ambiente

Criar arquivo `.env.local`:

```env
NEXT_PUBLIC_API_URL=http://localhost:3006
```

## Fluxo de Autenticação

1. **Acesso inicial** → Middleware verifica token → Redireciona para `/auth`
2. **Login/Signup** → Validação → API → Token armazenado
3. **Dashboard** → Verificação automática → Acesso liberado
4. **Token inválido** → Middleware → Redirecionamento para `/auth`

## Design System

### 🎨 Cores (baseadas no Zscan EVO)
- **Azul médico**: `#0066cc`
- **Azul escuro**: `#1a365d`
- **Azul claro**: `#e6f3ff`
- **Verde médico**: `#00a86b`
- **Cinza**: `#6b7280`

### 📱 Responsividade
- Mobile-first approach
- Breakpoints: sm (640px), md (768px), lg (1024px), xl (1280px)
- Layout adaptativo para todos os dispositivos

## Integração com API

### Endpoints Utilizados

- `POST /auth/login` - Autenticação
- `POST /auth/signup` - Cadastro
- `GET /boleto` - Listar documentos
- `POST /boleto/create` - Criar documento
- `GET /boleto/:id/pdf` - Baixar PDF

### Tratamento de Respostas

- **Sucesso**: Dados processados e estado atualizado
- **Erro**: Mensagem extraída e exibida via toast
- **Loading**: Estados gerenciados pelos hooks

## Testes e Validação

### ✅ Validações Implementadas

1. **Formulário de Login:**
   - Email obrigatório e formato válido
   - Senha obrigatória (mín 6 caracteres)

2. **Formulário de Signup:**
   - Nome obrigatório (3-100 caracteres)
   - Email obrigatório e formato válido
   - Senha complexa (maiúscula, minúscula, número)

3. **Formulário de Boleto:**
   - Nome obrigatório (3-200 caracteres)
   - Valor numérico positivo
   - Data não anterior a hoje
   - Descrição opcional (máx 500 caracteres)

### 🔄 Estados de Loading

- Spinner durante operações assíncronas
- Botões desabilitados durante loading
- Feedback visual para todas as ações

## Deploy

### Produção

O projeto está configurado para deploy com:

- **Docker**: Multi-stage build para otimização
- **Standalone output**: Redução de tamanho
- **Port 8080**: Configuração padrão

### Performance

- **Code splitting**: Automático do Next.js
- **Static generation**: Onde aplicável
- **Image optimization**: Configurado
- **Font optimization**: Google Fonts

## Contribuição

O projeto segue as melhores práticas:

1. **TypeScript strict**: Tipagem completa
2. **Component reusability**: Componentes atômicos
3. **Error boundaries**: Tratamento de erros
4. **Performance**: Otimizações implementadas
5. **Accessibility**: Semântica HTML5

---

© 2024 PDF Generator Medical. Todos os direitos reservados.

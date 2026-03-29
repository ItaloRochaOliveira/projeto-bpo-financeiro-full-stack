'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { Calculator, TrendingUp, BarChart3, Shield } from 'lucide-react'
import { useAuth } from '@/hooks/use-auth'
import { AuthForm } from '@/components/forms/auth-form'

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true)
  const { isAuthenticated, isLoading } = useAuth()
  const router = useRouter()

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-account-light to-white flex items-center justify-center p-4">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-account-primary mx-auto mb-4"></div>
          <p className="text-account-muted">Verificando autenticação...</p>
        </div>
      </div>
    )
  }

  if (isAuthenticated) {
    router.push('/dashboard')
    return null
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-account-light via-white to-account-light flex">
      {/* Painel esquerdo com informações */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-account-primary to-account-accent p-12 text-white">
        <div className="max-w-md">
          <div className="mb-8">
            <div className="flex items-center gap-3 mb-6">
              <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
                <Calculator className="w-6 h-6 text-white" />
              </div>
              <h1 className="text-3xl font-bold">Sistema Financeiro</h1>
            </div>
            <p className="text-lg text-white/90 mb-2">
              Gestão financeira profissional para seu negócio
            </p>
            <p className="text-white/70">
              Controle custos, monitore faturamento e analise preços com precisão e eficiência.
            </p>
          </div>

          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center">
                <TrendingUp className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-semibold">Análise Completa</h3>
                <p className="text-sm text-white/70">Dashboards detalhados e relatórios em tempo real</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center">
                <BarChart3 className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-semibold">Relatórios Financeiros</h3>
                <p className="text-sm text-white/70">Exporte dados e gere relatórios personalizados</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center">
                <Shield className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-semibold">Segurança Máxima</h3>
                <p className="text-sm text-white/70">Seus dados protegidos com criptografia avançada</p>
              </div>
            </div>
          </div>

          <div className="mt-12 pt-8 border-t border-white/20">
            <div className="flex items-center gap-6 text-sm text-white/60">
              <span>© 2024 Sistema Financeiro</span>
              <span>•</span>
              <span>Todos os direitos reservados</span>
            </div>
          </div>
        </div>
      </div>

      {/* Painel direito com formulário */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8">
        <div className="w-full max-w-md">
          <div className="text-center mb-8">
            <div className="lg:hidden flex items-center justify-center gap-3 mb-6">
              <div className="w-12 h-12 bg-account-primary rounded-xl flex items-center justify-center">
                <Calculator className="w-6 h-6 text-white" />
              </div>
              <h1 className="text-3xl font-bold text-account-primary">Sistema Financeiro</h1>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">
              {isLogin ? 'Bem-vindo de volta!' : 'Criar nova conta'}
            </h2>
            <p className="text-gray-600">
              {isLogin 
                ? 'Faça login para acessar seu painel financeiro' 
                : 'Cadastre-se para começar a gerenciar suas finanças'
              }
            </p>
          </div>

          <AuthForm
            isLogin={isLogin}
            onToggle={() => setIsLogin(!isLogin)}
          />

          <div className="mt-8 text-center">
            <div className="flex items-center justify-center gap-4 text-sm text-gray-500">
              <span>Termos de uso</span>
              <span>•</span>
              <span>Política de privacidade</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

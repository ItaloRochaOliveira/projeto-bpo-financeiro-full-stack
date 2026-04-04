'use client'

import { useState, useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Calculator, Lock, KeyRound } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { z } from 'zod'
import { apiReq } from '@/utils/ApiReq'

const firstPasswordSchema = z.object({
  newPassword: z.string().min(6, 'Senha deve ter pelo menos 6 caracteres'),
  confirmPassword: z.string().min(6, 'Confirmação de senha deve ter pelo menos 6 caracteres')
}).refine((data) => data.newPassword === data.confirmPassword, {
  message: "As senhas não conferem",
  path: ["confirmPassword"],
})

type FirstPasswordFormData = z.infer<typeof firstPasswordSchema>

export default function FirstLoginPage() {
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()
  const searchParams = useSearchParams()
  const token = searchParams.get('token')

  const form = useForm<FirstPasswordFormData>({
    resolver: zodResolver(firstPasswordSchema),
    defaultValues: {
      newPassword: '',
      confirmPassword: ''
    },
    mode: 'onChange',
  })

  useEffect(() => {
    if (!token) {
      router.push('/auth')
    }
  }, [token, router])

  const onSubmit = async (data: FirstPasswordFormData) => {
    if (!token) return

    try {
      setIsLoading(true)
      
      const response = await apiReq('http://localhost:3006/api/auth/first-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        data
      })

      if (response && response.data) {
        alert('Senha definida com sucesso! Você será redirecionado para o login.')
        router.push('/auth')
      } else {
        const error = response?.data.message || 'Erro ao definir senha'
        alert(error)
      }
    } catch (error) {
      console.error('Erro ao definir senha:', error)
      alert('Erro ao definir senha. Tente novamente.')
    } finally {
      setIsLoading(false)
    }
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
              Configuração de Primeiro Acesso
            </p>
            <p className="text-white/70">
              É hora de definir sua senha para acessar o sistema financeiro.
            </p>
          </div>

          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center">
                <Lock className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-semibold">Senha Segura</h3>
                <p className="text-sm text-white/70">Crie uma senha forte para proteger seus dados</p>
              </div>
            </div>

            <div className="flex items-center gap-4">
              <div className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-lg flex items-center justify-center">
                <KeyRound className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-semibold">Acesso Imediato</h3>
                <p className="text-sm text-white/70">Após definir sua senha, acesse o sistema imediatamente</p>
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
              Primeiro Acesso
            </h2>
            <p className="text-gray-600">
              Defina sua senha para acessar o sistema
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
            <div className="mb-6">
              <h3 className="text-xl font-semibold text-gray-900 mb-2">
                Criar Senha
              </h3>
              <p className="text-gray-600 text-sm">
                Digite sua nova senha e confirme para continuar
              </p>
            </div>

            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
                <FormField
                  control={form.control}
                  name="newPassword"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="account-label">Nova Senha</FormLabel>
                      <FormControl>
                        <Input
                          className="account-input"
                          type="password"
                          placeholder="Digite sua nova senha"
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="confirmPassword"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="account-label">Confirmar Senha</FormLabel>
                      <FormControl>
                        <Input
                          className="account-input"
                          type="password"
                          placeholder="Confirme sua nova senha"
                          {...field}
                          disabled={isLoading}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Button type="submit" className="account-btn-primary w-full" disabled={isLoading}>
                  {isLoading ? (
                    <div className="flex items-center gap-2">
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                      Definindo senha...
                    </div>
                  ) : (
                    'Definir Senha e Acessar'
                  )}
                </Button>
              </form>
            </Form>
          </div>
        </div>
      </div>
    </div>
  )
}

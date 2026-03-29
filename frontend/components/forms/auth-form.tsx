'use client'

import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { useAuth } from '@/hooks/use-auth'
import { loginSchema, signupSchema, LoginFormData, SignupFormData } from '@/lib/validations'
import { UserPlus, LogIn, User } from 'lucide-react'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface AuthFormProps {
  isLogin: boolean
  onToggle: () => void
}

export function AuthForm({ isLogin, onToggle }: AuthFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const { login, signup } = useAuth()

  const schema = isLogin ? loginSchema : signupSchema
  const form = useForm<LoginFormData | SignupFormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: '',
      password: '',
      ...(isLogin ? {} : { name: '', role: 'USER' }),
    },
    mode: 'onChange',
  })

  // Reset form when switching between login/signup
  React.useEffect(() => {
    form.reset({
      email: '',
      password: '',
      ...(isLogin ? {} : { name: '', role: 'USER' }),
    })
  }, [isLogin, form])

  const onSubmit = async (data: LoginFormData | SignupFormData) => {
    try {
      setIsLoading(true)
      if (isLogin) {
        await login(data as LoginFormData)
      } else {
        await signup(data as SignupFormData)
      }
    } catch (error) {
      // Error is handled in the hook
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-900 mb-2">
          {isLogin ? 'Acessar conta' : 'Criar conta'}
        </h3>
        <p className="text-gray-600 text-sm">
          {isLogin
            ? 'Preencha seus dados para fazer login'
            : 'Preencha os dados para criar sua conta'}
        </p>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
          {!isLogin && (
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="account-label">Nome Completo</FormLabel>
                  <FormControl>
                    <Input
                      className="account-input"
                      placeholder="Seu nome completo"
                      {...field}
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          )}

          <FormField
            control={form.control}
            name="email"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="account-label">E-mail</FormLabel>
                <FormControl>
                  <Input
                    className="account-input"
                    type="email"
                    placeholder="seu@email.com"
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
            name="password"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="account-label">Senha</FormLabel>
                <FormControl>
                  <Input
                    className="account-input"
                    type="password"
                    placeholder="••••••"
                    {...field}
                    disabled={isLoading}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {!isLogin && (
            <FormField
              control={form.control}
              name="role"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="account-label">Tipo de Usuário</FormLabel>
                  <FormControl>
                    <Select onValueChange={field.onChange} defaultValue={field.value} disabled={isLoading}>
                      <SelectTrigger className="account-input">
                        <SelectValue placeholder="Selecione o tipo" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="USER">Normal</SelectItem>
                      </SelectContent>
                    </Select>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          )}

          <Button type="submit" className="account-btn-primary w-full" disabled={isLoading}>
            {isLoading ? (
              <div className="flex items-center gap-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                {isLogin ? 'Entrando...' : 'Criando conta...'}
              </div>
            ) : isLogin ? (
              'Entrar'
            ) : (
              'Criar Conta'
            )}
          </Button>
        </form>
      </Form>

      <div className="mt-6 text-center">
        <button
          type="button"
          onClick={onToggle}
          className="text-account-primary hover:text-account-accent text-sm font-medium transition-colors"
          disabled={isLoading}
        >
          {isLogin
            ? 'Não tem uma conta? Crie uma agora'
            : 'Já tem uma conta? Faça login'}
        </button>
      </div>
    </div>
  )
}

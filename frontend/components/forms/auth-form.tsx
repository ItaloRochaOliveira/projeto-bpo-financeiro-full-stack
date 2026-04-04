'use client'

import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Button } from '@/components/ui/button'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { useAuth } from '@/hooks/use-auth'
import { loginSchema, LoginFormData } from '@/lib/validations'
import { LogIn } from 'lucide-react'
import { AccessRequestForm } from './access-request-form'

interface AuthFormProps {
  isLogin?: boolean
  onToggle?: () => void
}

export function AuthForm({ isLogin = true, onToggle }: AuthFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [showAccessRequest, setShowAccessRequest] = useState(false)
  const { login } = useAuth()

  const form = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: '',
      password: '',
    },
    mode: 'onChange',
  })

  const onSubmit = async (data: LoginFormData) => {
    try {
      setIsLoading(true)
      await login(data)
    } catch (error) {
      // Error is handled in the hook
    } finally {
      setIsLoading(false)
    }
  }

  if (showAccessRequest) {
    return (
      <AccessRequestForm
        onSuccess={() => {
          setShowAccessRequest(false)
        }}
        onBack={() => setShowAccessRequest(false)}
      />
    )
  }

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-900 mb-2">
          Acessar conta
        </h3>
        <p className="text-gray-600 text-sm">
          Preencha seus dados para fazer login
        </p>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
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

          <Button type="submit" className="account-btn-primary w-full" disabled={isLoading}>
            {isLoading ? (
              <div className="flex items-center gap-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                Entrando...
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <LogIn className="w-4 h-4" />
                Entrar
              </div>
            )}
          </Button>
        </form>
      </Form>

      <div className="mt-6 text-center">
        <button
          type="button"
          onClick={() => setShowAccessRequest(true)}
          className="text-account-primary hover:text-account-accent text-sm font-medium transition-colors"
          disabled={isLoading}
        >
          Não tem conta? Solicitar acesso
        </button>
      </div>
    </div>
  )
}

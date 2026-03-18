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
import { UserPlus, LogIn } from 'lucide-react'

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
      ...(isLogin ? {} : { name: '' }),
    },
    mode: 'onChange',
  })

  // Reset form when switching between login/signup
  React.useEffect(() => {
    form.reset({
      email: '',
      password: '',
      ...(isLogin ? {} : { name: '' }),
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
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          {isLogin ? (
            <>
              <LogIn className="w-5 h-5" />
              Entrar
            </>
          ) : (
            <>
              <UserPlus className="w-5 h-5" />
              Criar Conta
            </>
          )}
        </CardTitle>
        <CardDescription>
          {isLogin
            ? 'Faça login para acessar sua conta'
            : 'Crie uma nova conta para começar'}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {!isLogin && (
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Nome Completo</FormLabel>
                    <FormControl>
                      <Input
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
                  <FormLabel>E-mail</FormLabel>
                  <FormControl>
                    <Input
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
                  <FormLabel>Senha</FormLabel>
                  <FormControl>
                    <Input
                      type="password"
                      placeholder="••••••••"
                      {...field}
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button type="submit" className="w-full variant-medical" disabled={isLoading}>
              {isLoading ? (
                'Processando...'
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
            className="text-medical-blue hover:text-blue-700 text-sm font-medium transition-colors"
            disabled={isLoading}
          >
            {isLogin
              ? 'Não tem uma conta? Crie uma agora'
              : 'Já tem uma conta? Faça login'}
          </button>
        </div>
      </CardContent>
    </Card>
  )
}

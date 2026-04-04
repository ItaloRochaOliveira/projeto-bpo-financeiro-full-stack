'use client'

import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Button } from '@/components/ui/button'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Mail, Send, HelpCircle } from 'lucide-react'
import { z } from 'zod'
import { apiReq } from '@/utils/ApiReq'
import { AxiosError } from 'axios'
import { useDailyLimit } from '@/hooks/use-daily-limit'

const accessRequestSchema = z.object({
  name: z.string().min(3, 'Nome deve ter pelo menos 3 caracteres'),
  email: z.string().email('Email inválido'),
  accessReason: z.string().min(1, 'Selecione um motivo'),
  customReason: z.string().optional()
}).refine((data) => {
  if (data.accessReason === 'OUTRO') {
    return data.customReason && data.customReason.length >= 10
  }
  return true
}, {
  message: 'Descreva o motivo em pelo menos 10 caracteres',
  path: ['customReason']
})

type AccessRequestFormData = z.infer<typeof accessRequestSchema>

const ACCESS_REASONS = [
  { value: 'COMPREI_SERVICO_SENHA', label: 'Comprei o serviço e não veio a senha' },
  { value: 'PROBLEMA_ACESSO', label: 'Estou com problema para acessar minha conta' },
  { value: 'NOVO_USUARIO', label: 'Sou novo usuário e preciso de acesso' },
  { value: 'ESQUECI_SENHA', label: 'Esqueci minha senha e não consigo recuperar' },
  { value: 'BLOQUEIO_CONTA', label: 'Minha conta foi bloqueada' },
  { value: 'OUTRO', label: 'Outro motivo' }
]

interface AccessRequestFormProps {
  onSuccess?: () => void
  onBack?: () => void
}

export function AccessRequestForm({ onSuccess, onBack }: AccessRequestFormProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [isSubmitted, setIsSubmitted] = useState(false)
  
  // Hook para controle de limite diário
  const { isBlocked, nextRequestDate, canRequest, clearBlock, setRequestDate } = useDailyLimit()

  const form = useForm<AccessRequestFormData>({
    resolver: zodResolver(accessRequestSchema),
    defaultValues: {
      name: '',
      email: '',
      accessReason: '',
      customReason: ''
    },
    mode: 'onChange',
  })

  const watchAccessReason = form.watch('accessReason')

  const onSubmit = async (data: AccessRequestFormData) => {
    // Verificar limite diário antes de enviar
    if (!canRequest) {
      return // O toast será mostrado pelo hook useDailyLimit
    }

    try {
      setIsLoading(true)
      
      const response = await apiReq('http://localhost:3006/api/auth/request-access', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        data, 
      })

      if (response) {
        // Salvar data da solicitação usando o hook
        setRequestDate()
        
        setIsSubmitted(true)
        onSuccess?.()
      }
    } catch (error) {
      // Toast já é mostrado pelo ApiReq.ts
      console.error('Erro ao enviar solicitação:', error)
    } finally {
      setIsLoading(false)
    }
  }

  if (isSubmitted) {
    return (
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 text-center">
        <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <Mail className="w-8 h-8 text-green-600" />
        </div>
        <h3 className="text-2xl font-bold text-gray-900 mb-4">Solicitação Enviada!</h3>
        <p className="text-gray-600 mb-4">
          Sua solicitação de acesso foi enviada com sucesso. Entraremos em contato em breve com as instruções para seu primeiro login.
        </p>
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-6">
          <p className="text-blue-700 text-sm">
            <strong>Lembre-se:</strong> Você pode solicitar acesso apenas uma vez por dia. 
            Caso precise de ajuda, entre em contato com o administrador.
          </p>
        </div>
        <Button onClick={onBack} className="account-btn-primary">
          Voltar para Login
        </Button>
      </div>
    )
  }

  if (isBlocked) {
    return (
      <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8 text-center">
        <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-6">
          <HelpCircle className="w-8 h-8 text-yellow-600" />
        </div>
        <h3 className="text-2xl font-bold text-gray-900 mb-4">Limite Diário Atingido</h3>
        <p className="text-gray-600 mb-4">
          Você já fez uma solicitação de acesso hoje. Para proteger nosso sistema, permitimos apenas uma solicitação por dia.
        </p>
        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 mb-6">
          <p className="text-yellow-700 text-sm mb-2">
            <strong>Próxima solicitação disponível em:</strong>
          </p>
          <p className="text-yellow-800 font-medium">
            {nextRequestDate}
          </p>
        </div>
        <div className="space-y-3">
          <Button onClick={onBack} className="account-btn-primary w-full">
            Voltar para Login
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-2xl shadow-xl border border-gray-100 p-8">
      <div className="mb-6">
        <h3 className="text-xl font-semibold text-gray-900 mb-2">
          Solicitar Acesso
        </h3>
        <p className="text-gray-600 text-sm mb-3">
          Preencha seus dados para solicitar acesso ao sistema
        </p>
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
          <p className="text-blue-700 text-xs">
            <strong>Importante:</strong> Você pode solicitar acesso apenas uma vez por dia. 
            Aguarde aprovação após o envio.
          </p>
        </div>
      </div>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
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
            name="accessReason"
            render={({ field }) => (
              <FormItem>
                <FormLabel className="account-label">Motivo do Acesso</FormLabel>
                <FormControl>
                  <Select onValueChange={field.onChange} defaultValue={field.value} disabled={isLoading}>
                    <SelectTrigger className="account-input">
                      <SelectValue placeholder="Selecione o motivo do acesso" />
                    </SelectTrigger>
                    <SelectContent>
                      {ACCESS_REASONS.map((reason) => (
                        <SelectItem key={reason.value} value={reason.value}>
                          {reason.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {watchAccessReason === 'OUTRO' && (
            <FormField
              control={form.control}
              name="customReason"
              render={({ field }) => (
                <FormItem>
                  <FormLabel className="account-label">Descreva o motivo</FormLabel>
                  <FormControl>
                    <Textarea
                      className="account-input min-h-[100px]"
                      placeholder="Por favor, descreva em detalhes o motivo do seu acesso..."
                      {...field}
                      disabled={isLoading}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          )}

          <div className="flex gap-3">
            {onBack && (
              <Button 
                type="button" 
                variant="outline" 
                className="account-btn-secondary flex-1"
                onClick={onBack}
                disabled={isLoading}
              >
                Voltar
              </Button>
            )}
            
            <Button type="submit" className="account-btn-primary flex-1" disabled={isLoading}>
              {isLoading ? (
                <div className="flex items-center gap-2">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Enviando...
                </div>
              ) : (
                <div className="flex items-center gap-2">
                  <Send className="w-4 h-4" />
                  Enviar Solicitação
                </div>
              )}
            </Button>
          </div>
        </form>
      </Form>
    </div>
  )
}

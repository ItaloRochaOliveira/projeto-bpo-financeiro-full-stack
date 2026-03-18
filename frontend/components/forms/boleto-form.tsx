'use client'

import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { useBoletos } from '@/hooks/use-boletos'
import { boletoSchema, BoletoFormData } from '@/lib/validations'

interface BoletoFormProps {
  onClose: () => void
  onSubmit?: (data: BoletoFormData) => Promise<void>
}

export function BoletoForm({ onClose, onSubmit }: BoletoFormProps) {
  const { createBoleto, isCreating } = useBoletos()

  const form = useForm<BoletoFormData>({
    resolver: zodResolver(boletoSchema),
    defaultValues: {
      nomeEmpresa: '',
      cpfCnpj: '',
      endereco: '',
      descricaoReferencia: '',
      valor: '',
      vencimento: '',
    },
  })

  const handleSubmit = async (data: BoletoFormData) => {
    try {
      if (onSubmit) {
        await onSubmit(data)
      } else {
        await createBoleto(data)
      }
      onClose()
      form.reset()
    } catch (error) {
      // Error is handled in the hook
    }
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <Card className="w-full max-w-md max-h-[90vh] flex flex-col mx-4 sm:mx-auto">
        <CardHeader className="pb-4">
          <CardTitle className="text-lg sm:text-xl">Novo Documento</CardTitle>
          <CardDescription className="text-sm sm:text-base">
            Preencha as informações para criar um novo documento
          </CardDescription>
        </CardHeader>
        <CardContent className="flex-1 overflow-y-auto px-4 sm:px-6">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4 sm:space-y-6">
              <FormField
                control={form.control}
                name="nomeEmpresa"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">Nome da Empresa</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Nome da empresa"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="cpfCnpj"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">CPF/CNPJ</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="CPF ou CNPJ"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="endereco"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">Endereço</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Endereço completo"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="descricaoReferencia"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">Descrição</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="Descrição do documento"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base min-h-[80px] sm:min-h-[100px]"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="valor"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">Valor (R$)</FormLabel>
                    <FormControl>
                      <Input
                        type="number"
                        step="0.01"
                        placeholder="0,00"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="vencimento"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-sm sm:text-base">Data de Vencimento</FormLabel>
                    <FormControl>
                      <Input
                        type="date"
                        {...field}
                        disabled={isCreating}
                        className="text-sm sm:text-base"
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </form>
          </Form>
        </CardContent>
        <CardFooter className="flex flex-col sm:flex-row gap-2 sm:gap-3 border-t bg-gray-50 p-4 sm:p-6">
          <Button 
            type="button" 
            variant="outline" 
            onClick={onClose}
            disabled={isCreating}
            className="w-full sm:w-auto order-2 sm:order-1"
          >
            Cancelar
          </Button>
          <Button 
            type="button"
            variant="medical" 
            disabled={isCreating}
            onClick={async () => {
              try {
                const isValid = await form.trigger()
                if (!isValid) {
                  alert('Por favor, preencha todos os campos corretamente')
                  return
                }
                const data = form.getValues()
                await handleSubmit(data)
              } catch (error: any) {
                alert('Erro ao criar documento: ' + (error?.message || 'Erro desconhecido'))
              }
            }}
            className="w-full sm:w-auto order-1 sm:order-2"
          >
            {isCreating ? 'Criando...' : 'Criar Documento'}
          </Button>
        </CardFooter>
      </Card>
    </div>
  )
}

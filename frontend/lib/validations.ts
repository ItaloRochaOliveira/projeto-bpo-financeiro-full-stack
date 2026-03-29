import { z } from 'zod'

export const loginSchema = z.object({
  email: z
    .string()
    .min(1, 'E-mail é obrigatório')
    .email('Formato de e-mail inválido'),
  password: z
    .string()
    .min(1, 'Senha é obrigatória')
    .min(6, 'Senha deve ter pelo menos 6 caracteres'),
})

export const signupSchema = z.object({
  name: z
    .string()
    .min(1, 'Nome é obrigatório')
    .min(3, 'Nome deve ter pelo menos 3 caracteres')
    .max(100, 'Nome deve ter no máximo 100 caracteres'),
  email: z
    .string()
    .min(1, 'E-mail é obrigatório')
    .email('Formato de e-mail inválido'),
  password: z
    .string()
    .min(1, 'Senha é obrigatória')
    .min(6, 'Senha deve ter pelo menos 6 caracteres'),
  role: z
    .string()
    .min(1, 'Tipo de usuário é obrigatório')
    .default('USER'),
})

export const boletoSchema = z.object({
  nomeEmpresa: z
    .string()
    .min(1, 'Nome da empresa é obrigatório')
    .min(3, 'Nome deve ter pelo menos 3 caracteres')
    .max(100, 'Nome deve ter no máximo 100 caracteres'),
  cpfCnpj: z
    .string()
    .min(11, 'CPF/CNPJ inválido')
    .max(18, 'CPF/CNPJ inválido'),
  endereco: z
    .string()
    .min(1, 'Endereço é obrigatório')
    .max(255, 'Endereço deve ter no máximo 255 caracteres'),
  descricaoReferencia: z
    .string()
    .min(1, 'Descrição é obrigatória')
    .max(255, 'Descrição deve ter no máximo 255 caracteres'),
  valor: z
    .string()
    .min(1, 'Valor é obrigatório')
    .refine(
      (val) => !isNaN(Number(val)) && Number(val) > 0,
      'Valor deve ser um número maior que 0'
    ),
  vencimento: z
    .string()
    .min(1, 'Data de vencimento é obrigatória')
    .refine(
      (val) => {
        const date = new Date(val)
        const today = new Date()
        today.setHours(0, 0, 0, 0)
        return date >= today
      },
      'Data deve ser hoje ou futura'
    ),
})

export type LoginFormData = z.infer<typeof loginSchema>
export type SignupFormData = z.infer<typeof signupSchema>
export type BoletoFormData = z.infer<typeof boletoSchema>

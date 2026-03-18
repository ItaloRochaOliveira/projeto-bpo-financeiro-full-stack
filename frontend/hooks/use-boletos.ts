'use client'

import { useState, useEffect } from 'react'
import { boletoAPI, BoletoData, CreateBoletoData } from '@/lib/api'
import { BoletoFormData } from '@/lib/validations'
import { toast } from 'sonner'

interface UseBoletosReturn {
  boletos: BoletoData[]
  isLoading: boolean
  isCreating: boolean
  fetchBoletos: () => Promise<void>
  createBoleto: (data: BoletoFormData) => Promise<void>
  downloadPdf: (id: string, name: string) => Promise<void>
  downloadAllPdf: () => Promise<void>
  previewPdf: (id: string) => Promise<void>
}

export function useBoletos(): UseBoletosReturn {
  const [boletos, setBoletos] = useState<BoletoData[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isCreating, setIsCreating] = useState(false)

  useEffect(() => {
    fetchBoletos()
  }, [])

  const fetchBoletos = async () => {
    try {
      setIsLoading(true)
      console.log('Buscando boletos...')
      const response = await boletoAPI.list()
      console.log('Response completa:', response)
      
      // Backend retorna diretamente um array de BoletoResponse
      const boletosArray = Array.isArray(response) ? response : []
      console.log('Array de boletos:', boletosArray)
      console.log('Tipo do array:', typeof boletosArray)
      console.log('É array?', Array.isArray(boletosArray))
      console.log('Length:', boletosArray?.length)
      
      setBoletos(boletosArray)
    } catch (error: any) {
      console.error('Erro ao buscar boletos:', error)
      const message = error.response?.data?.message || 'Erro ao carregar documentos'
      toast.error(message)
      setBoletos([]) // Garante que seja um array em caso de erro
    } finally {
      setIsLoading(false)
    }
  }

  const createBoleto = async (data: BoletoFormData) => {
    try {
      setIsCreating(true)
      await boletoAPI.create({
        nomeEmpresa: data.nomeEmpresa,
        cpfCnpj: data.cpfCnpj,
        endereco: data.endereco,
        descricaoReferencia: data.descricaoReferencia,
        valor: parseFloat(data.valor),
        vencimento: data.vencimento,
      } as CreateBoletoData)
      toast.success('Documento criado com sucesso!')
      await fetchBoletos()
    } catch (error: any) {
      const message = error.response?.data?.message || 'Erro ao criar documento'
      toast.error(message)
      throw error
    } finally {
      setIsCreating(false)
    }
  }

  const downloadPdf = async (id: string, name: string) => {
    try {
      if (!id || !name) {
        toast.error('ID ou nome do boleto não fornecido')
        return
      }

      const blob = await boletoAPI.generatePdf(id)
      
      if (!blob || blob.size === 0) {
        toast.error('PDF gerado está vazio ou corrompido')
        return
      }
      
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${name}.pdf`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      toast.success('PDF baixado com sucesso!')
    } catch (error: any) {
      console.error('Erro ao baixar PDF:', error)
      
      let errorMessage = 'Erro ao baixar PDF'
      
      if (error.response) {
        if (error.response.status === 404) {
          errorMessage = 'Boleto não encontrado'
        } else if (error.response.status === 500) {
          errorMessage = 'Erro interno ao gerar PDF. Tente novamente.'
        } else if (error.response.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.response.data?.error) {
          errorMessage = error.response.data.error
        }
      } else if (error.message) {
        errorMessage = error.message
      }
      
      toast.error(errorMessage)
    }
  }

  const downloadAllPdf = async () => {
    try {
      const blob = await boletoAPI.generateAllPdf()
      
      if (!blob || blob.size === 0) {
        toast.error('PDF gerado está vazio ou corrompido')
        return
      }
      
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `boletos-${new Date().toISOString().split('T')[0]}.pdf`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      toast.success('PDF com todos os boletos baixado com sucesso!')
    } catch (error: any) {
      console.error('Erro ao baixar PDF de todos os boletos:', error)
      
      let errorMessage = 'Erro ao baixar PDF dos boletos'
      
      if (error.response) {
        if (error.response.status === 404) {
          errorMessage = 'Nenhum boleto encontrado para gerar PDF'
        } else if (error.response.status === 500) {
          errorMessage = 'Erro interno ao gerar PDF. Tente novamente.'
        } else if (error.response.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.response.data?.error) {
          errorMessage = error.response.data.error
        }
      } else if (error.message) {
        errorMessage = error.message
      }
      
      toast.error(errorMessage)
    }
  }

  const previewPdf = async (id: string) => {
    try {
      if (!id) {
        toast.error('ID do boleto não fornecido')
        return
      }

      console.log('previewPdf - ID do boleto:', id)
      const blob = await boletoAPI.generatePdf(id)
      
      if (!blob || blob.size === 0) {
        toast.error('PDF gerado está vazio ou corrompido')
        return
      }
      
      const url = window.URL.createObjectURL(blob)
      
      // Abre em nova aba para visualização e impressão
      const newWindow = window.open(url, '_blank')
      
      if (newWindow) {
        newWindow.onload = () => {
          setTimeout(() => {
            newWindow.print()
          }, 500)
        }
        
        // Limpa a URL quando a janela for fechada
        newWindow.onbeforeunload = () => {
          window.URL.revokeObjectURL(url)
        }
      } else {
        // Fallback: download direto se popup for bloqueado
        const a = document.createElement('a')
        a.href = url
        a.download = `boleto-${id}.pdf`
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        window.URL.revokeObjectURL(url)
        toast.warning('Popup bloqueado. PDF baixado diretamente.')
      }
      
      toast.success('PDF aberto para visualização e impressão!')
    } catch (error: any) {
      console.error('Erro ao gerar PDF:', error)
      
      let errorMessage = 'Erro ao abrir PDF para visualização'
      
      if (error.response) {
        if (error.response.status === 404) {
          errorMessage = 'Boleto não encontrado'
        } else if (error.response.status === 500) {
          errorMessage = 'Erro interno ao gerar PDF. Tente novamente.'
        } else if (error.response.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.response.data?.error) {
          errorMessage = error.response.data.error
        }
      } else if (error.message) {
        errorMessage = error.message
      }
      
      toast.error(errorMessage)
    }
  }

  return {
    boletos,
    isLoading,
    isCreating,
    fetchBoletos,
    createBoleto,
    downloadPdf,
    downloadAllPdf,
    previewPdf,
  }
}

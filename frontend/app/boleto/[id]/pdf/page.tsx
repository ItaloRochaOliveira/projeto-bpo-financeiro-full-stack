'use client'

import { useEffect } from 'react'
import { useSearchParams, useRouter } from 'next/navigation'
import api from '@/lib/api'

export default function PdfPage({ params }: { params: { id: string } }) {
  const router = useRouter()
  const searchParams = useSearchParams()

  useEffect(() => {
    const generatePdf = async () => {
      try {
        const response = await api.get(`/boleto/${params.id}/pdf`, {
          responseType: 'blob',
        })

        // Cria uma URL para o blob
        const blob = new Blob([response.data], { type: 'application/pdf' })
        const url = window.URL.createObjectURL(blob)
        
        // Abre em nova aba
        window.open(url, '_blank')
        
        // Limpa a URL após abrir
        setTimeout(() => {
          window.URL.revokeObjectURL(url)
        }, 100)
        
        // Volta para a página anterior
        router.back()
      } catch (error) {
        console.error('Erro ao gerar PDF:', error)
        router.back()
      }
    }

    generatePdf()
  }, [params.id, router])

  return (
    <div className="min-h-screen bg-gradient-to-br from-medical-light to-white flex items-center justify-center">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-medical-blue mx-auto mb-4"></div>
        <p className="text-gray-600">Gerando PDF...</p>
      </div>
    </div>
  )
}

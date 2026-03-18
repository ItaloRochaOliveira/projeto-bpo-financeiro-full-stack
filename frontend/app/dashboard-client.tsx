'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { DashboardHeader } from '@/components/dashboard/dashboard-header'
import { ModulesGrid } from '@/components/dashboard/modules-grid'
import { DocumentsList } from '@/components/dashboard/documents-list'
import { BoletoForm } from '@/components/forms/boleto-form'
import { useAuth } from '@/hooks/use-auth'
import { useBoletos } from '@/hooks/use-boletos'
import { BoletoFormData } from '@/lib/validations'

export function DashboardClient() {
  const { isAuthenticated, isLoading } = useAuth()
  const { boletos, isLoading: isLoadingBoletos, createBoleto, previewPdf } = useBoletos()
  const [showCreateForm, setShowCreateForm] = useState(false)
  const router = useRouter()

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-medical-light to-white flex items-center justify-center">
        <p className="text-gray-500">Carregando...</p>
      </div>
    )
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-medical-light to-white flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-4">Não autenticado</h1>
          <p className="text-gray-500">Por favor, faça login para acessar o dashboard</p>
          <a href="/auth" className="text-medical-blue hover:underline">Ir para login</a>
        </div>
      </div>
    )
  }

  const handleModuleClick = (moduleId: string) => {
    if (moduleId === 'pdf') {
      router.push('/pdf')
    }
  }

  const handleCreateBoleto = async (data: BoletoFormData) => {
    try {
      await createBoleto(data)
      setShowCreateForm(false)
    } catch (error) {
      // Error is handled in the hook
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-medical-light to-white">
      <DashboardHeader />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8">
        <div className="mb-6 sm:mb-8">
          <h2 className="text-2xl sm:text-3xl font-bold text-gray-900 mb-2">Bem-vindo ao Sistema</h2>
          <p className="text-sm sm:text-base text-gray-600">Gerencie seus documentos de forma eficiente e segura</p>
        </div>

        <div className="space-y-6 sm:space-y-8">
          <ModulesGrid onModuleClick={handleModuleClick} />
          <DocumentsList 
            onCreateNew={() => setShowCreateForm(true)} 
            boletos={boletos}
            isLoading={isLoadingBoletos}
            previewPdf={previewPdf}
          />
        </div>
      </main>

      {showCreateForm && (
        <BoletoForm
          onClose={() => setShowCreateForm(false)}
          onSubmit={handleCreateBoleto}
        />
      )}
    </div>
  )
}

'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Plus } from 'lucide-react'
import { BoletoForm } from '@/components/forms/boleto-form'
import { DocumentsList } from '@/components/dashboard/documents-list'
import { PdfHeader } from '@/components/dashboard/pdf-header'
import { useBoletos } from '@/hooks/use-boletos'

export default function PdfPage() {
  const [showForm, setShowForm] = useState(false)
  const { boletos, isLoading, previewPdf } = useBoletos()

  const handleCreateNew = () => {
    setShowForm(true)
  }

  const handleFormClose = () => {
    setShowForm(false)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-medical-light to-white">
      <PdfHeader />
      
      <div className="max-w-6xl mx-auto px-6 py-8">
        {/* Header Section */}
        <div className="mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h2 className="text-2xl sm:text-3xl font-bold text-gray-900">Gerenciador de PDFs</h2>
              <p className="text-gray-600 mt-1">Crie e gerencie seus documentos PDF</p>
            </div>
            <Button
              onClick={handleCreateNew}
              className="flex items-center gap-2"
              variant="medical"
            >
              <Plus className="w-4 h-4" />
              Novo Documento
            </Button>
          </div>
        </div>

        {/* Documents List */}
        <DocumentsList
          boletos={boletos}
          isLoading={isLoading}
          onCreateNew={handleCreateNew}
          previewPdf={previewPdf}
        />

        {/* Form Modal */}
        {showForm && (
          <BoletoForm onClose={handleFormClose} />
        )}
      </div>
    </div>
  )
}

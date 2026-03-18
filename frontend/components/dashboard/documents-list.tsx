'use client'

import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FileText, Download, Plus } from 'lucide-react'
import { BoletoForm } from '../forms/boleto-form'

import { BoletoData } from '@/lib/api'

interface DocumentsListProps {
  onCreateNew: () => void
  boletos: BoletoData[]
  isLoading: boolean
  previewPdf: (id: string) => void
}

export function DocumentsList({ 
  onCreateNew, 
  boletos, 
  isLoading,
  previewPdf 
}: DocumentsListProps) {
  console.log('DocumentsList render - boletos:', boletos, 'isLoading:', isLoading)
  
  if (isLoading) {
    return (
      <Card>
        <CardContent className="text-center py-8">
          <p className="text-gray-500">Carregando documentos...</p>
        </CardContent>
      </Card>
    )
  }

  if (boletos.length === 0) {
    return (
      <Card>
        <CardContent className="text-center py-8 sm:py-12">
          <FileText className="w-12 h-12 sm:w-16 sm:h-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-base sm:text-lg font-medium text-gray-900 mb-2">Nenhum documento encontrado</h3>
          <p className="text-sm sm:text-base text-gray-500 mb-4">Crie seu primeiro documento para começar</p>
          <Button variant="medical" onClick={onCreateNew} className="w-full sm:w-auto">
            <Plus className="w-4 h-4 mr-2" />
            Criar Documento
          </Button>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader className="pb-4 sm:pb-6">
        <div className="flex flex-col sm:flex-row sm:justify-between sm:items-start gap-4">
          <div>
            <CardTitle className="text-lg sm:text-xl">Documentos Recentes</CardTitle>
            <CardDescription className="text-sm sm:text-base">Visualize e baixe seus documentos gerados</CardDescription>
          </div>
          <Button
            variant="medical"
            onClick={onCreateNew}
            className="w-full sm:w-auto flex items-center gap-2"
          >
            <Plus className="w-4 h-4" />
            Novo Documento
          </Button>
        </div>
      </CardHeader>
      <CardContent className="px-4 sm:px-6">
        <div className="space-y-3 sm:space-y-4">
          {Array.isArray(boletos) && boletos.length > 0 ? boletos.map((boleto: BoletoData) => (
            <div
              key={boleto.id}
              className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-3 sm:p-4 border rounded-lg hover:bg-gray-50 gap-3 sm:gap-0"
            >
              <div className="flex-1 w-full sm:w-auto">
                <h4 className="font-medium text-gray-900 text-sm sm:text-base mb-1">{boleto.nomeEmpresa || 'Nome não informado'}</h4>
                <p className="text-sm text-gray-500 mb-1">{boleto.cpfCnpj || 'CPF/CNPJ não informado'}</p>
                <p className="text-sm text-gray-500 mb-1 truncate">{boleto.endereco || 'Endereço não informado'}</p>
                {boleto.descricaoReferencia && (
                  <p className="text-sm text-gray-500 mb-2 line-clamp-2">{boleto.descricaoReferencia}</p>
                )}
                <p className="text-xs sm:text-sm text-gray-400">
                  Valor: R$ {boleto.valor ? (typeof boleto.valor === 'string' ? parseFloat(boleto.valor).toFixed(2) : boleto.valor.toFixed(2)) : '0.00'} | Vencimento: {boleto.vencimento ? (typeof boleto.vencimento === 'string' ? new Date(boleto.vencimento).toLocaleDateString() : boleto.vencimento.toLocaleDateString()) : 'N/A'}
                </p>
              </div>
              <div className="flex items-center space-x-2 w-full sm:w-auto">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => previewPdf(boleto.id)}
                  className="w-full sm:w-auto"
                >
                  <FileText className="w-4 h-4 mr-2" />
                  <span className="hidden sm:inline">Visualizar e Imprimir</span>
                  <span className="sm:hidden">Visualizar</span>
                </Button>
              </div>
            </div>
          )) : (
            <div className="text-center py-8">
              <p className="text-gray-500">Nenhum documento encontrado</p>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}

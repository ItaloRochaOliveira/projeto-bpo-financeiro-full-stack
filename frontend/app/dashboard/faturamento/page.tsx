"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { toast } from 'sonner'
import { 
  Calculator, 
  Plus, 
  Edit2, 
  Trash2, 
  TrendingUp, 
  Users,
  Download,
  Filter,
  BarChart3,
  Activity
} from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { apiReq } from '@/utils/ApiReq'

interface Faturamento {
  id: string
  equipamento: string
  mediaAlugados: number
  porcentagem: number
}

export default function FaturamentoPage() {
  const router = useRouter()
  const [faturamentos, setFaturamentos] = useState<Faturamento[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingFaturamento, setEditingFaturamento] = useState<Faturamento | null>(null)
  const [formData, setFormData] = useState({
    equipamento: '',
    mediaAlugados: '',
    totalEquipamento: ''
  })

  useEffect(() => {
    // Verificar autenticação
    const isAuthenticated = localStorage.getItem('isAuthenticated')
    if (!isAuthenticated || isAuthenticated !== 'true') {
      router.push('/auth')
      return
    }

    loadFaturamentos()
  }, [])

  const loadFaturamentos = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/auth')
        return
      }
      
      const response = await apiReq(`http://localhost:3006/api/faturamento`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      
      if (response && response.status === 200) {
        const data = response.data
        setFaturamentos(data || [])
      } else {
        setFaturamentos([])
      }
      setIsLoading(false)
    } catch (error) {
      console.error('Erro ao carregar faturamento:', error)
      setFaturamentos([])
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.equipamento || !formData.mediaAlugados || !formData.totalEquipamento) {
      toast.error('Preencha todos os campos')
      return
    }

    try {
      if (editingFaturamento) {
        setFaturamentos(prev => prev.map(fat => 
          fat.id === editingFaturamento.id 
            ? { 
                ...fat, 
                equipamento: formData.equipamento, 
                mediaAlugados: parseFloat(formData.mediaAlugados),
                porcentagem: (parseFloat(formData.mediaAlugados) / parseFloat(formData.totalEquipamento)) * 100
              }
            : fat
        ))
        toast.success('Faturamento atualizado com sucesso!')
      } else {
        const newFaturamento: Faturamento = {
          id: Date.now().toString(),
          equipamento: formData.equipamento,
          mediaAlugados: parseFloat(formData.mediaAlugados),
          porcentagem: (parseFloat(formData.mediaAlugados) / parseFloat(formData.totalEquipamento)) * 100
        }
        setFaturamentos(prev => [...prev, newFaturamento])
        toast.success('Faturamento adicionado com sucesso!')
      }
      
      setDialogOpen(false)
      setEditingFaturamento(null)
      setFormData({ equipamento: '', mediaAlugados: '', totalEquipamento: '' })
    } catch (error) {
      toast.error('Erro ao salvar faturamento')
    }
  }

  const handleEdit = (faturamento: Faturamento) => {
    setEditingFaturamento(faturamento)
    setFormData({
      equipamento: faturamento.equipamento,
      mediaAlugados: faturamento.mediaAlugados.toString(),
      totalEquipamento: (faturamento.mediaAlugados / (faturamento.porcentagem / 100)).toString()
    })
    setDialogOpen(true)
  }

  const handleDelete = async (id: string) => {
    try {
      setFaturamentos(prev => prev.filter(fat => fat.id !== id))
      toast.success('Faturamento excluído com sucesso!')
    } catch (error) {
      toast.error('Erro ao excluir faturamento')
    }
  }

  const handleExport = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        toast.error('Token não encontrado')
        return
      }
      
      const response = await apiReq(`http://localhost:3006/api/export/excel`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      
      // Criar blob e download
      if(response && response.status === 200) {
        const blob = new Blob([response.data], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        })
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = `bpo_financeiro_${new Date().toISOString().split('T')[0]}.xlsx`
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
        toast.success('Planilha completa exportada com sucesso!')
      }
    } catch (error) {
      toast.error('Erro ao exportar planilha')
    }
  }

  const totalEquipamentos = faturamentos.length
  const totalAlugados = faturamentos.reduce((sum, fat) => sum + fat.mediaAlugados, 0)
  const taxaOcupacaoMedia = totalEquipamentos > 0 ? (totalAlugados / (totalEquipamentos * 20)) * 100 : 0

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="animate-pulse">
          <div className="h-8 w-48 bg-gray-200 rounded mb-6"></div>
          <div className="grid gap-6 md:grid-cols-3">
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="h-4 w-20 bg-gray-200 rounded mb-4"></div>
              <div className="h-8 w-24 bg-gray-200 rounded mb-2"></div>
              <div className="h-3 w-16 bg-gray-200 rounded"></div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="h-4 w-20 bg-gray-200 rounded mb-4"></div>
              <div className="h-8 w-24 bg-gray-200 rounded mb-2"></div>
              <div className="h-3 w-16 bg-gray-200 rounded"></div>
            </div>
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <div className="h-4 w-20 bg-gray-200 rounded mb-4"></div>
              <div className="h-8 w-24 bg-gray-200 rounded mb-2"></div>
              <div className="h-3 w-16 bg-gray-200 rounded"></div>
            </div>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Faturamento</h1>
          <p className="text-gray-600 mt-2">
            Monitore equipamentos e taxas de ocupação em tempo real
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button className="account-btn-secondary" onClick={handleExport}>
            <Download className="w-4 h-4 mr-2" />
            Exportar Tudo
          </button>
          <button className="account-btn-secondary">
            <BarChart3 className="w-4 h-4 mr-2" />
            Exportar Relatório
          </button>
          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger asChild>
              <button className="account-btn-primary" onClick={() => setEditingFaturamento(null)}>
                <Plus className="w-4 h-4 mr-2" />
                Adicionar Equipamento
              </button>
            </DialogTrigger>
            <DialogContent className="account-card border-0 shadow-2xl">
              <DialogHeader>
                <DialogTitle className="text-xl font-semibold text-gray-900">
                  {editingFaturamento ? 'Editar Equipamento' : 'Adicionar Novo Equipamento'}
                </DialogTitle>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="space-y-2">
                  <Label className="account-label">Equipamento</Label>
                  <Input
                    className="account-input"
                    value={formData.equipamento}
                    onChange={(e) => setFormData(prev => ({ ...prev, equipamento: e.target.value }))}
                    placeholder="Ex: Máquina CNC, Torno, etc."
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label className="account-label">Média Alugados</Label>
                  <Input
                    className="account-input"
                    type="number"
                    step="0.01"
                    value={formData.mediaAlugados}
                    onChange={(e) => setFormData(prev => ({ ...prev, mediaAlugados: e.target.value }))}
                    placeholder="0"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label className="account-label">Total de Equipamentos</Label>
                  <Input
                    className="account-input"
                    type="number"
                    step="0.01"
                    value={formData.totalEquipamento}
                    onChange={(e) => setFormData(prev => ({ ...prev, totalEquipamento: e.target.value }))}
                    placeholder="0"
                    required
                  />
                </div>
                <div className="flex gap-3 pt-4">
                  <Button type="submit" className="account-btn-primary flex-1">
                    {editingFaturamento ? 'Atualizar' : 'Adicionar'}
                  </Button>
                  <Button 
                    type="button" 
                    variant="outline" 
                    className="account-btn-secondary"
                    onClick={() => {
                      setDialogOpen(false)
                      setEditingFaturamento(null)
                      setFormData({ equipamento: '', mediaAlugados: '', totalEquipamento: '' })
                    }}
                  >
                    Cancelar
                  </Button>
                </div>
              </form>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-6 md:grid-cols-3">
        <div className="bg-gradient-to-br from-account-info to-cyan-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <Calculator className="w-6 h-6 text-white" />
            </div>
            <Activity className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Total de Equipamentos</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {totalEquipamentos}
          </p>
          <p className="text-white/80 text-sm">
            Cadastrados no sistema
          </p>
        </div>

        <div className="bg-gradient-to-br from-account-success to-emerald-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-white" />
            </div>
            <TrendingUp className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Média Alugados</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {totalAlugados}
          </p>
          <p className="text-white/80 text-sm">
            Equipamentos em uso
          </p>
        </div>

        <div className="bg-gradient-to-br from-account-warning to-amber-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <BarChart3 className="w-6 h-6 text-white" />
            </div>
            <div className="w-5 h-5 bg-white/20 rounded-full flex items-center justify-center">
              <span className="text-xs font-bold">%</span>
            </div>
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Taxa de Ocupação</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {taxaOcupacaoMedia.toFixed(1)}%
          </p>
          <p className="text-white/80 text-sm">
            Média geral
          </p>
        </div>
      </div>

      {/* Tabela de Faturamento */}
      <div className="account-card">
        <div className="account-card-header">
          <CardTitle className="flex items-center gap-2 text-lg font-semibold text-gray-900">
            <Calculator className="h-5 w-5 text-account-primary" />
            Equipamentos e Ocupação
          </CardTitle>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>{faturamentos.length} equipamentos cadastrados</span>
          </div>
        </div>
        <div className="account-card-content">
          <div className="overflow-x-auto">
            <table className="account-table">
              <thead>
                <tr>
                  <th>Equipamento</th>
                  <th>Média Alugados</th>
                  <th>Taxa de Ocupação</th>
                  <th>Status</th>
                  <th className="text-right">Ações</th>
                </tr>
              </thead>
              <tbody>
                {faturamentos.map((faturamento) => (
                  <tr key={faturamento.id}>
                    <td className="font-medium text-gray-900">{faturamento.equipamento}</td>
                    <td className="font-medium text-gray-900">{faturamento.mediaAlugados}</td>
                    <td>
                      <div className="flex items-center gap-3">
                        <div className="flex-1 max-w-24 bg-gray-200 rounded-full h-2">
                          <div 
                            className={`h-2 rounded-full transition-all duration-300 ${
                              faturamento.porcentagem >= 70 ? 'bg-green-500' :
                              faturamento.porcentagem >= 40 ? 'bg-yellow-500' :
                              'bg-red-500'
                            }`} 
                            style={{ width: `${faturamento.porcentagem}%` }}
                          ></div>
                        </div>
                        <span className="text-sm font-medium text-gray-700 min-w-[50px]">
                          {faturamento.porcentagem.toFixed(1)}%
                        </span>
                      </div>
                    </td>
                    <td>
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        faturamento.porcentagem >= 70 
                          ? 'bg-green-100 text-green-800 border border-green-200' 
                          : faturamento.porcentagem >= 40
                          ? 'bg-yellow-100 text-yellow-800 border border-yellow-200'
                          : 'bg-red-100 text-red-800 border border-red-200'
                      }`}>
                        {faturamento.porcentagem >= 70 ? 'Alta' : 
                         faturamento.porcentagem >= 40 ? 'Média' : 'Baixa'}
                      </span>
                    </td>
                    <td className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleEdit(faturamento)}
                          className="text-gray-500 hover:text-account-primary hover:bg-account-light"
                        >
                          <Edit2 className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleDelete(faturamento.id)}
                          className="text-gray-500 hover:text-red-600 hover:bg-red-50"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Performance Overview */}
      <div className="grid gap-6 lg:grid-cols-2">
        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Desempenho por Categoria
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="space-y-4">
              {[
                { name: 'Alta Ocupação (70%+)', count: faturamentos.filter(f => f.porcentagem >= 70).length, color: 'bg-green-500' },
                { name: 'Média Ocupação (40-70%)', count: faturamentos.filter(f => f.porcentagem >= 40 && f.porcentagem < 70).length, color: 'bg-yellow-500' },
                { name: 'Baixa Ocupação (<40%)', count: faturamentos.filter(f => f.porcentagem < 40).length, color: 'bg-red-500' }
              ].map((category, index) => (
                <div key={index} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className={`w-3 h-3 rounded-full ${category.color}`}></div>
                    <span className="text-sm font-medium text-gray-700">{category.name}</span>
                  </div>
                  <span className="text-sm font-bold text-gray-900">{category.count} equipamentos</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Recomendações
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="space-y-3">
              {faturamentos.filter(f => f.porcentagem < 40).length > 0 && (
                <div className="flex items-start gap-3 p-3 bg-red-50 rounded-lg border border-red-200">
                  <div className="w-2 h-2 bg-red-500 rounded-full mt-1.5"></div>
                  <div>
                    <p className="text-sm font-medium text-red-800">
                      {faturamentos.filter(f => f.porcentagem < 40).length} equipamentos com baixa ocupação
                    </p>
                    <p className="text-xs text-red-600 mt-1">
                      Considere revisar preços ou estratégias de marketing
                    </p>
                  </div>
                </div>
              )}
              {faturamentos.filter(f => f.porcentagem >= 70).length > 0 && (
                <div className="flex items-start gap-3 p-3 bg-green-50 rounded-lg border border-green-200">
                  <div className="w-2 h-2 bg-green-500 rounded-full mt-1.5"></div>
                  <div>
                    <p className="text-sm font-medium text-green-800">
                      {faturamentos.filter(f => f.porcentagem >= 70).length} equipamentos com alta performance
                    </p>
                    <p className="text-xs text-green-600 mt-1">
                      Ótimo desempenho, mantenha a estratégia atual
                    </p>
                  </div>
                </div>
              )}
              <div className="flex items-start gap-3 p-3 bg-blue-50 rounded-lg border border-blue-200">
                <div className="w-2 h-2 bg-blue-500 rounded-full mt-1.5"></div>
                <div>
                  <p className="text-sm font-medium text-blue-800">
                    Taxa média de ocupação: {taxaOcupacaoMedia.toFixed(1)}%
                  </p>
                  <p className="text-xs text-blue-600 mt-1">
                    {taxaOcupacaoMedia >= 60 ? 'Bom desempenho geral' : 
                     taxaOcupacaoMedia >= 40 ? 'Desempenho moderado' : 
                     'Revisar estratégias gerais'}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

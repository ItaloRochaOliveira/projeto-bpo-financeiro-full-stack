"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { toast } from 'sonner'
import { 
  TrendingUp, 
  Plus, 
  Edit2, 
  Trash2, 
  DollarSign,
  Target,
  Calculator,
  Download,
  Filter,
  AlertTriangle,
  Clock
} from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { apiReq } from '@/utils/ApiReq'

interface Preco {
  id: string
  equipamento: string
  investimento: number
  residual: number
  depreciacaoMeses: number
  precoAtualMensal: number
  margem: number
  manutencaoAnual: number
  qtde: number
  taxaOcupacao: number
  mediaAlugados: number
  rateio: number
  custoRateado: number
  manutencaoMensal: number
  custo: number
  depreciacao: number
  lucro: number
  pontoEquilibrio: number
  precoAdequado: number
  precoAtualMenosPrecoAdequado: number
  faturamentoEstimado: number
  resultado: number
  lucroTotal: number
  paybackMeses: number
}

export default function PrecosPage() {
  const router = useRouter()
  const [precos, setPrecos] = useState<Preco[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingPreco, setEditingPreco] = useState<Preco | null>(null)
  const [formData, setFormData] = useState({
    equipamento: '', 
    investimento: '', 
    residual: '', 
    depreciacaoMeses: '', 
    precoAtualMensal: '', 
    margem: '', 
    manutencaoAnual: ''
  })

  useEffect(() => {
    // Verificar autenticação
    const isAuthenticated = localStorage.getItem('isAuthenticated')
    if (!isAuthenticated || isAuthenticated !== 'true') {
      router.push('/auth')
      return
    }

    loadPrecos()
  }, [])

  const loadPrecos = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/auth')
        return
      }
      
      const response = await apiReq(`http://localhost:3006/api/precos`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      
      if (response.status === 200) {
        const data = response.data
        setPrecos(data || [])
      } else {
        setPrecos([])
      }
      setIsLoading(false)
    } catch (error) {
      console.error('Erro ao carregar preços:', error)
      setPrecos([])
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.equipamento || !formData.investimento || !formData.residual || 
        !formData.depreciacaoMeses || !formData.precoAtualMensal || 
        !formData.margem || !formData.manutencaoAnual) {
      toast.error('Preencha todos os campos')
      return
    }

    try {
      if (editingPreco) {
        setPrecos(prev => prev.map(preco => 
          preco.id === editingPreco.id 
            ? { 
                ...preco, 
                equipamento: formData.equipamento,
                investimento: parseFloat(formData.investimento),
                residual: parseFloat(formData.residual),
                depreciacaoMeses: parseInt(formData.depreciacaoMeses),
                precoAtualMensal: parseFloat(formData.precoAtualMensal),
                margem: parseFloat(formData.margem),
                manutencaoAnual: parseFloat(formData.manutencaoAnual)
              }
            : preco
        ))
        toast.success('Preço atualizado com sucesso!')
      } else {
        const newPreco: Preco = {
          id: Date.now().toString(),
          equipamento: formData.equipamento,
          investimento: parseFloat(formData.investimento),
          residual: parseFloat(formData.residual),
          depreciacaoMeses: parseInt(formData.depreciacaoMeses),
          precoAtualMensal: parseFloat(formData.precoAtualMensal),
          margem: parseFloat(formData.margem),
          manutencaoAnual: parseFloat(formData.manutencaoAnual),
          qtde: 0,
          taxaOcupacao: 0,
          mediaAlugados: 0,
          rateio: 0,
          custoRateado: 0,
          manutencaoMensal: 0,
          custo: 0,
          depreciacao: 0,
          lucro: 0,
          pontoEquilibrio: 0,
          precoAdequado: 0,
          precoAtualMenosPrecoAdequado: 0,
          faturamentoEstimado: 0,
          resultado: 0,
          lucroTotal: 0,
          paybackMeses: 0
        }
        setPrecos(prev => [...prev, newPreco])
        toast.success('Preço adicionado com sucesso!')
      }
      
      setDialogOpen(false)
      setEditingPreco(null)
      setFormData({ 
        equipamento: '', 
        investimento: '', 
        residual: '', 
        depreciacaoMeses: '', 
        precoAtualMensal: '', 
        margem: '', 
        manutencaoAnual: '' 
      })
    } catch (error) {
      toast.error('Erro ao salvar preço')
    }
  }

  const handleEdit = (preco: Preco) => {
    setEditingPreco(preco)
    setFormData({
      equipamento: preco.equipamento,
      investimento: preco.investimento.toString(),
      residual: preco.residual.toString(),
      depreciacaoMeses: preco.depreciacaoMeses.toString(),
      precoAtualMensal: preco.precoAtualMensal.toString(),
      margem: preco.margem.toString(),
      manutencaoAnual: preco.manutencaoAnual.toString()
    })
    setDialogOpen(true)
  }

  const handleDelete = async (id: string) => {
    try {
      setPrecos(prev => prev.filter(preco => preco.id !== id))
      toast.success('Preço excluído com sucesso!')
    } catch (error) {
      toast.error('Erro ao excluir preço')
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
      const blob = new Blob([response.data], { 
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
      })
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `bpo_financeiro_precos_${new Date().toISOString().split('T')[0]}.xlsx`
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      toast.success('Planilha de preços exportada com sucesso!')
    } catch (error) {
      toast.error('Erro ao exportar planilha')
    }
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value)
  }

  const formatPercentage = (value: number) => {
    return `${(value * 100).toFixed(1)}%`
  }

  const totalInvestment = precos.reduce((sum, preco) => sum + preco.investimento, 0)
  const totalRevenue = precos.reduce((sum, preco) => sum + preco.faturamentoEstimado, 0)
  const totalProfit = precos.reduce((sum, preco) => sum + preco.lucroTotal, 0)
  const avgPayback = precos.length > 0 ? precos.reduce((sum, preco) => sum + preco.paybackMeses, 0) / precos.length : 0

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
          <h1 className="text-3xl font-bold text-gray-900">Preços</h1>
          <p className="text-gray-600 mt-2">
            Analise preços adequados e margens de lucro otimizadas
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button className="account-btn-secondary" onClick={handleExport}>
            <Download className="w-4 h-4 mr-2" />
            Exportar Tudo
          </button>
          <button className="account-btn-secondary">
            <Target className="w-4 h-4 mr-2" />
            Exportar Análise
          </button>
          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger asChild>
              <button className="account-btn-primary" onClick={() => setEditingPreco(null)}>
                <Plus className="w-4 h-4 mr-2" />
                Adicionar Preço
              </button>
            </DialogTrigger>
            <DialogContent className="account-card border-0 shadow-2xl max-w-2xl">
              <DialogHeader>
                <DialogTitle className="text-xl font-semibold text-gray-900">
                  {editingPreco ? 'Editar Preço' : 'Adicionar Novo Preço'}
                </DialogTitle>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label className="account-label">Equipamento</Label>
                    <Input
                      className="account-input"
                      value={formData.equipamento}
                      onChange={(e) => setFormData(prev => ({ ...prev, equipamento: e.target.value }))}
                      placeholder="Ex: Máquina CNC"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="account-label">Investimento (R$)</Label>
                    <Input
                      className="account-input"
                      type="number"
                      step="0.01"
                      value={formData.investimento}
                      onChange={(e) => setFormData(prev => ({ ...prev, investimento: e.target.value }))}
                      placeholder="0,00"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="account-label">Residual (R$)</Label>
                    <Input
                      className="account-input"
                      type="number"
                      step="0.01"
                      value={formData.residual}
                      onChange={(e) => setFormData(prev => ({ ...prev, residual: e.target.value }))}
                      placeholder="0,00"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="account-label">Depreciação (meses)</Label>
                    <Input
                      className="account-input"
                      type="number"
                      value={formData.depreciacaoMeses}
                      onChange={(e) => setFormData(prev => ({ ...prev, depreciacaoMeses: e.target.value }))}
                      placeholder="0"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="account-label">Preço Atual Mensal (R$)</Label>
                    <Input
                      className="account-input"
                      type="number"
                      step="0.01"
                      value={formData.precoAtualMensal}
                      onChange={(e) => setFormData(prev => ({ ...prev, precoAtualMensal: e.target.value }))}
                      placeholder="0,00"
                      required
                    />
                  </div>
                  <div className="space-y-2">
                    <Label className="account-label">Margem</Label>
                    <Input
                      className="account-input"
                      type="number"
                      step="0.01"
                      value={formData.margem}
                      onChange={(e) => setFormData(prev => ({ ...prev, margem: e.target.value }))}
                      placeholder="0,00"
                      required
                    />
                  </div>
                  <div className="space-y-2 col-span-2">
                    <Label className="account-label">Manutenção Anual (R$)</Label>
                    <Input
                      className="account-input"
                      type="number"
                      step="0.01"
                      value={formData.manutencaoAnual}
                      onChange={(e) => setFormData(prev => ({ ...prev, manutencaoAnual: e.target.value }))}
                      placeholder="0,00"
                      required
                    />
                  </div>
                </div>
                <div className="flex gap-3 pt-4">
                  <Button type="submit" className="account-btn-primary flex-1">
                    {editingPreco ? 'Atualizar' : 'Adicionar'}
                  </Button>
                  <Button 
                    type="button" 
                    variant="outline" 
                    className="account-btn-secondary"
                    onClick={() => {
                      setDialogOpen(false)
                      setEditingPreco(null)
                      setFormData({ 
                        equipamento: '', 
                        investimento: '', 
                        residual: '', 
                        depreciacaoMeses: '', 
                        precoAtualMensal: '', 
                        margem: '', 
                        manutencaoAnual: '' 
                      })
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
        <div className="bg-gradient-to-br from-account-primary to-account-accent text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-white" />
            </div>
            <TrendingUp className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Investimento Total</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {formatCurrency(totalInvestment)}
          </p>
          <p className="text-white/80 text-sm">
            {precos.length} equipamentos
          </p>
        </div>

        <div className="bg-gradient-to-br from-account-success to-emerald-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <Target className="w-6 h-6 text-white" />
            </div>
            <TrendingUp className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Faturamento Estimado</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {formatCurrency(totalRevenue)}
          </p>
          <p className="text-white/80 text-sm">
            Mensal projetado
          </p>
        </div>

        <div className="bg-gradient-to-br from-account-info to-cyan-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <Clock className="w-6 h-6 text-white" />
            </div>
            <div className="w-5 h-5 bg-white/20 rounded-full flex items-center justify-center">
              <span className="text-xs font-bold">m</span>
            </div>
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Payback Médio</h3>
          <p className="text-3xl font-bold text-white mb-2">
            {avgPayback.toFixed(1)} meses
          </p>
          <p className="text-white/80 text-sm">
            Retorno sobre investimento
          </p>
        </div>
      </div>

      {/* Alertas */}
      {precos.some(p => p.precoAtualMenosPrecoAdequado < 0) && (
        <div className="account-card border-l-4 border-red-500 bg-red-50">
          <div className="account-card-content">
            <div className="flex items-start gap-3">
              <AlertTriangle className="w-5 h-5 text-red-600 mt-0.5" />
              <div>
                <h3 className="font-semibold text-red-800">Atenção: Preços Abaixo do Ideal</h3>
                <p className="text-red-600 text-sm mt-1">
                  {precos.filter(p => p.precoAtualMenosPrecoAdequado < 0).length} equipamentos com preço abaixo do recomendado. 
                  Considere ajustar para maximizar a margem de lucro.
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Tabela de Preços Detalhados */}
      <div className="account-card">
        <div className="account-card-header">
          <CardTitle className="flex items-center gap-2 text-lg font-semibold text-gray-900">
            <Target className="h-5 w-5 text-account-primary" />
            Análise de Preços
          </CardTitle>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>{precos.length} equipamentos analisados</span>
          </div>
        </div>
        <div className="account-card-content">
          <div className="overflow-x-auto">
            <table className="account-table">
              <thead>
                <tr>
                  <th>Equipamento</th>
                  <th>Investimento</th>
                  <th>Preço Adequado</th>
                  <th>Diferença</th>
                  <th>Margem</th>
                  <th>Payback</th>
                  <th>Status</th>
                  <th className="text-right">Ações</th>
                </tr>
              </thead>
              <tbody>
                {precos.map((preco) => (
                  <tr key={preco.id}>
                    <td className="font-medium text-gray-900">{preco.equipamento}</td>
                    <td className="font-medium text-gray-900">{formatCurrency(preco.investimento)}</td>
                    <td className="font-semibold text-gray-900">
                      {formatCurrency(preco.precoAdequado)}
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                          preco.precoAtualMenosPrecoAdequado >= 0 
                            ? 'bg-green-100 text-green-800 border border-green-200' 
                            : 'bg-red-100 text-red-800 border border-red-200'
                        }`}>
                          {formatCurrency(preco.precoAtualMenosPrecoAdequado)}
                        </span>
                      </div>
                    </td>
                    <td>{formatPercentage(preco.margem)}</td>
                    <td>
                      <div className="flex items-center gap-2">
                        <Clock className="w-4 h-4 text-gray-500" />
                        <span className="text-sm font-medium text-gray-700">
                          {preco.paybackMeses.toFixed(1)} meses
                        </span>
                      </div>
                    </td>
                    <td>
                      <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                        preco.precoAtualMenosPrecoAdequado >= 0 
                          ? 'bg-green-100 text-green-800 border border-green-200' 
                          : 'bg-red-100 text-red-800 border border-red-200'
                      }`}>
                        {preco.precoAtualMenosPrecoAdequado >= 0 ? 'Ótimo' : 'Ajustar'}
                      </span>
                    </td>
                    <td className="text-right">
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleEdit(preco)}
                          className="text-gray-500 hover:text-account-primary hover:bg-account-light"
                        >
                          <Edit2 className="h-4 w-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={() => handleDelete(preco.id)}
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

      {/* Análise Detalhada */}
      <div className="grid gap-6 lg:grid-cols-2">
        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Resumo Financeiro
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="space-y-4">
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-sm font-medium text-gray-600">Investimento Total</span>
                <span className="text-sm font-bold text-gray-900">{formatCurrency(totalInvestment)}</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-sm font-medium text-gray-600">Faturamento Estimado</span>
                <span className="text-sm font-bold text-green-600">{formatCurrency(totalRevenue)}</span>
              </div>
              <div className="flex justify-between items-center py-3 border-b border-gray-100">
                <span className="text-sm font-medium text-gray-600">Lucro Total Projetado</span>
                <span className="text-sm font-bold text-blue-600">{formatCurrency(totalProfit)}</span>
              </div>
              <div className="flex justify-between items-center py-3">
                <span className="text-sm font-medium text-gray-600">Payback Médio</span>
                <span className="text-sm font-bold text-gray-900">{avgPayback.toFixed(1)} meses</span>
              </div>
            </div>
          </div>
        </div>

        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Insights e Recomendações
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="space-y-3">
              {precos.filter(p => p.precoAtualMenosPrecoAdequado < 0).length > 0 && (
                <div className="flex items-start gap-3 p-3 bg-red-50 rounded-lg border border-red-200">
                  <div className="w-2 h-2 bg-red-500 rounded-full mt-1.5"></div>
                  <div>
                    <p className="text-sm font-medium text-red-800">
                      Oportunidade de Aumento
                    </p>
                    <p className="text-xs text-red-600 mt-1">
                      {precos.filter(p => p.precoAtualMenosPrecoAdequado < 0).length} equipamentos podem ter preço aumentado
                    </p>
                  </div>
                </div>
              )}
              <div className="flex items-start gap-3 p-3 bg-green-50 rounded-lg border border-green-200">
                <div className="w-2 h-2 bg-green-500 rounded-full mt-1.5"></div>
                <div>
                  <p className="text-sm font-medium text-green-800">
                    Payback Saudável
                  </p>
                  <p className="text-xs text-green-600 mt-1">
                    Média de {avgPayback.toFixed(1)} meses está dentro do esperado
                  </p>
                </div>
              </div>
              <div className="flex items-start gap-3 p-3 bg-blue-50 rounded-lg border border-blue-200">
                <div className="w-2 h-2 bg-blue-500 rounded-full mt-1.5"></div>
                <div>
                  <p className="text-sm font-medium text-blue-800">
                    Potencial de Lucro
                  </p>
                  <p className="text-xs text-blue-600 mt-1">
                    {formatCurrency(totalProfit)} de lucro total projetado mensalmente
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

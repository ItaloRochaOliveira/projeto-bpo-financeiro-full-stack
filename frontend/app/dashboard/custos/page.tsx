"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { toast } from 'sonner'
import { Plus, Edit2, Trash2, DollarSign, TrendingDown, TrendingUp, PieChart, Download } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'

interface Custo {
  id: string
  descricao: string
  valor: number
  tipoCusto: 'FIXO' | 'VARIAVEL'
  porcentagem: number
}

interface ResumoCustos {
  custosFixos: {
    value: number
    porcentagem: number
  }
  custosVariaveis: {
    value: number
    porcentagem: number
  }
  total: {
    value: number
    porcentagem: number
  }
}

export default function CustosPage() {
  const router = useRouter()
  const [custos, setCustos] = useState<Custo[]>([])
  const [resumo, setResumo] = useState<ResumoCustos>({
    custosFixos: { value: 0, porcentagem: 0 },
    custosVariaveis: { value: 0, porcentagem: 0 },
    total: { value: 0, porcentagem: 0 }
  })
  const [isLoading, setIsLoading] = useState(true)
  const [dialogOpen, setDialogOpen] = useState(false)
  const [editingCusto, setEditingCusto] = useState<Custo | null>(null)
  const [formData, setFormData] = useState({
    descricao: '',
    valor: '',
    tipoCusto: 'FIXO' as 'FIXO' | 'VARIAVEL'
  })

  useEffect(() => {
    // Verificar autenticação
    const isAuthenticated = localStorage.getItem('isAuthenticated')
    if (!isAuthenticated || isAuthenticated !== 'true') {
      router.push('/auth')
      return
    }

    loadCustos()
  }, [])

  const loadCustos = async () => {
    try {
      // Buscar dados da API real
      const user = JSON.parse(localStorage.getItem('user') || '{}')
      const userId = user.email || 'user123'
      const response = await fetch(`http://localhost:3006/api/custos/all?userId=${userId}`)
      
      if (response.ok) {
        const data = await response.json()
        setCustos(data.data || [])
        // Calcular resumo com base nos dados reais
        const custosFixos = data.data?.filter((c: any) => c.tipoCusto === 'FIXO') || []
        const custosVariaveis = data.data?.filter((c: any) => c.tipoCusto === 'VARIAVEL') || []
        const totalFixos = custosFixos.reduce((sum: number, c: any) => sum + c.valor, 0)
        const totalVariaveis = custosVariaveis.reduce((sum: number, c: any) => sum + c.valor, 0)
        const total = totalFixos + totalVariaveis
        
        setResumo({
          custosFixos: { value: totalFixos, porcentagem: total > 0 ? (totalFixos / total) * 100 : 0 },
          custosVariaveis: { value: totalVariaveis, porcentagem: total > 0 ? (totalVariaveis / total) * 100 : 0 },
          total: { value: total, porcentagem: 100 }
        })
      } else {
        // Se falhar, tenta endpoint sem autenticação para desenvolvimento
        const responseDev = await fetch(`http://localhost:3006/api/custos`)
        if (responseDev.ok) {
          const data = await responseDev.json()
          setCustos(data.data || [])
          setResumo({
            custosFixos: { value: 0, porcentagem: 0 },
            custosVariaveis: { value: 0, porcentagem: 0 },
            total: { value: 0, porcentagem: 0 }
          })
        } else {
          setCustos([])
          setResumo({
            custosFixos: { value: 0, porcentagem: 0 },
            custosVariaveis: { value: 0, porcentagem: 0 },
            total: { value: 0, porcentagem: 0 }
          })
        }
      }
      setIsLoading(false)
    } catch (error) {
      console.error('Erro ao carregar custos:', error)
      setCustos([])
      setResumo({
        custosFixos: { value: 0, porcentagem: 0 },
        custosVariaveis: { value: 0, porcentagem: 0 },
        total: { value: 0, porcentagem: 0 }
      })
      setIsLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!formData.descricao || !formData.valor) {
      toast.error('Preencha todos os campos')
      return
    }

    try {
      if (editingCusto) {
        setCustos(prev => prev.map(custo => 
          custo.id === editingCusto.id 
            ? { ...custo, descricao: formData.descricao, valor: parseFloat(formData.valor), tipoCusto: formData.tipoCusto }
            : custo
        ))
        toast.success('Custo atualizado com sucesso!')
      } else {
        const newCusto: Custo = {
          id: Date.now().toString(),
          descricao: formData.descricao,
          valor: parseFloat(formData.valor),
          tipoCusto: formData.tipoCusto,
          porcentagem: 0
        }
        setCustos(prev => [...prev, newCusto])
        toast.success('Custo adicionado com sucesso!')
      }
      
      setDialogOpen(false)
      setEditingCusto(null)
      setFormData({ descricao: '', valor: '', tipoCusto: 'FIXO' })
    } catch (error) {
      toast.error('Erro ao salvar custo')
    }
  }

  const handleEdit = (custo: Custo) => {
    setEditingCusto(custo)
    setFormData({
      descricao: custo.descricao,
      valor: custo.valor.toString(),
      tipoCusto: custo.tipoCusto
    })
    setDialogOpen(true)
  }

  const handleDelete = async (id: string) => {
    try {
      setCustos(prev => prev.filter(custo => custo.id !== id))
      toast.success('Custo excluído com sucesso!')
    } catch (error) {
      toast.error('Erro ao excluir custo')
    }
  }

  const handleExport = () => {
    toast.info('Funcionalidade de exportação indisponível nesta versão. Em breve estará disponível!')
  }

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
          <h1 className="text-3xl font-bold text-gray-900">Custos</h1>
          <p className="text-gray-600 mt-2">
            Gerencie seus custos fixos e variáveis com precisão
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button className="account-btn-secondary" onClick={handleExport}>
            <Download className="w-4 h-4 mr-2" />
            Exportar Tudo
          </button>
          <button className="account-btn-secondary">
            <PieChart className="w-4 h-4 mr-2" />
            Exportar Relatório
          </button>
          <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogTrigger asChild>
              <button className="account-btn-primary" onClick={() => setEditingCusto(null)}>
                <Plus className="w-4 h-4 mr-2" />
                Adicionar Custo
              </button>
            </DialogTrigger>
            <DialogContent className="account-card border-0 shadow-2xl">
              <DialogHeader>
                <DialogTitle className="text-xl font-semibold text-gray-900">
                  {editingCusto ? 'Editar Custo' : 'Adicionar Novo Custo'}
                </DialogTitle>
              </DialogHeader>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="space-y-2">
                  <Label className="account-label">Descrição</Label>
                  <Input
                    className="account-input"
                    value={formData.descricao}
                    onChange={(e) => setFormData(prev => ({ ...prev, descricao: e.target.value }))}
                    placeholder="Ex: Aluguel, Salários, etc."
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label className="account-label">Valor (R$)</Label>
                  <Input
                    className="account-input"
                    type="number"
                    step="0.01"
                    value={formData.valor}
                    onChange={(e) => setFormData(prev => ({ ...prev, valor: e.target.value }))}
                    placeholder="0,00"
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label className="account-label">Tipo de Custo</Label>
                  <Select 
                    value={formData.tipoCusto} 
                    onValueChange={(value: 'FIXO' | 'VARIAVEL') => 
                      setFormData(prev => ({ ...prev, tipoCusto: value }))
                    }
                  >
                    <SelectTrigger className="account-input">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="FIXO">Fixo</SelectItem>
                      <SelectItem value="VARIAVEL">Variável</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div className="flex gap-3 pt-4">
                  <Button type="submit" className="account-btn-primary flex-1">
                    {editingCusto ? 'Atualizar' : 'Adicionar'}
                  </Button>
                  <Button 
                    type="button" 
                    variant="outline" 
                    className="account-btn-secondary"
                    onClick={() => {
                      setDialogOpen(false)
                      setEditingCusto(null)
                      setFormData({ descricao: '', valor: '', tipoCusto: 'FIXO' })
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

      {/* Tabela de Resumo */}
      <div className="grid gap-6 md:grid-cols-3">
        <div className="account-stat-card">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-white" />
            </div>
            <TrendingDown className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Custos Fixos</h3>
          <p className="text-3xl font-bold text-white mb-2">
            R$ {resumo.custosFixos.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
          <p className="text-white/80 text-sm">
            {resumo.custosFixos.porcentagem.toFixed(1)}% do total
          </p>
        </div>

        <div className="bg-gradient-to-br from-account-success to-emerald-600 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-white" />
            </div>
            <TrendingUp className="w-5 h-5 text-white/70" />
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Custos Variáveis</h3>
          <p className="text-3xl font-bold text-white mb-2">
            R$ {resumo.custosVariaveis.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
          <p className="text-white/80 text-sm">
            {resumo.custosVariaveis.porcentagem.toFixed(1)}% do total
          </p>
        </div>

        <div className="bg-gradient-to-br from-gray-800 to-gray-900 text-white rounded-xl p-6 shadow-lg">
          <div className="flex items-center justify-between mb-4">
            <div className="w-12 h-12 bg-white/20 backdrop-blur-sm rounded-xl flex items-center justify-center">
              <PieChart className="w-6 h-6 text-white" />
            </div>
            <div className="w-5 h-5 bg-white/20 rounded-full flex items-center justify-center">
              <span className="text-xs font-bold">100%</span>
            </div>
          </div>
          <h3 className="text-lg font-semibold text-white mb-2">Total</h3>
          <p className="text-3xl font-bold text-white mb-2">
            R$ {resumo.total.value.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
          </p>
          <p className="text-white/80 text-sm">
            100% do total
          </p>
        </div>
      </div>

      {/* Tabela de Custos Detalhados */}
      <div className="account-card">
        <div className="account-card-header">
          <CardTitle className="flex items-center gap-2 text-lg font-semibold text-gray-900">
            <DollarSign className="h-5 w-5 text-account-primary" />
            Custos Detalhados
          </CardTitle>
          <div className="flex items-center gap-2 text-sm text-gray-500">
            <span>{custos.length} custos cadastrados</span>
          </div>
        </div>
        <div className="account-card-content">
          <div className="overflow-x-auto">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Descrição</TableHead>
                  <TableHead>Valor</TableHead>
                  <TableHead>Tipo</TableHead>
                  <TableHead>%</TableHead>
                  <TableHead className="text-right">Ações</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {custos.map((custo) => (
                  <TableRow key={custo.id}>
                    <TableCell className="font-medium">{custo.descricao}</TableCell>
                    <TableCell>R$ {custo.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</TableCell>
                    <TableCell>
                      <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                        custo.tipoCusto === 'FIXO' 
                          ? 'bg-red-100 text-red-800' 
                          : 'bg-blue-100 text-blue-800'
                      }`}>
                        {custo.tipoCusto === 'FIXO' ? 'Fixo' : 'Variável'}
                      </span>
                    </TableCell>
                    <TableCell>{custo.porcentagem.toFixed(1)}%</TableCell>
                    <TableCell className="text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleEdit(custo)}
                        >
                          <Edit2 className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleDelete(custo.id)}
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        </div>
      </div>
    </div>
  )
}

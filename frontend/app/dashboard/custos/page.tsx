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
import { apiReq } from '@/utils/ApiReq'

interface Custo {
  id: string
  descricao: string
  valor: number
  tipoCusto: string
  porcentagem?: number
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
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [custoToDelete, setCustoToDelete] = useState<Custo | null>(null)
  const [formData, setFormData] = useState({
    descricao: '',
    valor: '',
    tipoCusto: ''
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
      const token = localStorage.getItem('token')
      if (!token) {
        router.push('/auth')
        return
      }
      
      const response = await apiReq(`http://localhost:3006/api/custos`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`
        }
      })

      if (response && response.status === 200) {
        const data = response.data
        const totalValue = data.resumo?.total?.value || 0
        
        // Adicionar porcentagem individual a cada custo
        const custosComPorcentagem = (data.data || []).map((custo: any) => ({
          ...custo,
          porcentagem: totalValue > 0 ? (custo.valor / totalValue) * 100 : 0
        }))
        
        setCustos(custosComPorcentagem)
        
        // Usar resumo vindo do backend
        if (data.resumo) {
          setResumo({
            custosFixos: { 
              value: data.resumo.custosFixos?.value || 0, 
              porcentagem: (data.resumo.custosFixos?.porcentagem || 0) * 100 
            },
            custosVariaveis: { 
              value: data.resumo.custosVariaveis?.value || 0, 
              porcentagem: (data.resumo.custosVariaveis?.porcentagem || 0) * 100 
            },
            total: { 
              value: data.resumo.total?.value || 0, 
              porcentagem: (data.resumo.total?.porcentagem || 0) * 100 
            }
          })
        } else {
          // Fallback caso não venha resumo
          const totalFixos = data.data?.filter((c: any) => c.tipoCusto === 'FIXO').reduce((sum: number, c: any) => sum + (c.valor || 0), 0)
          const totalVariaveis = data.data?.filter((c: any) => c.tipoCusto === 'VARIAVEL').reduce((sum: number, c: any) => sum + (c.valor || 0), 0)
          const total = totalFixos + totalVariaveis
          
          setResumo({
            custosFixos: { value: totalFixos, porcentagem: total > 0 ? (totalFixos / total) * 100 : 0 },
            custosVariaveis: { value: totalVariaveis, porcentagem: total > 0 ? (totalVariaveis / total) * 100 : 0 },
            total: { value: total, porcentagem: 100 }
          })
        }
      } else {
        setCustos([])
        setResumo({
          custosFixos: { value: 0, porcentagem: 0 },
          custosVariaveis: { value: 0, porcentagem: 0 },
          total: { value: 0, porcentagem: 0 }
        })
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
      const token = localStorage.getItem('token')
      if (!token) {
        toast.error('Token não encontrado')
        return
      }

      if (editingCusto) {
        // Atualizar custo existente
        const response = await apiReq(`http://localhost:3006/api/custos/${editingCusto.id}`, {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: {
            descricao: formData.descricao,
            valor: parseFloat(formData.valor),
            tipoCusto: formData.tipoCusto
          }
        })

        if (response && response.status === 200) {
          toast.success('Custo atualizado com sucesso!')
          loadCustos() // Recarregar dados
        } else {
          toast.error('Erro ao atualizar custo')
        }
      } else {
        // Criar novo custo
        const response = await apiReq(`http://localhost:3006/api/custos`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          data: {
            descricao: formData.descricao,
            valor: parseFloat(formData.valor),
            tipoCusto: formData.tipoCusto
          }
        })

        if (response && response.status === 200) {
          toast.success('Custo adicionado com sucesso!')
          loadCustos() // Recarregar dados
        } else {
          toast.error('Erro ao adicionar custo')
        }
      }
      
      setDialogOpen(false)
      setEditingCusto(null)
      setFormData({ descricao: '', valor: '', tipoCusto: 'FIXO' })
    } catch (error) {
      console.error('Erro ao salvar custo:', error)
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

  const handleDelete = (custo: Custo) => {
    setCustoToDelete(custo)
    setDeleteDialogOpen(true)
  }

  const confirmDelete = async () => {
    if (!custoToDelete) return
    
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        toast.error('Token não encontrado')
        return
      }

      const response = await apiReq(`http://localhost:3006/api/custos/${custoToDelete.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (response && response.status === 200) {
        toast.success('Custo excluído com sucesso!')
        loadCustos() // Recarregar dados
      } else {
        toast.error('Erro ao excluir custo')
      }
    } catch (error) {
      console.error('Erro ao excluir custo:', error)
      toast.error('Erro ao excluir custo')
    } finally {
      setDeleteDialogOpen(false)
      setCustoToDelete(null)
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
                    onValueChange={(value: 'FIXO' | 'VARIÁVEL') => 
                      setFormData(prev => ({ ...prev, tipoCusto: value }))
                    }
                  >
                    <SelectTrigger className="account-input">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="FIXO">Fixo</SelectItem>
                      <SelectItem value="VARIÁVEL">Variável</SelectItem>
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
                    <TableCell>{custo.porcentagem ? custo.porcentagem.toFixed(1) : '0.0'}%</TableCell>
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
                          onClick={() => handleDelete(custo)}
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

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onOpenChange={setDeleteDialogOpen}>
        <DialogContent className="account-card border-0 shadow-2xl">
          <DialogHeader>
            <DialogTitle className="text-xl font-semibold text-gray-900">
              Confirmar Exclusão
            </DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <p className="text-gray-600">
              Tem certeza que deseja excluir o custo <strong>"{custoToDelete?.descricao}"</strong> no valor de 
              <br />
              <strong>R$ {custoToDelete?.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</strong>?
            </p>
            <p className="text-red-600 text-sm font-medium">
              ⚠️ Esta ação não pode ser desfeita.
            </p>
          </div>
          <div className="flex gap-3 pt-4">
            <Button 
              variant="outline" 
              className="account-btn-secondary flex-1"
              onClick={() => {
                setDeleteDialogOpen(false)
                setCustoToDelete(null)
              }}
            >
              Cancelar
            </Button>
            <Button 
              className="account-btn-primary bg-red-600 hover:bg-red-700 flex-1"
              onClick={confirmDelete}
            >
              Excluir
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  )
}

"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { toast } from 'sonner'
import { 
  TrendingUp, 
  DollarSign, 
  Users, 
  Activity,
  Download,
  FileText,
  PieChart,
  BarChart3,
  ArrowUpRight,
  ArrowDownRight,
  Target,
  Calculator,
  AlertTriangle
} from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { apiReq } from '@/utils/ApiReq'

interface DashboardStats {
  totalCustos: number
  totalFaturamento: number
  totalEquipamentos: number
  margemLucro: number
}

export default function DashboardPage() {
  const router = useRouter()
  const [stats, setStats] = useState<DashboardStats>({
    totalCustos: 0,
    totalFaturamento: 0,
    totalEquipamentos: 0,
    margemLucro: 0
  })
  const [isLoading, setIsLoading] = useState(true)

  const handleExportComplete = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        toast.error('Token não encontrado')
        return
      }
      
      const response = await apiReq(`http://localhost:3006/api/export/completo`, {
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
        a.download = `bpo_financeiro_completo_${new Date().toISOString().split('T')[0]}.xlsx`
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

  const handleExportReport = async () => {
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
        a.download = `bpo_financeiro_relatorio_${new Date().toISOString().split('T')[0]}.xlsx`
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
        toast.success('Relatório exportado com sucesso!')
      }
    } catch (error) {
      toast.error('Erro ao exportar relatório')
    }
  }

  const handleNewReport = () => {
    alert('Funcionalidade de novo relatório indisponível nesta versão. Em breve estará disponível!')
  }

  const handleGoToCustos = () => {
    router.push('/dashboard/custos')
  }

  const handleGoToFaturamento = () => {
    router.push('/dashboard/faturamento')
  }

  const handleGoToPrecos = () => {
    router.push('/dashboard/precos')
  }

  useEffect(() => {
    // Verificar autenticação
    const isAuthenticated = localStorage.getItem('isAuthenticated')
    if (!isAuthenticated || isAuthenticated !== 'true') {
      router.push('/auth')
      return
    }

    // Carregar dados reais das APIs
    const loadStats = async () => {
      try {
        const token = localStorage.getItem('token')
        if (!token) {
          router.push('/auth')
          return
        }
        
        // Buscar dados das APIs reais com token
        const [custosResponse, faturamentoResponse, precosResponse] = await Promise.all([
          apiReq(`http://localhost:3006/api/custos`, {
            method: 'GET',
            headers: { Authorization: `Bearer ${token}` }
          }),
          apiReq(`http://localhost:3006/api/faturamento`, {
            method: 'GET',
            headers: { Authorization: `Bearer ${token}` }
          }),
          apiReq(`http://localhost:3006/api/precos`, {
            method: 'GET',
            headers: { Authorization: `Bearer ${token}` }
          })
        ])

        let totalCustos = 0
        let totalFaturamento = 0
        let totalEquipamentos = 0

        // Processar dados de custos
        if (custosResponse && custosResponse.status === 200) {
          const custosData = custosResponse.data
          totalCustos = custosData.data?.reduce((sum: number, c: any) => sum + c.valor, 0) || 0
        }

        // Processar dados de faturamento
        if (faturamentoResponse && faturamentoResponse.status === 200) {
          const faturamentoData = faturamentoResponse.data
          totalFaturamento = faturamentoData?.reduce((sum: number, f: any) => sum + (f.mediaAlugados * 1000), 0) || 0
          totalEquipamentos = faturamentoData?.length || 0
        }

        // Calcular margem de lucro
        const margemLucro = totalFaturamento > 0 ? ((totalFaturamento - totalCustos) / totalFaturamento) * 100 : 0

        setStats({
          totalCustos,
          totalFaturamento,
          totalEquipamentos,
          margemLucro
        })
      } catch (error) {
        console.error('Erro ao carregar estatísticas:', error)
        // Em caso de erro, mostrar valores zerados
        setStats({
          totalCustos: 0,
          totalFaturamento: 0,
          totalEquipamentos: 0,
          margemLucro: 0
        })
      } finally {
        setIsLoading(false)
      }
    }

    loadStats()
  }, [])

  const statCards = [
    {
      title: "Custos Totais",
      value: `R$ ${stats.totalCustos.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
      icon: DollarSign,
      change: "+12.5%",
      changeType: "increase" as const,
      description: "vs mês anterior",
      color: "account-danger"
    },
    {
      title: "Faturamento",
      value: `R$ ${stats.totalFaturamento.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
      icon: TrendingUp,
      change: "+23.1%",
      changeType: "increase" as const,
      description: "vs mês anterior",
      color: "account-success"
    },
    {
      title: "Equipamentos",
      value: stats.totalEquipamentos.toString(),
      icon: Calculator,
      change: "+5",
      changeType: "increase" as const,
      description: "novos este mês",
      color: "account-info"
    },
    {
      title: "Margem de Lucro",
      value: `${stats.margemLucro}%`,
      icon: Activity,
      change: "+2.4%",
      changeType: "increase" as const,
      description: "vs mês anterior",
      color: "account-warning"
    }
  ]

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="animate-pulse">
          <div className="h-8 w-48 bg-gray-200 rounded mb-6"></div>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="bg-white rounded-xl border border-gray-200 p-6">
                <div className="h-4 w-20 bg-gray-200 rounded mb-4"></div>
                <div className="h-8 w-24 bg-gray-200 rounded mb-2"></div>
                <div className="h-3 w-16 bg-gray-200 rounded"></div>
              </div>
            ))}
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
          <h1 className="text-3xl font-bold text-gray-900">Dashboard Financeiro</h1>
          <p className="text-gray-600 mt-2">
            Visão geral completa do seu sistema financeiro
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button className="account-btn-secondary" onClick={handleExportComplete}>
            <Download className="w-4 h-4 mr-2" />
            Exportar Completo
          </button>
          <button className="account-btn-secondary" onClick={handleExportReport}>
            <BarChart3 className="w-4 h-4 mr-2" />
            Exportar Relatório
          </button>
          <button className="account-btn-primary" onClick={handleNewReport}>
            <PieChart className="w-4 h-4 mr-2" />
            Novo Relatório
          </button>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {statCards.map((card, index) => (
          <div key={index} className="account-card">
            <div className="account-card-header">
              <div className="flex items-center justify-between">
                <CardTitle className="text-sm font-medium text-gray-600">
                  {card.title}
                </CardTitle>
                <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                  card.color === 'account-danger' ? 'bg-red-100' :
                  card.color === 'account-success' ? 'bg-green-100' :
                  card.color === 'account-info' ? 'bg-blue-100' :
                  'bg-yellow-100'
                }`}>
                  <card.icon className={`h-4 w-4 ${
                    card.color === 'account-danger' ? 'text-red-600' :
                    card.color === 'account-success' ? 'text-green-600' :
                    card.color === 'account-info' ? 'text-blue-600' :
                    'text-yellow-600'
                  }`} />
                </div>
              </div>
            </div>
            <div className="account-card-content">
              <div className="text-2xl font-bold text-gray-900">
                {card.value}
              </div>
              <div className="flex items-center space-x-2 text-xs text-gray-600 mt-2">
                {card.changeType === 'increase' ? (
                  <ArrowUpRight className="h-3 w-3 text-green-600" />
                ) : (
                  <ArrowDownRight className="h-3 w-3 text-red-600" />
                )}
                <span className={card.changeType === 'increase' ? 'text-green-600' : 'text-red-600'}>
                  {card.change}
                </span>
                <span>{card.description}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Section */}
      <div className="grid gap-6 lg:grid-cols-2">
        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Evolução Mensal
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
              <div className="text-center text-gray-500">
                <BarChart3 className="w-8 h-8 mx-auto mb-2" />
                <p>Gráfico de evolução mensal</p>
                <p className="text-sm">Em desenvolvimento</p>
              </div>
            </div>
          </div>
        </div>

        <div className="account-card">
          <div className="account-card-header">
            <CardTitle className="text-lg font-semibold text-gray-900">
              Distribuição de Custos
            </CardTitle>
          </div>
          <div className="account-card-content">
            <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
              <div className="text-center text-gray-500">
                <PieChart className="w-8 h-8 mx-auto mb-2" />
                <p>Gráfico de distribuição</p>
                <p className="text-sm">Em desenvolvimento</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid gap-6 md:grid-cols-3">
        <div className="account-card hover:shadow-lg transition-shadow cursor-pointer group" onClick={handleGoToCustos}>
          <div className="account-card-header">
            <CardTitle className="flex items-center gap-2 text-gray-900">
              <DollarSign className="h-5 w-5 text-account-danger" />
              Gerenciar Custos
            </CardTitle>
          </div>
          <div className="account-card-content">
            <p className="text-gray-600 mb-4">
              Adicione e edite seus custos fixos e variáveis com controle total
            </p>
            <div className="flex items-center text-account-primary group-hover:text-account-accent transition-colors">
              <span className="text-sm font-medium">Acessar módulo</span>
              <ArrowUpRight className="w-4 h-4 ml-1" />
            </div>
          </div>
        </div>

        <div className="account-card hover:shadow-lg transition-shadow cursor-pointer group" onClick={handleGoToFaturamento}>
          <div className="account-card-header">
            <CardTitle className="flex items-center gap-2 text-gray-900">
              <Calculator className="h-5 w-5 text-account-info" />
              Controlar Faturamento
            </CardTitle>
          </div>
          <div className="account-card-content">
            <p className="text-gray-600 mb-4">
              Monitore equipamentos e taxas de ocupação em tempo real
            </p>
            <div className="flex items-center text-account-primary group-hover:text-account-accent transition-colors">
              <span className="text-sm font-medium">Acessar módulo</span>
              <ArrowUpRight className="w-4 h-4 ml-1" />
            </div>
          </div>
        </div>

        <div className="account-card hover:shadow-lg transition-shadow cursor-pointer group" onClick={handleGoToPrecos}>
          <div className="account-card-header">
            <CardTitle className="flex items-center gap-2 text-gray-900">
              <TrendingUp className="h-5 w-5 text-account-success" />
              Analisar Preços
            </CardTitle>
          </div>
          <div className="account-card-content">
            <p className="text-gray-600 mb-4">
              Calcule preços adequados e margens de lucro otimizadas
            </p>
            <div className="flex items-center text-account-primary group-hover:text-account-accent transition-colors">
              <span className="text-sm font-medium">Acessar módulo</span>
              <ArrowUpRight className="w-4 h-4 ml-1" />
            </div>
          </div>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="account-card">
        <div className="account-card-header">
          <CardTitle className="text-lg font-semibold text-gray-900">
            Atividade Recente
          </CardTitle>
        </div>
        <div className="account-card-content">
          <div className="space-y-4">
            {[1, 2, 3].map((item) => (
              <div key={item} className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 bg-account-light rounded-full flex items-center justify-center">
                    <Activity className="w-4 h-4 text-account-primary" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">
                      {item === 1 ? 'Novo custo adicionado' : item === 2 ? 'Preço atualizado' : 'Equipamento cadastrado'}
                    </p>
                    <p className="text-xs text-gray-500">
                      {item === 1 ? 'Aluguel - R$ 5.000,00' : item === 2 ? 'Máquina CNC - R$ 3.500,00' : 'Torno Mecânico'}
                    </p>
                  </div>
                </div>
                <span className="text-xs text-gray-500">
                  {item === 1 ? 'há 2 horas' : item === 2 ? 'há 5 horas' : 'há 1 dia'}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

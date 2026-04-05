'use client'

import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell
} from 'recharts'
import { TrendingUp, DollarSign, Target, AlertCircle, BarChart as BarChart3 } from 'lucide-react'

const COLORS = ['#ef4444', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6']

interface EvolucaoMensalChartProps {
  data: Array<{
    mes: string
    custos: number
    faturamento: number
    lucro: number
  }>
  isLoading?: boolean
}

export function EvolucaoMensalChart({ data, isLoading }: EvolucaoMensalChartProps) {
  if (isLoading) {
    return (
      <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (data.length === 0) {
    return (
      <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
        <div className="text-center text-gray-500">
          <BarChart3 className="w-8 h-8 mx-auto mb-2" />
          <p>Sem dados cadastrados</p>
          <p className="text-sm">Adicione custos e faturamento para visualizar</p>
        </div>
      </div>
    )
  }

  return (
    <ResponsiveContainer width="100%" height={256}>
      <BarChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="mes" />
        <YAxis />
        <Tooltip 
          formatter={(value: any) => [`R$ ${Number(value).toLocaleString('pt-BR')}`, '']}
          contentStyle={{ backgroundColor: '#fff', border: '1px solid #e5e7eb' }}
        />
        <Legend />
        <Bar dataKey="custos" fill="#ef4444" name="Custos" />
        <Bar dataKey="faturamento" fill="#10b981" name="Faturamento" />
        <Bar dataKey="lucro" fill="#3b82f6" name="Lucro" />
      </BarChart>
    </ResponsiveContainer>
  )
}

interface DistribuicaoCustosChartProps {
  data: Array<{
    categoria: string
    valor: number
    percentual: number
  }>
  isLoading?: boolean
}

export function DistribuicaoCustosChart({ data, isLoading }: DistribuicaoCustosChartProps) {
  if (isLoading) {
    return (
      <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    )
  }

  if (data.length === 0) {
    return (
      <div className="h-64 bg-gray-50 rounded-lg flex items-center justify-center">
        <div className="text-center text-gray-500">
          <PieChart className="w-8 h-8 mx-auto mb-2" />
          <p>Sem dados cadastrados</p>
          <p className="text-sm">Adicione custos para visualizar distribuição</p>
        </div>
      </div>
    )
  }

  const chartData = data.map(item => ({
    name: item.categoria,
    value: item.valor,
    percentual: item.percentual
  }))

  return (
    <ResponsiveContainer width="100%" height={256}>
      <PieChart>
        <Pie
          data={chartData}
          cx="50%"
          cy="50%"
          labelLine={false}
          label={({ name, index }: any) => `${name}: ${chartData[index].percentual.toFixed(1)}%`}
          outerRadius={80}
          fill="#8884d8"
          dataKey="value"
        >
          {chartData.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip 
          formatter={(value: any) => [`R$ ${Number(value).toLocaleString('pt-BR')}`, '']}
          contentStyle={{ backgroundColor: '#fff', border: '1px solid #e5e7eb' }}
        />
      </PieChart>
    </ResponsiveContainer>
  )
}

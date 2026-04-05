'use client'

import { useState, useEffect } from 'react'
import { apiReq } from '@/utils/ApiReq'

export interface ChartData {
  evolucaoMensal: Array<{
    mes: string
    custos: number
    faturamento: number
    lucro: number
  }>
  distribuicaoCustos: Array<{
    categoria: string
    valor: number
    percentual: number
  }>
}

// Função para processar dados mensais com base nas datas reais
function processarEvolucaoMensal(custosResponse: any, faturamentoResponse: any) {
  const dadosMensais: { [key: string]: { custos: number; faturamento: number; dataReferencia: Date } } = {}
  
  // Processar custos com datas reais
  if (custosResponse && custosResponse.data) {
    const custosData = Array.isArray(custosResponse.data.data) ? custosResponse.data.data : []
    
    custosData.forEach((custo: any) => {
      if (custo.createdAt) {
        const data = new Date(custo.createdAt)
        const mesAno = data.toLocaleDateString('pt-BR', { month: 'short', year: 'numeric' })
        
        if (!dadosMensais[mesAno]) {
          dadosMensais[mesAno] = { custos: 0, faturamento: 0, dataReferencia: data }
        }
        
        dadosMensais[mesAno].custos += Number(custo.valor) || 0
      }
    })
  }
  
  // Processar faturamento com datas reais
  if (faturamentoResponse && faturamentoResponse.data) {
    const faturamentoData = Array.isArray(faturamentoResponse.data) ? faturamentoResponse.data : []
    
    faturamentoData.forEach((fat: any) => {
      if (fat.createdAt) {
        const data = new Date(fat.createdAt)
        const mesAno = data.toLocaleDateString('pt-BR', { month: 'short', year: 'numeric' })
        
        if (!dadosMensais[mesAno]) {
          dadosMensais[mesAno] = { custos: 0, faturamento: 0, dataReferencia: data }
        }
        
        dadosMensais[mesAno].faturamento += Number(fat.mediaAlugados) || 0
      }
    })
  }
  
  // Converter para array e calcular lucro
  const evolucaoMensal = Object.entries(dadosMensais)
    .map(([mes, dados]) => ({
      mes,
      custos: dados.custos,
      faturamento: dados.faturamento,
      lucro: dados.faturamento - dados.custos,
      dataReferencia: dados.dataReferencia
    }))
    .sort((a, b) => {
      // Ordenar por data real (mais antigo primeiro)
      return a.dataReferencia.getTime() - b.dataReferencia.getTime()
    })
    .slice(-6) // Últimos 6 meses
  
  // Se não houver dados reais, retornar array vazio
  if (evolucaoMensal.length === 0) {
    return []
  }
  
  return evolucaoMensal
}

export function useDashboardCharts() {
  const [chartData, setChartData] = useState<ChartData>({
    evolucaoMensal: [],
    distribuicaoCustos: []
  })
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchChartData = async () => {
      try {
        setIsLoading(true)
        setError(null)

        const token = localStorage.getItem('token')
        if (!token) return

        // Buscar dados reais das APIs
        const [custosResponse, faturamentoResponse] = await Promise.all([
          apiReq('http://localhost:3006/api/custos', {
            method: 'GET',
            headers: { Authorization: `Bearer ${token}` }
          }),
          apiReq('http://localhost:3006/api/faturamento', {
            method: 'GET',
            headers: { Authorization: `Bearer ${token}` }
          })
        ])

        console.log('=== DEBUG DADOS API ===')
        console.log('Custos Response:', custosResponse)
        console.log('Custos Data:', custosResponse?.data)
        console.log('Custos Data.Data:', custosResponse?.data?.data)
        console.log('Custos Data.Data Type:', Array.isArray(custosResponse?.data?.data) ? 'array' : typeof custosResponse?.data?.data)
        if (custosResponse?.data?.data && custosResponse.data.data.length > 0) {
          console.log('Primeiro Custo:', custosResponse.data.data[0])
        }
        
        console.log('Faturamento Response:', faturamentoResponse)
        console.log('Faturamento Data:', faturamentoResponse?.data)
        console.log('Faturamento Data Type:', Array.isArray(faturamentoResponse?.data) ? 'array' : typeof faturamentoResponse?.data)
        if (faturamentoResponse?.data && faturamentoResponse.data.length > 0) {
          console.log('Primeiro Faturamento:', faturamentoResponse.data[0])
        }

        // Processar dados para evolução mensal com base nas datas reais
        const evolucaoMensal = processarEvolucaoMensal(custosResponse, faturamentoResponse)
        
        console.log('=== EVOLUÇÃO MENSAL PROCESSADA ===')
        console.log(evolucaoMensal)

        // Processar dados para distribuição de custos
        let distribuicaoCustos: Array<{ categoria: string; valor: number; percentual: number }> = []
        
        if (custosResponse && custosResponse.data) {
          const custosData = Array.isArray(custosResponse.data.data) ? custosResponse.data.data : []
          
          console.log('=== DEBUG DISTRIBUIÇÃO CUSTOS ===')
          console.log('Custos Data:', custosData)
          
          // Agrupar custos por tipo
          const custosPorTipo = custosData.reduce((acc: any, custo: any) => {
            const tipo = custo.tipoCusto || 'Outros'
            if (!acc[tipo]) {
              acc[tipo] = 0
            }
            acc[tipo] += Number(custo.valor) || 0
            return acc
          }, {})

          console.log('Custos por Tipo:', custosPorTipo)

          const totalCustos = Object.values(custosPorTipo).reduce((sum: number, valor: any) => sum + valor, 0)
          console.log('Total Custos:', totalCustos)

          distribuicaoCustos = Object.entries(custosPorTipo).map(([categoria, valor]) => {
            const percentual = totalCustos > 0 ? ((valor as number) / totalCustos) * 100 : 0
            console.log(`Categoria: ${categoria}, Valor: ${valor}, Percentual: ${percentual}%`)
            return {
              categoria,
              valor: valor as number,
              percentual: percentual
            }
          })
          
          console.log('Distribuição Final:', distribuicaoCustos)
        }

        setChartData({
          evolucaoMensal,
          distribuicaoCustos
        })
      } catch (err: any) {
        console.error('Erro ao buscar dados dos gráficos:', err)
        setError(err.message || 'Erro ao carregar gráficos')
        
        // Em caso de erro, retornar arrays vazios
        setChartData({
          evolucaoMensal: [],
          distribuicaoCustos: []
        })
      } finally {
        setIsLoading(false)
      }
    }

    fetchChartData()
  }, [])

  return {
    chartData,
    isLoading,
    error
  }
}

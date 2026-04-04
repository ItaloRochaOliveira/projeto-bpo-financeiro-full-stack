'use client'

import { useState, useEffect } from 'react'
import { toast } from 'sonner'

interface DailyLimitState {
  isBlocked: boolean
  nextRequestDate: string
  lastRequestDate: string | null
  canRequest: boolean
  clearBlock: () => void
  setRequestDate: () => void
}

export function useDailyLimit(storageKey: string = 'lastAccessRequest'): DailyLimitState {
  const [isBlocked, setIsBlocked] = useState(false)
  const [nextRequestDate, setNextRequestDate] = useState('')
  const [lastRequestDate, setLastRequestDate] = useState<string | null>(null)

  const checkDailyLimit = () => {
    const lastRequest = localStorage.getItem(storageKey)
    setLastRequestDate(lastRequest)
    
    if (lastRequest) {
      const lastDate = new Date(lastRequest)
      const now = new Date()
      const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
      const lastRequestDay = new Date(lastDate.getFullYear(), lastDate.getMonth(), lastDate.getDate())
      
      // Se a última solicitação foi hoje, bloquear
      if (lastRequestDay.getTime() === today.getTime()) {
        setIsBlocked(true)
        
        // Calcular próxima data de liberação (amanhã à meia-noite)
        const tomorrow = new Date(today)
        tomorrow.setDate(tomorrow.getDate() + 1)
        tomorrow.setHours(0, 0, 0, 0) // Meia-noite
        setNextRequestDate(tomorrow.toLocaleString('pt-BR'))
        
        // Mostrar toast informativo
        toast.error('Você já fez uma solicitação de acesso hoje. Tente novamente amanhã.')
        
        return false
      }
    }
    
    setIsBlocked(false)
    setNextRequestDate('')
    return true
  }

  const clearBlock = () => {
    localStorage.removeItem(storageKey)
    checkDailyLimit()
    toast.success('Bloqueio removido com sucesso!')
  }

  const setRequestDate = () => {
    localStorage.setItem(storageKey, new Date().toISOString())
    checkDailyLimit()
  }

  // Verificar limite ao montar o componente
  useEffect(() => {
    checkDailyLimit()
  }, [])

  return {
    isBlocked,
    nextRequestDate,
    lastRequestDate,
    canRequest: !isBlocked,
    clearBlock,
    setRequestDate
  }
}

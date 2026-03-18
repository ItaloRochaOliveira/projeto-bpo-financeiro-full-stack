'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { authAPI } from '@/lib/api'
import { LoginFormData, SignupFormData } from '@/lib/validations'
import { toast } from 'sonner'

interface UseAuthReturn {
  isAuthenticated: boolean
  isLoading: boolean
  login: (data: LoginFormData) => Promise<void>
  signup: (data: SignupFormData) => Promise<void>
  logout: () => void
}

export function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    checkAuth()
  }, [])

  const checkAuth = () => {
    const token = localStorage.getItem('token')
    setIsAuthenticated(!!token)
    setIsLoading(false)
  }

  const login = async (data: LoginFormData) => {
    try {
      setIsLoading(true)
      const response = await authAPI.login(data)
      console.log('Resposta login:', response)
      localStorage.setItem('token', response.token)
      setIsAuthenticated(true)
      toast.success('Login realizado com sucesso!')
      router.push('/')
    } catch (error: any) {
      console.error('Erro no login:', error)
      
      let message = 'Erro ao fazer login'
      if (error.response?.data) {
        const errorData = error.response.data
        console.log('Error data:', errorData)
        
        if (errorData.message) {
          message = errorData.message
        }
      }
      
      toast.error(message)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const signup = async (data: SignupFormData) => {
    try {
      setIsLoading(true)
      console.log('Enviando dados signup:', data)
      const response = await authAPI.signup(data)
      console.log('Resposta signup:', response)
      localStorage.setItem('token', response.token)
      setIsAuthenticated(true)
      toast.success('Conta criada com sucesso!')
      router.push('/')
    } catch (error: any) {
      console.error('Erro no signup:', error)
      
      let message = 'Erro ao criar conta'
      if (error.response?.data) {
        const errorData = error.response.data
        console.log('Error data:', errorData)
        
        if (errorData.message) {
          message = errorData.message
        }
      }
      
      toast.error(message)
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    setIsAuthenticated(false)
    toast.success('Logout realizado com sucesso!')
    router.push('/auth')
  }

  return {
    isAuthenticated,
    isLoading,
    login,
    signup,
    logout,
  }
}

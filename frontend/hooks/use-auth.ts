'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { LoginFormData, SignupFormData } from '@/lib/validations'
import { toast } from 'sonner'

interface User {
  email: string
}

interface UseAuthReturn {
  isAuthenticated: boolean
  isLoading: boolean
  user: User | null
  login: (data: LoginFormData) => Promise<void>
  signup: (data: SignupFormData) => Promise<void>
  logout: () => void
}

export function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [user, setUser] = useState<User | null>(null)
  const router = useRouter()

  useEffect(() => {
    checkAuth()
  }, [])

  const checkAuth = () => {
    const auth = localStorage.getItem('isAuthenticated')
    const userData = localStorage.getItem('user')
    
    setIsAuthenticated(auth === 'true')
    if (userData) {
      setUser(JSON.parse(userData))
    }
    setIsLoading(false)
  }

  const login = async (data: LoginFormData) => {
    try {
      setIsLoading(true)
      // Simulação de login - substituir com API real
      if (data.email && data.password) {
        const userData = { email: data.email }
        localStorage.setItem('isAuthenticated', 'true')
        localStorage.setItem('user', JSON.stringify(userData))
        setUser(userData)
        setIsAuthenticated(true)
        toast.success('Login realizado com sucesso!')
        router.push('/dashboard')
      }
    } catch (error: any) {
      console.error('Erro no login:', error)
      toast.error('Erro ao fazer login')
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const signup = async (data: SignupFormData) => {
    try {
      setIsLoading(true)
      // Simulação de signup - substituir com API real
      if (data.email && data.password) {
        const userData = { email: data.email }
        localStorage.setItem('isAuthenticated', 'true')
        localStorage.setItem('user', JSON.stringify(userData))
        setUser(userData)
        setIsAuthenticated(true)
        toast.success('Conta criada com sucesso!')
        router.push('/dashboard')
      }
    } catch (error: any) {
      console.error('Erro no signup:', error)
      toast.error('Erro ao criar conta')
      throw error
    } finally {
      setIsLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('isAuthenticated')
    localStorage.removeItem('user')
    setUser(null)
    setIsAuthenticated(false)
    toast.success('Logout realizado com sucesso!')
    router.push('/auth')
  }

  return {
    isAuthenticated,
    isLoading,
    user,
    login,
    signup,
    logout,
  }
}

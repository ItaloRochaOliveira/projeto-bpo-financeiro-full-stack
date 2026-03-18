'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { FileText } from 'lucide-react'
import { useAuth } from '@/hooks/use-auth'
import { AuthForm } from '@/components/forms/auth-form'

export default function AuthPage() {
  const [isLogin, setIsLogin] = useState(true)
  const { isAuthenticated, isLoading } = useAuth()
  const router = useRouter()

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-medical-light to-white flex items-center justify-center p-4">
        <div className="text-center">
          <p className="text-gray-500">Verificando autenticação...</p>
        </div>
      </div>
    )
  }

  if (isAuthenticated) {
    router.push('/')
    return null
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-medical-light to-white flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-medical-blue rounded-xl flex items-center justify-center mx-auto mb-4">
            <FileText className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">PDF Generator Medical</h1>
          <p className="text-gray-600">Sistema profissional para gestão de documentos</p>
        </div>

        <AuthForm
          isLogin={isLogin}
          onToggle={() => setIsLogin(!isLogin)}
        />

        <div className="mt-8 text-center text-sm text-gray-500">
          <p>© 2024 PDF Generator Medical. Todos os direitos reservados.</p>
        </div>
      </div>
    </div>
  )
}

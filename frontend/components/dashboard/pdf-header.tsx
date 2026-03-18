'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { FileText, Menu, X, Home, LogOut } from 'lucide-react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/hooks/use-auth'

export function PdfHeader() {
  const { logout } = useAuth()
  const router = useRouter()
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    setIsMenuOpen(false)
  }

  const handleGoHome = () => {
    router.push('/')
    setIsMenuOpen(false)
  }

  return (
    <header className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-medical-blue rounded-lg flex items-center justify-center">
              <FileText className="w-5 h-5 text-white" />
            </div>
            <h1 className="text-xl font-bold text-gray-900 hidden sm:block">Gerenciador de PDFs</h1>
            <h1 className="text-lg font-bold text-gray-900 sm:hidden">PDFs</h1>
          </div>
          
          {/* Desktop Menu */}
          <div className="hidden sm:flex items-center space-x-2">
            <Button variant="outline" onClick={handleGoHome}>
              <Home className="w-4 h-4 mr-2" />
              Início
            </Button>
            <Button variant="outline" onClick={logout}>
              <LogOut className="w-4 h-4 mr-2" />
              Sair
            </Button>
          </div>

          {/* Mobile Menu */}
          <div className="sm:hidden">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsMenuOpen(!isMenuOpen)}
            >
              {isMenuOpen ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
            </Button>
          </div>
        </div>

        {/* Mobile Menu Dropdown */}
        {isMenuOpen && (
          <div className="sm:hidden border-t bg-white shadow-lg">
            <div className="px-4 py-3 space-y-2">
              <div className="flex items-center space-x-2 pb-3 border-b">
                <div className="w-8 h-8 bg-medical-blue rounded-lg flex items-center justify-center">
                  <FileText className="w-5 h-5 text-white" />
                </div>
                <h1 className="text-xl font-bold text-gray-900">Gerenciador de PDFs</h1>
              </div>
              <Button 
                variant="outline" 
                onClick={handleGoHome}
                className="w-full justify-start"
              >
                <Home className="w-4 h-4 mr-2" />
                Início
              </Button>
              <Button 
                variant="outline" 
                onClick={handleLogout}
                className="w-full justify-start"
              >
                <LogOut className="w-4 h-4 mr-2" />
                Sair
              </Button>
            </div>
          </div>
        )}
      </div>
    </header>
  )
}

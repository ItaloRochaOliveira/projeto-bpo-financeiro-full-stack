'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { FileText, Menu, X, Shield } from 'lucide-react'
import { useAuth } from '@/hooks/use-auth'

export function DashboardHeader() {
  const { logout, user } = useAuth()
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const getRoleDisplay = (role: string) => {
    switch (role) {
      case 'ADMIN':
        return 'Administrador'
      case 'USER':
        return 'Usuário'
      default:
        return role
    }
  }

  return (
    <header className="bg-white shadow-sm border-b">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-medical-blue rounded-lg flex items-center justify-center">
              <FileText className="w-5 h-5 text-white" />
            </div>
            <h1 className="text-xl font-bold text-gray-900 hidden sm:block">PDF Generator Medical</h1>
            <h1 className="text-lg font-bold text-gray-900 sm:hidden">PDF Med</h1>
          </div>
          
          {/* Desktop Menu */}
          <div className="hidden sm:flex items-center gap-4">
            {user && (
              <div className="flex items-center gap-2 px-3 py-1 bg-gray-100 rounded-lg">
                <Shield className="w-4 h-4 text-gray-600" />
                <span className="text-sm font-medium text-gray-700">
                  {getRoleDisplay(user.role)}
                </span>
              </div>
            )}
            <Button variant="outline" onClick={logout}>
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
                <div>
                  <h1 className="text-xl font-bold text-gray-900">PDF Generator Medical</h1>
                  {user && (
                    <div className="flex items-center gap-2 mt-1">
                      <Shield className="w-3 h-3 text-gray-600" />
                      <span className="text-xs text-gray-600">
                        {getRoleDisplay(user.role)}
                      </span>
                    </div>
                  )}
                </div>
              </div>
              <Button 
                variant="outline" 
                onClick={() => {
                  logout()
                  setIsMenuOpen(false)
                }}
                className="w-full"
              >
                Sair
              </Button>
            </div>
          </div>
        )}
      </div>
    </header>
  )
}

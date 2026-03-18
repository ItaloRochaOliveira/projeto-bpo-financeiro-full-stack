'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { FileText, Menu, X } from 'lucide-react'
import { useAuth } from '@/hooks/use-auth'

export function DashboardHeader() {
  const { logout } = useAuth()
  const [isMenuOpen, setIsMenuOpen] = useState(false)

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
          <div className="hidden sm:block">
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
                <h1 className="text-xl font-bold text-gray-900">PDF Generator Medical</h1>
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

"use client"

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { 
  LayoutDashboard, 
  DollarSign, 
  Calculator, 
  Settings, 
  LogOut,
  Menu,
  X,
  TrendingUp
} from 'lucide-react'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/hooks/use-auth'
import { toast } from 'sonner'

const sidebarItems = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    icon: LayoutDashboard,
    href: '/dashboard'
  },
  {
    id: 'custos',
    label: 'Custos',
    icon: DollarSign,
    href: '/dashboard/custos'
  },
  {
    id: 'faturamento',
    label: 'Faturamento',
    icon: Calculator,
    href: '/dashboard/faturamento'
  },
  {
    id: 'precos',
    label: 'Preços',
    icon: TrendingUp,
    href: '/dashboard/precos'
  }
]

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const { user, logout } = useAuth()
  const router = useRouter()

  const handleLogout = () => {
    logout()
    toast.success('Logout realizado com sucesso!')
    router.push('/auth')
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Sidebar para mobile */}
      <div className={`fixed inset-0 z-50 lg:hidden ${sidebarOpen ? 'block' : 'hidden'}`}>
        <div className="fixed inset-0 bg-black/50" onClick={() => setSidebarOpen(false)} />
        <div className="fixed left-0 top-0 h-full w-72 bg-white shadow-xl">
          <div className="p-6 border-b border-gray-100">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 bg-account-primary rounded-lg flex items-center justify-center">
                  <Calculator className="w-4 h-4 text-white" />
                </div>
                <h2 className="text-lg font-semibold text-gray-900">Sistema Financeiro</h2>
              </div>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setSidebarOpen(false)}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          </div>
          <nav className="px-4 pb-6 pt-4">
            {sidebarItems.map((item) => (
              <Button
                key={item.id}
                variant="ghost"
                className="w-full justify-start mb-2 h-11 text-gray-700 hover:text-account-primary hover:bg-account-light"
                onClick={() => {
                  router.push(item.href)
                  setSidebarOpen(false)
                }}
              >
                <item.icon className="mr-3 h-5 w-5" />
                {item.label}
              </Button>
            ))}
          </nav>
        </div>
      </div>

      {/* Sidebar desktop */}
      <div className="hidden lg:fixed lg:inset-y-0 lg:left-0 lg:z-50 lg:block lg:w-72 lg:overflow-y-auto">
        <div className="flex h-16 shrink-0 items-center px-6 border-b border-gray-200 bg-white">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 bg-account-primary rounded-lg flex items-center justify-center">
              <Calculator className="w-4 h-4 text-white" />
            </div>
            <h1 className="text-lg font-bold text-gray-900">Sistema Financeiro</h1>
          </div>
        </div>
        <nav className="px-4 pb-6 pt-6 bg-white h-full">
          {sidebarItems.map((item) => (
            <Button
              key={item.id}
              variant="ghost"
              className="w-full justify-start mb-2 h-11 text-gray-700 hover:text-account-primary hover:bg-account-light"
              onClick={() => router.push(item.href)}
            >
              <item.icon className="mr-3 h-5 w-5" />
              {item.label}
            </Button>
          ))}
        </nav>
      </div>

      {/* Main content */}
      <div className="lg:pl-72">
        {/* Top bar */}
        <div className="sticky top-0 z-40 flex h-16 shrink-0 items-center gap-x-4 border-b border-gray-200 bg-white px-4 shadow-sm sm:gap-x-6 sm:px-6 lg:px-8">
          <Button
            variant="ghost"
            size="icon"
            className="lg:hidden"
            onClick={() => setSidebarOpen(true)}
          >
            <Menu className="h-5 w-5" />
          </Button>

          <div className="flex flex-1 gap-x-4 self-stretch lg:gap-x-6">
            <div className="flex flex-1 items-center">
              <h2 className="text-lg font-semibold text-gray-900">
                Dashboard Financeiro
              </h2>
            </div>
            
            <div className="flex items-center gap-x-4">
              <div className="hidden sm:block text-right">
                <p className="text-sm font-medium text-gray-900">{user?.email}</p>
                <p className="text-xs text-gray-500">
                  {user?.role === 'ADMIN' ? 'Administrador' : user?.role === 'USER' ? 'Usuário' : user?.role}
                </p>
              </div>
              <Button
                variant="ghost"
                size="icon"
                onClick={handleLogout}
                title="Sair"
                className="text-gray-500 hover:text-account-primary"
              >
                <LogOut className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>

        {/* Page content */}
        <main className="py-6">
          <div className="px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
            {children}
          </div>
        </main>
      </div>
    </div>
  )
}

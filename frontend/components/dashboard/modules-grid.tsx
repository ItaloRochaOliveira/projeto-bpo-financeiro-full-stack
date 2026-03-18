'use client'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { FileText, BarChart, Users, Settings } from 'lucide-react'

interface Module {
  id: string
  title: string
  description: string
  icon: any
  enabled: boolean
  color: string
}

const modules: Module[] = [
  {
    id: 'pdf',
    title: 'Gerador de PDF',
    description: 'Crie e gerencie documentos PDF',
    icon: FileText,
    enabled: true,
    color: 'bg-medical-blue',
  },
  {
    id: 'reports',
    title: 'Relatórios',
    description: 'Visualize relatórios e estatísticas',
    icon: BarChart,
    enabled: false,
    color: 'bg-gray-400',
  },
  {
    id: 'users',
    title: 'Usuários',
    description: 'Gerencie usuários e permissões',
    icon: Users,
    enabled: false,
    color: 'bg-gray-400',
  },
  {
    id: 'settings',
    title: 'Configurações',
    description: 'Configure o sistema',
    icon: Settings,
    enabled: false,
    color: 'bg-gray-400',
  },
]

interface ModulesGridProps {
  onModuleClick: (moduleId: string) => void
}

export function ModulesGrid({ onModuleClick }: ModulesGridProps) {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-6 sm:mb-8">
      {modules.map((module) => {
        const Icon = module.icon
        return (
          <Card
            key={module.id}
            className={`cursor-pointer transition-all hover:shadow-lg ${
              !module.enabled ? 'opacity-50 cursor-not-allowed' : ''
            }`}
            onClick={() => module.enabled && onModuleClick(module.id)}
          >
            <CardHeader className="pb-3 sm:pb-4">
              <div className={`w-10 h-10 sm:w-12 sm:h-12 ${module.color} rounded-lg flex items-center justify-center mb-2 sm:mb-3`}>
                <Icon className="w-5 h-5 sm:w-6 sm:h-6 text-white" />
              </div>
              <CardTitle className="text-base sm:text-lg">{module.title}</CardTitle>
              <CardDescription className="text-sm sm:text-base">{module.description}</CardDescription>
            </CardHeader>
          </Card>
        )
      })}
    </div>
  )
}

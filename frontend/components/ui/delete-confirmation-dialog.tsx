'use client'

import { Button } from '@/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'

interface DeleteConfirmationDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  title?: string
  description: string
  onConfirm: () => void
  isLoading?: boolean
}

export function DeleteConfirmationDialog({
  open,
  onOpenChange,
  title = "Confirmar Exclusão",
  description,
  onConfirm,
  isLoading = false
}: DeleteConfirmationDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="account-card border-0 shadow-2xl">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold text-gray-900">
            {title}
          </DialogTitle>
        </DialogHeader>
        <div className="space-y-4">
          <p className="text-gray-600">
            {description}
          </p>
          <p className="text-red-600 text-sm font-medium">
            ⚠️ Esta ação não pode ser desfeita.
          </p>
        </div>
        <div className="flex gap-3 pt-4">
          <Button 
            variant="outline" 
            className="account-btn-secondary flex-1"
            onClick={() => onOpenChange(false)}
            disabled={isLoading}
          >
            Cancelar
          </Button>
          <Button 
            className="account-btn-primary bg-red-600 hover:bg-red-700 flex-1"
            onClick={onConfirm}
            disabled={isLoading}
          >
            {isLoading ? 'Excluindo...' : 'Excluir'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}

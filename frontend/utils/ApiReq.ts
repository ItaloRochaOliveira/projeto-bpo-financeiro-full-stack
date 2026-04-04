import axios, { AxiosRequestConfig } from "axios";
import { toast } from "sonner";

export async function apiReq(url: string, config: AxiosRequestConfig = {}) {
  try {
    const logMessage = `=== API REQUEST START ===\nURL: ${url}\nConfig: ${JSON.stringify(config, null, 2)}\n`;
    console.log(logMessage);
    
    const response = await axios({
      url,
      ...config
    });

    const responseLog = `Response status: ${response.status}\nResponse data: ${JSON.stringify(response.data, null, 2)}\n=== API REQUEST END ===\n`;
    console.log(responseLog);
    
    return response;
  } catch (error: any) {
    const errorLog = `=== API ERROR START ===\nError: ${error}\nError status: ${error.response?.status}\nError data: ${JSON.stringify(error.response?.data, null, 2)}\n=== API ERROR END ===\n`;
    console.error(errorLog);
    
    // Verificar se o erro é de token expirado
    if (error.response?.status === 401) {
      const errorData = error.response.data;
      
      // Se for token expirado, limpar localStorage e redirecionar
      if (errorData?.error === 'TOKEN_EXPIRED') {
        toast.error('Sua sessão expirou. Faça login novamente.');
        
        // Limpar dados de autenticação
        localStorage.removeItem('token');
        localStorage.removeItem('isAuthenticated');
        localStorage.removeItem('user');
        
        // Redirecionar para login
        window.location.href = '/auth';
        return;
      }
      
      // Outros erros 401
      toast.error('Sessão expirada. Faça login novamente.');
      localStorage.removeItem('token');
      localStorage.removeItem('isAuthenticated');
      localStorage.removeItem('user');
      window.location.href = '/auth';
      return;
    }
    
    // Mostrar toast com mensagem de erro amigável
    if (error.response?.status === 400) {
      // Para erro 400, mostrar a mensagem específica do backend
      const errorMessage = error.response?.data || 'Dados inválidos. Verifique as informações.';
      toast.error(errorMessage);
    } else if (error.response?.status === 403) {
      toast.error('Acesso negado. Verifique suas permissões.');
    } else if (error.response?.status === 404) {
      toast.error('Recurso não encontrado.');
    } else if (error.response?.status >= 500) {
      toast.error('Erro no servidor. Tente novamente mais tarde.');
    } else {
      toast.error('Erro na conexão. Verifique sua internet.');
    }
    
    throw error;
  }
}
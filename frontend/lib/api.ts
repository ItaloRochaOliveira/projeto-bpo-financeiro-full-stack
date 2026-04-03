import axios from 'axios'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3006/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/auth'
    }
    return Promise.reject(error)
  }
)

export interface ApiResponse<T> {
        status: string,
        message: {
            code: number,
            message: T,
        },
    }

export interface Users {
  id: string
  name: string | null
  email: string
  password: string | null
  role: string | null
  createdAt: string | null
  updatedAt: string | null
  deleted: boolean | null
  deletedAt: string | null
}

export interface LoginData {
  email: string
  password: string
}   

export interface LoginResponse {
  token: string
  userId: string
  email: string
  name: string
  role: string
}

export interface SignupData {
  name: string
  email: string
  password: string
}

export interface BoletoData {
  id: string
  nomeEmpresa: string | null
  cpfCnpj: string | null
  endereco: string | null
  descricaoReferencia: string | null
  valor: number | null
  vencimento: Date | null
  createdAt: string | null
  updatedAt: string | null
  deleted: boolean
  deleted_at: string | null
  userId: string
}

export interface CreateBoletoData {
  nomeEmpresa: string
  cpfCnpj: string
  endereco: string
  descricaoReferencia: string
  valor: number
  vencimento: string
}

export interface BoletoListResponse {
  message: string
  boletos: BoletoData[]
  total: number
  page: number
  limit: number
}
export interface Boleto {
  id: string
  nomeEmpresa: string
  cpfCnpj: string
  endereco: string
  descricaoReferencia: string
  valor: number
  vencimento: string
  createdAt: string
  updatedAt: string
}

export const authAPI = {
  login: async (data: LoginData): Promise<LoginResponse> => {
    const response = await api.post('/auth/login', data)
    return response.data
  },

  signup: async (data: SignupData): Promise<LoginResponse> => {
    const response = await api.post('/auth/signup', data)
    return response.data
  },
}

export const boletoAPI = {
  list: async (): Promise<BoletoListResponse> => {
    const response = await api.get('/boleto')
    return response.data
  },

  get: async (id: string): Promise<ApiResponse<BoletoData>> => {
    const response = await api.get(`/boleto/${id}`)
    return response.data
  },

  create: async (data: CreateBoletoData): Promise<ApiResponse<BoletoData>> => {
    const response = await api.post('/boleto/create', data)
    return response.data
  },

  generatePdf: async (id: string): Promise<Blob> => {
    const response = await api.get(`/boleto/${id}/pdf`, {
      responseType: 'blob',
    })
    return response.data
  },

  generateAllPdf: async (): Promise<Blob> => {
    const response = await api.get('/boleto/pdf/all', {
      responseType: 'blob',
    })
    return response.data
  },
}

export default api

import axios, { AxiosError } from 'axios'
import { useAuthStore } from './stores/auth'
import type { ApiErrorData } from './types'

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL ?? '/api' })

api.interceptors.request.use(cfg => {
  const token = useAuthStore().token
  if (token) cfg.headers.Authorization = `Bearer ${token}`
  return cfg
})

export const isApiError = (e: unknown): e is AxiosError<ApiErrorData> =>
  axios.isAxiosError(e);

export default api

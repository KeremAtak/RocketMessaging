import { defineStore } from 'pinia'
import api from '../api'

type User = { id: number; username: string }

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null as User | null,
    loading: false,
    error: '' as string | null,
  }),
  actions: {
    setToken(t: string) {
      this.token = t
      localStorage.setItem('token', t)
    },
    clear() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
    },
    async register(username: string, password: string) {
      this.loading = true; this.error = null
      try {
        const { data } = await api.post('/auth/register', { username, password })
        this.setToken(data.token)
        this.user = data.user
        return data.user
      } catch (e: any) {
        this.error = e?.response?.data?.error || 'Registration failed'
        throw e
      } finally { this.loading = false }
    },
    async login(username: string, password: string) {
      this.loading = true; this.error = null
      try {
        const { data } = await api.post('/auth/login', { username, password })
        this.setToken(data.token)
        this.user = data.user
        return data.user
      } catch (e: any) {
        this.error = e?.response?.data?.error || 'Login failed'
        throw e
      } finally { this.loading = false }
    },
  },
  getters: {
    isAuthed: (s) => !!s.token
  }
})

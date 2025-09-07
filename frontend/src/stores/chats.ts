import { defineStore } from 'pinia'
import api from '../api'

export type Chat = { id:number; kind:'group'|'direct'; title?:string|null; createdAt?:string }
export type Message = { id:number; chatId:number; senderId:number; body:string; createdAt?:string; username:string }

export const useChatStore = defineStore('chats', {
  state: () => ({
    chats: [] as Chat[],
    messages: new Map<number, Message[]>(), // chatId -> messages DESC from server
    loadingList: false,
    loadingMsgs: new Set<number>(),
    error: '' as string|undefined,
  }),
  actions: {
    async fetchChats() {
      this.loadingList = true
      try {
        const { data } = await api.get<Chat[]>('/chats', { params: { limit: 100, offset: 0 } })
        this.chats = data
      } finally {
        this.loadingList = false
      }
    },
    async fetchMessages(chatId: number) {
      if (!chatId) return
      this.loadingMsgs.add(chatId)
      try {
        const { data } = await api.get<Message[]>(`/chats/${chatId}/messages`)
        this.messages.set(chatId, data) // assume newest-first (DESC)
      } finally {
        this.loadingMsgs.delete(chatId)
      }
    },
    async sendMessage(chatId: number, body: string) {
      await api.post(`/chats/${chatId}/messages`, { 'message-body': body })
      await this.fetchMessages(chatId)
    },
  },
  getters: {
    byId: (s) => (id?: number) => s.chats.find(c => c.id === id),
    msgsAsc: (s) => (id?: number) => {
      const arr = id ? s.messages.get(id) ?? [] : []
      return [...arr].reverse() // newest at bottom
    },
  },
})

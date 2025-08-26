import { defineStore } from 'pinia'
import api from '../api'

type Chat = { id: number; kind: 'group'|'direct'; title?: string|null }
type Participant = { chat_id: number; user_id: number }
type NewChatPayload = { title: string; user_ids: number[] } // backend expects snake_case? adapt as needed

export const useChatStore = defineStore('chats', {
  state: () => ({
    chats: [] as Chat[],
    current: null as Chat|null,
    participants: [] as Participant[],
    loading: false,
    error: '' as string|undefined,
  }),
  actions: {
    async fetchChats() {
      this.loading = true
      try { const { data } = await api.get<Chat[]>('/chats')
            this.chats = data }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      catch (e: any) { this.error = e?.response?.data?.error || 'Failed to load chats' }
      finally { this.loading = false }
    },
    async createChat(payload: NewChatPayload) {
      this.loading = true
      try {
        const { data } = await api.post('/chats/new-chat', payload)
        // assuming response body: { chat: {...}, participants: [...] }
        this.current = data.chat
        this.participants = data.participants
        this.chats.unshift(data.chat)
        return data
      } finally { this.loading = false }
    },
  },
})

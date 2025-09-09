import { defineStore } from 'pinia'
import api, { isApiError } from '../api'
import type { Chat } from '@/types';
import type { Message } from 'postcss';

export const useChatStore = defineStore('chats', {
  state: () => ({
    chats: [] as Chat[],
    messages: new Map<number, Message[]>(), // chatId -> messages DESC from server
    loadingList: false,
    loadingMsgs: new Set<number>(),
    creatingChat: false,
    creatingChatError: '' as string|null,
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
    async createChat(payload: { title: string; userIds: number[] }) {
      this.creatingChat = true;
      this.creatingChatError = null
      try {
        const { data } = await api.post('/chats/new-chat', payload)
        if (data?.chat) {
          this.chats = [data.chat, ...this.chats.filter(c => c.id !== data.chat.id)]
        }
        return data
      } catch (e: unknown) {
        this.creatingChatError = isApiError(e) && e.response
        ? (e.response.data?.error ?? 'Failed to create chat')
        : 'Failed to create chat'
        throw e
      } finally { this.creatingChat = false }
    },
    async searchUsers(q: string) {
      const { data } = await api.get('/users', { params: { q } })
      return data as { id:number; username:string }[]
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

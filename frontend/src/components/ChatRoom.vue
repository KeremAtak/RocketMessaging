<!-- eslint-disable @typescript-eslint/no-explicit-any -->
<!-- src/components/ChatRoom.vue -->
<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useChatStore } from '../stores/chats'
import { useSocket } from '../composables/useSocket'

const route = useRoute()
const chats = useChatStore()
const chat = computed(() => chats.byId(chatId.value))
const { on, send } = useSocket()

const chatId = computed(() => Number(route.params.id))
const messagesAsc = computed(() => chats.msgsAsc(chatId.value))
const input = ref('')
const typingUsers = ref<Set<number>>(new Set())
let typingTimer: number | null = null

function scrollBottom(el: HTMLElement | null) {
  nextTick(() => { if (el) el.scrollTop = el.scrollHeight })
}

const listRef = ref<HTMLElement | null>(null)

onMounted(async () => {
  if (chatId.value) await chats.fetchMessages(chatId.value)
  scrollBottom(listRef.value)
  // subscribe to WS events for this chat
  send({ type: 'subscribe', chatId: chatId.value })

  // handle events
  on('message:new', (d: any) => {
    if (d.chatId === chatId.value) {
      chats.fetchMessages(chatId.value).then(() => scrollBottom(listRef.value))
    }
  })
  on('typing', (d: any) => {
    if (d.chatId !== chatId.value) {
      return
    }
    const id = d.userId as number
    if (d.isTyping) {
      typingUsers.value.add(id)
    } else {
      typingUsers.value.delete(id)
    }
    typingUsers.value = new Set(typingUsers.value) // trigger change
  })
  on('chat:updated', (e) => {
    chats.bumpChatPreview(e.chatId, {
      body: e.lastMessageBody ?? null,
      username: e.lastSenderUsername ?? null,
      at: e.lastMessageAt ?? null,
  })
})
})

watch(() => chatId.value, (newId, oldId) => {
  if (oldId) send({ type: 'unsubscribe', chatId: oldId }, { queueIfDisconnected: true })
  if (newId) send({ type: 'subscribe', chatId: newId }, { queueIfDisconnected: true })
}, { immediate: true })

function sendTyping(on: boolean) {
  if (!chatId.value) return
  send({ type: 'typing', chatId: chatId.value, isTyping: on }, { queueIfDisconnected: false })
}

function onInput() {
  sendTyping(true)
  if (typingTimer) clearTimeout(typingTimer)
  typingTimer = window.setTimeout(() => sendTyping(false), 1200)
}

async function sendMsg() {
  const text = input.value.trim()
  if (!text || !chatId.value) return
  await chats.sendMessage(chatId.value, text)
  input.value = ''
  sendTyping(false)
  scrollBottom(listRef.value)
}
</script>

<template>
  <div class="flex flex-col h-full">
    <div class="h-14 border-b px-4 flex items-center">
       <div class="font-semibold truncate">
          {{ chat?.title || '(untitled)' }}
       </div>

    </div>

    <div ref="listRef" class="flex-1 overflow-auto p-4 space-y-2">
      <div v-for="m in messagesAsc" :key="m.id" class="p-2 border rounded">
        <div class="text-xs opacity-60">{{ m.username }}</div>
        <div>{{ m.body }}</div>
      </div>
      <div v-if="typingUsers.size" class="text-sm opacity-60 italic">Someone is typing…</div>
    </div>

    <form @submit.prevent="sendMsg" class="p-3 border-t flex gap-2">
      <input v-model="input" @input="onInput"
             class="flex-1 border rounded px-3 py-2" placeholder="Type a message…" />
      <button class="px-4 py-2 rounded bg-black text-white">Send</button>
    </form>
  </div>
</template>

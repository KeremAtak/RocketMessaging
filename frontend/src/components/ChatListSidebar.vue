<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '../stores/chats'
import type { CreateChatResponse } from '@/types'
import NewChatModal from './NewChatModal.vue'

const route = useRoute()
const router = useRouter()
const chats = useChatStore()
const q = ref('')

onMounted(() => { if (!chats.chats.length) chats.fetchChats() })
const items = computed(() => chats.chats)

function openChat(id:number) { router.push(`/chats/${id}`) }

// modal state
const showNew = ref(false)
function onCreated(res:CreateChatResponse) {
  showNew.value = false
  if (res?.chat?.id) router.push(`/chats/${res.chat.id}`)
}
</script>

<template>
  <div class="p-3 flex flex-col gap-3">
    <button class="px-3 py-2 rounded bg-black text-white" @click="showNew = true">New chat</button>
    <input v-model="q" placeholder="Search chats" class="w-full border rounded px-3 py-2" />
    <ul class="space-y-1 overflow-y-auto">
      <li v-for="c in items.filter(c => (c.title||'').toLowerCase().includes(q.toLowerCase()))"
          :key="c.id"
          @click="openChat(c.id)"
          class="p-3 rounded cursor-pointer hover:bg-gray-50"
          :class="Number(route.params.id) === c.id ? 'bg-gray-100' : ''">
        <div class="font-medium truncate">{{ c.title || '(untitled)' }}</div>
        <div class="text-xs opacity-60 truncate">
          <span v-if="c.lastSenderUsername" class="font-medium">{{ c.lastSenderUsername }}</span>
          <span v-if="c.lastSenderUsername && c.lastMessageBody"> • </span>
          <span v-if="c.lastMessageBody">{{ c.lastMessageBody }}</span>
          <span v-else>No messages yet.</span>
        </div>
      </li>
    </ul>

    <NewChatModal v-if="showNew" @close="showNew=false" @created="onCreated" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useChatStore } from '../stores/chats'

const route = useRoute()
const chats = useChatStore()

const chatId = computed(() => Number(route.params.id))
const chat = computed(() => chats.byId(chatId.value))
const messagesAsc = computed(() => chats.msgsAsc(chatId.value))
const loading = computed(() => chats.loadingMsgs.has(chatId.value))

const input = ref('')
const listRef = ref<HTMLElement|null>(null)
const scrollToBottom = () => nextTick(() => {
  if (listRef.value) listRef.value.scrollTop = listRef.value.scrollHeight
})

watch(messagesAsc, scrollToBottom)

onMounted(async () => {
  if (chatId.value) await chats.fetchMessages(chatId.value)
  scrollToBottom()
})

async function send() {
  const text = input.value.trim()
  if (!text || !chatId.value) return
  await chats.sendMessage(chatId.value, text)
  input.value = ''
  scrollToBottom()
}
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- header -->
    <div class="h-14 border-b px-4 flex items-center">
      <div class="font-semibold truncate">
        {{ chat?.title || '(untitled)' }}
      </div>
    </div>

    <!-- messages -->
    <div ref="listRef" class="flex-1 overflow-auto p-4 space-y-2">
      <div v-if="loading" class="text-sm opacity-60">Loading…</div>
      <template v-else>
        <div v-for="m in messagesAsc" :key="m.id" class="p-2 border rounded">
          <div class="text-xs">{{ m.username }}</div>
          <div>{{ m.body }}</div>
        </div>
        <div v-if="!messagesAsc.length" class="text-sm opacity-60">No messages yet.</div>
      </template>
    </div>

    <!-- composer -->
    <form @submit.prevent="send" class="p-3 border-t flex gap-2">
      <input v-model="input" class="flex-1 border rounded px-3 py-2" placeholder="Type a message…" />
      <button class="px-4 py-2 rounded bg-black text-white">Send</button>
    </form>
  </div>
</template>

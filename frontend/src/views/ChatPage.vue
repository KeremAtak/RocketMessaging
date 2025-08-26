<script setup lang="ts">
import { onMounted, ref, computed, nextTick, watch } from 'vue'
import api from '../api'
import { useRoute } from 'vue-router'

const route = useRoute()
const chatId = Number(route.params.id)
const messages = ref<{ id:number; body:string; senderId:number; createdAt:string }[]>([])
const body = ref('')
async function load() {
  const { data } = await api.get(`/chats/${chatId}/messages`)
  messages.value = data
}
async function send() {
  if (!body.value.trim()) return
  await api.post(`/chats/${chatId}/messages`, { 'message-body': body.value }) // match backend schema
  body.value = ''
  await load()
}

// show newest at bottom without mutating original
const messagesAsc = computed(() => [...messages.value].reverse())

const listRef = ref<HTMLElement | null>(null)

const scrollToBottom = () => nextTick(() => {
  if (listRef.value) listRef.value.scrollTop = listRef.value.scrollHeight
})

onMounted(async () => { await load(); scrollToBottom() })
watch(messagesAsc, scrollToBottom) // scroll on new data

</script>

<template>
  <div class="space-y-4">
    <ul class="space-y-2">
      <li v-for="m in messagesAsc" :key="m.id" class="p-2 border rounded">
        <div class="text-sm opacity-60">#{{ m.id }} — user {{ m.senderId }} - sent {{ m.createdAt }}</div>
        <div>{{ m.body }}</div>
      </li>
    </ul>
    <form @submit.prevent="send" class="flex gap-2">
      <input v-model="body" class="flex-1 border rounded px-3 py-2" placeholder="Type a message…" />
      <button class="px-4 py-2 rounded bg-black text-white">Send</button>
    </form>
  </div>
</template>

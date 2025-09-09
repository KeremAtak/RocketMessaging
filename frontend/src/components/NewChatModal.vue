<script setup lang="ts">
import { ref, watch } from 'vue'
import { useChatStore } from '../stores/chats'
import type { CreateChatResponse, User } from '../types'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'created', payload: CreateChatResponse): void
}>()

const chats = useChatStore()
const title = ref('')
const search = ref('')
const results = ref<User[]>([])
const selected = ref<User[]>([])
const loading = ref(false)
const error = ref('')

watch(search, async (q) => {
  if (!q.trim()) { results.value = []; return }
  try { results.value = await chats.searchUsers(q) }
  catch { results.value = [] }
})

function addUser(u: User) {
  if (!selected.value.find(x => x.id === u.id)) selected.value.push(u)
}

async function createChat() {
  error.value = ''
  if (!title.value.trim()) { error.value = 'Title required'; return }
  loading.value = true
  try {
    const res = await chats.createChat({
      title: title.value.trim(),
      userIds: selected.value.map(s => s.id),
    })
    emit('created', res)
  } catch {
    error.value = chats.creatingChatError || 'Failed'
  } finally { loading.value = false }
}
</script>

<template>
  <div class="fixed inset-0 bg-black/30 flex items-center justify-center">
    <div class="bg-white rounded-xl w-full max-w-md p-4 space-y-3">
      <div class="flex justify-between items-center">
        <h3 class="text-lg font-semibold">New chat</h3>
        <button @click="$emit('close')" class="text-sm">Close</button>
      </div>

      <label class="block text-sm">Title</label>
      <input v-model="title" class="w-full border rounded px-3 py-2" placeholder="Team Rocket" />

      <label class="block text-sm mt-2">Add participants</label>
      <input v-model="search" class="w-full border rounded px-3 py-2" placeholder="Search usernames…" />

      <div class="max-h-40 overflow-auto border rounded p-2 space-y-1" v-if="results.length">
        <button v-for="u in results" :key="u.id" @click="addUser(u)"
                class="w-full text-left px-2 py-1 rounded hover:bg-gray-100">
          {{ u.username }} <span class="opacity-60">(#{{ u.id }})</span>
        </button>
      </div>

      <div v-if="selected.length" class="flex flex-wrap gap-2">
        <span v-for="u in selected" :key="u.id" class="px-2 py-1 bg-gray-100 rounded text-sm">
          {{ u.username }}
        </span>
      </div>

      <div class="flex gap-2 mt-2">
        <button :disabled="loading" @click="createChat"
                class="px-4 py-2 rounded bg-black text-white disabled:opacity-50">
          {{ loading ? 'Creating…' : 'Create chat' }}
        </button>
        <span v-if="error" class="text-red-600 text-sm">{{ error }}</span>
      </div>
    </div>
  </div>
</template>
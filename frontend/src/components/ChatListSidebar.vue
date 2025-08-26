<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '../stores/chats'

const route = useRoute()
const router = useRouter()
const chats = useChatStore()
const q = ref('')

onMounted(() => { if (!chats.chats.length) chats.fetchChats() })

const filtered = computed(() => {
  const s = q.value.trim().toLowerCase()
  if (!s) return chats.chats
  return chats.chats.filter(c => (c.title ?? '').toLowerCase().includes(s))
})

function openChat(id:number) { router.push(`/chats/${id}`) }
</script>

<template>
  <div class="p-3">
    <ul class="divide-y">
      <li v-for="c in filtered" :key="c.id"
          @click="openChat(c.id)"
          class="py-3 px-2 cursor-pointer rounded hover:bg-gray-50"
          :class="Number(route.params.id) === c.id ? 'bg-gray-100' : ''">
        <div class="font-medium truncate">{{ c.title || '(untitled)' }}</div>
        <div class="text-xs opacity-60">{{ c.kind }}</div>
      </li>
    </ul>
  </div>
</template>

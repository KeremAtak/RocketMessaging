<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chats'
import ChatListSidebar from '@/components/ChatListSidebar.vue'
import ChatRoom from '@/components/ChatRoom.vue'

const route = useRoute()
const router = useRouter()
const chats = useChatStore()

onMounted(async () => {
  await chats.fetchChats()
  // if no :id, pick first chat (if any)
  if (!route.params.id && chats.chats.length) {
    router.replace(`/chats/${chats.chats[0].id}`)
  }
})
// (optional) if navigating to a chat, ensure messages are loaded
watch(() => route.params.id, (id) => {
  const cid = Number(id)
  if (cid) chats.fetchMessages(cid)
}, { immediate: true })
</script>

<template>
  <div class="h-screen grid grid-cols-[320px_1fr]">
    <!-- Sidebar -->
    <aside class="border-r overflow-y-auto">
      <ChatListSidebar />
    </aside>

    <!-- Main chat area -->
    <main class="flex flex-col">
      <ChatRoom />
    </main>
  </div>
</template>
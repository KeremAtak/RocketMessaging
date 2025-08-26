<script setup lang="ts">
import { onMounted } from 'vue'
import { useChatStore } from '../stores/chats'

const chats = useChatStore()
onMounted(() => chats.fetchChats())
</script>

<template>
  <div class="space-y-4">
    <NewChatDialog @created="(c: { chat: { id: any } }) => $router.push(`/chats/${c.chat.id}`)" />

    <ul class="divide-y">
      <li v-for="c in chats.chats" :key="c.id" class="py-3 flex justify-between">
        <router-link :to="`/chats/${c.id}`" class="font-medium">
          {{ c.title || '(untitled)' }}
        </router-link>
        <span class="text-sm opacity-60">{{ c.kind }}</span>
      </li>
    </ul>
  </div>
</template>
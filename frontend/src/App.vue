<script setup lang="ts">
import { useAuthStore } from './stores/auth'
import { useRouter } from 'vue-router'
const auth = useAuthStore()
const router = useRouter()
function logout() { auth.clear(); router.push('/auth') }
</script>

<template>
  <div class="min-h-screen">
    <header class="p-4 border-b flex items-center justify-between">
      <h1 class="text-lg font-semibold">RocketMessaging</h1>
      <div v-if="auth.isAuthed" class="flex items-center gap-3">
        <span class="text-sm opacity-70">Logged in</span>
        <button class="px-3 py-1.5 rounded bg-gray-200" @click="logout">Sign out</button>
      </div>
    </header>
    <router-view />
  </div>
</template>

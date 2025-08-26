<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const tab = ref<'login' | 'register'>('login')

const u1 = ref('')  // username
const p1 = ref('')  // password
const u2 = ref('')
const p2 = ref('')

async function doLogin() {
  await auth.login(u1.value.trim(), p1.value)
  router.push('/chats')
}
async function doRegister() {
  await auth.register(u2.value.trim(), p2.value)
  router.push('/chats')
}
</script>

<template>
  <div class="max-w-md mx-auto mt-10 p-6 border rounded-2xl space-y-6">
    <div class="flex gap-2">
      <button class="px-3 py-2 rounded" :class="tab==='login' ? 'bg-black text-white' : 'bg-gray-100'"
              @click="tab='login'">Login</button>
      <button class="px-3 py-2 rounded" :class="tab==='register' ? 'bg-black text-white' : 'bg-gray-100'"
              @click="tab='register'">Register</button>
    </div>

    <form v-if="tab==='login'" class="space-y-3" @submit.prevent="doLogin">
      <div>
        <label class="block text-sm">Username</label>
        <input v-model="u1" class="w-full border rounded px-3 py-2" autocomplete="username" />
      </div>
      <div>
        <label class="block text-sm">Password</label>
        <input v-model="p1" type="password" class="w-full border rounded px-3 py-2" autocomplete="current-password" />
      </div>
      <button :disabled="auth.loading" class="px-4 py-2 rounded bg-black text-white w-full">
        {{ auth.loading ? 'Signing in…' : 'Sign in' }}
      </button>
      <p v-if="auth.error" class="text-red-600 text-sm">{{ auth.error }}</p>
    </form>

    <form v-else class="space-y-3" @submit.prevent="doRegister">
      <div>
        <label class="block text-sm">Username</label>
        <input v-model="u2" class="w-full border rounded px-3 py-2" autocomplete="username" />
      </div>
      <div>
        <label class="block text-sm">Password</label>
        <input v-model="p2" type="password" class="w-full border rounded px-3 py-2" autocomplete="new-password" />
      </div>
      <button :disabled="auth.loading" class="px-4 py-2 rounded bg-black text-white w-full">
        {{ auth.loading ? 'Creating…' : 'Create account' }}
      </button>
      <p v-if="auth.error" class="text-red-600 text-sm">{{ auth.error }}</p>
    </form>
  </div>
</template>
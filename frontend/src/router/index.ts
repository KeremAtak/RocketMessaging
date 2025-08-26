import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthView from '@/views/AuthView.vue'
import ChatList from '@/views/ChatList.vue'
import ChatPage from '@/views/ChatPage.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/chats' },
    { path: '/auth', component: AuthView, meta: { public: true } },
    { path: '/chats', component: ChatList },
    { path: '/chats/:id', component: ChatPage, props: true },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthed) return { path: '/auth' }
  return true
})

export default router;
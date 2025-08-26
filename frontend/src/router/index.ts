import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import AuthView from '@/views/AuthView.vue'
import ChatWorkspace from '@/views/ChatWorkspace.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/chats' },
    { path: '/auth', component: AuthView, meta: { public: true } },
    { path: '/chats/:id?', component: ChatWorkspace, props: true }, // id optional
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (!to.meta.public && !auth.isAuthed) return { path: '/auth' }
  return true
})

export default router;
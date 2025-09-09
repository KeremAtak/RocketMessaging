import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'


// https://vite.dev/config/
export default defineConfig((env) => {
  const isProd = env.mode === 'production'

  return {
    plugins: [
        vue(),
        vueJsx(),
        vueDevTools(),
        tailwindcss(),
      ],
      resolve: {
        alias: {
          '@': fileURLToPath(new URL('./src', import.meta.url))
        },
      },
      server: {
        port: 5173,
        proxy: {
          // anything starting with /api will be proxied to the backend
          '/api': {
            target: 'http://localhost:3000',
            changeOrigin: true,
            // if your backend doesn’t actually include /api in routes, uncomment:
            // rewrite: (p) => p.replace(/^\/api/, ''),
          },
          '/ws':  {
            target: 'http://localhost:3000',
            ws: true,
            changeOrigin: true
          },
        },
        build: {
          outDir: 'dist',
          sourcemap: !isProd
        }
      },
  }
})

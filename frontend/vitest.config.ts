// vitest.config.ts
import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig as defineVitestConfig, configDefaults } from 'vitest/config'
import viteBase from './vite.config'

export default defineVitestConfig(async () => {
  const base =
    typeof viteBase === 'function'
      ? await viteBase({ mode: 'test', command: 'serve' })
      : await viteBase

  return mergeConfig(base, {
    test: {
      environment: 'jsdom',
      exclude: [...configDefaults.exclude, 'e2e/**'],
      root: fileURLToPath(new URL('./', import.meta.url)),
    },
  })
})

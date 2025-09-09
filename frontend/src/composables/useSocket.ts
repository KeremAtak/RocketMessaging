import { onMounted, onUnmounted, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import type { WsServerEvent, WsClientEvent } from '../types/ws'
import { isWsServerEvent } from '../types/ws'

type EventType = WsServerEvent['type']
type Unsub = () => void

export function useSocket() {
  const auth = useAuthStore()
  const ws = ref<WebSocket | null>(null)
  const connected = ref(false)

  // simple pub/sub
  const handlers = new Map<EventType, Set<(e: WsServerEvent) => void>>()
  function on<T extends EventType>(type: T, fn: (e: Extract<WsServerEvent, {type: T}>) => void): Unsub {
    if (!handlers.has(type)) handlers.set(type, new Set())
    const set = handlers.get(type)!
    const wrapped = fn as (e: WsServerEvent) => void
    set.add(wrapped)
    return () => set.delete(wrapped)
  }

  // buffer only important events (not typing)
  const outbox: WsClientEvent[] = []
  let reconnectDelay = 500 // ms, backoff

  function flush() {
    while (ws.value && ws.value.readyState === WebSocket.OPEN && outbox.length) {
      ws.value.send(JSON.stringify(outbox.shift()))
    }
  }

  function connect() {
    if (!auth.token || ws.value) return
    const url = new URL('/ws', window.location.origin)
    url.protocol = url.protocol.replace('http', 'ws')
    url.searchParams.set('token', auth.token)
    const sock = new WebSocket(url.toString())
    ws.value = sock

    sock.onopen = () => {
      connected.value = true
      reconnectDelay = 500
      flush()
    }
    sock.onclose = () => {
      connected.value = false
      ws.value = null
      // try reconnect with capped backoff
      setTimeout(connect, reconnectDelay)
      reconnectDelay = Math.min(reconnectDelay * 2, 8000)
    }
    sock.onerror = () => {
      // errors also lead to onclose; no-op
    }
    sock.onmessage = (ev) => {
      try {
        const data: unknown = JSON.parse(ev.data)
        if (isWsServerEvent(data)) {
          handlers.get(data.type)?.forEach(fn => fn(data))
        }
      } catch { /* ignore */ }
    }
  }

  /**
   * Send an event. If not open:
   *  - when queueIfDisconnected=true, buffer & attempt connect
   *  - otherwise, drop and return false
   */
  function send(ev: WsClientEvent, opts: { queueIfDisconnected?: boolean } = {}): boolean {
    const q = opts.queueIfDisconnected ?? true
    const s = ws.value?.readyState
    if (s === WebSocket.OPEN) {
      ws.value!.send(JSON.stringify(ev))
      return true
    }
    if (q) {
      outbox.push(ev)
      if (!ws.value) connect()
    }
    return false
  }

  onMounted(connect)
  onUnmounted(() => { ws.value?.close() })

  return { on, send, connected }
}

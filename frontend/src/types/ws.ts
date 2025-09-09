import type { Message } from '../types' // reuse your existing shapes

export type ID = number
export type ISO8601 = string

// ---- server → client
export interface WsReady {
  type: 'ws:ready'
  ts: ISO8601
}

export interface WsMessageNew {
  type: 'message:new'
  chatId: ID
  message: Message
  ts: ISO8601
}

export interface WsTyping {
  type: 'typing'
  chatId: ID
  userId: ID
  isTyping: boolean
  ts: ISO8601
}

export interface WsChatUpdated {
  type: 'chat:updated'
  chatId: ID
  kind: 'direct' | 'kind'
  lastMessageBody: string | null
  lastSenderUsername: string | null
  lastMessageAt: ISO8601 | null
}

export type WsServerEvent =
  | WsReady
  | WsMessageNew
  | WsTyping
  | WsChatUpdated

// ---- client → server
export interface WsSubscribe { type: 'subscribe'; chatId: ID }
export interface WsUnsubscribe { type: 'unsubscribe'; chatId: ID }
export interface WsTypingCmd { type: 'typing'; chatId: ID; isTyping: boolean }

export type WsClientEvent =
  | WsSubscribe
  | WsUnsubscribe
  | WsTypingCmd

// runtime guard
export function isWsServerEvent(x: unknown): x is WsServerEvent {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  return typeof x === 'object' && x !== null && 'type' in (x as any)
}
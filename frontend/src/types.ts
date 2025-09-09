export interface User {
    id: number;
    username: string
}

export interface Chat {
    id: number;
    kind: 'group' | 'direct';
    title?: string | null;
    lastMessageAt?: string | null;
    lastMessageBody?: string | null;
    lastSenderUsername?: string | null;
}

export interface Message {
    id: number;
    chatId: number;
    senderId: number;
    body: string;
    createdAt?: string;
    username:string
}

export interface Participant {
    chatId: number;
    userId: number;
}

export interface CreateChatPayload {
    title: string;
    userIds: number[];
}

export interface CreateChatResponse {
    chat: Chat;
    participants: Participant[];
}

export interface ApiErrorData {
    error?: string;
    message?: string;
}
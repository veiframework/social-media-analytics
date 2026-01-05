// stores/chat.js
import { defineStore } from 'pinia'
import { chat, getChatHistory, getSessionList } from '@/api/chat'

export const useChatStore = defineStore('chat', () => {
  // ====== state ======
  const messages = ref([])
  const loading = ref(false)
  const total = ref(0)
  const pageNo = ref(1)
  const pageSize = ref(5)
  const sessions = ref([])
  const currentSession = ref(null)

  // ====== getters (可选) ======
  // 如果需要计算属性，可用 computed(() => ...)

  // ====== actions ======

  // 发送消息
  const sendMessageAction = async ({ message, sessionId }) => {
    loading.value = true
    try {
      const response = await chat(message, sessionId)

      if (response && response.message) {
        return response
      } else if (response && response.response) {
        return {
          message: response.response,
          sessionId: response.sessionId || sessionId
        }
      } else {
        console.warn('无法识别的消息响应格式:', response)
        return null
      }
    } catch (error) {
      console.error('发送消息失败:', error)
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取聊天历史
  const fetchChatHistory = async (sessionId) => {
    loading.value = true
    try {
      if (!sessionId) {
        console.warn('没有选择任何会话')
        messages.value = []
        total.value = 0
        return null
      }

      const response = await getChatHistory(pageNo.value, pageSize.value, sessionId)

      if (response && response.records) {
        messages.value = response.records
        total.value = response.total || 0
      } else if (response && Array.isArray(response)) {
        const formattedRecords = response.map((item, index) => ({
          id: item.id || index + 1,
          userMessage: item.userMessage || item.request || '',
          aiMessage: item.aiMessage || item.response || '',
          createTime: item.createTime || item.timestamp || new Date().toISOString(),
          updateTime: item.updateTime || item.createTime || new Date().toISOString()
        }))
        messages.value = formattedRecords
        total.value = formattedRecords.length
      } else {
        console.warn('无法识别的聊天记录格式:', response)
        messages.value = []
        total.value = 0
      }

      return response
    } catch (error) {
      console.error('获取聊天历史失败:', error)
      messages.value = []
      total.value = 0
      return null
    } finally {
      loading.value = false
    }
  }

  // 获取会话列表
  const fetchSessionList = async () => {
    try {
      const response = await getSessionList(pageNo.value, 100)
      if (response && response.records) {
        sessions.value = response.records

        if (!currentSession.value && response.records.length > 0) {
          currentSession.value = response.records[0]
        }

        return response.records
      } else if (response && Array.isArray(response)) {
        sessions.value = response

        if (!currentSession.value && response.length > 0) {
          currentSession.value = response[0]
        }

        return response
      } else {
        console.warn('无法识别的会话列表格式:', response)
        sessions.value = []
        return []
      }
    } catch (error) {
      console.error('获取会话列表失败:', error)
      return []
    }
  }

  // ====== 返回 state 和 actions ======
  return {
    // state
    messages,
    loading,
    total,
    pageNo,
    pageSize,
    sessions,
    currentSession,

    // actions
    sendMessageAction,
    fetchChatHistory,
    fetchSessionList
  }
})
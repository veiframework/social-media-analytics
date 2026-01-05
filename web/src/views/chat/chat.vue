<template>
  <div class="chat-box">
    <div class="chat-page" @click="handlePageClick">
      <!-- 登录遮罩层 -->
      <div v-if="!hasToken && showLoginMask" class="login-mask" @click="goToLogin">
        <div class="login-mask-content">
          <i class="el-icon-warning-outline"></i>
          <h3>请先登录</h3>
          <p>登录后即可开始聊天</p>
          <el-button type="primary" @click.stop="goToLogin">去登录</el-button>
        </div>
      </div>

      <!-- 左侧对话列表 -->
      <div class="chat-sidebar" :class="{ 'disabled': !hasToken }">


        <div class="sidebar-header">
          <el-button type="primary" size="small" @click="createNewChat" class="rounded-new-chat" :disabled="!hasToken">
            <i class="el-icon-plus"></i>
            <span>开启新对话</span>
          </el-button>
        </div>
        <div class="sidebar-content">
          <div
              v-for="chat in chatList"
              :key="chat.id"
              class="chat-item"
              :class="{ active: currentChatId === chat.id }"
              @click="selectChat(chat.id)"
          >
            <i class="el-icon-chat-dot-round"></i>
            <span class="chat-title">{{ chat.title || '新对话' }}</span>
          </div>
          <div v-if="chatList.length === 0" class="empty-chat-list">
            <p>暂无对话记录</p>
            <el-button type="text" @click="createNewChat" :disabled="!hasToken">创建新对话</el-button>
          </div>
        </div>


      </div>

      <!-- 右侧聊天区域 -->
      <div class="chat-container">
        <div class="chat-header">
          <h2>{{ currentSessionTitle }}</h2>
        </div>
        <div class="chat-messages" ref="messageContainer" @scroll="handleScroll" :key="currentChatId">
          <div v-if="isLoadingMore" class="loading-more">
            <i class="el-icon-loading"></i> 加载更多消息...
          </div>
          <div v-for="message in messages" :key="message.id" class="chat-message">
            <!-- 用户消息 -->
            <div class="message user">
              <img class="message-avatar" alt="" src="../../assets/images/avatar.png"/>
              <div class="message-content-wrapper">
                <div class="message-content">{{ message.userMessage }}</div>
                <div class="message-time">{{ formatTime(message.createTime) }}</div>
              </div>
            </div>

            <!-- AI消息 -->
            <div class="message ai">
              <div class="message-avatar">
                <i class="el-icon-service"></i>
              </div>
              <div class="message-content-wrapper">
                <div class="message-content" v-html="formatAiMessage(message.aiMessage)"></div>
                <div class="message-time">{{ formatTime(message.createTime) }}</div>
              </div>
            </div>
          </div>
          <div v-if="thinking" class="loading-indicator">
            <i class="el-icon-loading"></i> AI正在思考中...
          </div>
          <div v-if="messages.length === 0" class="empty-message">
            <i class="el-icon-chat-dot-round"></i>
            <p>暂无聊天记录</p>
          </div>
        </div>
        <div class="chat-input">
          <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="请输入您的问题..."
              @keyup.enter.native="sendMessage"
              class="rounded-input"
              :disabled="!hasToken"
          ></el-input>
          <el-button type="primary" @click="sendMessage" :loading="thinking" :disabled="!hasToken"
                     class="rounded-button">
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="chat">
import {ref, reactive, computed, onMounted, nextTick} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useChatStore} from '@/store/modules/chat'
import {getToken, removeToken} from '@/utils/auth'
import {getInfo, logout} from '@/api/login'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/dark.css'
// 注意：markdown-it-highlightjs 可能需要额外安装
// import highlightjs from 'markdown-it-highlightjs'

// ====== refs ======
const inputMessage = ref('')
const messages = ref([])
const scrollDistance = 100
const isLoadingMore = ref(false)
const hasMoreMessages = ref(true)
const chatList = ref([])
const currentChatId = ref(null)
const hasToken = ref(false)
const showLoginMask = ref(false)
const messageContainer = ref()
const thinking = ref(false)

// 用户信息
const userInfo = reactive({
  userName: '',
  email: '',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

// ====== store ======
const chatStore = useChatStore()
const {sessions, currentSession} = chatStore // 自动解包 ref

// ====== computed ======
const currentSessionTitle = computed(() => {
  return chatList.value.find(session => session.id === currentChatId.value)?.title || '新对话'
})

// Markdown 渲染函数（不依赖响应式，直接定义）
const getMarkdown = () => {
  const md = new MarkdownIt({
    html: true,
    linkify: true,
    typographer: true,
    highlight: function (str, lang) {
      if (lang && hljs.getLanguage(lang)) {
        try {
          return '<pre class="hljs"><code>' +
              hljs.highlight(str, {language: lang}).value +
              '</code></pre>'
        } catch (__) {
        }
      }
      return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>'
    }
  })

  // 表格样式
  md.renderer.rules.table_open = () => '<table style="border-collapse: collapse; width: 100%;">'
  md.renderer.rules.table_close = () => '</table>'
  md.renderer.rules.th_open = () => '<th style="border: 1px solid #ddd; padding: 8px;">'
  md.renderer.rules.td_open = () => '<td style="border: 1px solid #ddd; padding: 8px;">'

  return md
}

const formatAiMessage = (message) => {
  if (!message) return ''
  return getMarkdown().render(message)
}

// ====== methods ======
const router = useRouter()

const checkLoginStatus = () => {
  hasToken.value = !!getToken()
}

const handlePageClick = () => {
  if (!hasToken.value) {
    showLoginMask.value = true
  }
}

const goToLogin = () => {
  router.push('/login')
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await logout()
    removeToken()
    hasToken.value = false
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      ElMessage.error('退出登录失败')
    }
  }
}

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const scrollToBottom = () => {
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight
  }
}

const sendMessage = async () => {
  if (!hasToken.value) {
    ElMessage.warning('请先登录后再发送消息')
    return
  }

  if (!inputMessage.value.trim()) return

  thinking.value = true
  const message = inputMessage.value
  inputMessage.value = ''

  const userMessageObj = {
    id: Date.now(),
    userMessage: message,
    aiMessage: '',
    createTime: new Date().getTime()
  }

  messages.value.push(userMessageObj)

  await nextTick()
  scrollToBottom()

  try {
    const response = await chatStore.sendMessageAction({
      message,
      sessionId: currentChatId.value || undefined
    })

    if (response?.message) {
      const lastMessage = messages.value[messages.value.length - 1]
      if (lastMessage) {
        lastMessage.aiMessage = response.message
      }
    }

    if (response?.sessionId) {
      currentChatId.value = response.sessionId

      const sessionExists = chatList.value.some(chat => chat.id === response.sessionId)
      if (!sessionExists) {
        chatList.value.unshift({
          id: response.sessionId,
          title: message.substring(0, 20) + (message.length > 20 ? '...' : ''),
          createTime: new Date()
        })
      }
    }

    await nextTick()
    scrollToBottom()
    thinking.value = false
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败，请重试')
    thinking.value = false
  }
}

const loadHistory = async () => {
  if (!hasToken.value) return

  chatStore.pageNo = 1 // 直接赋值（Pinia state 是 ref）

  try {
    const response = await chatStore.fetchChatHistory(currentChatId.value)
    if (response?.records) {
      messages.value = response.records
      hasMoreMessages.value = response.records.length === chatStore.pageSize
    } else {
      console.warn('获取聊天记录成功，但返回数据格式不正确:', response)
      ElMessage.warning('获取聊天记录成功，但数据格式不正确')
      messages.value = []
      hasMoreMessages.value = false
    }

    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载聊天历史失败:', error)
    ElMessage.error('加载聊天历史失败')
    messages.value = []
    hasMoreMessages.value = false
  }
}

const handleScroll = (e) => {
  if (!hasToken.value) return

  const {scrollTop} = e.target
  if (scrollTop < scrollDistance && !isLoadingMore.value && hasMoreMessages.value) {
    loadMoreMessages()
  }
}

const loadMoreMessages = async () => {
  if (!hasToken.value || isLoadingMore.value) return

  isLoadingMore.value = true
  const container = messageContainer.value
  const oldHeight = container.scrollHeight

  chatStore.pageNo += 1

  try {
    const response = await chatStore.fetchChatHistory(currentChatId.value)
    if (response?.records?.length > 0) {
      messages.value = [...response.records, ...messages.value]
      hasMoreMessages.value = response.records.length === chatStore.pageSize

      await nextTick()
      const newHeight = container.scrollHeight
      container.scrollTop = newHeight - oldHeight
    } else {
      hasMoreMessages.value = false
    }
  } catch (error) {
    console.error('加载更多消息失败:', error)
    ElMessage.error('加载更多消息失败')
  } finally {
    isLoadingMore.value = false
  }
}

const createNewChat = () => {
  if (!hasToken.value) {
    ElMessage.warning('请先登录后再创建新对话')
    return
  }

  currentChatId.value = null
  messages.value = []
  chatStore.currentSession = null
}

const selectChat = async (chatId) => {
  if (!hasToken.value) {
    ElMessage.warning('请先登录后再选择对话')
    return
  }

  currentChatId.value = chatId

  const selectedChat = chatList.value.find(chat => chat.id === chatId)
  if (selectedChat) {
    chatStore.currentSession = selectedChat
  }

  chatStore.pageNo = 1
  await loadHistory()
}

const loadChatList = async () => {
  if (!hasToken.value) return

  try {
    const sessions = await chatStore.fetchSessionList()
    chatList.value = sessions

    if (chatList.value.length > 0) {
      currentChatId.value = chatList.value[0].id
      await loadHistory()
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
    ElMessage.error('加载会话列表失败')
  }
}

const loadUserInfo = async () => {
  try {
    const response = await getInfo()
    if (response?.user) {
      userInfo.userName = response.user.userName
      userInfo.email = response.user.email
      userInfo.avatar = response.user.avatar || userInfo.avatar
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

const init = () => {
  checkLoginStatus()
  if (hasToken.value) {
    loadChatList()
  }
}

init()

</script>

<style scoped>
.chat-box {
  display: flex;
  height: 89vh;
}

.chat-page {
  margin: 10px;
  height: 100%;
  width: 100vw;
  display: flex;
  background-color: #1a1a1a;
  position: relative;
  overflow: hidden;
}

.login-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  cursor: pointer;
}

.login-mask-content {
  background-color: #2d2d2d;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
  color: #e0e0e0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  cursor: pointer;
}

.login-mask-content i {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.login-mask-content h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

.login-mask-content p {
  color: #a0a0a0;
  margin-bottom: 20px;
}

.chat-sidebar {
  width: 250px;
  height: 100%;
  background-color: #2d2d2d;
  border-right: 1px solid #3d3d3d;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.chat-sidebar.disabled {
  opacity: 0.6;
  pointer-events: none;
}

.user-profile {
  padding: 20px 15px;
  border-bottom: 1px solid #3d3d3d;
  display: flex;
  align-items: center;
  background-color: #252525;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 12px;
  flex-shrink: 0;
  border: 2px solid #3d3d3d;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  overflow: hidden;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #e0e0e0;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  font-size: 12px;
  color: #a0a0a0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-header {
  padding: 15px;
  border-bottom: 1px solid #3d3d3d;
  display: flex;
  justify-content: center;
  align-items: center;
}

.rounded-new-chat {
  border-radius: 20px;
  height: 36px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #409EFF;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  width: 180px;
}

.rounded-new-chat:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  background-color: #66b1ff;
}

.rounded-new-chat:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  background-color: #3a8ee6;
}

.rounded-new-chat i {
  font-size: 16px;
  margin-right: 6px;
}

.rounded-new-chat span {
  font-size: 14px;
  font-weight: 500;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.chat-item {
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: background-color 0.3s;
  color: #c0c0c0;
}

.chat-item:hover {
  background-color: #3d3d3d;
}

.chat-item.active {
  background-color: #3d3d3d;
  color: #409EFF;
}

.chat-item i {
  margin-right: 10px;
  font-size: 16px;
}

.chat-title {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.empty-chat-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100px;
  color: #a0a0a0;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  flex: 1;
  width: 90%;
  margin: 0 auto;
  padding: 20px;
  background-color: #2d2d2d;
}

.chat-header {
  text-align: center;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 1px solid #3d3d3d;
}

.chat-header h2 {
  color: #e0e0e0;
  font-size: 16px;
  margin: 0;
  padding: 5px 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #252525;
  border-radius: 8px;
  margin-bottom: 20px;

}

.chat-message {
  margin-bottom: 20px;
}

.message {
  margin-bottom: 15px;
  display: flex;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message.ai {
  flex-direction: row;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 10px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background-color: #409EFF;
  color: #fff;
}

.message.ai .message-avatar {
  background-color: #67C23A;
  color: #fff;
}

.message-content-wrapper {
  max-width: 80%;
  display: flex;
  flex-direction: column;
}

.message.user .message-content-wrapper {
  align-items: flex-end;
}

.message.ai .message-content-wrapper {
  align-items: flex-start;
}

.message-content {
  padding: 12px 16px;
  border-radius: 8px;
  word-break: break-word;
  line-height: 1.5;
}

.message.user .message-content {
  background-color: #409EFF;
  color: #fff;
  border-top-right-radius: 0;
}

.message.ai .message-content {
  background-color: #3d3d3d;
  color: #e0e0e0;
  border-top-left-radius: 0;
}

.message-time {
  font-size: 12px;
  color: #a0a0a0;
  margin-top: 5px;
}

.chat-input {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 输入框样式 */
.chat-input .el-textarea >>> .el-textarea__inner {
  background-color: #1a1a1a !important;
  border-color: #3d3d3d !important;
  color: #e0e0e0 !important;
  border-radius: 20px !important;
  padding: 15px 20px !important;
  transition: all 0.3s ease !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2) !important;
  resize: none !important;
  font-size: 14px !important;
  line-height: 1.5 !important;
  border: 2px solid transparent !important;
}

.chat-input .el-textarea >>> .el-textarea__inner:focus {
  border-color: #409EFF !important;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2) !important;
  background-color: #1f1f1f !important;
}

.chat-input .el-textarea >>> .el-textarea__inner:hover {
  border-color: #409EFF !important;
  background-color: #1f1f1f !important;
}

.chat-input .el-textarea >>> .el-textarea__inner::placeholder {
  color: #a0a0a0 !important;
  font-size: 14px !important;
}

.rounded-button {
  border-radius: 20px;
  padding: 12px 30px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  align-self: flex-end;
  margin-top: 10px;
  background-color: #409EFF;
  border: none;
  font-size: 14px;
  letter-spacing: 0.5px;
}

/* 移动端响应式设计 */
@media (max-width: 768px) {
  /* 隐藏侧边栏 */
  .chat-sidebar {
    display: none;
  }

  /* 聊天容器占满整个宽度 */
  .chat-container {
    max-width: 100%;
    padding: 10px;
  }

  /* 调整聊天消息区域高度 */
  .chat-messages {
    height: calc(100vh - 160px);
  }

  /* 调整消息内容宽度 */
  .message-content-wrapper {
    max-width: 90%;
  }

  /* 调整消息内容内边距 */
  .message-content {
    padding: 10px 14px;
    font-size: 14px;
  }

  /* 调整头像大小 */
  .message-avatar {
    width: 35px;
    height: 35px;
  }

  /* 调整按钮大小 */
  .rounded-button {
    padding: 10px 24px;
    font-size: 13px;
    width: 100%;
    margin-top: 5px;
  }

  /* 调整登录遮罩内容 */
  .login-mask-content {
    padding: 20px;
    margin: 0 20px;
  }

  .login-mask-content i {
    font-size: 36px;
  }

  .login-mask-content h3 {
    font-size: 20px;
  }
}

.rounded-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  background-color: #66b1ff;
}

.rounded-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  background-color: #3a8ee6;
}

.loading-indicator {
  text-align: center;
  padding: 10px;
  color: #a0a0a0;
}

.loading-more {
  text-align: center;
  padding: 10px;
  color: #a0a0a0;
  background-color: #3d3d3d;
  border-radius: 4px;
  margin-bottom: 10px;
}

.empty-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #a0a0a0;
}

.empty-message i {
  font-size: 48px;
  margin-bottom: 10px;
}

.empty-message p {
  font-size: 14px;
}

/* 自定义滚动条样式 */
.chat-messages::-webkit-scrollbar,
.sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-thumb,
.sidebar-content::-webkit-scrollbar-thumb {
  background-color: #3d3d3d;
  border-radius: 3px;
}

.chat-messages::-webkit-scrollbar-track,
.sidebar-content::-webkit-scrollbar-track {
  background-color: #252525;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

.fade-enter-to,
.fade-leave {
  opacity: 1;
}

/* 消息动画 */
.chat-message {
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 加载动画 */
.loading-indicator {
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

.sidebar-footer {
  padding: 15px;
  border-top: 1px solid #3d3d3d;
  display: flex;
  justify-content: center;
  align-items: center;
}

.rounded-logout {
  border-radius: 20px;
  height: 36px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #F56C6C;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  width: 180px;
}

.rounded-logout:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  background-color: #f78989;
}

.rounded-logout:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  background-color: #dd6161;
}

.rounded-logout i {
  font-size: 16px;
  margin-right: 6px;
}

.rounded-logout span {
  font-size: 14px;
  font-weight: 500;
}

/* Markdown样式 */
.message-content :deep(pre) {
  background-color: #1e1e1e;
  border-radius: 4px;
  padding: 12px;
  margin: 8px 0;
  overflow-x: auto;
}

.message-content :deep(code) {
  font-family: 'Courier New', Courier, monospace;
  background-color: #2d2d2d;
  padding: 2px 4px;
  border-radius: 3px;
  font-size: 0.9em;
}

.message-content :deep(p) {
  margin: 8px 0;
  line-height: 1.5;
}

.message-content :deep(ul),
.message-content :deep(ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.message-content :deep(li) {
  margin: 4px 0;
}

.message-content :deep(h1),
.message-content :deep(h2),
.message-content :deep(h3),
.message-content :deep(h4),
.message-content :deep(h5),
.message-content :deep(h6) {
  margin: 16px 0 8px;
  font-weight: 600;
}

.message-content :deep(blockquote) {
  border-left: 4px solid #409EFF;
  margin: 8px 0;
  padding: 4px 12px;
  background-color: #2d2d2d;
  color: #a0a0a0;
}

/* 表格样式 - 白色边框 */
.message-content :deep(table) {
  border-collapse: collapse !important;
  width: 100% !important;
  margin: 8px 0 !important;
  border: 1px solid #ffffff !important;
}

.message-content :deep(th),
.message-content :deep(td) {
  border: 1px solid #ffffff !important;
  padding: 8px !important;
  text-align: left !important;
}

.message-content :deep(th) {
  background-color: #2d2d2d !important;
}

.message-content :deep(a) {
  color: #409EFF;
  text-decoration: none;
}

.message-content :deep(a:hover) {
  text-decoration: underline;
}

.message-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
  margin: 8px 0;
}

.message-content :deep(hr) {
  border: none;
  border-top: 1px solid #3d3d3d;
  margin: 16px 0;
}
</style>

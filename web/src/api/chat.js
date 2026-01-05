import request from '@/utils/request';

// 发送聊天消息
export function chat(message, sessionId) {
  return request({
    url: '/mcp/chat',
    method: 'post',
    data: {
      message,
      sessionId
    }
  });
}

// 获取聊天历史
export function getChatHistory(pageNo, pageSize, sessionId) {
  return request({
    url: '/mcp/chat/history',
    method: 'get',
    params: {
      pageNo: pageNo,
      pageSize: pageSize,
      sessionId: sessionId
    }
  });
}

// 获取会话列表
export function getSessionList(pageNo, pageSize) {
  return request({
    url: '/mcp/chat/sessions',
    method: 'get',
    params: { pageNo, pageSize }
  });
}

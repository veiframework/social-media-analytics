const extractUrlFromText = (text) => {
  // 使用正则表达式匹配 URL
  const urlRegex = /(https?:\/\/[^\s]+)/;
  const match = text.match(urlRegex);
  if (match) {
    return match[1]; // 返回匹配到的链接
  } else {
    return null; // 没有找到链接时返回 null
  }
}

export { extractUrlFromText }
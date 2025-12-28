// xbogus-batch.js
// 用途：批量生成抖音 X-Bogus 签名，不挂载到 window，直接返回结果
// 调用方式：page.evaluate(await fs.promises.readFile('xbogus-batch.js', 'utf8'), urls)
// msToken位置 webmssdk.es5.js搜索xmstr关键字或者找到这样的_0x180b4c && (_0x402a35['msToken'] = _0x180b4c关键字
((urls) => {
    // 检查 byted_acrawler 是否可用
    if (!window.byted_acrawler || typeof window.byted_acrawler.frontierSign !== 'function') {
        throw new Error('byted_acrawler is not ready or missing frontierSign function');
    }

    // 遍历每个 URL 生成签名
    return urls.map(url => {
        try {
            // 解析 URL，确保格式合法
            const urlObj = new URL(url);
            const cleanUrl = urlObj.origin + urlObj.pathname + urlObj.search;

            // 使用当前 navigator.userAgent（应已被伪造）
            const signResult = window.byted_acrawler.frontierSign({
                url: cleanUrl,
                user_agent: navigator.userAgent
            });

            const xBogus = signResult?.x_bogus || '';

            return {
                original: url,
                signed_url: xBogus ? `${cleanUrl}&X-Bogus=${xBogus}` : '',
                x_bogus: xBogus
            };
        } catch (err) {
            return {
                original: url,
                error: err.message || 'Unknown error during signing'
            };
        }
    });
});
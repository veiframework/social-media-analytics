// ========== 修改后的 fetch 拦截器（支持 response._url） ==========
(function () {
    if (window.__originalFetch) {
        return;
    }

    window.__originalFetch = window.fetch;

    window.fetch = function (input, init = {}) {
        const method = (init.method || 'GET').toUpperCase();

        if (method === 'GET') {
            const finalUrl = typeof input === 'string'
                ? input
                : input instanceof Request
                    ? input.url
                    : String(input);


            console.log('%c[✅ 拦截 GET] 最终请求 URL:', 'color: green; font-weight: bold;', finalUrl);
            let fakeBody = JSON.stringify({realUrl: finalUrl})
            // 创建假响应
            const fakeResponse = new Response(fakeBody, {
                status: 200,
                headers: {'Content-Type': 'application/json'}
            });

            return Promise.resolve(fakeResponse);
        }

        return window.__originalFetch.apply(this, arguments);
    };


})();
// ======================================
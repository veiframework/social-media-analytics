async (id) => {

    let fetchWithTimeout = (url, options, timeout = 30000) => {
        return Promise.race([
            fetch(url, options),
            new Promise((_, reject) => setTimeout(() => reject(new Error('timeout')), timeout))
        ]);
    };

    const sleep = (delayMs) => {
        if (delayMs > 0) {
            const minDelay = Math.floor(delayMs * 0.5);
            const jitter = minDelay + Math.floor(Math.random() * (delayMs - minDelay + 1));
            return new Promise(resolve => setTimeout(resolve, jitter));
        }
        return Promise.resolve();
    };

    const buildUrl = (awemeId) => {
        const p = new URLSearchParams({
            aweme_id: awemeId,
            device_platform: "webapp",
            aid: "6383",
            channel: "channel_pc_web"
        });
        return `https://www.douyin.com/aweme/v1/web/aweme/detail/?${p}`;
    };

    const isAntiCrawler = (text) => {
        const t = text.toLowerCase();
        return t.includes('<html') || t.includes('验证码') || t.includes('security') || t.includes('滑块');
    };

    const maybeDelay = async (attempt) => {
        if (attempt === 0) {
            return;
        }
        const baseDelay = Math.min(Math.pow(2, attempt) * 1000, 30000);
        await sleep(baseDelay);
        console.log(`[Retry ${attempt}, base=${baseDelay}ms]`);
    };

    const doFetch = async (awemeId) => {
        const url = buildUrl(awemeId);
        const res = await fetchWithTimeout(url, {
            credentials: "include"
        }, 30000);
        if (res.ok) {
            return await res.text();
        }
        throw new Error(`HTTP ${res.status}`);
    };

    const validateText = (text) => {
        if (!text?.trim()) {
            return false
        }
        if (isAntiCrawler(text)) {
            return false
        }
        try {
            JSON.parse(text);
            return true;
        } catch (e) {
            return false;
        }
    };


    // fetch one work detail
    const fetchOne = async (awemeId, maxRetries = 4) => {
        for (let attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                await maybeDelay(attempt);
                const text = await doFetch(awemeId);
                if (validateText(text)) {
                    return text;
                }
                return 'reload';
            } catch (err) {
                if (attempt === maxRetries) {
                    console.error(`[douyin] fetch failed for ${awemeId}:`, err.message);
                    return '';
                }
            }
        }
    };
    return await fetchOne(id);
}


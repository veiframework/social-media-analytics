async (ids) => {

    let fetchWithTimeout = (url, options, timeout = 30000) => {
        return Promise.race([
            fetch(url, options),
            new Promise((_, reject) => setTimeout(() => reject(new Error('timeout')), timeout))
        ]);
    };

    const sleep = (minMs, maxMs) =>
        new Promise(resolve =>
            setTimeout(resolve, Math.floor(Math.random() * (maxMs - minMs + 1)) + minMs)
        );

    const buildUrl = (awemeId) => {
        const p = new URLSearchParams({
            aweme_id: awemeId,
            device_platform: "web",
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
        const base = Math.pow(2, attempt) * 1000;
        await sleep(base, base + 1000);
        console.log(`[Retry ${attempt}]`);
    };

    const doFetch = async (awemeId) => {
        const url = buildUrl(awemeId);
        const res = await fetchWithTimeout(url, {
            headers: {"x-secsdk-csrf-token": "DOWNGRADE"},
            credentials: "include"
        }, 30000);
        if (res.ok) {
            return await res.text();
        }
        throw new Error(`HTTP ${res.status}`);
    };

    const validateText = (text) => {
        if (!text?.trim()) {
            throw new Error('Empty response');
        }
        if (isAntiCrawler(text)) {
            throw new Error('Anti-crawler page detected');
        }
    };


    // fetch one work detail
    const fetchOne = async (awemeId, maxRetries = 4) => {
        for (let i = 0; i <= maxRetries; i++) {
            try {
                await maybeDelay(i);
                const text = await doFetch(awemeId);
                validateText(text);
                return text;
            } catch (err) {
                if (i === maxRetries) {
                    console.error(`[douyin] fetch work failed for ${awemeId}:`, err.message);
                    return '';
                }
            }
        }
    };
    let promises = ids.map(id => fetchOne(id));
    return await Promise.all(promises);
}


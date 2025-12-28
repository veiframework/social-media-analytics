async (ids) => {
    // 超时封装
    let fetchWithTimeout = (url, options, timeout = 10000) => {
        return Promise.race([
            fetch(url, options),
            new Promise((_, reject) => setTimeout(() => reject(new Error('timeout')), timeout))
        ]);
    };
    let fetchOne = async (awemeId) => {
        try {
            let params = new URLSearchParams({
                aweme_id: awemeId,
                device_platform: "web",
                aid: "6383",
                channel: "channel_pc_web"
            });
            let url = `https://www.douyin.com/aweme/v1/web/aweme/detail/?${params.toString()}`;
            let res = await fetchWithTimeout(url, {
                headers: {"x-secsdk-csrf-token": "DOWNGRADE"},
                credentials: "include"
            }, 30000);
            if (res.status === 200) {
                let result = await res.json()
                return JSON.stringify(result);
            } else {
                return '';
            }
        } catch (err) {
            console.error('[douyin] Fetch error for', awemeId, err.message);
            return '';
        }
    }
    let promises = ids.map(id => fetchOne(id));
    return await Promise.all(promises);
}


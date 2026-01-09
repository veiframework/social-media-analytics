(() => {
    // ======================
    // 1. Navigator 属性（Safari on macOS）
    // ======================
    const hardwareProfiles = [
        {hardwareConcurrency: 4, deviceMemory: 8},
        {hardwareConcurrency: 8, deviceMemory: 16},
        {hardwareConcurrency: 10, deviceMemory: 16},
        {hardwareConcurrency: 12, deviceMemory: 32}
    ];

    const randomProfile = hardwareProfiles[Math.floor(Math.random() * hardwareProfiles.length)];

    // 隐藏 webdriver
    Object.defineProperty(navigator, 'webdriver', {get: () => false});

    // 平台信息（macOS）
    Object.defineProperty(navigator, 'platform', {get: () => 'MacIntel'});
    Object.defineProperty(navigator, 'language', {get: () => 'zh-CN'});
    Object.defineProperty(navigator, 'languages', {get: () => ['zh-CN']});
    Object.defineProperty(navigator, 'hardwareConcurrency', {get: () => randomProfile.hardwareConcurrency});
    Object.defineProperty(navigator, 'maxTouchPoints', {get: () => 0});
    Object.defineProperty(navigator, 'cookieEnabled', {get: () => true});
    Object.defineProperty(navigator, 'pdfViewerEnabled', {get: () => true});

    // Safari 特有
    Object.defineProperty(navigator, 'vendor', {get: () => 'Apple Computer, Inc.'});
    Object.defineProperty(navigator, 'vendorSub', {get: () => ''});
    Object.defineProperty(navigator, 'product', {get: () => 'Gecko'});      // 历史遗留
    Object.defineProperty(navigator, 'productSub', {get: () => '20030107'}); // Safari 固定值

    // Safari 不暴露 oscpu
    if ('oscpu' in navigator) {
        delete navigator.oscpu;
    }

    // ======================
    // 2. Plugins & MIME Types（Safari 风格）
    // ======================
    const fakePlugins = [
        {name: "PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format"},
        {name: "Chrome PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format"},
        {name: "Chromium PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format"},
        {name: "Microsoft Edge PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format"},
        {name: "WebKit built-in PDF", filename: "internal-pdf-viewer", description: "Portable Document Format"}
    ];

    const pluginArray = Object.assign([...fakePlugins], {
        item: i => fakePlugins[i] || null,
        namedItem: name => fakePlugins.find(p => p.name === name) || null,
        length: fakePlugins.length
    });

    Object.defineProperty(navigator, 'plugins', {get: () => pluginArray});


    const fakeMimeTypes = [
        {type: "application/pdf", suffixes: "pdf", description: "Portable Document Format"},
        {type: "text/pdf", suffixes: "pdf", description: "Portable Document Format"}
    ];

    const mimeTypeArray = Object.assign([...fakeMimeTypes], {
        item: i => fakeMimeTypes[i] || null,
        namedItem: type => fakeMimeTypes.find(m => m.type === type) || null,
        length: fakeMimeTypes.length
    });

    Object.defineProperty(navigator, 'mimeTypes', {get: () => mimeTypeArray});

    // ======================
    // 3. WebGL 指纹（Safari on macOS）
    // ======================
    const MAX_TEXTURE_SIZES = [8192];

    const webglProfiles = [
        // Apple Silicon
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M1'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M2'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M3'},

    ];

    const webglProfile = webglProfiles[Math.floor(Math.random() * webglProfiles.length)];
    const randomMaxTextureSize = MAX_TEXTURE_SIZES[Math.floor(Math.random() * MAX_TEXTURE_SIZES.length)];

    const _g = HTMLCanvasElement.prototype.getContext;
    HTMLCanvasElement.prototype.getContext = function (type, attributes) {
        const ctx = _g.call(this, type, attributes);

        if (ctx && ['webgl', 'experimental-webgl', 'webgl2', 'webkit-3d'].includes(type)) {
            const originalGetParameter = ctx.getParameter;
            const originalGetExtension = ctx.getExtension;

            // 创建伪造的 getParameter 函数
            const fakeGetParameter = function (param) {
                if (param === ctx.VENDOR) return webglProfile.VENDOR;          // 'Apple Inc.'
                if (param === ctx.RENDERER) return webglProfile.RENDERER;      // e.g., 'Apple GPU'
                if (param === ctx.VERSION) return 'WebGL 1.0';
                if (param === ctx.SHADING_LANGUAGE_VERSION) return 'WebGL GLSL ES 1.0 (1.0)';
                if (param === ctx.MAX_TEXTURE_SIZE) return randomMaxTextureSize; // 通常 4096

                // Safari 不支持 UNMASKED，所有其他参数走原生
                return originalGetParameter.call(this, param);
            };

            // 🔑 关键修复：让 toString() 返回原生格式
            try {
                Object.defineProperty(fakeGetParameter, 'toString', {
                    value: function () {
                        return 'function getParameter() { [native code] }';
                    },
                    writable: false,
                    configurable: false,
                    enumerable: false
                });
            } catch (e) {
                // 忽略 defineProperty 失败（如严格 CSP 环境）
            }

            ctx.getParameter = fakeGetParameter;

            // 🔑 Safari 真实行为：不支持 WEBGL_debug_renderer_info，必须返回 null
            ctx.getExtension = function (name) {
                if (name === 'WEBGL_debug_renderer_info') {
                    return null; // ← 这是 Safari 的标准行为！
                }
                return originalGetExtension ? originalGetExtension.call(this, name) : null;
            };
        }

        return ctx;
    };

    // ======================
    // 4. 清理 Service Worker（可选）
    // ======================
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistrations().then(regs => {
            regs.forEach(r => r.unregister());
        }).catch(() => {
        });
    }

    // ======================
    // 5. 移除非 Safari 对象
    // ======================
    if ('chrome' in window) delete window.chrome;
    if ('opr' in window) delete window.opr;

})();
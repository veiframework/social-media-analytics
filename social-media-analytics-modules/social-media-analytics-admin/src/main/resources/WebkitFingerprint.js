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
    const MAX_TEXTURE_SIZES = [4096, 8192, 16384];

    const webglProfiles = [
        // Apple Silicon
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M1'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M1 Pro'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M1 Max'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M2'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M2 Pro'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M2 Max'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Apple M3'},

        // Intel Mac + AMD GPU
        {VENDOR: 'Apple Inc.', RENDERER: 'AMD Radeon Pro 5500M'},
        {VENDOR: 'Apple Inc.', RENDERER: 'AMD Radeon Pro 5300M'},
        {VENDOR: 'Apple Inc.', RENDERER: 'AMD Radeon Pro Vega II'},

        // Intel Mac + Intel 核显
        {VENDOR: 'Apple Inc.', RENDERER: 'Intel Iris Plus Graphics'},
        {VENDOR: 'Apple Inc.', RENDERER: 'Intel UHD Graphics 630'}
    ];

    const webglProfile = webglProfiles[Math.floor(Math.random() * webglProfiles.length)];
    const randomMaxTextureSize = MAX_TEXTURE_SIZES[Math.floor(Math.random() * MAX_TEXTURE_SIZES.length)];

    const _g = HTMLCanvasElement.prototype.getContext;
    HTMLCanvasElement.prototype.getContext = function (type, attributes) {
        const ctx = _g.call(this, type, attributes);

        if (ctx && (['webgl', 'experimental-webgl', 'webgl2', 'webkit-3d'].includes(type))) {
            const originalGetParameter = ctx.getParameter;
            const originalGetExtension = ctx.getExtension;

            ctx.getParameter = function (param) {
                if (param === ctx.VENDOR) return webglProfile.VENDOR; // 'Apple Inc.'
                if (param === ctx.RENDERER) return webglProfile.RENDERER; // e.g., 'Apple M2'
                if (param === ctx.VERSION) return 'WebGL 1.0';
                if (param === ctx.SHADING_LANGUAGE_VERSION) return 'WebGL GLSL ES 1.0 (1.0)';
                if (param === ctx.MAX_TEXTURE_SIZE) return randomMaxTextureSize;

                // Safari 不支持 UNMASKED，所以即使查 0x9245 也应走原生（通常返回 null）
                return originalGetParameter.call(this, param);
            };

            // 🔑 关键：Safari 不支持 WEBGL_debug_renderer_info
            ctx.getExtension = function (name) {
                if (name === 'WEBGL_debug_renderer_info') {
                    return null; // ← 必须返回 null！
                }
                return originalGetExtension.call(this, name);
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
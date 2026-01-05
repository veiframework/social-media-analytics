(() => {
    // ======================
    // 1. navigator 基础属性（完全按你提供的值）
    // ======================
    const REAL_USER_AGENT = "{REAL_USER_AGENT}";

    Object.defineProperty(navigator, 'webdriver', {
        get: () => false
    });

    Object.defineProperty(navigator, 'platform', {
        get: () => 'Win32'
    });

    Object.defineProperty(navigator, 'language', {
        get: () => 'zh-CN'
    });

    Object.defineProperty(navigator, 'languages', {
        get: () => ['zh-CN', 'zh', 'zh-TW', 'zh-HK', 'en-US', 'en']
    });

    Object.defineProperty(navigator, 'hardwareConcurrency', {
        get: () => 12
    });

    Object.defineProperty(navigator, 'maxTouchPoints', {
        get: () => 0
    });

    Object.defineProperty(navigator, 'pdfViewerEnabled', {
        get: () => true
    });

    Object.defineProperty(navigator, 'cookieEnabled', {
        get: () => true
    });

    Object.defineProperty(navigator, 'product', {
        get: () => 'Gecko'
    });

    Object.defineProperty(navigator, 'productSub', {
        get: () => '20100101'
    });

    Object.defineProperty(navigator, 'vendor', {
        get: () => ''
    });

    Object.defineProperty(navigator, 'vendorSub', {
        get: () => ''
    });

    Object.defineProperty(navigator, 'userAgent', {
        get: () => REAL_USER_AGENT
    });

    // ======================
    // 2. Plugins & MIME Types（完全按你提供的列表）
    // ======================
    const fakePlugins = [
        { name: "PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Chrome PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Chromium PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Microsoft Edge PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "WebKit built-in PDF", filename: "internal-pdf-viewer", description: "Portable Document Format" }
    ];

    const pluginArray = Object.assign([...fakePlugins], {
        item: function(i) { return fakePlugins[i] || null; },
        namedItem: function(name) { return fakePlugins.find(p => p.name === name) || null; },
        refresh: function() {},
        length: fakePlugins.length
    });

    Object.defineProperty(navigator, 'plugins', {
        get: () => pluginArray
    });

    const fakeMimeTypes = [
        { type: "application/pdf", suffixes: "pdf", description: "Portable Document Format" },
        { type: "text/pdf", suffixes: "pdf", description: "Portable Document Format" }
    ];

    const mimeTypeArray = Object.assign([...fakeMimeTypes], {
        item: function(i) { return fakeMimeTypes[i] || null; },
        namedItem: function(type) { return fakeMimeTypes.find(m => m.type === type) || null; },
        length: fakeMimeTypes.length
    });

    Object.defineProperty(navigator, 'mimeTypes', {
        get: () => mimeTypeArray
    });

    // ======================
    // 3. WebGL 伪装 → 改为无 GPU 安全指纹（SwiftShader）
    // ======================
    const FAKE_WEBGL = {
        VENDOR: "Google Inc.",
        RENDERER: "Google SwiftShader",
        VERSION: "WebGL 1.0",
        SHADING_LANGUAGE_VERSION: "WebGL GLSL ES 1.0",
        MAX_TEXTURE_SIZE: 8192,   // 软件渲染典型值（≤8192）
        hasDebugExtension: false  // 无 GPU 环境通常不支持
    };

    const _g = HTMLCanvasElement.prototype.getContext;
    HTMLCanvasElement.prototype.getContext = function(type, attributes) {
        const ctx = _g.call(this, type, attributes);
        if (ctx && (type === 'webgl' || type === 'experimental-webgl')) {
            const _getParam = ctx.getParameter;
            ctx.getParameter = function(param) {
                if (param === ctx.VENDOR) return FAKE_WEBGL.VENDOR;
                if (param === ctx.RENDERER) return FAKE_WEBGL.RENDERER;
                if (param === ctx.VERSION) return FAKE_WEBGL.VERSION;
                if (param === ctx.SHADING_LANGUAGE_VERSION) return FAKE_WEBGL.SHADING_LANGUAGE_VERSION;
                if (param === ctx.MAX_TEXTURE_SIZE) return FAKE_WEBGL.MAX_TEXTURE_SIZE;
                // 不暴露 UNMASKED_*（因 hasDebugExtension=false）
                return _getParam.call(this, param);
            };

            const _getExtension = ctx.getExtension;
            ctx.getExtension = function(name) {
                // 禁用 WEBGL_debug_renderer_info（更真实）
                if (name === 'WEBGL_debug_renderer_info') {
                    return null;
                }
                return _getExtension.call(this, name);
            };
        }
        return ctx;
    };

    // ======================
    // 4. Screen 信息（保持不变）
    // ======================
    const screenProps = {
        width: 1920,
        height: 1080,
        availWidth: 1920,
        availHeight: 1040,
        colorDepth: 24,
        pixelDepth: 24
    };

    Object.keys(screenProps).forEach(key => {
        try {
            Object.defineProperty(screen, key, {
                value: screenProps[key],
                writable: false,
                configurable: false,
                enumerable: true
            });
        } catch (e) {}
    });

    // ======================
    // 5. 清理 Service Worker
    // ======================
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistrations().then(registrations => {
            for (const reg of registrations) reg.unregister();
        }).catch(() => {});
    }

    // ======================
    // 6. 环境对象（严格按你提供的布尔值）
    // ======================
    if ('chrome' in window) {
        delete window.chrome;
    }



})();
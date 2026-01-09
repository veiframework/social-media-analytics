(() => {

    const MAX_TEXTURE_SIZES = [8192];

    const webglProfiles = [
        // ── NVIDIA 主流 ───────────────────────
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 3050 Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 3050 Direct3D11 vs_5_0 ps_5_0)'
        },
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce GTX 1650 Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce GTX 1650 Direct3D11 vs_5_0 ps_5_0)'
        },
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce GTX 1050 Ti Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce GTX 1050 Ti Direct3D11 vs_5_0 ps_5_0)'
        },

        // ── AMD 显卡（Firefox + ANGLE on Windows 也支持）──────────────
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (AMD, AMD Radeon(TM) RX 6500M Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (AMD)',
            UNMASKED_RENDERER: 'ANGLE (AMD, AMD Radeon(TM) RX 6500M Direct3D11 vs_5_0 ps_5_0)'
        },
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (AMD, AMD Radeon(TM) RX 6600M Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (AMD)',
            UNMASKED_RENDERER: 'ANGLE (AMD, AMD Radeon(TM) RX 6600M Direct3D11 vs_5_0 ps_5_0)'
        },

        // ── Intel 核显 ───────────────────────
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (Intel, Intel(R) HD Graphics 630 Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (Intel)',
            UNMASKED_RENDERER: 'ANGLE (Intel, Intel(R) HD Graphics 630 Direct3D11 vs_5_0 ps_5_0)'
        },
        {
            VENDOR: 'Mozilla',
            RENDERER: 'ANGLE (Intel, Intel(R) UHD Graphics Direct3D11 vs_5_0 ps_5_0)',
            UNMASKED_VENDOR: 'Google Inc. (Intel)',
            UNMASKED_RENDERER: 'ANGLE (Intel, Intel(R) UHD Graphics Direct3D11 vs_5_0 ps_5_0)'
        }


    ];

    const hardwareProfiles = [
        // 低配入门级
        {hardwareConcurrency: 4, deviceMemory: 4},

        // 主流中端（最常见）
        {hardwareConcurrency: 4, deviceMemory: 8},
        {hardwareConcurrency: 6, deviceMemory: 8},
        {hardwareConcurrency: 8, deviceMemory: 8},

        // 高配性能机
        {hardwareConcurrency: 8, deviceMemory: 16},
        {hardwareConcurrency: 12, deviceMemory: 16},
        {hardwareConcurrency: 16, deviceMemory: 16},

    ];

    const randomIndex = Math.floor(Math.random() * hardwareProfiles.length);

    let randomProfile = hardwareProfiles[randomIndex];

    Object.defineProperty(navigator, 'webdriver', {get: () => false});
    Object.defineProperty(navigator, 'platform', {get: () => 'Win32'});
    Object.defineProperty(navigator, 'language', {get: () => 'zh-CN'});
    Object.defineProperty(navigator, 'languages', {get: () => ['zh-CN', 'zh', 'zh-TW', 'zh-HK', 'en-US', 'en']});
    Object.defineProperty(navigator, 'hardwareConcurrency', {get: () => randomProfile.hardwareConcurrency});
    Object.defineProperty(navigator, 'maxTouchPoints', {get: () => 0});
    Object.defineProperty(navigator, 'pdfViewerEnabled', {get: () => true});
    Object.defineProperty(navigator, 'cookieEnabled', {get: () => true});
    Object.defineProperty(navigator, 'product', {get: () => 'Gecko'});
    Object.defineProperty(navigator, 'productSub', {get: () => '20100101'});
    Object.defineProperty(navigator, 'vendor', {get: () => ''});
    Object.defineProperty(navigator, 'vendorSub', {get: () => ''});
    Object.defineProperty(navigator, 'oscpu', {get: () => 'Windows NT 10.0; Win64; x64'});

    // ======================
    // 2. Plugins & MIME Types（完全按你提供的列表）
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


    // WebGL
    const webglProfile = webglProfiles[Math.floor(Math.random() * webglProfiles.length)];
    const randomMaxTextureSize = MAX_TEXTURE_SIZES[Math.floor(Math.random() * MAX_TEXTURE_SIZES.length)];
    const _g = HTMLCanvasElement.prototype.getContext;
    HTMLCanvasElement.prototype.getContext = function (type, attributes) {
        const ctx = _g.call(this, type, attributes);

        if (ctx && (['webgl', 'experimental-webgl', 'webgl2'].includes(type))) {
            const originalGetParameter = ctx.getParameter;
            const originalGetExtension = ctx.getExtension;

            // 🔁 创建伪造的 getParameter 函数
            const fakeGetParameter = function (param) {
                if (param === ctx.VENDOR) return webglProfile.VENDOR;
                if (param === ctx.RENDERER) return webglProfile.RENDERER;
                if (param === ctx.VERSION) return 'WebGL 1.0';
                if (param === ctx.SHADING_LANGUAGE_VERSION) return 'WebGL GLSL ES 1.0';
                if (param === ctx.MAX_TEXTURE_SIZE) return randomMaxTextureSize;

                // UNMASKED 参数（通过 WEBGL_debug_renderer_info 暴露）
                if (param === 0x9245) return webglProfile.UNMASKED_VENDOR;   // UNMASKED_VENDOR_WEBGL
                if (param === 0x9246) return webglProfile.UNMASKED_RENDERER; // UNMASKED_RENDERER_WEBGL

                return originalGetParameter.call(this, param);
            };

            // 关键修复：让 toString() 返回原生格式，避免被检测
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
                // 忽略 defineProperty 失败（某些环境严格模式）
            }

            // 替换为伪造函数
            ctx.getParameter = fakeGetParameter;

            // 重写 getExtension 以支持 UNMASKED 查询
            ctx.getExtension = function (name) {
                if (name === 'WEBGL_debug_renderer_info') {
                    return {
                        UNMASKED_VENDOR_WEBGL: 0x9245,
                        UNMASKED_RENDERER_WEBGL: 0x9246
                    };
                }
                return originalGetExtension ? originalGetExtension.call(this, name) : null;
            };
        }

        return ctx;
    };

    // ======================
    // 5. 清理 Service Worker
    // ======================
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistrations().then(registrations => {
            for (const reg of registrations) reg.unregister();
        }).catch(() => {
        });
    }

    // ======================
    // 6. 环境对象（严格按你提供的布尔值）
    // ======================
    if ('chrome' in window) {
        delete window.chrome;
    }

})();
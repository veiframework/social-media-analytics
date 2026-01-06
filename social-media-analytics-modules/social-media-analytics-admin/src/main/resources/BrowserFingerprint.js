// fingerprint-spoof.js
(() => {

    const MAX_TEXTURE_SIZES = [4096, 8192, 16384];

    const webglProfiles = [
        // ── NVIDIA 高端 ───────────────────────
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 4090 (0x00002684) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Ti (0x00002704) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 3080 (0x00002206) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },

        // ── NVIDIA 主流/入门 ───────────────────
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce RTX 3060 (0x00002504) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (NVIDIA)',
            UNMASKED_RENDERER: 'ANGLE (NVIDIA, NVIDIA GeForce GTX 1650 (0x00001F0A) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },

        // ── AMD 高端/主流 ─────────────────────
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (AMD)',
            UNMASKED_RENDERER: 'ANGLE (AMD, AMD Radeon RX 7900 XT (0x000073A0) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (AMD)',
            UNMASKED_RENDERER: 'ANGLE (AMD, AMD Radeon RX 6700 XT (0x000073DF) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },

        // ── Intel 核显（近 5 年主流）────────────
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (Intel)',
            UNMASKED_RENDERER: 'ANGLE (Intel, Intel(R) Iris(R) Xe Graphics (0x00009A49) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },
        {
            VENDOR: 'WebKit',
            RENDERER: 'WebKit WebGL',
            UNMASKED_VENDOR: 'Google Inc. (Intel)',
            UNMASKED_RENDERER: 'ANGLE (Intel, Intel(R) UHD Graphics 630 (0x00003E9B) Direct3D11 vs_5_0 ps_5_0, D3D11)'
        },

    ];

    const hardwareProfiles = [
        // 低配入门级
        {hardwareConcurrency: 2, deviceMemory: 2},
        {hardwareConcurrency: 2, deviceMemory: 4},
        {hardwareConcurrency: 4, deviceMemory: 4},

        // 主流中端（最常见）
        {hardwareConcurrency: 4, deviceMemory: 8},
        {hardwareConcurrency: 6, deviceMemory: 8},
        {hardwareConcurrency: 8, deviceMemory: 8},

        // 高配性能机
        {hardwareConcurrency: 8, deviceMemory: 16},
        {hardwareConcurrency: 12, deviceMemory: 16},
        {hardwareConcurrency: 16, deviceMemory: 16},

        // 工作站/旗舰
        {hardwareConcurrency: 16, deviceMemory: 32},
        {hardwareConcurrency: 12, deviceMemory: 32}
    ];

    const randomIndex = Math.floor(Math.random() * hardwareProfiles.length);

    let randomProfile = hardwareProfiles[randomIndex];

    Object.defineProperty(navigator, 'webdriver', {get: () => undefined});
    Object.defineProperty(navigator, 'platform', {get: () => 'Win32'});
    Object.defineProperty(navigator, 'language', {get: () => 'zh-CN'});
    Object.defineProperty(navigator, 'languages', {get: () => ['zh-CN', 'zh']});
    Object.defineProperty(navigator, 'hardwareConcurrency', {get: () => randomProfile.hardwareConcurrency});
    Object.defineProperty(navigator, 'deviceMemory', {get: () => randomProfile.deviceMemory});
    Object.defineProperty(navigator, 'pdfViewerEnabled', {get: () => true});
    Object.defineProperty(navigator, 'cookieEnabled', {get: () => true});
    Object.defineProperty(navigator, 'product', {get: () => 'Gecko'});
    Object.defineProperty(navigator, 'productSub', {get: () => '20030107'});
    Object.defineProperty(navigator, 'vendor', {get: () => 'Google Inc.'});
    Object.defineProperty(navigator, 'vendorSub', {get: () => ''});
    Object.defineProperty(navigator, 'oscpu', {get: () => 'Windows NT 10.0; Win64; x64'});

    // Plugins
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

    // MIME Types
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

            // 🔁 只重写一次 getParameter
            ctx.getParameter = function (param) {
                // 标准参数
                if (param === ctx.VENDOR) return webglProfile.VENDOR;
                if (param === ctx.RENDERER) return webglProfile.RENDERER;
                if (param === ctx.VERSION) return 'WebGL 1.0 (OpenGL ES 2.0 Chromium)';
                if (param === ctx.SHADING_LANGUAGE_VERSION) return 'WebGL GLSL ES 1.0 (OpenGL ES GLSL ES 1.0 Chromium)';
                if (param === ctx.MAX_TEXTURE_SIZE) return randomMaxTextureSize;

                // UNMASKED 参数（通过 WEBGL_debug_renderer_info 暴露）
                if (param === 0x9245) return webglProfile.UNMASKED_VENDOR;   // UNMASKED_VENDOR_WEBGL
                if (param === 0x9246) return webglProfile.UNMASKED_RENDERER; // UNMASKED_RENDERER_WEBGL

                return originalGetParameter.call(this, param);
            };

            // 重写 getExtension 以支持 UNMASKED 查询
            ctx.getExtension = function (name) {
                if (name === 'WEBGL_debug_renderer_info') {
                    return {
                        UNMASKED_VENDOR_WEBGL: 0x9245,
                        UNMASKED_RENDERER_WEBGL: 0x9246
                    };
                }
                return originalGetExtension.call(this, name);
            };
        }

        return ctx;
    };


    // forbidden sw.js
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistrations().then(registrations => {
            for (let reg of registrations) {
                reg.unregister();
            }
        }).catch(() => {
        });
    }

    // Chrome object
    if (!window.chrome) window.chrome = {runtime: {}};
})();
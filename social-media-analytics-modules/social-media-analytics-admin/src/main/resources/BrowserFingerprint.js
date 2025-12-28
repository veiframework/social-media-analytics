// fingerprint-spoof.js
(() => {
    Object.defineProperty(navigator, 'webdriver', { get: () => undefined });
    Object.defineProperty(navigator, 'platform', { get: () => 'Win32' });
    Object.defineProperty(navigator, 'language', { get: () => 'zh-CN' });
    Object.defineProperty(navigator, 'languages', { get: () => ['zh-CN', 'zh'] });
    Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 6 });
    Object.defineProperty(navigator, 'deviceMemory', { get: () => 8 });
    Object.defineProperty(navigator, 'maxTouchPoints', { get: () => 20 });
    Object.defineProperty(navigator, 'pdfViewerEnabled', { get: () => true });
    Object.defineProperty(navigator, 'cookieEnabled', { get: () => true });
    Object.defineProperty(navigator, 'product', { get: () => 'Gecko' });
    Object.defineProperty(navigator, 'productSub', { get: () => '20030107' });
    Object.defineProperty(navigator, 'vendor', { get: () => 'Google Inc.' });
    Object.defineProperty(navigator, 'vendorSub', { get: () => '' });
    Object.defineProperty(navigator, 'userAgent', { get: () => "{REAL_USER_AGENT}" });

    // Plugins
    const fakePlugins = [
        { name: "PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Chrome PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Chromium PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "Microsoft Edge PDF Viewer", filename: "internal-pdf-viewer", description: "Portable Document Format" },
        { name: "WebKit built-in PDF", filename: "internal-pdf-viewer", description: "Portable Document Format" }
    ];
    const pluginArray = Object.assign([...fakePlugins], {
        item: i => fakePlugins[i] || null,
        namedItem: name => fakePlugins.find(p => p.name === name) || null,
        length: fakePlugins.length
    });
    Object.defineProperty(navigator, 'plugins', { get: () => pluginArray });

    // MIME Types
    const fakeMimeTypes = [
        { type: "application/pdf", suffixes: "pdf", description: "Portable Document Format" },
        { type: "text/pdf", suffixes: "pdf", description: "Portable Document Format" }
    ];
    const mimeTypeArray = Object.assign([...fakeMimeTypes], {
        item: i => fakeMimeTypes[i] || null,
        namedItem: type => fakeMimeTypes.find(m => m.type === type) || null,
        length: fakeMimeTypes.length
    });
    Object.defineProperty(navigator, 'mimeTypes', { get: () => mimeTypeArray });

    // WebGL
    const _g = HTMLCanvasElement.prototype.getContext;
    HTMLCanvasElement.prototype.getContext = function(t, a) {
        const c = _g.call(this, t, a);
        if (c && (t === 'webgl' || t === 'experimental-webgl')) {
            const gP = c.getParameter;
            c.getParameter = function(p) {
                if (p === c.VENDOR) return 'WebKit';
                if (p === c.RENDERER) return 'WebKit WebGL';
                if (p === c.VERSION) return 'WebGL 1.0 (OpenGL ES 2.0 Chromium)';
                if (p === c.SHADING_LANGUAGE_VERSION) return 'WebGL GLSL ES 1.0 (OpenGL ES GLSL ES 1.0 Chromium)';
                if (p === c.MAX_TEXTURE_SIZE) return 8192;
                return gP.call(this, p);
            };
            const gE = c.getExtension;
            c.getExtension = function(n) {
                if (n === 'WEBGL_debug_renderer_info') {
                    return { UNMASKED_VENDOR_WEBGL: 0x9245, UNMASKED_RENDERER_WEBGL: 0x9246 };
                }
                return gE.call(this, n);
            };
            const realGetParam = c.getParameter;
            c.getParameter = function(p) {
                if (p === 0x9245) return 'Google Inc. (Google)';
                if (p === 0x9246) return 'ANGLE (Google, Vulkan 1.3.0 (SwiftShader Device (Subzero) (0x0000C0DE)), SwiftShader driver)';
                return realGetParam.call(this, p);
            };
        }
        return c;
    };

    // Screen
    const screenProps = { width:1920, height:1080, availWidth:1920, availHeight:1040, colorDepth:24, pixelDepth:24 };
    Object.keys(screenProps).forEach(k => {
        try { Object.defineProperty(screen, k, { value: screenProps[k], writable: false }); } catch(e) {}
    });

    // forbidden sw.js
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.getRegistrations().then(registrations => {
            for (let reg of registrations) {
                reg.unregister();
            }
        }).catch(() => {});
    }

    // Chrome object
    if (!window.chrome) window.chrome = { runtime: {} };
})();
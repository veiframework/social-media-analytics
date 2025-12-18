//import com.google.common.io.ByteStreams;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.handler.codec.http.HttpObject;
//import io.netty.handler.codec.http.HttpRequest;
//import io.netty.handler.codec.http.HttpResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.littleshoot.proxy.*;
//import org.littleshoot.proxy.extras.SelfSignedSslEngineSource;
//import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
//
//import javax.net.ssl.*;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.security.KeyStore;
//import java.security.Security;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.Arrays;
//
///**
// * @author zhanghaowei
// * @since 1.0
// */
//@Slf4j
//public class LittleProxyTest {
//
//
//    public static void main(String[] args) throws Exception {
////
////        Map<String, String> map = new HashMap<>();
////        map.put("proxyType", "manual");
////        map.put("httpProxy", "192.168.1.31:27304");
////        map.put("sslProxy", "192.168.1.31:27304");
////
////        Capabilities options = new BaseOptions()
////                .amend("platformName", "Android")
////                .amend("appium:platformVersion", "9")
////                .amend("appium:automationName", "UiAutomator2")
////                .amend("appium:deviceName", "127.0.0.1:5555")
////                .amend("appium:appPackage", "com.ss.android.ugc.aweme")
////                .amend("appium:noReset", true)
////                .amend("appium:appActivity", "com.ss.android.ugc.aweme.splash.SplashActivity")
////                .amend("appium:ensureWebviewsHavePages", true)
////                .amend("appium:nativeWebScreenshot", true)
////                .amend("appium:newCommandTimeout", 3600)
////                .amend("appium:enablePerformanceLogging", true)
////                .amend("appium:connectHardwareKeyboard", true)
////                .amend("appium:proxy", map);
////        AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723"), options);
//
////        KeyStore keyStore = KeyStore.getInstance("JKS");
////        keyStore.load(new FileInputStream(), "qywcvjan".toCharArray());
//        // 添加请求过滤器
//        SelfSsl selfSsl = new SelfSsl("E:\\soft\\21670677_lumengshop.com_jks\\lumengshop.com.jks", true, true, "qywcvjan");
//        SelfSignedSslEngineSource selfSignedSslEngineSource = new SelfSignedSslEngineSource();
//        HttpProxyServer server = DefaultHttpProxyServer.bootstrap()
//                .withAllowLocalOnly(false)
//                .withSslEngineSource(selfSignedSslEngineSource)
//                .withTransparent(true)
//                .withFiltersSource(new HttpFiltersSourceAdapter() {
//                    @Override
//                    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
//                        return new HttpFiltersAdapter(originalRequest) {
//                            @Override
//                            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
//                                if (originalRequest.getUri().startsWith("http://") ||
//                                        originalRequest.getUri().startsWith("https://")) {
//                                    System.out.println("Request: " + originalRequest.getUri());
//                                } else {
//                                    System.err.println("Invalid request format: " + originalRequest.getUri());
//                                }
//                                return null;
//                            }
//
//                            @Override
//                            public HttpObject proxyToClientResponse(HttpObject httpObject) {
//                                // 拦截服务器响应
//                                return httpObject;
//                            }
//                        };
//                    }
//                })
//                .withPort(27304)
//                .start();
//    }
//
//    public static class SelfSsl implements SslEngineSource {
//        private static final String ALIAS = "littleproxy";
//        private String password;
//        private static final String PROTOCOL = "TLS";
//        private final File keyStoreFile;
//        private final boolean trustAllServers;
//        private final boolean sendCerts;
//
//        private SSLContext sslContext;
//
//        public SelfSsl(String keyStorePath,
//                       boolean trustAllServers, boolean sendCerts, String password) {
//            this.trustAllServers = trustAllServers;
//            this.sendCerts = sendCerts;
//            this.keyStoreFile = new File(keyStorePath);
//            this.password = password;
//            initializeSSLContext();
//        }
//
//
//        @Override
//        public SSLEngine newSslEngine() {
//            return sslContext.createSSLEngine();
//        }
//
//        @Override
//        public SSLEngine newSslEngine(String peerHost, int peerPort) {
//            SSLEngine engine = sslContext.createSSLEngine(peerHost, peerPort);
//            engine.setUseClientMode(false);
//            engine.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
//            return engine;
//        }
//
//        private void initializeSSLContext() {
//            String algorithm = Security
//                    .getProperty("ssl.KeyManagerFactory.algorithm");
//            if (algorithm == null) {
//                algorithm = "SunX509";
//            }
//
//            try {
//                final KeyStore ks = KeyStore.getInstance("JKS");
//                ks.load(new FileInputStream(keyStoreFile), password.toCharArray());
//
//                // Set up key manager factory to use our key store
//                final KeyManagerFactory kmf =
//                        KeyManagerFactory.getInstance(algorithm);
//                kmf.init(ks, password.toCharArray());
//
//                // Set up a trust manager factory to use our key store
//                TrustManagerFactory tmf = TrustManagerFactory
//                        .getInstance(algorithm);
//                tmf.init(ks);
//
//                TrustManager[] trustManagers = null;
//                if (!trustAllServers) {
//                    trustManagers = tmf.getTrustManagers();
//                } else {
//                    trustManagers = new TrustManager[]{new X509TrustManager() {
//                        // TrustManager that trusts all servers
//                        @Override
//                        public void checkClientTrusted(X509Certificate[] arg0,
//                                                       String arg1)
//                                throws CertificateException {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(X509Certificate[] arg0,
//                                                       String arg1)
//                                throws CertificateException {
//                        }
//
//                        @Override
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return new X509Certificate[0];
//                        }
//                    }};
//                }
//
//                KeyManager[] keyManagers = null;
//                if (sendCerts) {
//                    keyManagers = kmf.getKeyManagers();
//                } else {
//                    keyManagers = new KeyManager[0];
//                }
//
//                // Initialize the SSLContext to work with our key managers.
//                sslContext = SSLContext.getInstance(PROTOCOL);
//                sslContext.init(keyManagers, trustManagers, null);
//            } catch (final Exception e) {
//                throw new Error(
//                        "Failed to initialize the server-side SSLContext", e);
//            }
//        }
//
//        private String nativeCall(final String... commands) {
//            log.info("Running '{}'", Arrays.asList(commands));
//            final ProcessBuilder pb = new ProcessBuilder(commands);
//            try {
//                final Process process = pb.start();
//                final InputStream is = process.getInputStream();
//
//                byte[] data = ByteStreams.toByteArray(is);
//                String dataAsString = new String(data);
//
//                log.info("Completed native call: '{}'\nResponse: '" + dataAsString + "'",
//                        Arrays.asList(commands));
//                return dataAsString;
//            } catch (final IOException e) {
//                log.error("Error running commands: " + Arrays.asList(commands), e);
//                return "";
//            }
//        }
//
//    }
//
//}


/**
 *
 *         <dependency>
 *             <groupId>io.appium</groupId>
 *             <artifactId>java-client</artifactId>
 *             <version>8.6.0</version>
 *             <exclusions>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-api</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-support</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-remote-driver</artifactId>
 *                 </exclusion>
 *
 *             </exclusions>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.seleniumhq.selenium</groupId>
 *             <artifactId>selenium-http</artifactId>
 *             <version>4.9.0</version>
 *             <exclusions>
 *                 <exclusion>
 *                     <groupId>com.google.guava</groupId>
 *                     <artifactId>guava</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-json</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-api</artifactId>
 *                 </exclusion>
 *             </exclusions>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.seleniumhq.selenium</groupId>
 *             <artifactId>selenium-json</artifactId>
 *             <version>4.9.0</version>
 *             <exclusions>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-api</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>com.google.guava</groupId>
 *                     <artifactId>guava</artifactId>
 *                 </exclusion>
 *             </exclusions>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.littleshoot</groupId>
 *             <artifactId>littleproxy</artifactId>
 *             <version>1.1.2</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.seleniumhq.selenium</groupId>
 *             <artifactId>selenium-api</artifactId>
 *             <version>4.9.0</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.seleniumhq.selenium</groupId>
 *             <artifactId>selenium-remote-driver</artifactId>
 *             <version>4.9.0</version>
 *             <exclusions>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-http</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-api</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-json</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>com.google.guava</groupId>
 *                     <artifactId>guava</artifactId>
 *                 </exclusion>
 *             </exclusions>
 *         </dependency>
 *         <dependency>
 *             <groupId>org.seleniumhq.selenium</groupId>
 *             <artifactId>selenium-support</artifactId>
 *             <version>4.9.0</version>
 *             <exclusions>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-remote-driver</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-api</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>org.seleniumhq.selenium</groupId>
 *                     <artifactId>selenium-json</artifactId>
 *                 </exclusion>
 *                 <exclusion>
 *                     <groupId>com.google.guava</groupId>
 *                     <artifactId>guava</artifactId>
 *                 </exclusion>
 *             </exclusions>
 *         </dependency>
 *
 */

package com.roy.common.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.RequestConfig.Builder;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Roy(luoyh) - Aug 11, 2023
 * @since 1.8
 */
@Slf4j
public final class HttpUtils {
    
    //private static final Logger log = LoggerFactory.getLogger("http");
    
    @SneakyThrows
    public static void main(String[] args) {
        Map<String, String> param = Maps.newHashMap();
        param.put("a", "adda");
        param.put("b", "大舍大得22d");
        List<NameValuePair> nvps = Lists.newArrayListWithCapacity(param.size());
        param.forEach((k, v) -> nvps.add(new BasicNameValuePair(k, v)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);
        System.out.println(EntityUtils.toString(entity, StandardCharsets.UTF_8));
        
        HttpResponse r = post(HttpRequest.of("http://roy.com/archive/page/list")
                .addHeader("Authentication", "V1222222231xus")
                .entity(new StringEntity("{\"pageNo\":1,\"pageSize\":50}", ContentType.APPLICATION_JSON))
              //  .addHeader("Content-Type", "application/json")
                );
        
        System.out.println(r.result);
        
    }
    
    
    public static HttpResponse get(String url) {
        return execute(null, new HttpGet(url));
    }
    
    public static Optional<String> downloadAsBase64(String url) {
        return download(url).map(e -> Base64.getEncoder().encodeToString(e));
    }
    
    public static Optional<byte[]> download(String url) {
        HttpGet http = new HttpGet(url);
        try (CloseableHttpClient client = client(null);) {
            return Optional.ofNullable(client.execute(http, response -> {
                return EntityUtils.toByteArray(response.getEntity());
            }));
        } catch (Exception e) {
            log.error("http error", e);
        }
        return Optional.empty();
    }
    
    public static HttpResponse post(HttpRequest request) {
        return execute(request, _post(request));
    }
    
    public static HttpResponse get(HttpRequest request) {
        return execute(request, _get(request));
    }

    
    public static HttpResponse postJSON(HttpRequest request, String json) {
        HttpPost http = _post(request);
        http.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        http.setEntity(entity);
        request.entity(entity);
        return execute(request, http);
    }

    public static HttpResponse postFormURLEncode(HttpRequest request, Map<String, String> param) {
        HttpPost http = _post(request);
        http.setHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        List<NameValuePair> nvps = Lists.newArrayListWithCapacity(param.size());
        param.forEach((k, v) -> nvps.add(new BasicNameValuePair(k, v)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, request.charset);
        http.setEntity(entity);
        request.entity(entity);
        return execute(request, http);
    }
    
    @SneakyThrows
    private static HttpResponse execute(HttpRequest request, HttpUriRequest http) {
        log.info("request: {}, {}", request.url, request.body());
//        log.info("request: {}, {}", request.url, "");
        
        try (CloseableHttpClient client = client(request);) {
            return client.execute(http, response -> {
                String res = EntityUtils.toString(response.getEntity(), null == request ? StandardCharsets.UTF_8 : request.charset);
                String line = response.getReasonPhrase();
                int code = response.getCode();
                //log.info("result: {}, {}", line, res);
                log.info("result: {}, {}, {}", line, code, res);
                return HttpResponse.ok().code(code).line(line).result(res).headers(response.getHeaders());
            });
            
        } catch (Exception e) {
            log.error("http error", e);
            return HttpResponse.fail().msg(e.getMessage());
        }
    }
    
    @SneakyThrows
    private static CloseableHttpClient client(HttpRequest request) {
        Builder builder = RequestConfig.copy(defaultConfig);
        if (null != request) {
            int millis = 0;
            if (null != request.connectTimeout && (millis = (int) request.connectTimeout.toMillis()) > 0) {
                builder.setConnectionRequestTimeout(Timeout.ofMilliseconds(millis));
            }
            millis = 0;
            if (null != request.readTimeout && (millis = (int) request.readTimeout.toMillis()) > 0) {
                builder.setResponseTimeout(Timeout.ofMilliseconds(millis));
            }
            if (request.disabledRedirect()) {
                builder.setRedirectsEnabled(false);
            }
        }
        
        
//        final PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
//                .setConnectionConfigResolver(route -> {
//                    // Use different settings for all secure (TLS) connections
//                    final HttpHost targetHost = route.getTargetHost();
//                    if (route.isSecure()) {
//                        return ConnectionConfig.custom()
//                                .setConnectTimeout(Timeout.ofMinutes(2))
//                                .setSocketTimeout(Timeout.ofMinutes(2))
//                                .setValidateAfterInactivity(TimeValue.ofMinutes(1))
//                                .setTimeToLive(TimeValue.ofHours(1))
//                                .build();
//                    } else {
//                        return ConnectionConfig.custom()
//                                .setConnectTimeout(Timeout.ofMinutes(1))
//                                .setSocketTimeout(Timeout.ofMinutes(1))
//                                .setValidateAfterInactivity(TimeValue.ofSeconds(15))
//                                .setTimeToLive(TimeValue.ofMinutes(15))
//                                .build();
//                    }
//                })
//                .setTlsConfigResolver(host -> {
//                    return TlsConfig.custom()
//                            .setSupportedProtocols(TLS.V_1_3)
//                            .setHandshakeTimeout(Timeout.ofSeconds(30))
//                            .build();
//                })
//                .build();
        HttpClientBuilder hc = HttpClientBuilder.create().setDefaultRequestConfig(builder.build());
        if (request.tls) {

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            
            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder
                    .create()
                    .setSslContext(sslContext)
                    .build();
            HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .setDefaultTlsConfig(TlsConfig.custom()
                            .setHandshakeTimeout(Timeout.ofSeconds(30))
                            .setSupportedProtocols(TLS.V_1_3)
                            .build())
                    .build();
            hc.setConnectionManager(cm);
        }
        return hc.build();
    }
    
    private static HttpGet _get(HttpRequest request) {
        HttpGet http = new HttpGet(request.url);
        request.header.forEach(http::addHeader);
        return http;
    }
    
    private static HttpPost _post(HttpRequest request) {
        HttpPost http = new HttpPost(request.url);
        if (null != request.entity) {
            http.setEntity(request.entity);
        }
        request.header.forEach(http::addHeader);
        return http;
    }
    
    /**
     * HTTP请求
     * @author Roy(luoyh) - Sep 26, 2023
     * @since 1.8
     */
    @Data
    @Accessors(fluent = true)
    public static class HttpRequest {
        /**
         * 请求地址
         */
        private String url;
        /**
         * 请求体
         */
        private HttpEntity entity;
        /**
         * 请求头
         */
        private List<Header> header = Lists.newArrayList();
        /**
         * 字符集,默认utf8
         */
        private Charset charset = StandardCharsets.UTF_8;
        /**
         * 是否使用HTTPS
         */
        private boolean tls;
        /**
         * 连接超时
         */
        private Duration connectTimeout;
        
        /**
         * 读取超时
         */
        private Duration readTimeout;
        
        /**
         * 是否禁止重定向
         */
        private boolean disabledRedirect;
        
        public static HttpRequest of(String url) {
            return new HttpRequest().url(url);
        }
        
        public HttpRequest addHeader(String name, String value) {
            header.add(new BasicHeader(name, value));
            return this;
        }
        
        @SneakyThrows
        private String body() {
            if (null == entity) {
                return "";
            }
            if (entity instanceof StringEntity) {
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
            return "";
        }
    }
    
    
    /**
     * HTTP返回结果
     * @author Roy(luoyh) - Sep 26, 2023
     * @since 1.8
     */
    @Data
    @Accessors(fluent = true)
    public static class HttpResponse {
        /**
         * true 表示请求成功, false 表示请求失败
         */
        private boolean success = false;
        /**
         * 响应状态码
         */
        private int code;
        private String line;
        /**
         * 响应结果
         */
        private String result;
        /**
         * 当请求失败时(success=false)的错误信息
         */
        private String msg;
        /**
         * 响应头
         */
        private Header[] headers;
        
        private static HttpResponse ok() {
            return new HttpResponse().success(true);
        }
        

        private static HttpResponse fail() {
            return new HttpResponse().success(false);
        }
        
        public Optional<JSONObject> okJSON() {
            if (requestOK()) {
                return asJSON();
            }
            return Optional.empty();
        }
        
        public Optional<JSONObject> asJSON() {
            return Cons.nex(() -> JSON.parseObject(result));
        }
        
        public Optional<HttpResponse> ops() {
            return Optional.of(this);
        }
        
        /**
         * 请求成功并且状态码为200
         * @return
         */
        public boolean requestOK() {
            return success && 200 == code;
        }
        
        /**
         * 当请求成功并且状态码为200时的结果
         * @return
         */
        public Optional<String> okResult() {
            if (requestOK()) {
                return Optional.ofNullable(result);
            }
            return Optional.empty();
        }

        /**
         * 当请求成功并且状态码为200时的结果
         * @return
         */
        public <T> Optional<T> okResult(Function<String, T> map) {
            if (requestOK()) {
                return Cons.nex(() -> map.apply(result));
            }
            return Optional.empty();
        }
        
        /**
         * 请求成功的结果
         * @return
         */
        public Optional<String> successResult() {
            if (success) {
                return Optional.ofNullable(result);
            }
            return Optional.empty();
        }
    }
    

    // 默认超时10秒
    static RequestConfig defaultConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(10))
            .setResponseTimeout(Timeout.ofSeconds(10))
            .build();
    

}

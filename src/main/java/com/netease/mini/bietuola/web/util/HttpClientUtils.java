package com.netease.mini.bietuola.web.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http Client 工具封装
 */
public class HttpClientUtils {

    private static final String HTTP_PROTOCOL = "http";
    private static final String HTTPS_PROTOCOL = "https";
    private static final String[] SUPPORTED_PROTOCOLS = new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};
    private static final long KEEP_ALIVE_DURATION = 10000; // 长连接时间，10秒

    private static final int DEFAULT_MAX_THREADS = 500;
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000; //请求连接超时时间，5秒
    private static final int DEFAULT_SOCKET_TIMEOUT = 10000; //连接建立后，返回结果的超时时间，10秒
    private static final int DEFAULT_RETRY_COUNT = 0; // 重试次数，0
    private static final boolean DEFAULT_REQUEST_SENT_RETRY_ENABLED = false;

    private static CloseableHttpClient httpClient;

    public static CloseableHttpClient createHttpClient(int maxThreads, int connectionTimeoutSecs,
                                                       int soTimeoutSecs, int retryCount,
                                                       boolean requestSentRetryEnabled) {
        // ssl连接配置，测试发现一些国外的https地址无法建立连接，如https://yesno.wtf/api
        // 无服务器证书，采用自定义信任机制，全部信任，不做身份鉴定
        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("create CloseableHttpClient fail, Failed to register https scheme.", e);
        }
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                SUPPORTED_PROTOCOLS,
                null,
                NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register(HTTP_PROTOCOL, PlainConnectionSocketFactory.getSocketFactory())
                .register(HTTPS_PROTOCOL, sslsf)
                .build();
        // 连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(maxThreads);
        cm.setDefaultMaxPerRoute(maxThreads);
        //自定义连接保持存活策略:如果响应头中有Keep-Alive相关的timeout设置，则按Keep-Alive头中的timeout设定存活时间。不然默认为5秒。
        ConnectionKeepAliveStrategy ckas = new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                long keepAlive = super.getKeepAliveDuration(response, context);
                if (keepAlive == -1) {
                    keepAlive = KEEP_ALIVE_DURATION;
                }
                return keepAlive;
            }
        };
        //设置默认的请求和传输超时时间
        RequestConfig rc = RequestConfig.custom()
                .setConnectTimeout(connectionTimeoutSecs * 1000)
                .setSocketTimeout(soTimeoutSecs * 1000)
                .build();
        // 重试策略
        HttpRequestRetryHandler hrrh = new DefaultHttpRequestRetryHandler(retryCount, requestSentRetryEnabled);
        return HttpClients.custom()
                .setConnectionManager(cm)
                .setKeepAliveStrategy(ckas)
                .setDefaultRequestConfig(rc)
                .setRetryHandler(hrrh)
                .build();
    }

    public static CloseableHttpClient getDefaultHttpClient() {
        if (httpClient == null) {
            synchronized (HttpClientUtils.class) {
                if (httpClient == null) {
                    httpClient = createHttpClient(DEFAULT_MAX_THREADS, DEFAULT_CONNECT_TIMEOUT,
                            DEFAULT_SOCKET_TIMEOUT, DEFAULT_RETRY_COUNT, DEFAULT_REQUEST_SENT_RETRY_ENABLED);
                }
            }
        }
        return httpClient;
    }

    /**
     * httpClient get请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，url查询字符串，没有传null
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> header, Map<String, String> param) throws IOException {
        return get(url, header, param, getDefaultHttpClient());
    }

    /**
     * httpClient get请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，url查询字符串，没有传null
     * @param httpClient   自定义请求的httpclient客户端
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response get(String url, Map<String, String> header, Map<String, String> param, CloseableHttpClient httpClient)
            throws IOException {
        Response result;
        StringBuilder sb = new StringBuilder(url);
        if (MapUtils.isNotEmpty(param)) {
            boolean firstFlag = true;
            for (Map.Entry<String, String> entry : param.entrySet()) {
                if (firstFlag) {
                    sb.append("?")
                            .append(URLEncoder.encode(entry.getKey(),
                                    Consts.UTF_8.toString()))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(),
                                    Consts.UTF_8.toString()));
                    firstFlag = false;
                } else {
                    sb.append("&")
                            .append(URLEncoder.encode(entry.getKey(),
                                    Consts.UTF_8.toString()))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(),
                                    Consts.UTF_8.toString()));
                }
            }
        }
        HttpGet httpGet = new HttpGet(sb.toString());
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet); // 该方法为阻塞方法，会返回最终的结果
            result = extractResponse(response);
            EntityUtils.consume(response.getEntity());
        } finally {
            httpGet.releaseConnection();
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * httpClient post请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，form提交适用
     * @param reqJsonEntity    请求json字符串，设置该参数会覆盖param
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> header,
                                Map<String, String> param, String reqJsonEntity) throws IOException {
        return post(url, header, param, reqJsonEntity, getDefaultHttpClient());
    }

    /**
     * httpClient post请求
     *
     * @param url          请求url
     * @param header       头部信息
     * @param param        请求参数，form提交适用
     * @param reqJsonEntity    请求json字符串，设置该参数会覆盖param
     * @param httpClient   自定义请求的httpclient客户端
     * @return 响应结果，包含状态码和响应体
     * @throws IOException
     */
    public static Response post(String url, Map<String, String> header,
                                Map<String, String> param, String reqJsonEntity, CloseableHttpClient httpClient)
            throws IOException {
        Response result;
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(header)) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (MapUtils.isNotEmpty(param)) {
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(),
                        entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formparams,
                    Consts.UTF_8));
        }
        // 设置json实体，优先级高，会覆盖前面的urlEncodedFormEntity
        if (StringUtils.isNotBlank(reqJsonEntity)) {
            httpPost.setEntity(new StringEntity(reqJsonEntity,
                    ContentType.APPLICATION_JSON));
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            result = extractResponse(response);
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            throw e;
        } finally {
            httpPost.releaseConnection();
            try {
                if (null != response)
                    response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String formatHttpResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        StringBuilder builder = new StringBuilder();
        HttpEntity entity = httpResponse.getEntity();
        builder.append(httpResponse.getStatusLine()).append("\n");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            builder.append(iterator.nextHeader()).append("\n");
        }
        builder.append("\n");
        if (entity != null && entity.getContent() != null) {
            builder.append(EntityUtils.toString(entity));
        }
        return builder.toString();
    }

    private static Response extractResponse(CloseableHttpResponse response)
            throws IOException {
        Response r = new Response();
        r.setStatusCode(response.getStatusLine().getStatusCode());
        r.setResponseBody(EntityUtils.toString(response.getEntity(),
                Consts.UTF_8));
        return r;
    }

    public static class Response {
        private Integer statusCode;

        private String responseBody;

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "statusCode=" + statusCode +
                    ", responseBody='" + responseBody + '\'' +
                    '}';
        }
    }

}


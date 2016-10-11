package com.tw.go.plugin.common;

import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Created by MarkusW on 20.10.2015.
 */
public abstract class ApiRequestBase {

    private String apiUrl;
    private String secretKey;
    private String accessKey;
    private OkHttpClient client;
    private String username = null;
    private String password = null;
    private String accept = null;
    private String defaultAccept = null;
    private long http202RetryMillis = 3000;
    private long http202RetryCount = 20;


    public void setHttp202RetryMillis(long http202RetryMillis) {
        this.http202RetryMillis = http202RetryMillis;
    }

    public ApiRequestBase(String apiUrl, String accessKey, String secretKey, boolean disableSslVerification) throws GeneralSecurityException {
        this(apiUrl, accessKey, secretKey, disableSslVerification, false);
    }

    public ApiRequestBase(String apiUrl, String accessKey, String secretKey, boolean disableSslVerification, boolean cookiesEnabled) throws GeneralSecurityException {
        client = getOkHttpClient(disableSslVerification, cookiesEnabled);

        this.apiUrl = apiUrl;
        this.secretKey = secretKey;
        this.accessKey = accessKey;
    }

    public void setBasicAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setDefaultAccept(String accept) {
        this.defaultAccept = accept;
        if (accept == null) {
            setAccept(accept);
        }
    }

    public String getApiUrl() {
        return apiUrl;
    }

    protected String requestGet(String requestUrlString) throws IOException {
        return request(requestUrlString, "GET");
    }

    protected void requestDelete(String requestUrl) throws IOException {
        request(requestUrl, "DELETE");
    }

    protected String requestPost(String requestUrlString, String jsonData) throws IOException {
        return request(requestUrlString, jsonData, "POST");
    }

    protected String requestPostFormUrlEncoded(String requestUrlString, String data) throws IOException {
        return request(requestUrlString, "POST", data, "application/x-www-form-urlencoded");
    }

    private String request(String requestUrlString, String jsonData, String requestMethod) throws IOException {
        return request(requestUrlString, requestMethod, jsonData, "application/json");
    }

    private String request(String requestUrlString, String requestMethod) throws IOException {
        return request(requestUrlString, requestMethod, null, "application/json");
    }

    public String request(String requestUrlString, String requestMethod, String data, String contentType) throws IOException {
        return requestBody(requestUrlString, requestMethod, data, contentType).string();
    }

    public InputStream requestStream(String requestUrlString, String requestMethod, String data, String contentType) throws IOException {
        return requestBody(requestUrlString, requestMethod, data, contentType).byteStream();
    }

    private ResponseBody requestBody(String requestUrlString, String requestMethod, String data, String contentType) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(requestUrlString);

        // use whatever is set
        if (accept != null) {
            builder.addHeader("Accept", accept);
        }

        // next time, use again the default
        accept = defaultAccept;

        if (username != null && password != null) {
            builder.header("Authorization", Credentials.basic(username, password));
        }

        if (accessKey != null && secretKey != null) {
            builder.header("X-ApiKeys", getXApiKeys()); // API keys for secure access
        }

        if (data != null) {
            builder.method(requestMethod, RequestBody.create(MediaType.parse(contentType), data));
            builder.header("Confirm", "true");
        }

        Request request = builder.build();

        for (long retriesLeft = http202RetryCount; ; ) {
            Response response = client.newCall(request).execute();

            if (response.code() < 200 || response.code() >= 300) {
                throw new IOException("REST call failed; URL=" + response.request().url() + "; Code=" + response.code() + "; Message=" + response.message());
            }

            if (response.code() != 202) {
                return response.body();
            }

            if (--retriesLeft == 0) {
                throw new IOException("REST call failed; URL=" + response.request().url() + "; Code=" + response.code() +
                        String.format("; Message= %d retries with timeout %dms failed", http202RetryCount, http202RetryMillis));
            }

            try {
                TimeUnit.MILLISECONDS.sleep(http202RetryMillis);
            } catch (InterruptedException e) {
                // do nothing ...
            }
        }
    }

    private String getXApiKeys() {
        String apiKeys = "accessKey=%1$s; secretKey=%2$s;";
        return String.format(apiKeys, accessKey, secretKey);
    }

    public static String encodeFormData(String name, String value) throws UnsupportedEncodingException {
        HashMap<String, String> map = new HashMap<>();
        map.put(name, value);
        return encodeFormData(map);
    }

    public static String encodeFormData(Map<String, String> data) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : data.entrySet()) {
            sb.append(e.getKey()).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
            return sb.toString();
        }
        return sb.toString();
    }

    private static OkHttpClient getOkHttpClient(boolean disableSslVerification, boolean cookiesEnabled) {
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (disableSslVerification) {
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                final SSLSocketFactory delegate = sslContext.getSocketFactory();

                builder.sslSocketFactory(delegate);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            if (cookiesEnabled) {
                builder.cookieJar(new CookieJar() {
                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                });
            }

            return builder
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
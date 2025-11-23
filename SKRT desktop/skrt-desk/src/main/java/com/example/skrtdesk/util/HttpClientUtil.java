package com.example.skrtdesk.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;

public class HttpClientUtil {
    private static CloseableHttpClient httpClient;
    
    private HttpClientUtil() {
    }
    
    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(Timeout.ofSeconds(30))
                .build();
            
            PoolingHttpClientConnectionManager connectionManager = 
                new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(100);
            connectionManager.setDefaultMaxPerRoute(20);
            connectionManager.setDefaultSocketConfig(socketConfig);
            
            httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        }
        return httpClient;
    }
    
    public static void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


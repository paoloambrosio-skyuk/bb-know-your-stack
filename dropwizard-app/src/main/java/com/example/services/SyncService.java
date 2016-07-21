package com.example.services;

import io.dropwizard.client.HttpClientConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;

public class SyncService {

    private final Executor executor;

    public SyncService(HttpClientConfiguration httpConfig) {
        // Configuring the HTTP client without Dropwizard's HttpClientBuilder
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(httpConfig.getMaxConnections());
        cm.setDefaultMaxPerRoute(httpConfig.getMaxConnectionsPerRoute());

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(cm)
                .setDefaultSocketConfig(SocketConfig.custom()
                        // Disable Nagle
                        .setTcpNoDelay(true)
// Don't confuse these with those in the RequestConfig!
//                        .setSoKeepAlive(...) // this is TCP and not HTTP keep-alive
//                        .setSoTimeout(...)   // for non-blocking I/O operations
                        .build()
                ).setDefaultRequestConfig(RequestConfig.custom()
                        // to get a connection from connection manager
                        .setConnectionRequestTimeout((int)httpConfig.getConnectionRequestTimeout().toMilliseconds())
                        // to establish the TCP connection
                        .setConnectTimeout((int)httpConfig.getConnectionTimeout().toMilliseconds())
                        // max inactivity between data packets
                        .setSocketTimeout((int)httpConfig.getTimeout().toMilliseconds())
                        .build()
                )
                .setRetryHandler(new StandardHttpRequestRetryHandler(httpConfig.getRetries(), false))
                .build();
        executor = Executor.newInstance(httpClient);
    }

    public String call() throws IOException {
        String body = executor.execute(Request.Get("http://localhost:8001/call")).returnContent().asString();
        if ("OK".equals(body)) {
            return "sync";
        } else {
            throw new RuntimeException("Something went wrong!");
        }
    }
}

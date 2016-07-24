package com.example.services;

import com.example.config.HttpDependencyConfiguration;
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
    private final HttpDependencyConfiguration config;

    public SyncService(HttpDependencyConfiguration config) {
        this.config = config;
        this.executor = createExecutor(config);
    }

    /**
     * Configuring the HTTP client without Dropwizard's HttpClientBuilder
     */
    private Executor createExecutor(HttpDependencyConfiguration config) {
        // Connection pooling
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(config.getMaxConnections());
        cm.setDefaultMaxPerRoute(config.getMaxConnectionsPerRoute());

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
                        .setConnectionRequestTimeout((int)config.getConnectionRequestTimeout().toMilliseconds())
                        // to establish the TCP connection
                        .setConnectTimeout((int)config.getConnectionTimeout().toMilliseconds())
                        // max inactivity between data packets
                        .setSocketTimeout((int)config.getTimeout().toMilliseconds())
                        .build()
                )
                .setRetryHandler(new StandardHttpRequestRetryHandler(config.getRetries(), false))
                .build();
        return Executor.newInstance(httpClient);
    }

    public String call() throws IOException {
        String body = executor.execute(Request.Get(config.getUrl())).returnContent().asString();
        if ("OK".equals(body)) {
            return "sync";
        } else {
            throw new RuntimeException("Something went wrong!");
        }
    }
}

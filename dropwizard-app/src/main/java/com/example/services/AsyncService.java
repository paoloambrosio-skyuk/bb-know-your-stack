package com.example.services;

import com.example.config.HttpDependencyConfiguration;
import org.asynchttpclient.*;

import java.util.concurrent.CompletableFuture;

public class AsyncService {

    private final AsyncHttpClient asyncHttpClient;
    private final HttpDependencyConfiguration config;

    public AsyncService(HttpDependencyConfiguration config) {
        this.config = config;
        asyncHttpClient = createHttpClient(config);
    }

    private AsyncHttpClient createHttpClient(HttpDependencyConfiguration config) {
        AsyncHttpClientConfig ahcc = new DefaultAsyncHttpClientConfig.Builder()
                // Connection pooling
                .setMaxConnections(config.getMaxConnections())
                .setMaxConnectionsPerHost(config.getMaxConnectionsPerRoute())
                // Disable Nagle
                .setTcpNoDelay(true)
                // to establish the TCP connection
                .setConnectTimeout((int)config.getConnectionTimeout().toMilliseconds())
                // total request timeout
                .setRequestTimeout((int)config.getTimeout().toMilliseconds())
                // max inactivity between data packets
                .setReadTimeout((int)config.getTimeout().toMilliseconds())
                .setMaxRequestRetry(config.getRetries())
                .setKeepAlive(true)
                .build();

        return new DefaultAsyncHttpClient(ahcc);
    }

    public CompletableFuture<String> call() {
        final CompletableFuture<String> toBeCompleted = new CompletableFuture<>();
        asyncHttpClient.prepareGet(config.getUrl()).execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                String body = response.getResponseBody();
                if ("OK".equals(body)) {
                    toBeCompleted.complete("async");
                } else {
                    toBeCompleted.completeExceptionally(new RuntimeException("Something went wrong!"));
                }
                return response;
            }
            @Override
            public void onThrowable(Throwable t) {
                toBeCompleted.completeExceptionally(t);
            }
        });
        return toBeCompleted;
    }
}

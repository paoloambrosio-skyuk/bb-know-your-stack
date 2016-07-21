package com.example.services;

import com.example.ExampleConfiguration;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import io.dropwizard.client.HttpClientConfiguration;

import java.util.concurrent.CompletableFuture;

public class AsyncService {

    private final AsyncHttpClient asyncHttpClient;

    public AsyncService(HttpClientConfiguration httpConfig) {
        asyncHttpClient = new AsyncHttpClient();
    }

    public CompletableFuture<String> call() {
        final CompletableFuture<String> toBeCompleted = new CompletableFuture<>();
        asyncHttpClient.prepareGet("http://localhost:8001/call").execute(new AsyncCompletionHandler<Response>() {
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

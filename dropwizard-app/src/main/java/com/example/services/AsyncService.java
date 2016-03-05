package com.example.services;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.util.concurrent.CompletableFuture;

public class AsyncService {

    // TODO: DI
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    public CompletableFuture<String> call() {
        final CompletableFuture<String> toBeCompleted = new CompletableFuture<>();
        asyncHttpClient.prepareGet("http://localhost:8001/").execute(new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                toBeCompleted.complete("async");
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

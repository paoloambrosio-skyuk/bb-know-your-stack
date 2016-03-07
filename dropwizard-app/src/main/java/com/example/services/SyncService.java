package com.example.services;

import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class SyncService {

    public String call() throws IOException {
        String body = Request.Get("http://localhost:8001/call").execute().returnContent().asString();
        if ("OK".equals(body)) {
            return "sync";
        } else {
            throw new RuntimeException("Something went wrong!");
        }
    }
}

package com.example.services;

import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class SyncService {

    public String call() throws IOException {
        Request.Get("http://localhost:8001/").execute();
        return "sync";
    }
}

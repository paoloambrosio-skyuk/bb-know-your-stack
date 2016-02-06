package com.example.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ExampleResource {

    private final int POOL_SIZE = 2;
    private final Semaphore syncPool = new Semaphore(POOL_SIZE, true);
    private final ExecutorService asyncPool = Executors.newFixedThreadPool(POOL_SIZE);

    @GET @Path("sync")
    public String sync() throws InterruptedException {
        syncPool.acquire();
        expensiveOperation();
        syncPool.release();
        return "sync";
    }

    @GET @Path("async")
    public void async(@Suspended final AsyncResponse asyncResponse) {
        asyncPool.submit(() -> {
            expensiveOperation();
            asyncResponse.resume("async");
        });
    }

    private void expensiveOperation() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted!");
        }
    }
}
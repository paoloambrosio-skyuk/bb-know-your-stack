package com.example.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ExampleResource {

    private ExecutorService executor = Executors.newFixedThreadPool(10);

    @GET @Path("sync")
    public String sync() throws InterruptedException {
        expensiveOperation();
        return "sync";
    }

    @GET @Path("async")
    public void async(@Suspended final AsyncResponse asyncResponse) {
        executor.submit(() -> {
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
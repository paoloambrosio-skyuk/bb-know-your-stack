package com.example.resources;

import com.example.ExampleConfiguration;
import com.example.services.AsyncService;
import com.example.services.SyncService;
import io.dropwizard.client.HttpClientConfiguration;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class ExampleResource {

    private final SyncService syncService;
    private final AsyncService asyncService;

    public ExampleResource(ExampleConfiguration configuration) {
        HttpClientConfiguration httpConfig = configuration.getHttpClientConfiguration();
        syncService = new SyncService(httpConfig);
        asyncService = new AsyncService(httpConfig);
    }

    @GET @Path("sync")
    public String sync() throws Exception {
        return syncService.call();
    }

    @GET @Path("async")
    public void async(@Suspended final AsyncResponse asyncResponse) {
        asyncService.call().whenComplete((result, t) -> {
            if (t != null) {
                asyncResponse.resume(t);
            } else {
                asyncResponse.resume(result);
            }
        });
    }
}
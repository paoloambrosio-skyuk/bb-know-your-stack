package com.example.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/sync")
@Produces(MediaType.TEXT_PLAIN)
public class SyncResource {

    @GET
    public String sync() {
        return "sync";
    }
}
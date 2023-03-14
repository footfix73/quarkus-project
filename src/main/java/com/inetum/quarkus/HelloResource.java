package com.inetum.quarkus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

    @Inject
    HelloService helloService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hola() {
        return "Mr and Ms Reactive";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("v1/polite/{name}")
    public String hola2(@PathParam("name") String name) {
        return helloService.politeHello(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("v2/polite/{name}")
    public String hola3(@PathParam("name") String name) {
        return helloService.politeHello2(name);
    }
}
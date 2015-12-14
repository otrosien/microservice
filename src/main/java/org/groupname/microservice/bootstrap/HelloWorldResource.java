package org.groupname.microservice.bootstrap;

import javax.annotation.ManagedBean;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ManagedBean("HelloManagedBean")
@Path("jersey")
public class HelloWorldResource {

    @GET
    public String hello() {
        return "hello jersey";
    }
}

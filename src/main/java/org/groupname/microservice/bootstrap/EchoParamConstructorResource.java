package org.groupname.microservice.bootstrap;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@Path("echoparamconstructor/{a}")
public class EchoParamConstructorResource {

    static final Logger LOGGER = LoggerFactory.getLogger(EchoParamConstructorResource.class);

    String a;

    // no-arg ctor is required by WLS
    public EchoParamConstructorResource() {
    }

    @Inject
    public EchoParamConstructorResource(@PathParam("a") String a) {
        this.a = a;
    }

    @PostConstruct
    void postConstruct() {
        LOGGER.info(String.format("in post construct, a=%s", a));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "ECHO " + a;
    }
}

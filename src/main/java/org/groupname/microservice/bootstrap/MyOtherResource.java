package org.groupname.microservice.bootstrap;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@ManagedBean
@RequestScoped
@Path("/other/{c}/{d}")
public class MyOtherResource {

    public static class MyInterceptor {

        @AroundInvoke
        public Object around(InvocationContext ctx) throws Exception {
            return String.format("INTERCEPTED: %s", ctx.proceed());
        }
    }

    @Context UriInfo uriInfo;
    @Context Request request;

    @PathParam("c") String c;
    @PathParam("d") String d;

    @GET
    @Produces("text/plain")
    @Interceptors(MyInterceptor.class)
    public String get() {
        return String.format("OK %s %s, c=%s, d=%s", request.getMethod(), uriInfo.getRequestUri(), c, d);
    }
}

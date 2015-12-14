package org.groupname.microservice.bootstrap;

import javax.enterprise.inject.Vetoed;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

@Vetoed
public class StartMain {

    public static void main(String[] args) throws Exception {
        System.setProperty(Weld.SHUTDOWN_HOOK_SYSTEM_PROPERTY, "true");
        final Weld weld = new Weld("microcontainer-1");
        WeldContainer container = weld.initialize();
        final MicroServer server = container.select(MicroServer.class).get();
        server.start();
    }

}

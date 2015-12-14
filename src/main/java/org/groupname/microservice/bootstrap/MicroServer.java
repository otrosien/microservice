package org.groupname.microservice.bootstrap;

import java.lang.management.ManagementFactory;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.StatisticsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.ViewStatusMessagesServlet;

@Singleton
public class MicroServer {

    private static final Logger LOG = LoggerFactory.getLogger(MicroServer.class);

    private final Server server;

    public MicroServer() {
        LOG.info("MicroServer.start()");
        server = new Server();
        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);
        server.setStopAtShutdown(true);

        // Setup JMX
        MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);

        NCSARequestLog requestLog = new NCSARequestLog("./logs/microservice-yyyy_mm_dd.request.log");
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setRetainDays(10);
        server.setRequestLog(requestLog);

        final HandlerList handlers = new HandlerList();
        server.setHandler(handlers);

        // Create the ResourceHandler. It is the object that will actually
        // handle the request for a given file. It is
        // a Jetty Handler object so it is suitable for chaining with other
        // handlers as you will see in other examples.
        ResourceHandler resource_handler = new ResourceHandler();
        // Configure the ResourceHandler. Setting the resource base indicates
        // where the files should be served out of.
        // In this example it is the current directory but it can be configured
        // to anything that the jvm has access to.
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[] { "index.html" });
        resource_handler.setResourceBase(".");

        // The ServletHandler is a dead simple way to create a context handler
        // that is backed by an instance of a Servlet.
        // This handler then needs to be registered with the Server object.
        ServletContextHandler servlet_handler = new ServletContextHandler(
                ServletContextHandler.GZIP | ServletContextHandler.NO_SECURITY | ServletContextHandler.NO_SESSIONS);
        server.setHandler(servlet_handler);

        // Passing in the class for the Servlet allows jetty to instantiate an
        // instance of that Servlet and mount it on a given context path.

        // IMPORTANT:
        // This is a raw Servlet, not a Servlet that has been configured
        // through a web.xml @WebServlet annotation, or anything similar.
        servlet_handler.addServlet(HelloServlet.class, "/hello");
        servlet_handler.addServlet(ViewStatusMessagesServlet.class, "/logger");
        servlet_handler.addServlet(DefaultServlet.class, "/");
        servlet_handler.addServlet(StatisticsServlet.class, "/stats");
        ServletHolder jerseyServletHolder = new ServletHolder("MyApplication", org.glassfish.jersey.servlet.ServletContainer.class);
        jerseyServletHolder.setInitOrder(0);
        jerseyServletHolder.setInitParameter("javax.ws.rs.Application", MyApplication.class.getName());
        servlet_handler.addServlet(jerseyServletHolder, "/rs/*");
        // Add the ResourceHandler to the server.
        final GzipHandler gzip = new GzipHandler();
        server.setHandler(gzip);
        handlers.setHandlers(new Handler[] { resource_handler, servlet_handler });
        gzip.setHandler(handlers);

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);

    }

    public void start() throws Exception {
        // print internal state
        LOG.debug(server.dump());
        server.start();
        server.join();
    }

}

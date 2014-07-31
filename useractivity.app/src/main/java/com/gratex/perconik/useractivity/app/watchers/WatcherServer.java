package com.gratex.perconik.useractivity.app.watchers;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

public class WatcherServer {
  ServletHolder servletHolder;
  Server server;
  ServletContextHandler servletContextHandler;
  int port;

  public WatcherServer(int port) {
    org.eclipse.jetty.util.log.Log.setLog(new AppTracerJettyLogger());

    this.port = port;
    this.servletHolder = new ServletHolder(ServletContainer.class);

    //		MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
  }

  //	public void addProviderPackage(String packageName) {
  //		servletHolder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, packageName);
  //	}
  //
  //	public void addProviderClass(String className) {
  //		servletHolder.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, className);
  //	}

  public void setServiceClasses(Class<?> ... classes) {
    if (this.server != null) {
      throw new IllegalStateException("Already started");
    }
    StringBuilder strb = new StringBuilder();
    for (Class<?> svcClass: classes) {
      strb.append(svcClass.getName()).append(',');
    }
    this.servletHolder.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, strb.toString());
  }

  public void start() throws Exception {
    if (this.server != null) {
      throw new IllegalStateException("Already started");
    }

    this.server = new Server();

    //listen only on localhost - http://stackoverflow.com/a/1955591
    ServerConnector connector = new ServerConnector(this.server);
    connector.setPort(this.port);
    connector.setHost("localhost");
    this.server.setConnectors(new Connector[] {connector});

    this.servletContextHandler = new ServletContextHandler(this.server, "/", ServletContextHandler.SESSIONS);
    this.servletContextHandler.addServlet(this.servletHolder, "/*");

    this.server.start();
  }

  public void stop() throws Exception {
    if (this.server == null) {
      throw new IllegalStateException("Server not started");
    }
    this.server.stop();

    this.server = null;
    this.servletContextHandler = null;
  }
}

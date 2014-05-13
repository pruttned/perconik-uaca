package com.gratex.perconik.useractivity.app.watchers;

import org.eclipse.jetty.server.Server;
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
		this.port = port;
		servletHolder = new ServletHolder(ServletContainer.class);
		
//		MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
	}

//	public void addProviderPackage(String packageName) {
//		servletHolder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, packageName);
//	}
//
//	public void addProviderClass(String className) {
//		servletHolder.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, className);
//	}

	public void setServiceClasses(Class<?>... classes) {
		if (server != null) {
			throw new IllegalStateException("Already started");
		}
		StringBuilder strb = new StringBuilder();
		for (Class<?> svcClass : classes) {
			strb.append(svcClass.getName()).append(',');
		}
		servletHolder.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, strb.toString());
	}
	
	public void start() throws Exception {
		if (server != null) {
			throw new IllegalStateException("Already started");
		}

		server = new Server(port);
		servletContextHandler = new ServletContextHandler(server, "/",
				ServletContextHandler.SESSIONS);
		servletContextHandler.addServlet(servletHolder, "/*");

		server.start();		
	}

	public void stop() throws Exception {
		if (server == null) {
			throw new IllegalStateException("Server not started");
		}
		server.stop();

		server = null;
		servletContextHandler = null;
	}
}

package com.gratex.perconik.useractivity.app.watchers;

/*
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
*/


import com.gratex.perconik.useractivity.app.watchers.WatcherServer;
import com.gratex.perconik.useractivity.app.watchers.ide.IdeWatcherSvc;
import com.gratex.perconik.useractivity.app.watchers.web.WebWatcherSvc;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String[] args) throws Exception {
		/*
        ServletHolder sh = new ServletHolder(ServletContainer.class);    
        //sh.setInitParameter("org.glassfish.jersey.config.property.resourceConfigClass", "org.glassfish.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "com.gratex.perconik.useractivity.app.watchers.web");//Set the package where the services reside
        //sh.setInitParameter("org.glassfish.jersey.api.json.POJOMappingFeature", "true");
      
        Server server = new Server(9999);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");
        server.start();
        server.join();      */
/*
  		PostSaveEventRequest r = new PostSaveEventRequest(); 
 
		 MOXyJsonProvider moxyJsonProvider = new MOXyJsonProvider();
		 moxyJsonProvider.writeTo(r, PostSaveEventRequest.class.getClass(), PostSaveEventRequest.class.getGenericSuperclass(), null, MediaType.APPLICATION_JSON_TYPE, null, System.out);
		 
		 */
		
		WatcherServer srv = new WatcherServer(16375);
		//srv.addProviderPackage("com.gratex.perconik.useractivity.app.watchers.web");
		//srv.addProviderPackage("com.gratex.perconik.useractivity.app.watchers.ide");
		srv.setServiceClasses(IdeWatcherSvc.class, WebWatcherSvc.class);
		//srv.addProviderClass(WebWatcherSvc.class.getName());
		srv.start();
		
		System.out.println("started");
		System.in.read();
		
		srv.stop();
     }
	
	/*
    public static void main(String[] args) throws Exception {
        Server server = new Server(1789);
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(HelloServlet.class, "/hello");//Set the servlet to run.
        handler.addServletWithMapping(HelloServlet2.class, "/hello2");//Set the servlet to run.
        server.setHandler(handler);    
        server.start();
        server.join();
    }
    
    @SuppressWarnings("serial")
    public static class HelloServlet extends HttpServlet {
 
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet</h1>");
        }
    }
    
    @SuppressWarnings("serial")
    public static class HelloServlet2 extends HttpServlet {
 
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("<h1>Hello SimpleServlet  22222 1!!</h1>");
        }
    }*/
}

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


import java.net.HttpURLConnection;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientResponse;

import com.gratex.perconik.useractivity.app.UserActivityServiceProxy;
import com.gratex.perconik.useractivity.app.dto.CachedEvent;
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
		
		//"http://localhost:55592/api/UserActivity/28DE8734-C586-4B4A-96AB-573F1F7BDEF8"
		String data = "{\"timestamp\":1399959657581,\"user\":\"pruttkay-nedecky\",\"workstation\":\"prutkaypc\",\"eventTypeUri\":\"http://perconik.gratex.com/UserActivity/Event/System/ProcessesChangedSinceChceck\",\"startedProcesses\":[{\"name\":\"svchost\",\"pid\":548},{\"name\":\"conhost\",\"pid\":2748},{\"name\":\"System\",\"pid\":4},{\"name\":\"dwm\",\"pid\":3280},{\"name\":\"Gratex.PerConIK.UserActivity.App\",\"pid\":2984},{\"name\":\"PresentationFontCache\",\"pid\":4608},{\"name\":\"notepad++\",\"pid\":5696},{\"name\":\"chrome\",\"pid\":5968},{\"name\":\"iexplore\",\"pid\":5220},{\"name\":\"svchost\",\"pid\":824},{\"name\":\"conhost\",\"pid\":552},{\"name\":\"conhost\",\"pid\":6584},{\"name\":\"chrome\",\"pid\":8144},{\"name\":\"chrome\",\"pid\":5960},{\"name\":\"jusched\",\"pid\":264},{\"name\":\"IPROSetMonitor\",\"pid\":1648},{\"name\":\"javaw\",\"pid\":7060},{\"name\":\"foobar2000\",\"pid\":2992},{\"name\":\"svchost\",\"pid\":1404},{\"name\":\"SearchFilterHost\",\"pid\":6012},{\"name\":\"RAVCpl64\",\"pid\":4040},{\"name\":\"conhost\",\"pid\":5212},{\"name\":\"sqlservr\",\"pid\":1864},{\"name\":\"igfxtray\",\"pid\":4048},{\"name\":\"WmiPrvSE\",\"pid\":4116},{\"name\":\"explorer\",\"pid\":3296},{\"name\":\"iexplore\",\"pid\":5204},{\"name\":\"ccSvcHst\",\"pid\":3784},{\"name\":\"spoolsv\",\"pid\":1376},{\"name\":\"svchost\",\"pid\":1136},{\"name\":\"Smc\",\"pid\":2808},{\"name\":\"csrss\",\"pid\":612},{\"name\":\"cygrunsrv\",\"pid\":1576},{\"name\":\"MSBuild\",\"pid\":7836},{\"name\":\"OSPPSVC\",\"pid\":888},{\"name\":\"smss\",\"pid\":348},{\"name\":\"chrome\",\"pid\":5180},{\"name\":\"igfxpers\",\"pid\":4024},{\"name\":\"pageant\",\"pid\":4940},{\"name\":\"epsng_certd\",\"pid\":3492},{\"name\":\"taskhost\",\"pid\":852},{\"name\":\"WINWORD\",\"pid\":8108},{\"name\":\"cntlm\",\"pid\":1812},{\"name\":\"communicator\",\"pid\":4168},{\"name\":\"mongod\",\"pid\":1804},{\"name\":\"wininit\",\"pid\":596},{\"name\":\"conhost\",\"pid\":6340},{\"name\":\"SearchIndexer\",\"pid\":1556},{\"name\":\"chrome\",\"pid\":2752},{\"name\":\"conhost\",\"pid\":6884},{\"name\":\"sqlwriter\",\"pid\":2032},{\"name\":\"svchost\",\"pid\":2304},{\"name\":\"svchost\",\"pid\":2068},{\"name\":\"svchost\",\"pid\":132},{\"name\":\"taskhost\",\"pid\":5004},{\"name\":\"svchost\",\"pid\":392},{\"name\":\"services\",\"pid\":696},{\"name\":\"conhost\",\"pid\":1784},{\"name\":\"ccSvcHst\",\"pid\":1484},{\"name\":\"MSBuild\",\"pid\":6936},{\"name\":\"ngslotd\",\"pid\":1524},{\"name\":\"javaw\",\"pid\":8040},{\"name\":\"eclipse\",\"pid\":3676},{\"name\":\"CmRcService\",\"pid\":5324},{\"name\":\"chrome\",\"pid\":2348},{\"name\":\"CcmExec\",\"pid\":3668},{\"name\":\"conhost\",\"pid\":6140},{\"name\":\"WebDev.WebServer40\",\"pid\":2012},{\"name\":\"rundll32\",\"pid\":3184},{\"name\":\"ONENOTE\",\"pid\":5036},{\"name\":\"MSBuild\",\"pid\":428},{\"name\":\"svchost\",\"pid\":904},{\"name\":\"winlogon\",\"pid\":660},{\"name\":\"chrome\",\"pid\":4480},{\"name\":\"unsecapp\",\"pid\":3652},{\"name\":\"SearchProtocolHost\",\"pid\":1272},{\"name\":\"UcMapi64\",\"pid\":3168},{\"name\":\"ICCProxy\",\"pid\":2916},{\"name\":\"chrome\",\"pid\":6740},{\"name\":\"SCNotification\",\"pid\":4600},{\"name\":\"MSBuild\",\"pid\":7228},{\"name\":\"audiodg\",\"pid\":3088},{\"name\":\"WmiPrvSE\",\"pid\":4324},{\"name\":\"MSBuild\",\"pid\":7940},{\"name\":\"WmiPrvSE\",\"pid\":5564},{\"name\":\"iexplore\",\"pid\":6988},{\"name\":\"TOTALCMD64\",\"pid\":5540},{\"name\":\"svchost\",\"pid\":712},{\"name\":\"ONENOTEM\",\"pid\":3852},{\"name\":\"PresentationHost\",\"pid\":6768},{\"name\":\"MSBuild\",\"pid\":984},{\"name\":\"csrss\",\"pid\":504},{\"name\":\"svchost\",\"pid\":988},{\"name\":\"WmiPrvSE\",\"pid\":5256},{\"name\":\"lsass\",\"pid\":708},{\"name\":\"OUTLOOK\",\"pid\":5108},{\"name\":\"svchost\",\"pid\":1684},{\"name\":\"hkcmd\",\"pid\":2628},{\"name\":\"devenv\",\"pid\":7028},{\"name\":\"svchost\",\"pid\":1444},{\"name\":\"chrome\",\"pid\":4312},{\"name\":\"lsm\",\"pid\":720},{\"name\":\"iexplore\",\"pid\":4548},{\"name\":\"WmiPrvSE\",\"pid\":2636},{\"name\":\"MSBuild\",\"pid\":6488},{\"name\":\"svchost\",\"pid\":1928}],\"killedProcesses\":[]}";

		UserActivityServiceProxy p = new UserActivityServiceProxy();
		//p.commitEvent(new CachedEvent(UUID.fromString("28DE8734-C586-4B4A-96AB-573F1F7BDEF8"), new Date(), data));
		
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
		
	/*	WatcherServer srv = new WatcherServer(16375);
		//srv.addProviderPackage("com.gratex.perconik.useractivity.app.watchers.web");
		//srv.addProviderPackage("com.gratex.perconik.useractivity.app.watchers.ide");
		srv.setServiceClasses(IdeWatcherSvc.class, WebWatcherSvc.class);
		//srv.addProviderClass(WebWatcherSvc.class.getName());
		srv.start();
		
		System.out.println("started");
		System.in.read();
		
		srv.stop();*/
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

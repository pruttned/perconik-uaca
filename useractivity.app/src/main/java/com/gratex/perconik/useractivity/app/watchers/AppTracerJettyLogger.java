package com.gratex.perconik.useractivity.app.watchers;

import org.eclipse.jetty.util.log.Logger;

import com.gratex.perconik.useractivity.app.AppTracer;

/**
 * Logger for Jetty that redirects all messages to AppTracer.
 */
public class AppTracerJettyLogger implements org.eclipse.jetty.util.log.Logger {

	@Override
	public String getName() {
		return AppTracerJettyLogger.class.getName();		
	}

	@Override
	public Logger getLogger(String arg0) {
		return this;
	}
	
	@Override
	public void ignore(Throwable arg0) {	
	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean arg0) {
	}
	
	@Override
	public void debug(Throwable arg0) {
	}

	@Override
	public void debug(String arg0, Object... arg1) {
	}

	@Override
	public void debug(String arg0, long arg1) {
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
	}

	@Override
	public void info(Throwable arg0) {
		AppTracer.getInstance().writeInfo("Jetty: " + AppTracer.getExceptionFullText(arg0));
	}

	@Override
	public void info(String arg0, Object... arg1) {
		AppTracer.getInstance().writeInfo("Jetty: " + format(arg0, arg1));		
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		AppTracer.getInstance().writeInfo("Jetty: " + arg0 + "\n" + AppTracer.getExceptionFullText(arg1));		
	}

	@Override
	public void warn(Throwable arg0) {
		AppTracer.getInstance().writeWarning("Jetty: " + AppTracer.getExceptionFullText(arg0));
	}

	@Override
	public void warn(String arg0, Object... arg1) {
		AppTracer.getInstance().writeWarning("Jetty: " + format(arg0, arg1));
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		AppTracer.getInstance().writeWarning("Jetty: " + arg0 + "\n" + AppTracer.getExceptionFullText(arg1));
	}
	
	// http://grepcode.com/file/repo1.maven.org/maven2/org.eclipse.jetty.aggregate/jetty-all/9.1.3.v20140225/org/eclipse/jetty/util/log/JavaUtilLog.java#JavaUtilLog.format%28java.lang.String%2Cjava.lang.Object%5B%5D%29
	private String format(String msg, Object... args) {
		msg = String.valueOf(msg); // Avoids NPE
		String braces = "{}";
		StringBuilder builder = new StringBuilder();
		int start = 0;
		for (Object arg : args) {
			int bracesIndex = msg.indexOf(braces, start);
			if (bracesIndex < 0) {
				builder.append(msg.substring(start));
				builder.append(" ");
				builder.append(arg);
				start = msg.length();
			} else {
				builder.append(msg.substring(start, bracesIndex));
				builder.append(String.valueOf(arg));
				start = bracesIndex + braces.length();
			}
		}
		builder.append(msg.substring(start));
		return builder.toString();
	}
}

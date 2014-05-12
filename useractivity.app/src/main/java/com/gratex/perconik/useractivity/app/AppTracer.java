package com.gratex.perconik.useractivity.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

public class AppTracer {
	private static final int MAX_ROW_COUNT = 100;
	private static final AppTracer INSTANCE = new AppTracer();
	private ArrayList<AppTracerRow> rows = new ArrayList<AppTracerRow>();
	private Object syncObj = new Object();

	private AppTracer() {
	}

	public static AppTracer getInstance() {
		return INSTANCE;
	}

	public AppTracerRow[] getRows() {
		synchronized (this.syncObj) {
			return this.rows.toArray(new AppTracerRow[this.rows.size()]);
		}
	}

	public void writeError(String message, Throwable exception) {
		writeError(String.format("%s\nError:%s", message, getExceptionFullText(exception)));
	}
	
	public void writeError(Throwable exception) {
		writeError(getExceptionFullText(exception));
	}

	public void writeError(String message) {
		write(message, MessageSeverity.ERROR);
	}

	public void writeWarning(String message) {
		write(message, MessageSeverity.WARNING);
	}

	public void writeInfo(String message) {
		write(message, MessageSeverity.INFO);
	}

	public void write(String message, MessageSeverity severity) {
		synchronized (this.syncObj) {
			this.rows.add(new AppTracerRow(message, severity, new Date()));

			if (this.rows.size() > MAX_ROW_COUNT) {
				this.rows.remove(0);
			}
		}
	}

	public void clear() {
		synchronized (this.syncObj) {
			this.rows.clear();
		}
	}

	public static String getExceptionFullText(Throwable exception) {
		if(exception == null) {
			return "";
		}
		
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		String exceptionFullText = stringWriter.toString();

		printWriter.close();
		try {
			stringWriter.close();
		} catch (IOException e) {
			// 'close' call has no effect
		}

		return exceptionFullText;
	}
}
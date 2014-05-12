package com.gratex.perconik.useractivity.app;

public enum EventCommitJobState {
	
	/**
	 * The job is waiting for a time interval to pass. After the time interval, committing events to the server will begin.
	 */
	WAITING,
	
	/**
	 * Events are currently being sent to the server.
	 */
	COMMITTING,
	
	/**
	 * Sending of events to the server and the timer are stopped.
	 */
	STOPPED
}

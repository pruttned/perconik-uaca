package com.gratex.perconik.useractivity.app;

import java.awt.Component;

import javax.swing.JOptionPane;

public class MessageBox {
	public static void showError(Component parent, String message, Throwable exception, String title) {
		if(exception != null) {
			showError(parent, String.format("%s\nError:\n%s", message, AppTracer.getExceptionFullText(exception)), title);
		} else {
			showError(parent, message, title);
		}
	}
	
	public static void showError(Component parent, String message, String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
	}	
}

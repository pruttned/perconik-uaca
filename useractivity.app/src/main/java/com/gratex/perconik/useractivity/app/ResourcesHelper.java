package com.gratex.perconik.useractivity.app;

import javax.swing.ImageIcon;

public class ResourcesHelper {
	public static ImageIcon getUserActivityIcon16() { return getPngImage("UserActivity16");	}
	public static ImageIcon getInfoSeverityIcon16() { return getPngImage("InfoSeverity16");	}
	public static ImageIcon getWarningSeverityIcon16() { return getPngImage("WarningSeverity16"); }
	public static ImageIcon getErrorSeverityIcon16() { return getPngImage("ErrorSeverity16"); }
	
	public static ImageIcon getPngImage(String nameWithoutExtension) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(nameWithoutExtension, "nameWithoutExtension");
		return getImage(nameWithoutExtension + ".png");
	}
	
	public static ImageIcon getImage(String name) {
		ValidationHelper.checkStringArgNotNullOrWhitespace(name, "name");
		return new ImageIcon(ResourcesHelper.class.getClassLoader().getResource(name));
	}
}

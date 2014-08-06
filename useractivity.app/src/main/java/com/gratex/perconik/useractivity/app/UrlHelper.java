package com.gratex.perconik.useractivity.app;

import java.util.regex.Pattern;

public final class UrlHelper {
  //based on http://stackoverflow.com/a/2514986
  static final Pattern URL_WITH_USER_INFO_REGEX = Pattern.compile("^\\s*((\\w+://).+@([\\w\\d\\.]+)(:[\\d]+){0,1}/*(.*))|(.+@([\\w\\d\\.]+):(.*))");
  static final Pattern USER_INFO_REGEX = Pattern.compile("(?<=(://|^\\s*))[^\\s][^@/]+@");

  public static boolean isUrlWithUserInfo(String str){
    return URL_WITH_USER_INFO_REGEX.matcher(str).matches();
  }

  public static String removeUserInfo(String url){
    return USER_INFO_REGEX.matcher(url).replaceFirst("");
  }
}

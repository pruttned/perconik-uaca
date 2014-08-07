package com.gratex.perconik.useractivity.app;

import java.util.regex.Pattern;

public final class UrlHelper {
  //based on http://stackoverflow.com/a/2514986
  static final Pattern URL_WITH_USER_INFO_REGEX = Pattern.compile("^\\s*(((\\w+://).+@([\\w\\d\\.]+)(:[\\d]+){0,1}/*(.*))|([^\\s]+@([\\w\\d\\.]+):(.*)))");
  static final Pattern USER_INFO_REGEX = Pattern.compile("((?<=://)[^@/]+@|(?<=^|\\s*|^)[^\\s/][^\\s@/]+@)");
  //(?<=(://|^|\\s*))[^\\s][^@/]+@
  //(?<=://)[^@/]+@|
  /**
   * str is url with user info
   * @param str
   * @return
   */
  public static boolean isUrlWithUserInfo(String str){
    return URL_WITH_USER_INFO_REGEX.matcher(str).matches();
  }

  /**
   * removes first occurrence of user info in string (also usable in git clone ..) 
   * @param url
   * @return
   */
  public static String removeUserInfo(String url){
    return USER_INFO_REGEX.matcher(url).replaceFirst("");
  }
}

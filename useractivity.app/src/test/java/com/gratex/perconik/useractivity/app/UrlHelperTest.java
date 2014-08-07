package com.gratex.perconik.useractivity.app;

import org.junit.Test;
import static org.junit.Assert.*;

public class UrlHelperTest{

  @Test
  public void isUrlWithUserInfo() {
   
    assertEquals(true, UrlHelper.isUrlWithUserInfo("user@server:project.git"));
    assertEquals(true, UrlHelper.isUrlWithUserInfo("https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg"));
    assertEquals(true, UrlHelper.isUrlWithUserInfo("https://name@gitsrv01.hq.gratex.com/git/UniusNg"));
    assertEquals(true, UrlHelper.isUrlWithUserInfo("name@gitsrv01.hq.gratex.com:git/UniusNg"));
    assertEquals(false, UrlHelper.isUrlWithUserInfo("name@gitsrv01.hq.gratex.com/git/UniusNg"));
    assertEquals(false, UrlHelper.isUrlWithUserInfo("gitsrv01.hq.gratex.com/git/UniusNg"));
    assertEquals(false, UrlHelper.isUrlWithUserInfo("http://gitsrv01.hq.gratex.com/git/UniusNg"));
    
    assertEquals(true, UrlHelper.isUrlWithUserInfo("  user@server:project.git "));
    assertEquals(true, UrlHelper.isUrlWithUserInfo("   https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(true, UrlHelper.isUrlWithUserInfo(" https://name@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(true, UrlHelper.isUrlWithUserInfo("  name@gitsrv01.hq.gratex.com:git/UniusNg  "));
    assertEquals(false, UrlHelper.isUrlWithUserInfo(" name@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(false, UrlHelper.isUrlWithUserInfo(" gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(false, UrlHelper.isUrlWithUserInfo(" http://gitsrv01.hq.gratex.com/git/UniusNg  "));
    
    assertEquals(false, UrlHelper.isUrlWithUserInfo("text user@server:project.git "));
    assertEquals(false, UrlHelper.isUrlWithUserInfo("text   https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(false, UrlHelper.isUrlWithUserInfo("text https://name@gitsrv01.hq.gratex.com/git/UniusNg  "));
  }

  @Test
  public void removeUserInfo() {
    assertEquals("   https://gitsrv01.hq.gratex.com/git/UniusNg  ", UrlHelper.removeUserInfo("   https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals("https://gitsrv01.hq.gratex.com/git/UniusNg  ", UrlHelper.removeUserInfo("https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals(" https://gitsrv01.hq.gratex.com/git/UniusNg  ", UrlHelper.removeUserInfo(" https://name@gitsrv01.hq.gratex.com/git/UniusNg  "));
    assertEquals("https://gitsrv01.hq.gratex.com/git/UniusNg  ", UrlHelper.removeUserInfo("https://name@gitsrv01.hq.gratex.com/git/UniusNg  "));

    assertEquals("  server:project.git ", UrlHelper.removeUserInfo("  user@server:project.git "));
    assertEquals("server:project.git ", UrlHelper.removeUserInfo("user@server:project.git "));
    assertEquals("  gitsrv01.hq.gratex.com:git/UniusNg ", UrlHelper.removeUserInfo("  name@gitsrv01.hq.gratex.com:git/UniusNg "));
    assertEquals("gitsrv01.hq.gratex.com:git/UniusNg", UrlHelper.removeUserInfo("name@gitsrv01.hq.gratex.com:git/UniusNg"));
    
    assertEquals("git clone text1 text2 gitsrv01.hq.gratex.com:git/UniusNg", UrlHelper.removeUserInfo("git clone text1 text2 name@gitsrv01.hq.gratex.com:git/UniusNg"));
    assertEquals("git clone text1 text2 https://gitsrv01.hq.gratex.com/git/UniusNg", UrlHelper.removeUserInfo("git clone text1 text2 https://name:pwd@gitsrv01.hq.gratex.com/git/UniusNg"));
    assertEquals("git clone text1 text2 gitsrv01.hq.gratex.com:git/UniusNg", UrlHelper.removeUserInfo("git clone text1 text2 gitsrv01.hq.gratex.com:git/UniusNg"));
    assertEquals("git clone text1 text2 https://gitsrv01.hq.gratex.com/git/UniusNg", UrlHelper.removeUserInfo("git clone text1 text2 https://gitsrv01.hq.gratex.com/git/UniusNg"));

  }
}

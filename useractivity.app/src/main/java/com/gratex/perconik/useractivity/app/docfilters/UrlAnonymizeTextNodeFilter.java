package com.gratex.perconik.useractivity.app.docfilters;

import com.gratex.perconik.useractivity.app.UrlHelper;

public class UrlAnonymizeTextNodeFilter implements TextNodeFilter {

  @Override
  public void filter(TextNodeFilterCtx ctx) {
    if (UrlHelper.isUrlWithUserInfo(ctx.getNodeValue())) {
      ctx.setNodeValue(UrlHelper.removeUserInfo(ctx.getNodeValue()));
    }
  }
}

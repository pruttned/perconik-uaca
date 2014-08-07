package com.gratex.perconik.useractivity.app.docfilters;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.gratex.perconik.useractivity.app.EventDocument;
import com.gratex.perconik.useractivity.app.UrlHelper;

public class GitCloneAnonymizeEventDocumentFilter implements EventDocumentFilter {

  private static final String COMMAND_LINE_FIELD = "commandLine";
  static final Pattern IS_GIT_CLONE_REGEX = Pattern.compile(".*git\\s+clone.*");

  @Override
  public void filter(EventDocument doc) {
    //if (TypeUris.BASH_COMMAND.equals(doc.getEventTypeUri())) { //not set yet
    ObjectNode rootNode = doc.getDataTree();

    JsonNode commandLineNode = rootNode.findValue(COMMAND_LINE_FIELD);
    if (commandLineNode != null) {
      String commandLineValue = commandLineNode.textValue();
      if (IS_GIT_CLONE_REGEX.matcher(commandLineValue).matches()) {
        commandLineValue = UrlHelper.removeUserInfo(commandLineValue);
        rootNode.replace(COMMAND_LINE_FIELD, new TextNode(commandLineValue));
      }
    }
  }
  //  }
}

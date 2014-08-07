package com.gratex.perconik.useractivity.app.docfilters;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.gratex.perconik.useractivity.app.EventDocument;

public class BashCurlAnonymizeEventDocumentFilter implements EventDocumentFilter {

  private static final String COMMAND_LINE_FIELD = "commandLine";
  static final Pattern IS_CURL_U_REGEX = Pattern.compile(".*curl.*\\s\\-u\\s.*", Pattern.CASE_INSENSITIVE);
  static final Pattern CURL_U_REGEX = Pattern.compile("(?<=\\-[uU])\\s+[^\\s]+");

  @Override
  public void filter(EventDocument doc) {
    //if (TypeUris.BASH_COMMAND.equals(doc.getEventTypeUri())) { //not set yet
      ObjectNode rootNode = doc.getDataTree();

      JsonNode commandLineNode = rootNode.findValue(COMMAND_LINE_FIELD);
      if (commandLineNode != null) {
        String commandLineValue = commandLineNode.textValue();
        if (IS_CURL_U_REGEX.matcher(commandLineValue).matches()) {
          commandLineValue = CURL_U_REGEX.matcher(commandLineValue).replaceFirst(" *REMOVED*");
          rootNode.replace(COMMAND_LINE_FIELD, new TextNode(commandLineValue));
        }
      }
    }
  //}
}

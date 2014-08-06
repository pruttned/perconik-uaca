package com.gratex.perconik.useractivity.app.docfilters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.gratex.perconik.useractivity.app.EventDocument;

public class EventDocumentTextNodeFilter implements EventDocumentFilter {
  private ArrayList<TextNodeFilter> textNodeFilters = new ArrayList<TextNodeFilter>();

  public EventDocumentTextNodeFilter() {
    //TODO: dynamic registration
    this.textNodeFilters.add(new UrlAnonymizeTextNodeFilter());
  }

  public void filter(EventDocument doc) {
    TextNodeFilterCtx ctx = new TextNodeFilterCtx();

    this.filterNodes(doc.getDataTree(), ctx);
  }

  private void filterNodes(JsonNode node, TextNodeFilterCtx ctx) {
    if (node == null) {
      return;
    }

    if (node.isObject()) {
      this.filterNodes((ObjectNode) node, ctx);
    } else if (node.isArray()) {
      this.filterNodes((ArrayNode) node, ctx);
    }
  }

  private void filterNodes(ArrayNode node, TextNodeFilterCtx ctx) {
    if (node == null) {
      return;
    }

    for (int i = 0; i < node.size(); ++ i) {
      JsonNode childNode = node.get(i);
      if (childNode.isTextual()) {
        String childNodeValue = childNode.textValue();
        if (childNodeValue != null) {

          if (this.executeTextNodeFilters(childNodeValue, ctx)) {
            node.set(i, new TextNode(ctx.getNodeValue()));
          }
        }
      } else {
        this.filterNodes(childNode, ctx);
      }
    }

  }

  private void filterNodes(ObjectNode node, TextNodeFilterCtx ctx) {
    if (node == null) {
      return;
    }

    Iterator<Entry<String, JsonNode>> fieldsIter = node.fields();
    while (fieldsIter.hasNext()) {
      Entry<String, JsonNode> field = fieldsIter.next();
      JsonNode childNode = field.getValue();
      if (childNode.isTextual()) {
        String childNodeValue = childNode.textValue();
        if (childNodeValue != null) {
          String childNodeName = field.getKey();

          if (this.executeTextNodeFilters(childNodeValue, ctx)) {
            node.replace(childNodeName, new TextNode(ctx.getNodeValue()));
          }
        }
      } else {
        this.filterNodes(childNode, ctx);
      }
    }
  }

  private boolean executeTextNodeFilters(String nodeValue, TextNodeFilterCtx ctx) {
    ctx.reset(nodeValue);
    for (TextNodeFilter filter: this.textNodeFilters) {
      filter.filter(ctx);
    }
    return ctx.valueChanged();
  }
}

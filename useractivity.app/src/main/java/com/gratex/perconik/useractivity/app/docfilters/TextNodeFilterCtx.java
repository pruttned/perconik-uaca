package com.gratex.perconik.useractivity.app.docfilters;

public class TextNodeFilterCtx {
  private String nodeValue;
  private boolean changed;

  public String getNodeValue() {
    return this.nodeValue;
  }

  public void setNodeValue(String nodeValue) {
    this.nodeValue = nodeValue;
    this.changed = true;
  }

  public boolean valueChanged() {
    return this.changed;
  }

  public void reset(String nodeValue) {
    this.nodeValue = nodeValue;
    this.changed = false;
  }
}

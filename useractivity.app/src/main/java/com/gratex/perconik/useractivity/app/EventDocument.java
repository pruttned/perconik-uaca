package com.gratex.perconik.useractivity.app;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.gratex.perconik.useractivity.app.docfilters.EventDocumentFilterManager;

/**
 * Writes/Read into serialized event string (e.g. CahcedEvent.getData())
 */
public final class EventDocument {
  private static final ObjectMapper mapper = new ObjectMapper(); //thread safe
  private static final ObjectMapper mapperFormated = new ObjectMapper(); //thread safe
  private static final JavaType mapperFormatedType = TypeFactory.defaultInstance().constructMapType(LinkedHashMap.class, String.class, Object.class);

  private ObjectNode dataTree;

  static {
    mapperFormated.configure(SerializationFeature.INDENT_OUTPUT, true);
    mapperFormated.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
  }

  /**
   * Loads EventDocument from the json svc requesta and also ensures/fix required properties
   * @return
   * @throws IOException
   */
  public static EventDocument loadFromRequest(String jsonStr) throws IOException {
    EventDocument doc = new EventDocument(jsonStr);
    doc.fixAfterReceive();


    return doc;
  }

  public EventDocument(String jsonStr) throws IOException {
    if (ValidationHelper.isStringNullOrWhitespace(jsonStr)) {
      this.dataTree = JsonNodeFactory.instance.objectNode();
    }
    this.dataTree = (ObjectNode) mapper.readTree(jsonStr);
  }

  public ObjectNode getDataTree(){
    return this.dataTree;
  }

  public String toJsonString() throws JsonProcessingException {
    return mapper.writeValueAsString(this.dataTree);
  }

  public String toFormatedJsonString() throws JsonProcessingException {
    return mapperFormated.writeValueAsString(mapperFormated.convertValue(this.dataTree, mapperFormatedType));
  }

  public boolean hasEventTypeUri() {
    return this.dataTree.hasNonNull("eventTypeUri");
  }

  /**
   *
   * @return event type uir or null
   */
  public String getEventTypeUri() {
    return this.tryGetNodeAsString("eventTypeUri");
  }

  public void setEventTypeUri(String uri) {
    this.dataTree.put("eventTypeUri", uri);
  }

  /**
   * For code/paste
   * @return event text field or null
   */
  public String getText() {
    return this.tryGetNodeAsString("text");
  }

  /**
   * For code/paste
   */
  public void setWebUrl(String url) {
    this.dataTree.put("webUrl", url);
  }

  /**
   * For web/copy
   * @return event text field or null
   */
  public String getUrl() {
    return this.tryGetNodeAsString("url");
  }

  /**
   * For web/copy
   * @return event text field or null
   */
  public String getContent() {
    return this.tryGetNodeAsString("content");
  }

  public void setWasCommitForcedByUser(boolean wasCommitForcedByUser) {
    this.dataTree.put("wasCommitForcedByUser", wasCommitForcedByUser);
  }

  public boolean hasTimestamp() {
    return this.dataTree.hasNonNull("timestamp");
  }

  /**
   *
   * @return timestamp or null
   */
  public XMLGregorianCalendar getTimestamp() {
    JsonNode node = this.dataTree.findValue("timestamp");
    if (node == null) {
      return null;
    }

    try {
      return XmlGregorianCalendarHelper.fromString(node.asText());
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Invalid timestamp format");
    }
  }

  public void setTimestamp(XMLGregorianCalendar timestamp) {
    this.dataTree.put("timestamp", timestamp.toString());
  }

  public boolean hasEventId() {
    return this.dataTree.hasNonNull("eventId");
  }

  /**
   *
   * @return eventId or null
   */
  public String getEventId() {
    return this.tryGetNodeAsString("eventId");
  }

  public void setEventId(String eventId) {
    this.dataTree.put("eventId", eventId);
  }

  public boolean hasUser() {
    return this.dataTree.hasNonNull("user");
  }

  public void setUser(String user) {
    this.dataTree.put("user", user);
  }

  public boolean hasWorkstation() {
    return this.dataTree.hasNonNull("workstation");
  }

  public void setWorkstation(String workstation) {
    this.dataTree.put("workstation", workstation);
  }

  private void fixAfterReceive() {
    //ensure timestamp + ToUtc
    XMLGregorianCalendar timestamp = this.getTimestamp();
    if (timestamp != null) {
      this.setTimestamp(XmlGregorianCalendarHelper.toUtc(timestamp)); //ensure UTC
    } else {
      this.setTimestamp(XmlGregorianCalendarHelper.createUtcNow());
    }

    //eventId
    if (!this.hasEventId()) {
      this.setEventId(UUID.randomUUID().toString());
    }

    //user
    this.setUser(Settings.getInstance().getUserName());

    //workstation
    this.setWorkstation(Settings.getInstance().getWorkstationName());

    EventDocumentFilterManager.getInstance().filter(this);
  }

  private String tryGetNodeAsString(String nodeName) {
    JsonNode node = this.dataTree.findValue(nodeName);
    if (node == null) {
      return null;
    }
    return node.asText();
  }
}

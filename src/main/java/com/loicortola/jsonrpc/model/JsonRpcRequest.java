package com.loicortola.jsonrpc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class JsonRpcRequest {
  public enum Type {
    NOTIFICATION, REQUEST
  }

  protected String method;
  protected Map<String, String> params;
  protected String id;
  protected Type type;

  public String getMethod() {
    return method;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public String getId() {
    return id;
  }

  public Type getType() {
    return type;
  }

  /**
   * Combine a sequence of JsonRpcRequests into a list.
   *
   * @param requests the JsonRpcRequests
   * @return the List of JsonRpcRequests
   */
  public static List<JsonRpcRequest> combine(JsonRpcRequest... requests) {
    if (requests == null || requests.length == 0) {
      return null;
    }
    List<JsonRpcRequest> combined = new ArrayList<JsonRpcRequest>(requests.length);
    for (int i = 0; i < requests.length; i++) {
      combined.add(requests[i]);
    }
    return combined;
  }

  // BEGIN GENERATED CODE

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JsonRpcRequest request = (JsonRpcRequest) o;

    if (method != null ? !method.equals(request.method) : request.method != null) return false;
    return !(id != null ? !id.equals(request.id) : request.id != null);

  }

  @Override
  public int hashCode() {
    int result = method != null ? method.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Request{" +
      "method='" + method + '\'' +
      ", params=" + params +
      ", id='" + id + '\'' +
      ", type=" + type +
      '}';
  }


  /**
   * @return a new Request Builder for a normal JSON RPC Request
   */
  public static Builder builder() {
    return new Builder(Type.REQUEST);
  }

  /**
   * @return a new Request Builder for a JSON RPC Notification
   */
  public static Builder notifBuilder() {
    return new Builder(Type.NOTIFICATION);
  }

  public static class Builder {
    protected String method;
    protected Map<String, String> params;
    protected String id;
    protected Type type;

    private Builder(Type t) {
      this.type = t;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder param(String key, String value) {
      if (this.params == null) {
        this.params = new HashMap<String, String>();
      }
      this.params.put(key, value);
      return this;
    }

    public JsonRpcRequest build() {
      JsonRpcRequest request = new JsonRpcRequest();
      request.method = method;
      request.params = params;
      request.id = Type.REQUEST.equals(type) ? UUID.randomUUID().toString() : null;
      request.type = type;
      return request;
    }
  }

  // END GENERATED CODE
}

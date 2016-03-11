package com.loicortola.jsonrpc.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.loicortola.jsonrpc.model.JsonRpcRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public abstract class RequestConverter {
 
  private static final TextNode JSON_RPC_VERSION = new TextNode("2.0"); 
  
  public static ObjectNode convert(JsonRpcRequest request) {
    ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
    // Set protocol version
    node.set("jsonrpc", JSON_RPC_VERSION);
    // Set method
    node.set("method", new TextNode(request.getMethod()));
    
    // Set id
    if (request.getId() != null) {
      node.set("id", new TextNode(request.getId()));
    }
    
    // Set Params
    Map<String, String> params = request.getParams();
    if (params != null && !params.isEmpty()) {
      ObjectMapper mapper = new ObjectMapper();
      node.set("params", mapper.valueToTree(params));  
    }
    return node;
  }

  public static JsonNode convert(List<JsonRpcRequest> requests) {
    ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
    for (JsonRpcRequest request : requests) {
      node.add(convert(request));  
    }
    return node;
  }
}
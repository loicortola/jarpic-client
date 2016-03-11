package com.loicortola.jsonrpc.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.loicortola.jsonrpc.model.Error;
import com.loicortola.jsonrpc.model.JsonRpcResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public abstract class ResponseParser {
  
  public static <T> JsonRpcResponse<T> parseOne(InputStream content, Class<T> resultClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    // One should only return a JSON object
    ObjectNode node = (ObjectNode) mapper.readTree(content);
    return parse(mapper, node, resultClass);
  }
  
  public static <T> List<JsonRpcResponse<T>> parseList(InputStream content, Class<T> resultClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    // List should return a JSON Array
    ArrayNode nodes = (ArrayNode) mapper.readTree(content);
    
    List<JsonRpcResponse<T>> responses = new ArrayList<>(nodes.size());
    for (int i = 0; i < nodes.size(); i++) {
      ObjectNode node = (ObjectNode) nodes.get(i);
      responses.add(parse(mapper, node, resultClass));
    }
    return responses;
  }
  
  private static <T> JsonRpcResponse<T> parse(ObjectMapper mapper, ObjectNode node, Class<T> resultClass) throws IOException {
    JsonRpcResponse.Builder builder = JsonRpcResponse.builder();
    builder.id(node.get("id").asText());
    if (node.has("error")) {
      Error err = mapper.readValue(node.get("error").traverse(), Error.class);
      builder.error(err);
    } else {
      T result = mapper.readValue(node.get("result").traverse(), resultClass);
      builder.result(result);
    }
    return builder.build();
  }
}

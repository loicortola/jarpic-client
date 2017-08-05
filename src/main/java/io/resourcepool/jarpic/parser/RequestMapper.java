package io.resourcepool.jarpic.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import io.resourcepool.jarpic.model.JsonRpcRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Loïc Ortola on 11/03/2016.
 *         This class maps a JsonRpcRequest into a Jackson JsonNode.
 */
public abstract class RequestMapper {

  private static final TextNode JSON_RPC_VERSION = new TextNode("2.0");

  /**
   * Map existing JsonRpcRequest to Jackson JsonNode.
   *
   * @param request the JsonRpcRequest
   * @return the ObjectNode
   */
  public static ObjectNode map(JsonRpcRequest request) {
    if (request == null) {
      return null;
    }
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

  /**
   * Map existing JsonRpcRequests to Jackson JsonNode.
   *
   * @param requests the JsonRpcRequests
   * @return the JsonNode (ArrayNode)
   */
  public static JsonNode map(List<JsonRpcRequest> requests) {
    ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
    for (JsonRpcRequest request : requests) {
      node.add(map(request));
    }
    return node;
  }
}
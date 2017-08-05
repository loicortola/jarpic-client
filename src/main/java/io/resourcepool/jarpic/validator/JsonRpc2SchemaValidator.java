package io.resourcepool.jarpic.validator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Loïc Ortola on 12/03/2016.
 *         This class validates the schema of objects against the JSON RPC 2.0 specification.
 */
public abstract class JsonRpc2SchemaValidator {

  /**
   * Check whether a JSON node is a valid JsonRPC 2.0 object or not.
   *
   * @param tree the JSON tree
   */
  public static void assertValid(JsonNode tree) {
    // Check for nullity or non-object tree
    if (tree == null) {
      throw new NullPointerException("The provided JSON cannot be null");
    }
    if (!tree.isObject()) {
      throw new IllegalArgumentException("The provided JSON is not an Object");
    }
    // Check jsonrpc version
    if (!tree.has("jsonrpc") || !"2.0".equals(tree.get("jsonrpc").asText())) {
      throw new IllegalArgumentException("Only JSON RPC 2.0 schema is supported. Please supply a valid format.");
    }
    // Check if "method" member present <=> request
    if (tree.has("method")) {
      checkValidRequest((ObjectNode) tree);
    } else {
      checkValidResponse((ObjectNode) tree);
    }
  }

  /**
   * Check whether an ObjectNode tree is a valid JsonRPC 2.0 request or not.
   *
   * @param tree the JSON tree
   */
  private static void checkValidRequest(ObjectNode tree) {
    // Check params type
    if (tree.has("params")) {
      JsonNode params = tree.get("params");
      if (!params.isArray() && !params.isObject()) {
        throw new IllegalArgumentException("JSON member 'params' must be an object or an array.");
      }
    }
    // Check id type
    if (tree.has("id")) {
      JsonNode id = tree.get("id");
      if (!(id.isTextual() || id.isNumber() || id.isNull())) {
        throw new IllegalArgumentException("When provided, JSON member 'id' must be a String, a number, or NULL.");
      }
    }
  }

  /**
   * Check whether an ObjectNode tree is a valid JsonRPC 2.0 response or not.
   *
   * @param tree the JSON tree
   */
  private static void checkValidResponse(ObjectNode tree) {
    // Check id
    if (!tree.has("id")) {
      throw new IllegalArgumentException("JSON member 'id' must be provided.");
    }
    // Check id type
    JsonNode id = tree.get("id");
    if (!(id.isTextual() || id.isNumber() || id.isNull())) {
      throw new IllegalArgumentException("When provided, JSON member 'id' must be a String, a number, or NULL.");
    }

    if (tree.has("error")) {
      // Check if result is present (should not)
      if (tree.has("result")) {
        throw new IllegalArgumentException("JSON member 'result' cannot be provided when 'error' is present.");
      }
      // Check error
      JsonNode error = tree.get("error");
      // Check error type
      if (!error.isObject() || !error.has("code") || !error.get("code").isInt() || !error.has("message") || !error.get("message").isTextual()) {
        throw new IllegalArgumentException("JSON member 'error' is not valid.");
      }
    } else {
      // Check result
      if (!tree.has("result")) {
        throw new IllegalArgumentException("JSON member 'result' is mandatory.");
      }
    }
  }
}

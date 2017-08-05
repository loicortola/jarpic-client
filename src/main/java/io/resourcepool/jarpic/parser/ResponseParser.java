package io.resourcepool.jarpic.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.resourcepool.jarpic.model.Error;
import io.resourcepool.jarpic.model.JsonRpcResponse;
import io.resourcepool.jarpic.validator.JsonRpc2SchemaValidator;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 *         This class parses a response (inputstream or JsonNode) to create a JsonRpcResponse object
 */
public abstract class ResponseParser {

  /**
   * Assert JsonNode is valid JSON RPC 2.0 Response (either object or array).
   *
   * @param res the response
   * @throws ParseException     if response is null, empty, or invalid
   */
  public static void assertValid(JsonNode res) throws ParseException {
    if (res.isArray()) {
      // Test valid schema for all entries
      ArrayNode values = (ArrayNode) res;
      for (int i = 0; i < values.size(); i++) {
        JsonRpc2SchemaValidator.assertValid(values.get(i));
      }
    } else if (res.isObject()) {
      // Test valid schema for current entry
      JsonRpc2SchemaValidator.assertValid(res);
    }
  }

  /**
   * Parse JsonNode to JsonRpcResponse with resultClass.
   *
   * @param content     the ObjectNode JSON tree
   * @param resultClass the result payload DTO class. Should be a Serializable POJO
   * @param <T>         the type inference for the result
   * @return the JsonRpcResponse
   * @throws IOException              if response is parsing error occurs
   * @throws ParseException     if response is null, empty, or invalid
   */
  public static <T> JsonRpcResponse<T> parseOne(JsonNode content, Class<T> resultClass) throws IOException, ParseException {
    ObjectMapper mapper = new ObjectMapper();
    assertValid(content);
    // One should only return a JSON object
    return parse(mapper, (ObjectNode) content, resultClass);

  }

  /**
   * Parse InputStream to JsonRpcResponse with resultClass.
   *
   * @param content     the InputStream content
   * @param resultClass the result payload DTO class. Should be a Serializable POJO
   * @param <T>         the type inference for the result
   * @return the JsonRpcResponse
   * @throws IOException              if response is parsing error occurs
   * @throws ParseException     if response is null, empty, or invalid
   */
  public static <T> JsonRpcResponse<T> parseOne(InputStream content, Class<T> resultClass) throws IOException, ParseException {
    ObjectMapper mapper = new ObjectMapper();
    // One should only return a JSON object
    JsonNode node = mapper.readTree(content);
    if (!node.isObject()) {
      return null;
    }
    return parseOne(node, resultClass);
  }

  /**
   * Parse JsonNode to JsonRpcResponse List with resultClass.
   *
   * @param content     the ArrayNode JSON Tree
   * @param resultClass the result payload DTO class. Should be a Serializable POJO
   * @param <T>         the type inference for the result
   * @return the JsonRpcResponse List
   * @throws IOException              if response is parsing error occurs
   */
  public static <T> List<JsonRpcResponse<T>> parseList(JsonNode content, Class<T> resultClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    List<JsonRpcResponse<T>> responses = new ArrayList<JsonRpcResponse<T>>(content.size());
    for (int i = 0; i < content.size(); i++) {
      ObjectNode node = (ObjectNode) content.get(i);
      responses.add(parse(mapper, node, resultClass));
    }
    return responses;
  }

  /**
   * Parse InputStream to JsonRpcResponse List with resultClass.
   *
   * @param content     the InputStream content
   * @param resultClass the result payload DTO class. Should be a Serializable POJO
   * @param <T>         the type inference for the result
   * @return the JsonRpcResponse List
   * @throws IOException              if response is parsing error occurs
   */
  public static <T> List<JsonRpcResponse<T>> parseList(InputStream content, Class<T> resultClass) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    // List should return a JSON Array
    JsonNode nodes = mapper.readTree(content);
    if (!nodes.isArray()) {
      return null;
    }
    return parseList(nodes, resultClass);
  }

  /**
   * Private parsing method.
   *
   * @param mapper      the Jackson ObjectMapper
   * @param node        the Original JsonNode onto which place the following object
   * @param resultClass the result class
   * @param <T>         the type inference for the result
   * @return the JsonRpcResponse
   * @throws IOException              if response is parsing error occurs
   */
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

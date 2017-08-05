package io.resourcepool.jarpic.client;

import io.resourcepool.jarpic.model.JsonRpcCallback;
import io.resourcepool.jarpic.model.JsonRpcMultiCallback;
import io.resourcepool.jarpic.model.JsonRpcRequest;
import io.resourcepool.jarpic.model.JsonRpcResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public interface JsonRpcClient {

  /**
   * Send synchronous Json RPC request to server.
   * Deserialize response as Json RPC Response
   * If you are looking for an asynchronous response, take a look at {@link io.resourcepool.jarpic.client.JsonRpcClient#send(JsonRpcRequest, Class, JsonRpcCallback)} instead.
   *
   * @param req         the JsonRpcRequest object
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param <T>         The type inference for the result class
   * @return the JsonRpcResponse
   * @throws IOException if request fails
   */
  <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException;

  /**
   * Send asynchronous Json RPC request to server.
   * Response will be Deserialized as a Json RPC Response.
   *
   * @param req         the JsonRpcRequest object
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param callback    the asynchronous callback which will return the response once received.
   */
  void send(JsonRpcRequest req, Class resultClass, JsonRpcCallback callback);

  /**
   * Send Json RPC request array to server.
   * Deserialize response synchronously as Json RPC Response array
   *
   * @param reqs        the list of JsonRpcRequest objects
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param <T>         The type inference for the result class
   * @return the list of JsonRpcResponse objects
   * @throws IOException if client request fails
   */
  @Nullable
  <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException;

  /**
   * Send asynchronous Json RPC request array to server.
   * Response will be deserialize as Json RPC Response array.
   *
   * @param reqs        the list of JsonRpcRequest objects
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param callback    the asynchronous callback which will return the response once received.
   */
  void send(List<JsonRpcRequest> reqs, Class resultClass, JsonRpcMultiCallback callback);


}

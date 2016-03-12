package com.loicortola.jsonrpc.client;

import com.loicortola.jsonrpc.model.JsonRpcRequest;
import com.loicortola.jsonrpc.model.JsonRpcResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public interface JsonRpcClient {

  /**
   * Send Json RPC request to server.
   * Deserialize response as Json RPC Response
   * @param req the JsonRpcRequest object
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param <T> The type inference for the result class
   * @return the JsonRpcResponse
   * @throws IOException if request fails
   */
  <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException;

  /**
   * Send Json RPC request array to server.
   * Deserialize response as Json RPC Response array
   * @param reqs the list of JsonRpcRequest objects
   * @param resultClass the class for the result. Needs to be a POJO, Serializable
   * @param <T> The type inference for the result class
   * @return the list of JsonRpcResponse objects
   * @throws IOException if client request fails
   */
  <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException;
}

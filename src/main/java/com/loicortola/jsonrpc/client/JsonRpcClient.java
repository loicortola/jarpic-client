package com.loicortola.jsonrpc.client;

import com.loicortola.jsonrpc.dto.JsonRpcRequest;
import com.loicortola.jsonrpc.dto.JsonRpcResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public interface JsonRpcClient {

  <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException;

  <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException;
}

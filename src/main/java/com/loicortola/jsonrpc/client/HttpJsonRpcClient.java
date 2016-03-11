package com.loicortola.jsonrpc.client;

import com.loicortola.jsonrpc.model.JsonRpcRequest;
import com.loicortola.jsonrpc.model.JsonRpcResponse;
import com.loicortola.jsonrpc.parser.RequestConverter;
import com.loicortola.jsonrpc.parser.ResponseParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class HttpJsonRpcClient implements JsonRpcClient {
  
  private OkHttpClient client;
  private String endpoint;
  private static final MediaType JSON = MediaType.parse("application/json");
  
  public HttpJsonRpcClient(String endpoint) {
    this.endpoint = endpoint;
    this.client = new OkHttpClient();
  }
  
  public HttpJsonRpcClient(String endpoint, OkHttpClient client) {
    this.endpoint = endpoint;
    this.client = client;
  }
  
  @Override
  public <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException {
    Request request = new Request.Builder()
      .url(endpoint)
      .post(RequestBody.create(JSON, RequestConverter.convert(req).toString()))
      .build();
    Response response = client.newCall(request).execute();
    return ResponseParser.parseOne(response.body().byteStream(), resultClass);
  }

  @Override
  public <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException {
    Request request = new Request.Builder()
      .url(endpoint)
      .post(RequestBody.create(JSON, RequestConverter.convert(reqs).toString()))
      .build();
    Response response = client.newCall(request).execute();
    return ResponseParser.parseList(response.body().byteStream(), resultClass);
  }
}

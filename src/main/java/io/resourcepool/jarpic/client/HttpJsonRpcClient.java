package io.resourcepool.jarpic.client;

import io.resourcepool.jarpic.model.Error;
import io.resourcepool.jarpic.model.JsonRpcRequest;
import io.resourcepool.jarpic.model.JsonRpcResponse;
import io.resourcepool.jarpic.parser.RequestMapper;
import io.resourcepool.jarpic.parser.ResponseParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Loïc Ortola on 11/03/2016.
 *         This implementation proposes a simple Http Json RPC 2.0 Client.
 */
public class HttpJsonRpcClient implements JsonRpcClient {

  private OkHttpClient client;
  private String endpoint;
  private static final MediaType JSON = MediaType.parse("application/json; charset=UTF-8");

  /**
   * Construct a new HttpJsonRpcClient.
   * One Client is made for one endpoint.
   *
   * @param endpoint the target url endpoint
   */
  public HttpJsonRpcClient(String endpoint) {
    this.endpoint = endpoint;
    this.client = new OkHttpClient();
  }

  /**
   * Construct a new HttpJsonRpcClient.
   * One Client is made for one endpoint.
   *
   * @param endpoint the target url endpoint
   * @param client   the http client instance
   */
  public HttpJsonRpcClient(String endpoint, OkHttpClient client) {
    this.endpoint = endpoint;
    this.client = client;
  }

  @Override
  public <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException {
    Request request = new Request.Builder()
      .url(endpoint)
      .post(RequestBody.create(JSON, RequestMapper.map(req).toString()))
      .build();
    Response response = client.newCall(request).execute();
    if (response.header("content-type", "").contains("application/json")) {
      return ResponseParser.parseOne(response.body().byteStream(), resultClass);
    } else {
      // No JSON response. If 200, we assume it was a notification. Else, it was an error
      if (response.code() == 200) {
        return null;
      }
      return JsonRpcResponse.builder().id(req.getId()).error(new Error(-32000, new StringBuilder(Integer.toString(response.code())).append(" - ").append(response.message()).toString(), null)).build();
    }
  }

  @Override
  public <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException {
    Request request = new Request.Builder()
      .url(endpoint)
      .post(RequestBody.create(JSON, RequestMapper.map(reqs).toString()))
      .build();
    Response response = client.newCall(request).execute();
    if (response.header("content-type", "").contains("application/json")) {
      return ResponseParser.parseList(response.body().byteStream(), resultClass);
    } else {
      // No JSON response. If 200, we assume it was a notification. Else, it was an error
      if (response.code() == 200) {
        return null;
      }
      List<JsonRpcResponse<T>> responses = new ArrayList<JsonRpcResponse<T>>(reqs.size());
      for (JsonRpcRequest req : reqs) {
        responses.add(JsonRpcResponse.builder().id(req.getId()).error(new Error(-32000, new StringBuilder(Integer.toString(response.code())).append(" - ").append(response.message()).toString(), null)).build());
      }
      return responses;
    }
  }
}

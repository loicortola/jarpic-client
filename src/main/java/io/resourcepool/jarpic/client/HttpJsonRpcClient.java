package io.resourcepool.jarpic.client;
import io.resourcepool.jarpic.model.Error;
import io.resourcepool.jarpic.model.JsonRpcCallback;
import io.resourcepool.jarpic.model.JsonRpcMultiCallback;
import io.resourcepool.jarpic.model.JsonRpcRequest;
import io.resourcepool.jarpic.model.JsonRpcResponse;
import io.resourcepool.jarpic.parser.RequestMapper;
import io.resourcepool.jarpic.parser.ResponseParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.text.ParseException;
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> JsonRpcResponse<T> send(JsonRpcRequest req, Class<T> resultClass) throws IOException {
    Request request = buildOkHttpRequest(req);
    Response response = client.newCall(request).execute();
    if (response.code() >= 300) {
      return buildError(req, response);
    }
    String contentType = response.header("content-type");
    if (contentType != null && contentType.contains("application/json")) {
      try {
        return ResponseParser.parseOne(response.body().byteStream(), resultClass);
      } catch (ParseException e) {
        return buildError(req, null);
      }
    } else {
      // No JSON response. If 200, we assume it was a notification.
      return null;
    }
  }

  @Override
  public void send(final JsonRpcRequest req, final Class resultClass, final JsonRpcCallback callback) {
    Request request = buildOkHttpRequest(req);
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        callback.onFailure(e);
      }

      @SuppressWarnings("unchecked")
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.code() >= 300) {
          callback.onResponse(buildError(req, response));
          return;
        }
        String contentType = response.header("content-type");
        if (contentType != null && contentType.contains("application/json")) {
          try {
            callback.onResponse(ResponseParser.parseOne(response.body().byteStream(), resultClass));
          } catch (ParseException e) {
            callback.onResponse(buildError(req, null));
          }
        } else {
          // No JSON response. If 200, we assume it was a notification.
          callback.onResponse(null);
        }
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<JsonRpcResponse<T>> send(List<JsonRpcRequest> reqs, Class<T> resultClass) throws IOException {
    Request request = new Request.Builder()
        .url(endpoint)
        .post(RequestBody.create(JSON, RequestMapper.map(reqs).toString()))
        .build();
    Response response = client.newCall(request).execute();
    if (response.code() >= 300) {
      List<JsonRpcResponse<T>> responses = new ArrayList<JsonRpcResponse<T>>(reqs.size());
      for (JsonRpcRequest req : reqs) {
        responses.add(buildError(req, response));
      }
      return responses;
    }
    String contentType = response.header("content-type");
    if (contentType != null && contentType.contains("application/json")) {
      return ResponseParser.parseList(response.body().byteStream(), resultClass);
    }
    // No JSON response. If 200, we assume it was a notification.
    return null;
  }

  @Override
  public void send(final List<JsonRpcRequest> reqs, final Class resultClass, final JsonRpcMultiCallback callback) {
    Request request = buildOkHttpRequest(reqs);
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        callback.onFailure(e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.code() >= 300) {
          List<JsonRpcResponse> responses = new ArrayList<JsonRpcResponse>(reqs.size());
          for (JsonRpcRequest req : reqs) {
            responses.add(buildError(req, response));
          }
          callback.onResponse(responses);
          return;
        }
        String contentType = response.header("content-type");
        if (contentType != null && contentType.contains("application/json")) {
          callback.onResponse(ResponseParser.parseList(response.body().byteStream(), resultClass));
          return;
        }
        // No JSON response. If 200, we assume it was a notification.
        callback.onResponse(null);
      }
    });
  }

  /**
   * Build regular json-rpc http request.
   *
   * @param req the JsonRpcRequest object
   * @return the OkHttp request
   */
  private Request buildOkHttpRequest(JsonRpcRequest req) {
    return new Request.Builder()
        .url(endpoint)
        .post(RequestBody.create(JSON, RequestMapper.map(req).toString()))
        .build();
  }

  /**
   * Build regular json-rpc http request.
   *
   * @param reqs the list of JsonRpcRequest objects
   * @return the OkHttp request
   */
  private Request buildOkHttpRequest(List<JsonRpcRequest> reqs) {
    return new Request.Builder()
        .url(endpoint)
        .post(RequestBody.create(JSON, RequestMapper.map(reqs).toString()))
        .build();
  }

  /**
   * Build Error Response depending on statuscode and content.
   *
   * @param req      the JsonRpcRequest object
   * @param response the OkHttp response
   * @return the OkHttp request
   */
  private JsonRpcResponse buildError(JsonRpcRequest req, Response response) {
    JsonRpcResponse.Builder b = JsonRpcResponse
        .builder()
        .id(req.getId());
    if (response == null) {
      b.error(new Error(-32700, "Parse error", null));
    } else if (response.code() == 400) {
      b.error(new Error(-32602, "Invalid params", null));
    } else if (response.code() == 404) {
      b.error(new Error(-32601, "Method not found", null));
    } else {
      b.error(new Error(-32000, Integer.toString(response.code()) + " - " + response.message(), null));
    }
    return b.build();
  }
}

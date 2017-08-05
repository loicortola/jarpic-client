package io.resourcepool.jarpic;

import io.resourcepool.jarpic.model.Error;
import io.resourcepool.jarpic.model.Result;
import io.resourcepool.jarpic.client.HttpJsonRpcClient;
import io.resourcepool.jarpic.client.JsonRpcClient;
import io.resourcepool.jarpic.model.JsonRpcRequest;
import io.resourcepool.jarpic.model.JsonRpcResponse;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class ClientTest {
  
  public final String endpoint;
  public final String apiKey;
  
  public ClientTest() throws IOException {
    Properties p = new Properties();
    // Load client.properties sample file
    p.load(this.getClass().getResourceAsStream("/client.properties"));
    this.endpoint = p.getProperty("jsonrpc.endpoint");
    this.apiKey = p.getProperty("jsonrpc.api  key");
  }

  @Test
  public void trySingleNotificationValid() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/notify");

    JsonRpcRequest req = JsonRpcRequest.notifBuilder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<Result> res = client.send(req, Result.class);
    
    Assert.assertNull(req.getId());
    Assert.assertNull(res);
  }

  @Test
  public void trySingleRequestValid() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single");
    
    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("hello")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<String> res = client.send(req, String.class);
    
    Assert.assertEquals(res.getId(), req.getId());
    Assert.assertEquals(res.getResult(), "Hey Baby");
  }

  @Test
  public void trySingleRequestError() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single-error");

    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<Result> res = client.send(req, Result.class);

    Assert.assertEquals(res.getId(), req.getId());
    Assert.assertEquals(res.getError(), new Error(-32602, "Invalid params", null));
  }

  public void trySingleRequestIllegal() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single-illegal");

    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<Result> res = client.send(req, Result.class);
    Assert.assertEquals(res.getError(), new Error(-32700, "Parse error", null));
  }

  @Test
  public void trySingleRequestNotFound() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/not-here");

    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<Result> res = client.send(req, Result.class);
    Assert.assertEquals(res.getError(), new Error(-32601, "Method not found", null));
  }

  @Test
  public void tryMultiRequestsValid() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/multi");

    JsonRpcRequest req1 = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcRequest req2 = JsonRpcRequest.builder()
      .method("cmd::status")
      .param("apiKey", apiKey)
      .build();
    JsonRpcRequest req3 = JsonRpcRequest.builder()
      .method("cmd::display")
      .param("message", "Hello World!")
      .param("apiKey", apiKey)
      .build();

    List<JsonRpcRequest> reqs = JsonRpcRequest.combine(req1, req2, req3);
    
    List<JsonRpcResponse<Result>> res = client.send(reqs, Result.class);
    
    Assert.assertEquals(res.size(), 3);
    
    Result result = new Result();
    // Res1
    result.value = "start-successful";
    result.collectDate = "2016-03-03";
    Assert.assertEquals(res.get(0).getId(), req1.getId());
    Assert.assertEquals(res.get(0).getResult(), result);
    // Res2
    result.value = "server-started";
    Assert.assertEquals(res.get(1).getId(), req2.getId());
    Assert.assertEquals(res.get(1).getResult(), result);
    // Res2
    result.value = "Hello World!";
    Assert.assertEquals(res.get(2).getId(), req3.getId());
    Assert.assertEquals(res.get(2).getResult(), result);
    
  }

  @Test
  public void tryMultiRequestsInvalid() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/multi-invalid");

    JsonRpcRequest req1 = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    JsonRpcRequest req2 = JsonRpcRequest.builder()
      .method("cmd::status")
      .param("apiKey", apiKey)
      .build();
    JsonRpcRequest req3 = JsonRpcRequest.builder()
      .method("cmd::display")
      .param("message", "Hello World!")
      .param("apiKey", apiKey)
      .build();

    List<JsonRpcRequest> reqs = JsonRpcRequest.combine(req1, req2, req3);

    List<JsonRpcResponse<Result>> res = client.send(reqs, Result.class);

    Assert.assertEquals(res.size(), 3);
    // Res1
    Assert.assertEquals(res.get(0).getError(), new Error(-32601, "Method not found", null));
    // Res2
    Assert.assertEquals(res.get(1).getError(), new Error(-32601, "Method not found", null));
    // Res3
    Assert.assertEquals(res.get(2).getError(), new Error(-32601, "Method not found", null));

  }
}

package io.resourcepool.jarpic;

import io.resourcepool.jarpic.client.HttpJsonRpcClient;
import io.resourcepool.jarpic.client.JsonRpcClient;
import io.resourcepool.jarpic.model.Error;
import io.resourcepool.jarpic.model.JsonRpcCallback;
import io.resourcepool.jarpic.model.JsonRpcMultiCallback;
import io.resourcepool.jarpic.model.JsonRpcRequest;
import io.resourcepool.jarpic.model.JsonRpcResponse;
import io.resourcepool.jarpic.model.Result;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class AsyncClientTest {
  
  public final String endpoint;
  public final String apiKey;
  /**
   * Countdown latch
   */
  private CountDownLatch lock;
  
  public AsyncClientTest() throws IOException {
    Properties p = new Properties();
    // Load client.properties sample file
    p.load(this.getClass().getResourceAsStream("/client.properties"));
    this.endpoint = p.getProperty("jsonrpc.endpoint");
    this.apiKey = p.getProperty("jsonrpc.api  key");
  }
  
  @Before
  public void initLatch() {
    lock = new CountDownLatch(1);
  }

  @Test
  public void trySingleNotificationValid() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/notify");

    final JsonRpcRequest req = JsonRpcRequest.notifBuilder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    client.send(req, Result.class, new JsonRpcCallback() {
      @Override
      public void onResponse(@Nullable JsonRpcResponse res) {
        Assert.assertNull(req.getId());
        Assert.assertNull(res);
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    
    lock.await(10, TimeUnit.SECONDS);
    
  }

  @Test
  public void trySingleRequestValid() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single");
    
    final JsonRpcRequest req = JsonRpcRequest.builder()
      .method("hello")
      .param("apiKey", apiKey)
      .build();
    client.send(req, String.class, new JsonRpcCallback() {
      @Override
      public void onResponse(@Nullable JsonRpcResponse res) {
        Assert.assertEquals(res.getId(), req.getId());
        Assert.assertEquals(res.getResult(), "Hey Baby");
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    
    lock.await(10, TimeUnit.SECONDS);
  }

  @Test
  public void trySingleRequestError() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single-error");

    final JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    client.send(req, Result.class, new JsonRpcCallback() {
      @Override
      public void onResponse(@Nullable JsonRpcResponse res) {
        Assert.assertEquals(req.getId(), res.getId());
        Assert.assertEquals(new Error(-32602, "Invalid params", null), res.getError()); 
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });

    lock.await(10, TimeUnit.SECONDS);
  }

  public void trySingleRequestIllegal() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/single-illegal");

    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    client.send(req, Result.class, new JsonRpcCallback() {
      @Override
      public void onResponse(@Nullable JsonRpcResponse res) {
        Assert.assertEquals(res.getError(), new Error(-32700, "Parse error", null));
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    lock.await(10, TimeUnit.SECONDS);
  }

  @Test
  public void trySingleRequestNotFound() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/not-here");

    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    client.send(req, Result.class, new JsonRpcCallback() {
      @Override
      public void onResponse(@Nullable JsonRpcResponse res) {
        Assert.assertEquals(res.getError(), new Error(-32601, "Method not found", null));
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    lock.await(10, TimeUnit.SECONDS);
  }

  @Test
  public void tryMultiRequestsValid() throws IOException, InterruptedException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint + "/multi");

    final JsonRpcRequest req1 = JsonRpcRequest.builder()
      .method("cmd::start")
      .param("apiKey", apiKey)
      .build();
    final JsonRpcRequest req2 = JsonRpcRequest.builder()
      .method("cmd::status")
      .param("apiKey", apiKey)
      .build();
    final JsonRpcRequest req3 = JsonRpcRequest.builder()
      .method("cmd::display")
      .param("message", "Hello World!")
      .param("apiKey", apiKey)
      .build();

    List<JsonRpcRequest> reqs = JsonRpcRequest.combine(req1, req2, req3);
    
    client.send(reqs, Result.class, new JsonRpcMultiCallback<Result>() {
      @Override
      public void onResponse(@Nullable List<JsonRpcResponse<Result>> res) {
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
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    lock.await(10, TimeUnit.SECONDS);
    
  }

  @Test
  public void tryMultiRequestsInvalid() throws IOException, InterruptedException {
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

    client.send(reqs, Result.class, new JsonRpcMultiCallback<Result>() {
      @Override
      public void onResponse(@Nullable List<JsonRpcResponse<Result>> res) {
        
        Assert.assertEquals(res.size(), 3);
        // Res1
        Assert.assertEquals(res.get(0).getError(), new Error(-32601, "Method not found", null));
        // Res2
        Assert.assertEquals(res.get(1).getError(), new Error(-32601, "Method not found", null));
        // Res3
        Assert.assertEquals(res.get(2).getError(), new Error(-32601, "Method not found", null));
        lock.countDown();
      }

      @Override
      public void onFailure(IOException ex) {
        Assert.fail();
        lock.countDown();
      }
    });
    lock.await(10, TimeUnit.SECONDS);
  }
}

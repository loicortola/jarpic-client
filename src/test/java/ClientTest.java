import com.loicortola.jsonrpc.client.HttpJsonRpcClient;
import com.loicortola.jsonrpc.client.JsonRpcClient;
import com.loicortola.jsonrpc.dto.JsonRpcRequest;
import com.loicortola.jsonrpc.dto.JsonRpcResponse;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class ClientTest {
  
  public final String endpoint;
  public final String apiKey;
  
  public static class Result {
    public String value;
    public String collectDate;

    @Override
    public String toString() {
      return "Result{" +
        "value='" + value + '\'' +
        ", collectDate='" + collectDate + '\'' +
        '}';
    }
  }
  
  public ClientTest() throws IOException {
    Properties p = new Properties();
    p.load(this.getClass().getResourceAsStream("client.properties"));
    this.endpoint = p.getProperty("jsonrpc.endpoint");
    this.apiKey = p.getProperty("jsonrpc.apikey");
  }

  @Test
  public void tryClient() throws IOException {
    JsonRpcClient client = new HttpJsonRpcClient(endpoint);
    
    JsonRpcRequest req = JsonRpcRequest.builder()
      .method("cmd::execCmd")
      .param("apiKey", apiKey)
      .build();
    JsonRpcResponse<Result> res = client.send(req, Result.class);
    System.out.println("Response is:");
    System.out.println(res);
  }
}

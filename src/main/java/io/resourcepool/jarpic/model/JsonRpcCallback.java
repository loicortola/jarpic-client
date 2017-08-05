package io.resourcepool.jarpic.model;


import javax.annotation.Nullable;
import java.io.IOException;

/**
 * This class allows for a JSON RPC request to be made asynchronously.
 * The client will call either onResponse with the successfully deserialized content, or onFailure with the relevant exception if failed.
 *
 * @author Loïc Ortola on 05/08/2017
 */
public interface JsonRpcCallback<T> {
  /**
   * Called when response has been received from remote server.
   *
   * @param result the parsed JsonRpcResponse, or null if none returned
   */
  void onResponse(@Nullable JsonRpcResponse<T> result);

  /**
   * Called when an error occured during the Http Request.
   *
   * @param ex the exception
   */
  void onFailure(IOException ex);
}

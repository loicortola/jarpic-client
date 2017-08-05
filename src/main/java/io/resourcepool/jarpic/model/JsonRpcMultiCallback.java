package io.resourcepool.jarpic.model;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

/**
 * This class allows for a set of JSON RPC requests to be made asynchronously.
 * The client will call either onResponse with the successfully deserialized content, or onFailure with the relevant exception if failed.
 *
 * @author Loïc Ortola on 05/08/2017
 */
public interface JsonRpcMultiCallback<T> {
  /**
   * Called when response has been received from remote server.
   *
   * @param results the parsed set of JsonRpcResponse objects, or null if none returned
   */
  void onResponse(@Nullable List<JsonRpcResponse<T>> results);

  /**
   * Called when an error occured during the Http Request.
   *
   * @param ex the exception
   */
  void onFailure(IOException ex);
}
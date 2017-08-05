package io.resourcepool.jarpic.model;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class JsonRpcResponse<T> {
  protected String id;
  protected T result;
  protected Error error;

  public String getId() {
    return id;
  }

  public T getResult() {
    return result;
  }

  public Error getError() {
    return error;
  }

  // BEGIN GENERATED CODE

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JsonRpcResponse response = (JsonRpcResponse) o;

    return !(id != null ? !id.equals(response.id) : response.id != null);

  }

  // BEGIN GENERATED CODE

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Response{" +
      "id='" + id + '\'' +
      ", result=" + result +
      ", error=" + error +
      '}';
  }

  /**
   * @param <T> type inference for result
   * @return the response builder
   */
  public static <T> Builder<T> builder() {
    return new Builder<T>();
  }

  public static class Builder<T> {
    protected String id;
    protected T result;
    protected Error error;


    private Builder() {

    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder result(T result) {
      this.result = result;
      return this;
    }

    public Builder error(Error error) {
      this.error = error;
      return this;
    }

    public JsonRpcResponse build() {
      JsonRpcResponse response = new JsonRpcResponse<T>();
      response.id = id;
      response.result = result;
      response.error = error;
      return response;
    }
  }

  // END GENERATED CODE
}

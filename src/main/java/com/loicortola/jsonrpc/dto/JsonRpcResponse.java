package com.loicortola.jsonrpc.dto;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class JsonRpcResponse<T> {
  protected String id;
  protected T result;
  protected Error error;
  
  public JsonRpcResponse() {
    
  }
  
  public T getResult() {
    return result;
  }

  public Error getError() {
    return error;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    JsonRpcResponse response = (JsonRpcResponse) o;

    return !(id != null ? !id.equals(response.id) : response.id != null);

  }

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

  public static <T> Builder<T> builder() {
    return new Builder<>();
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
}

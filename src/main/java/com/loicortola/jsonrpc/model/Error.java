package com.loicortola.jsonrpc.model;

/**
 * @author Loïc Ortola on 11/03/2016.
 */
public class Error {
  private String code;
  private String message;
  private Object data;

  /**
   * Constructor.
   * @param code the error code
   * @param message the error message
   * @param data the error data Payload
   */
  public Error(String code, String message, Object data) {
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }

  // BEGIN GENERATED CODE

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Error error = (Error) o;

    if (code != null ? !code.equals(error.code) : error.code != null) return false;
    return !(message != null ? !message.equals(error.message) : error.message != null);

  }

  @Override
  public int hashCode() {
    int result = code != null ? code.hashCode() : 0;
    result = 31 * result + (message != null ? message.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Error{" +
      "code='" + code + '\'' +
      ", message='" + message + '\'' +
      ", data=" + data +
      '}';
  }

  // END GENERATED CODE
}

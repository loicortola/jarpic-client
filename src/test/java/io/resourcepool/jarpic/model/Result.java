package io.resourcepool.jarpic.model;

/**
 * @author Loïc Ortola on 12/03/2016.
 */
public class Result {
  public String value;
  public String collectDate;

  /**
   * Default Constructor.
   */
  public Result() {
    
  }

  public String getCollectDate() {
    return collectDate;
  }

  public String getValue() {
    return value;
  }

  public void setCollectDate(String collectDate) {
    this.collectDate = collectDate;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Result result = (Result) o;

    if (value != null ? !value.equals(result.value) : result.value != null) return false;
    return !(collectDate != null ? !collectDate.equals(result.collectDate) : result.collectDate != null);

  }

  @Override
  public int hashCode() {
    int result = value != null ? value.hashCode() : 0;
    result = 31 * result + (collectDate != null ? collectDate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Result{" +
      "value='" + value + '\'' +
      ", collectDate='" + collectDate + '\'' +
      '}';
  }
}

/** This class auto-generated by org.synchronoss.utils.cpo.Proxy **/

package org.synchronoss.cpo.meta.bean;

public class CpoFunctionBean implements java.io.Serializable {

  /* Properties */
  private java.lang.String expression;
  private java.lang.String description;

  public CpoFunctionBean() {
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  /* Getters and Setters */

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CpoFunctionBean that = (CpoFunctionBean)o;

    if (getExpression() != null ? !getExpression().equals(that.getExpression()) : that.getExpression() != null)
      return false;
    if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
      return false;

    return true;
  }

  public int hashCode() {
    int result = 0;
    result = 31 * result + getClass().getName().hashCode();
    result = 31 * result + (getExpression() != null ? getExpression().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("expression = " + getExpression() + "\n");
    str.append("description = " + getDescription() + "\n");
    return str.toString();
  }
}
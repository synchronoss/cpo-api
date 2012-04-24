/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */

package org.synchronoss.cpo.meta.bean;

public class CpoArgumentBean implements java.io.Serializable {

  /* Properties */
  private java.lang.String attributeName;
  private java.lang.String description;

  public CpoArgumentBean() {
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /* Getters and Setters */

  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CpoArgumentBean that = (CpoArgumentBean)o;

    if (getAttributeName() != null ? !getAttributeName().equals(that.getAttributeName()) : that.getAttributeName() != null)
      return false;
    if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
      return false;

    return true;
  }

  public int hashCode() {
    int result = 0;
    result = 31 * result + getClass().getName().hashCode();
    result = 31 * result + (getAttributeName() != null ? getAttributeName().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("attributeName = " + getAttributeName() + "\n");
    str.append("description = " + getDescription() + "\n");
    return str.toString();
  }
}

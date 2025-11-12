/*
 * Copyright (C) 2003-2025 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.meta.domain.CpoArgument;

import java.io.Serial;

/**
 * JdbcCpoArgument is a class that defines the arguments to a JDBC expression
 *
 * @author david berry
 */
public class JdbcCpoArgument extends CpoArgument implements java.io.Serializable, Cloneable {

  /**
   * Version Id for this class.
   */
  @Serial
  private static final long serialVersionUID = 1L;
  private static final String IN_PARAMETER = "IN";
  private static final String OUT_PARAMETER = "OUT";
  private static final String INOUT_PARAMETER = "BOTH";  
  private String scope = null;
  private String typeInfo = null;

    /**
     * Construct a JdbcCpoArgument
     */
  public JdbcCpoArgument() {
    super();
  }

  @Override
  public JdbcCpoAttribute getAttribute() {
    return (JdbcCpoAttribute) super.getAttribute();
  }

    /**
     * Is this attribute an IN parameter
     *
     * @return true if this attribute is an IN parameter
     */
  public boolean isInParameter() {
    return IN_PARAMETER.equals(getScope()) || INOUT_PARAMETER.equals(getScope());
  }

    /**
     * Is this attribute an OUT parameter
     *
     * @return true if this attribute is an OUT parameter
     */
  public boolean isOutParameter() {
    return OUT_PARAMETER.equals(getScope()) || INOUT_PARAMETER.equals(getScope());
  }

    /**
     * Gets the scope of this argument
     *
     * @return The scope
     */
  public String getScope() {
    return scope;
  }

    /**
     * Sets this arguments scope
     *
     * @param scope - The scope of the argument
     */
  public void setScope(String scope) {
    this.scope = scope;
  }

    /**
     * Get the type info for this argument
     *
     * @return The type info for this argument
     */
  public String getTypeInfo() {
    return typeInfo;
  }

    /**
     * Sets the type info for this argument
     * @param typeInfo - The type info to set
     */
  public void setTypeInfo(String typeInfo) {
    this.typeInfo = typeInfo;
  }
    
}
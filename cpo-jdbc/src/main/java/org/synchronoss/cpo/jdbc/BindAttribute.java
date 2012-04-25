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
package org.synchronoss.cpo.jdbc;

/**
 * @author david.berry
 *
 */
public class BindAttribute {

  private JdbcCpoAttribute jdbcAttribute = null;
  private Object bindObject = null;
  private String name = null;
  private boolean isIn = false;

  public BindAttribute(JdbcCpoAttribute jdbcAttribute, Object bindObject) {
    this.jdbcAttribute = jdbcAttribute;
    this.bindObject = bindObject;
  }

  public BindAttribute(String name, Object bindObject) {
    this.name = name;
    this.bindObject = bindObject;
  }

  public BindAttribute(JdbcCpoAttribute jdbcAttribute, Object bindObject, boolean isIn) {
    this.jdbcAttribute = jdbcAttribute;
    this.bindObject = bindObject;
    this.isIn = isIn;
  }

  public BindAttribute(String name, Object bindObject, boolean isIn) {
    this.name = name;
    this.bindObject = bindObject;
    this.isIn = isIn;
  }

  public JdbcCpoAttribute getJdbcAttribute() {
    return jdbcAttribute;
  }

  public Object getBindObject() {
    return bindObject;
  }

  public String getName() {
    return name;
  }

  public boolean isIn() {
    return isIn;
  }
}

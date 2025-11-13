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
package org.synchronoss.cpo.enums;

public enum Crud {
  /**
   * Identifies the operation to be processed by the CPO. CREATE signifies that the CPO will try to
   * add the object to the datasource.
   */
  CREATE("CREATE"),

  /**
   * Identifies the operation to be processed by CPO. UPDATE signifies that the CPO will try to
   * update the object in the datasource.
   */
  UPDATE("UPDATE"),

  /**
   * Identifies the operation to be processed by CPO. DELETE signifies that the CPO will try to
   * delete the object in the datasource.
   */
  DELETE("DELETE"),

  /**
   * Identifies the operation to be processed by CPO. RETRIEVE signifies that the CPO will try to
   * retrieve a single object from the datasource.
   */
  RETRIEVE("RETRIEVE"),

  /**
   * Identifies the operation to be processed by CPO. LIST signifies that the CPO will try to
   * retrieve one or more objects from the datasource.
   */
  LIST("LIST"),

  /**
   * Identifies the operation to be processed by CPO. UPSERT signifies that the CPO will try to add
   * or update the object in the datasource.
   */
  UPSERT("UPSERT"),

  /**
   * Identifies the operation to be processed by CPO. EXIST signifies that the CPO will check to see
   * if the object exists in the datasource.
   */
  EXIST("EXIST"),

  /**
   * Identifies the operation to be processed by the CPO. EXECUTE signifies that the CPO will try to
   * execute a function or procedure in the datasource.
   */
  EXECUTE("EXECUTE");

  /** The string operation in this enum */
  public final String operation;

  Crud(String operation) {
    this.operation = operation;
  }
}

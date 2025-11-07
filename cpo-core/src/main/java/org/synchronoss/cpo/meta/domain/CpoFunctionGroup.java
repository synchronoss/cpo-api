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
package org.synchronoss.cpo.meta.domain;

import org.synchronoss.cpo.meta.bean.CpoFunctionGroupBean;

import java.util.*;

public class CpoFunctionGroup extends CpoFunctionGroupBean implements Comparable<CpoFunctionGroup> {

  private static final long serialVersionUID = 1L;

  List<CpoFunction> functions = new ArrayList<>();

  public CpoFunctionGroup() {
  }

  public List<CpoFunction> getFunctions() {
    return functions;
  }

  public void addFunction(CpoFunction function) {
    if (function != null) {
      functions.add(function);
    }
  }

  public boolean removeFunction(CpoFunction function) {
    if (function != null) {
      return functions.remove(function);
    }
    return false;
  }
  
  public void clearFunctions() {
    functions.clear();
  }

  @Override
  public String toString() {
    return this.getType() + " - " + this.getName();
  }

  public String toStringFull() {
    return super.toString();
  }

  @Override
  public int compareTo(CpoFunctionGroup fg) {
    return this.toString().compareTo(fg.toString());
  }
}

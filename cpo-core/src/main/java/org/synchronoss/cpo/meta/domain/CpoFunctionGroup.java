package org.synchronoss.cpo.meta.domain;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * --------------------------------------------------------------------------]
 */

import java.util.ArrayList;
import java.util.List;
import org.synchronoss.cpo.meta.bean.CpoFunctionGroupBean;

public class CpoFunctionGroup extends CpoFunctionGroupBean implements Comparable<CpoFunctionGroup> {

  private static final long serialVersionUID = 1L;

  List<CpoFunction> functions = new ArrayList<>();

  public CpoFunctionGroup() {}

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

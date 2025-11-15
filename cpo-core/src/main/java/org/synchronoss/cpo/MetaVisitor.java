package org.synchronoss.cpo;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
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
 * ]]
 */

import org.synchronoss.cpo.meta.domain.*;

/**
 * This defines a depth first meta visitor.
 *
 * @author Michael Bellomo
 */
public interface MetaVisitor {

  /**
   * @param cpoClass The class to be visited
   */
  void visit(CpoClass cpoClass);

  /**
   * @param cpoAttribute The attribute to be visited
   */
  void visit(CpoAttribute cpoAttribute);

  /**
   * @param cpoFunctionGroup The function group to be visited
   */
  void visit(CpoFunctionGroup cpoFunctionGroup);

  /**
   * @param cpoFunction The function to be visited
   */
  void visit(CpoFunction cpoFunction);

  /**
   * @param cpoArgument The argument to be visited
   */
  void visit(CpoArgument cpoArgument);
}

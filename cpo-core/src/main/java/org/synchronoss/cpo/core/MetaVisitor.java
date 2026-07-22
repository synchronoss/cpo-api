package org.synchronoss.cpo.core;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;
import org.synchronoss.cpo.core.meta.domain.CpoFunctionGroup;

/**
 * This defines a depth first meta visitor.
 *
 * @author Michael Bellomo
 */
public interface MetaVisitor {

  /**
   * Visits a class node in the meta model.
   *
   * @param cpoClass the class to be visited
   */
  void visit(CpoClass cpoClass);

  /**
   * Visits an attribute node in the meta model.
   *
   * @param cpoAttribute the attribute to be visited
   */
  void visit(CpoAttribute cpoAttribute);

  /**
   * Visits a function group node in the meta model.
   *
   * @param cpoFunctionGroup the function group to be visited
   */
  void visit(CpoFunctionGroup cpoFunctionGroup);

  /**
   * Visits a function node in the meta model.
   *
   * @param cpoFunction the function to be visited
   */
  void visit(CpoFunction cpoFunction);

  /**
   * Visits an argument node in the meta model.
   *
   * @param cpoArgument the argument to be visited
   */
  void visit(CpoArgument cpoArgument);
}

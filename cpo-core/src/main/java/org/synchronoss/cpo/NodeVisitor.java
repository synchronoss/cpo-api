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

/** This is the interface for the visitors to the Node Hierarchy */
public interface NodeVisitor {

  /**
   * This is called by composite nodes prior to visiting children
   *
   * @param node The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   * @throws Exception error visiting the node for the first time
   */
  boolean visitBegin(Node node) throws Exception;

  /**
   * This is called for composite nodes between visiting children
   *
   * @param node The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   * @throws Exception error visiting the node between children
   */
  boolean visitMiddle(Node node) throws Exception;

  /**
   * This is called by composite nodes after visiting children
   *
   * @param node The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   * @throws Exception error visiting the node after all the children
   */
  boolean visitEnd(Node node) throws Exception;

  /**
   * This is called for component elements which have no children
   *
   * @param node The element to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   * @throws Exception error visiting the childless node
   */
  boolean visit(Node node) throws Exception;
}

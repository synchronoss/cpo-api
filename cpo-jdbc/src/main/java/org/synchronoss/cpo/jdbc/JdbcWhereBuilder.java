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

import java.util.ArrayList;
import java.util.Collection;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.Node;
import org.synchronoss.cpo.NodeVisitor;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * JdbcWhereBuilder is an interface for specifying the sort order in which objects are returned from the Datasource.
 *
 * @author david berry
 */
public class JdbcWhereBuilder<T> implements NodeVisitor {

  private StringBuilder whereClause = new StringBuilder();
  private CpoClass cpoClass = null;
  private Collection<BindAttribute> bindValues = new ArrayList<BindAttribute>();

  public String getWhereClause() {
    return whereClause.toString();
  }

  public Collection<BindAttribute> getBindValues() {
    return this.bindValues;
  }

  @SuppressWarnings("unused")
  private JdbcWhereBuilder() {
  }

  public JdbcWhereBuilder(CpoClass cpoClass) {
    this.cpoClass = cpoClass;
  }

  /**
   * This is called by composite nodes prior to visiting children
   *
   * @param val The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   */
  @Override
  public boolean visitBegin(Node node) throws Exception {
    JdbcCpoWhere jcw = (JdbcCpoWhere) node;
    whereClause.append(jcw.toString(cpoClass));
    if (jcw.hasParent() || jcw.getLogical() != CpoWhere.LOGIC_NONE) {
      whereClause.append(" (");
    } else {
      whereClause.append(" ");
    }

    return true;
  }

  /**
   * This is called for composite nodes between visiting children
   *
   * @param val The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   */
  @Override
  public boolean visitMiddle(Node node) throws Exception {
    return true;
  }

  /**
   * This is called by composite nodes after visiting children
   *
   * @param val The node to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   */
  @Override
  public boolean visitEnd(Node node) throws Exception {
    JdbcCpoWhere jcw = (JdbcCpoWhere) node;
    if (jcw.hasParent() || jcw.getLogical() != CpoWhere.LOGIC_NONE) {
      whereClause.append(")");
    }
    return true;
  }

  /**
   * This is called for component elements which have no children
   *
   * @param val The element to be visited
   * @return a boolean (false) to end visit or (true) to continue visiting
   */
  @Override
  public boolean visit(Node node) throws Exception {
    JdbcCpoWhere jcw = (JdbcCpoWhere) node;
    JdbcAttribute attribute;
    whereClause.append(jcw.toString(cpoClass));
    if (jcw.getValue() != null) {
      attribute = (JdbcAttribute) cpoClass.getAttributeJava(jcw.getAttribute());
      if (attribute == null) {
        attribute = (JdbcAttribute) cpoClass.getAttributeJava(jcw.getRightAttribute());
      }
      if (attribute == null) {
        bindValues.add(new BindAttribute(jcw.getAttribute() == null ? jcw.getRightAttribute() : jcw.getAttribute(), jcw.getValue(), jcw.getComparison() == CpoWhere.COMP_IN));
      } else {
        bindValues.add(new BindAttribute(attribute, jcw.getValue(), jcw.getComparison() == CpoWhere.COMP_IN));
      }
    }
    return true;
  }
}

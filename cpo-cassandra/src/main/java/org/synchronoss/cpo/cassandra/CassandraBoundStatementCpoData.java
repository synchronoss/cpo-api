package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import org.synchronoss.cpo.cassandra.transform.CassandraCpoTransform;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.transform.CpoTransform;

/**
 * Helps manage data transfer between the CPO object and the Cassandra bound statement. Bind values
 * are actually applied in {@link CassandraBoundStatementFactory#setBindValues}, which builds the
 * whole value array and binds it in a single {@code PreparedStatement.bind(Object...)} call; this
 * class only supplies the transform half of the contract (see {@link #transformOut(Object)}), used
 * to resolve each attribute's value before it goes into that array.
 *
 * @author dberry
 */
public class CassandraBoundStatementCpoData extends AbstractBindableCpoData {

  private final CassandraBoundStatementFactory cpoStatementFactory;

  /**
   * Constructs the CassandraBoundStatementCpoData
   *
   * @param cpoStatementFactory The CassandraBoundStatementFactory
   * @param cpoAttribute The CpoAttribute
   * @param index The index of the attribute in the bound statement
   */
  public CassandraBoundStatementCpoData(
      CassandraBoundStatementFactory cpoStatementFactory, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.cpoStatementFactory = cpoStatementFactory;
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();

    if (cpoTransform != null) {
      if (cpoTransform instanceof CassandraCpoTransform) {
        retObj =
            ((CassandraCpoTransform) cpoTransform)
                .transformOut(cpoStatementFactory, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }
}

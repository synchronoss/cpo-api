package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapEntry;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.cassandra.transform.CassandraCpoTransform;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;

/**
 * Helps manage data transfer between the CPO object and the Cassandra bound statement
 *
 * @author dberry
 */
public class CassandraBoundStatementCpoData extends AbstractBindableCpoData {

  private static final Logger logger =
      LoggerFactory.getLogger(CassandraBoundStatementCpoData.class);
  private CassandraBoundStatementFactory cpoStatementFactory = null;

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
  public void invokeSetter(Object instanceObject) throws CpoException {
    Logger localLogger =
        instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    CpoAttribute cpoAttribute = getCpoAttribute();
    Object param = transformOut(cpoAttribute.invokeGetter(instanceObject));
    CassandraMethodMapEntry<?, ?> methodMapEntry =
        CassandraMethodMapper.getDatasourceMethod(getDataSetterParamType());
    if (methodMapEntry == null) {
      throw new CpoException(
          "Error Retrieveing Cassandra Method for type: " + getDataSetterParamType().getName());
    }
    localLogger.info(cpoAttribute.getDataName() + "=" + param);
    try {
      switch (methodMapEntry.getMethodType()) {
        case CassandraMethodMapEntry.METHOD_TYPE_BASIC:
        case CassandraMethodMapEntry.METHOD_TYPE_ONE:
        case CassandraMethodMapEntry.METHOD_TYPE_TWO:
          methodMapEntry
              .getBsSetter()
              .invoke(cpoStatementFactory.getBoundStatement(), getIndex(), param);
          break;
        default:
          throw new CpoException(
              "Invalid CassandraMethodMapEntry MetthodType: " + methodMapEntry.getMethodType());
      }
    } catch (Exception e) {
      throw new CpoException(
          "Error Invoking Cassandra Method: "
              + methodMapEntry.getBsSetter().getName()
              + ":"
              + ExceptionHelper.getLocalizedMessage(e));
    }
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

package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.transform.CpoTransform;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.transform.JdbcCpoTransform;

/**
 * The data handler for a prepared statement
 *
 * @author dberry
 */
public class JdbcPreparedStatementCpoData extends AbstractStatementCpoData {

  private static final Logger logger = LoggerFactory.getLogger(JdbcPreparedStatementCpoData.class);
  private JdbcPreparedStatementFactory cpoStatementFactory = null;

  /**
   * Construct a JdbcPreparedStatementCpoData
   *
   * @param cpoStatementFactory - The JdbcPreparedStatementFactory
   * @param cpoAttribute - The CpoAttribute to manage
   * @param index - The index of the CpoAttribute
   */
  public JdbcPreparedStatementCpoData(
      JdbcPreparedStatementFactory cpoStatementFactory, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.cpoStatementFactory = cpoStatementFactory;
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    JdbcMethodMapEntry<?, ?> methodMapEntry = getJdbcMethodMapEntry(instanceObject);
    Logger localLogger =
        instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    try {
      Object param = transformOut(getCpoAttribute().invokeGetter(instanceObject));
      localLogger.debug(getCpoAttribute().getDataName() + "=" + param);
      methodMapEntry
          .getBsSetter()
          .invoke(cpoStatementFactory.getPreparedStatement(), getIndex(), param);
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new CpoException(
          "Error Invoking Jdbc Method: "
              + getCpoAttribute().getDataName()
              + ":"
              + getCpoAttribute().getJavaName()
              + ":"
              + methodMapEntry.getBsSetter().getName()
              + ":",
          e);
    }
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();

    if (cpoTransform != null) {
      if (cpoTransform instanceof JdbcCpoTransform) {
        retObj =
            ((JdbcCpoTransform) cpoTransform).transformOut(cpoStatementFactory, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }
}

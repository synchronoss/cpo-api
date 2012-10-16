/*
 * Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.jdbc.jmeter;

import org.apache.jmeter.config.*;
import org.apache.jmeter.protocol.java.sampler.*;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log.Logger;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.jdbc.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * JMeter Samplier Client to allow a load test of cpo
 *
 * @author Michael Bellomo
 * @since 5/9/12
 */
public class CpoJavaSamplerClient extends AbstractJavaSamplerClient {

  private Logger logger = getLogger();

  private CpoAdapter cpoAdapter = null;
  private boolean hasMilliSupport = true;

  private static final String PROP_FILE = "jdbcCpoFactory";
  private static final String PROP_DB_MILLI_SUPPORTED="default.dbMilliSupport";

  private static final String START_ID = "Starting Id";
  private static final String USER_NAME = "username";
  private static final String PASSWORD = "password";
  private static final String URL = "url";
  private static final String DRIVER = "driver";
  private static final String TABLE_PREFIX = "table_prefix";
  private static final String CONFIG_PROCESSOR = "org.synchronoss.cpo.jdbc.config.JdbcCpoConfigProcessor";

  @Override
  public void setupTest(JavaSamplerContext javaSamplerContext) {
    super.setupTest(javaSamplerContext);

    try {

      String userName = javaSamplerContext.getParameter(USER_NAME);
      String pass = javaSamplerContext.getParameter(PASSWORD);
      String url = javaSamplerContext.getParameter(URL);
      String driver = javaSamplerContext.getParameter(DRIVER);
      String tablePrefix = javaSamplerContext.getParameter(TABLE_PREFIX);

      Map<String, String> map = new HashMap<String, String>();
      map.put("user", userName);
      map.put("password", pass);
      map.put("URL", url);

      DataSourceInfo dsi = new ClassDataSourceInfo(driver, map, tablePrefix);
      cpoAdapter = new JdbcCpoAdapter(dsi);
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void teardownTest(JavaSamplerContext javaSamplerContext) {
    super.teardownTest(javaSamplerContext);
    cpoAdapter = null;
  }

  @Override
  public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
    SampleResult result = new SampleResult();
    result.sampleStart();

    int id = Integer.parseInt(javaSamplerContext.getParameter(START_ID));

    ValueObject valueObject = new ValueObject(id);
    result.setSampleLabel(Thread.currentThread().getName());

    valueObject.setAttrVarChar("testInsert");
    valueObject.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!hasMilliSupport) {
      ts.setNanos(0);
    }

    valueObject.setAttrDatetime(ts);
    valueObject.setAttrBit(true);

    try {
      cpoAdapter.insertObject(valueObject);
      ValueObject vo = cpoAdapter.retrieveBean(null, valueObject, valueObject, null, null);
      if (vo != null && vo.getId() == valueObject.getId()) {
        result.setSuccessful(true);
      } else {
        result.setSuccessful(false);
      }
    } catch (CpoException ex) {
      logger.error(ex.getMessage(), ex);
      result.setSuccessful(false);
    }
    result.sampleEnd();

    return result;
  }

  @Override
  public Arguments getDefaultParameters() {
    Arguments args = new Arguments();

    args.addArgument(new Argument(START_ID, "1"));
    args.addArgument(new Argument(USER_NAME, "cpo"));
    args.addArgument(new Argument(PASSWORD, "cpo"));
    args.addArgument(new Argument(URL, ""));
    args.addArgument(new Argument(DRIVER, ""));
    args.addArgument(new Argument(TABLE_PREFIX, "TEST_"));

    return args;
  }
}

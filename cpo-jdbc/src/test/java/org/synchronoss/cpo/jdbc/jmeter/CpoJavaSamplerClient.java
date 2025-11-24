package org.synchronoss.cpo.jdbc.jmeter;

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

import java.sql.Timestamp;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.synchronoss.cpo.jdbc.adapter.ValueObjectFactory;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtJdbcConfig;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtJdbcReadWriteConfig;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

/**
 * JMeter Samplier Client to allow a load test of cpo
 *
 * @author Michael Bellomo
 * @since 5/9/12
 */
public class CpoJavaSamplerClient extends AbstractJavaSamplerClient {
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private CpoAdapter cpoAdapter = null;

  private static final String START_ID = "Starting Id";
  private static final String USER_NAME = "username";
  private static final String PASSWORD = "password";
  private static final String URL = "url";
  private static final String DRIVER = "driver";
  private static final String CONFIG_PROCESSOR =
      "org.synchronoss.cpo.jdbc.config.JdbcCpoConfigProcessor";
  private static boolean isSupportsMillis = true;

  @Parameters({"db.millisupport"})
  @BeforeClass
  public void setUp(boolean milliSupport) {
    String method = "setUp:";
    isSupportsMillis = milliSupport;
  }

  @Override
  public void setupTest(JavaSamplerContext javaSamplerContext) {
    super.setupTest(javaSamplerContext);

    try {
      metaDescriptor =
          (JdbcCpoMetaDescriptor)
              CpoMetaDescriptor.getInstance(
                  "jmeter-" + System.currentTimeMillis(),
                  "/oracle/classdef/oracleValueMetaData.xml",
                  true);

      CtJdbcConfig jdbcConfig = CtJdbcConfig.Factory.newInstance();
      jdbcConfig.setName(this.getClass().getName());
      jdbcConfig.setMetaDescriptorName(metaDescriptor.getName());
      jdbcConfig.setCpoConfigProcessor(CONFIG_PROCESSOR);

      CtJdbcReadWriteConfig rwc = jdbcConfig.addNewReadWriteConfig();
      rwc.setUser(javaSamplerContext.getParameter(USER_NAME));
      rwc.setPassword(javaSamplerContext.getParameter(PASSWORD));
      rwc.setUrl(javaSamplerContext.getParameter(URL));
      rwc.setDataSourceClassName(javaSamplerContext.getParameter(DRIVER));

      cpoAdapter = CpoAdapterFactoryManager.makeCpoAdapterFactory(jdbcConfig).getCpoAdapter();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  public void teardownTest(JavaSamplerContext javaSamplerContext) {
    super.teardownTest(javaSamplerContext);
    cpoAdapter = null;
    metaDescriptor = null;
  }

  @Override
  public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
    SampleResult result = new SampleResult();
    result.sampleStart();

    int id = Integer.parseInt(javaSamplerContext.getParameter(START_ID));

    ValueObject valueObject = ValueObjectFactory.createValueObject(id);
    result.setSampleLabel(Thread.currentThread().getName());

    valueObject.setAttrVarChar("testInsert");
    valueObject.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!isSupportsMillis) {
      ts.setNanos(0);
    }

    valueObject.setAttrDatetime(ts);
    valueObject.setAttrBit(true);

    try {
      cpoAdapter.insertBean(valueObject);
      ValueObject vo =
          cpoAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL, valueObject, valueObject, null, null);
      result.setSuccessful(vo != null && vo.getId() == valueObject.getId());
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

    return args;
  }
}

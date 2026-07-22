package org.synchronoss.cpo.jdbc.config;

/*-
 * [[
 * jdbc
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

import static org.testng.Assert.*;

import java.math.BigInteger;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;
import org.synchronoss.cpo.cpoconfig.CtJdbcConfig;
import org.synchronoss.cpo.cpoconfig.CtJdbcReadWriteConfig;
import org.synchronoss.cpo.cpoconfig.CtProperty;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Tests JdbcCpoConfigProcessor datasource-mode branches using in-memory H2. */
public class JdbcCpoConfigProcessorTest {

  private static final String H2_MEM_URL = "jdbc:h2:mem:cfgProcessorTest;DB_CLOSE_DELAY=-1";
  private String metaDescriptorName;

  @BeforeClass
  public void setUp() throws CpoException {
    for (String name : CpoMetaDescriptor.getCpoMetaDescriptorNames()) {
      if (CpoMetaDescriptor.getInstance(name) instanceof JdbcCpoMetaDescriptor) {
        metaDescriptorName = name;
        break;
      }
    }
    assertNotNull(metaDescriptorName, "suite should have loaded a jdbc meta descriptor");
  }

  private CtJdbcConfig config(String name) {
    CtJdbcConfig cfg = new CtJdbcConfig();
    cfg.setName(name);
    cfg.setMetaDescriptorName(metaDescriptorName);
    cfg.setFetchSize(BigInteger.valueOf(10));
    cfg.setBatchSize(BigInteger.valueOf(10));
    return cfg;
  }

  @Test
  public void testDataSourceClassConfig() throws Exception {
    CtJdbcReadWriteConfig rw = new CtJdbcReadWriteConfig();
    rw.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
    rw.setUrl(H2_MEM_URL);
    rw.setUser("sa");
    rw.setPassword("");
    CtProperty property = new CtProperty();
    property.setName("Description");
    property.setValue("test datasource");
    rw.getProperty().add(property);

    CtJdbcConfig cfg = config("cfgClassFull");
    cfg.setReadWriteConfig(rw);

    CpoAdapterFactory factory = new JdbcCpoConfigProcessor().processCpoConfig(cfg);
    assertNotNull(factory);
    assertNotNull(factory.getCpoAdapter().getDataSourceName());
  }

  @Test
  public void testDriverWithUserConfig() throws Exception {
    CtJdbcReadWriteConfig rw = new CtJdbcReadWriteConfig();
    rw.setDriverClassName("org.h2.Driver");
    rw.setUrl(H2_MEM_URL);
    rw.setUser("sa");
    rw.setPassword("");

    CtJdbcConfig cfg = config("cfgDriverUser");
    cfg.setReadWriteConfig(rw);

    assertNotNull(new JdbcCpoConfigProcessor().processCpoConfig(cfg));
  }

  @Test
  public void testDriverWithPropertiesConfig() throws Exception {
    CtJdbcReadWriteConfig rw = new CtJdbcReadWriteConfig();
    rw.setDriverClassName("org.h2.Driver");
    rw.setUrl(H2_MEM_URL);
    CtProperty user = new CtProperty();
    user.setName("user");
    user.setValue("sa");
    rw.getProperty().add(user);

    CtJdbcConfig cfg = config("cfgDriverProps");
    cfg.setReadWriteConfig(rw);

    assertNotNull(new JdbcCpoConfigProcessor().processCpoConfig(cfg));
  }

  @Test
  public void testDriverUrlOnlyConfig() throws Exception {
    CtJdbcReadWriteConfig rw = new CtJdbcReadWriteConfig();
    rw.setDriverClassName("org.h2.Driver");
    // a dedicated database: url-only connects with H2's default user, which must own the db
    rw.setUrl("jdbc:h2:mem:cfgUrlOnlyTest;DB_CLOSE_DELAY=-1");

    CtJdbcConfig cfg = config("cfgDriverUrlOnly");
    cfg.setReadWriteConfig(rw);

    assertNotNull(new JdbcCpoConfigProcessor().processCpoConfig(cfg));
  }

  @Test
  public void testReadWriteSplitConfig() throws Exception {
    CtJdbcReadWriteConfig read = new CtJdbcReadWriteConfig();
    read.setDriverClassName("org.h2.Driver");
    read.setUrl(H2_MEM_URL);
    read.setUser("sa");
    read.setPassword("");
    CtJdbcReadWriteConfig write = new CtJdbcReadWriteConfig();
    write.setDriverClassName("org.h2.Driver");
    write.setUrl(H2_MEM_URL);
    write.setUser("sa");
    write.setPassword("");

    CtJdbcConfig cfg = config("cfgSplit");
    cfg.setReadConfig(read);
    cfg.setWriteConfig(write);

    assertNotNull(new JdbcCpoConfigProcessor().processCpoConfig(cfg));
  }

  @Test
  public void testJndiConfig() {
    CtJdbcReadWriteConfig rw = new CtJdbcReadWriteConfig();
    rw.setJndiName("jdbc/noSuchDataSource");
    CtJdbcConfig cfg = config("cfgJndi");
    cfg.setReadWriteConfig(rw);

    // no JNDI provider is configured in the test JVM; the branch is what matters
    try {
      assertNotNull(new JdbcCpoConfigProcessor().processCpoConfig(cfg));
    } catch (CpoException expected) {
      // acceptable: the lookup fails without an InitialContext provider
    }
  }

  @Test
  public void testInvalidConfigRejected() {
    JdbcCpoConfigProcessor processor = new JdbcCpoConfigProcessor();
    expectThrows(CpoException.class, () -> processor.processCpoConfig(null));
    expectThrows(CpoException.class, () -> processor.processCpoConfig(new CtDataSourceConfig()));
  }
}

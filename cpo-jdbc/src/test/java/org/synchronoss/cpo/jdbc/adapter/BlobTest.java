package org.synchronoss.cpo.jdbc.adapter;

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

import static org.testng.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.jdbc.LobValueObject;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class BlobTest {

  private static final Logger logger = LoggerFactory.getLogger(BlobTest.class);
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private CpoAdapter cpoAdapter = null;
  private byte[] testBlob = null;
  private char[] testClob = "This is a test Clob used for testing clobs".toCharArray();
  private byte[] testBlob2 = null;
  private char[] testClob2 = "This is a second test Clob used for testing clobs".toCharArray();
  private boolean isSupportsBlobs = true;

  public BlobTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: BlobTest.java,v 1.15 2006/02/15 18:34:19 dberry Exp $'
   */
  @Parameters({"db.blobsupport"})
  @BeforeClass
  public void setUp(boolean blobSupport) {
    String method = "setUp:";
    isSupportsBlobs = blobSupport;

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testTrxGZipBlobInsertandDelete() {

    if (isSupportsBlobs) {

      testBlob = new byte[JdbcStatics.BLOB_SIZE];
      for (int i = 0; i < JdbcStatics.BLOB_SIZE; i++) {
        testBlob[i] = (byte) (((int) 'a') + (i % 26));
      }

      testBlob2 = new byte[JdbcStatics.BLOB_SIZE];
      for (int i = 0; i < JdbcStatics.BLOB_SIZE; i++) {
        testBlob2[i] = (byte) (((int) 'z') - (i % 26));
      }

      LobValueObject lvo = LobValueObjectFactory.createLobValueObject(1, testBlob, testClob);
      LobValueObject lvo2 = null;

      lvo.setBLob2(testBlob2);

      try {
        cpoAdapter.deleteBean(LobValueObject.FG_DELETE_DELETELVO, lvo);
      } catch (Exception ie) {
        logger.error("error deleting lob");
        fail(ie.getMessage());
      }

      try {
        cpoAdapter.insertBean(LobValueObject.FG_CREATE_CREATELVO, lvo);
      } catch (Exception ie) {
        logger.error("error inserting lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        String blob1 = new String(lvo.getBLob());
        String blob2 = new String(lvo2.getBLob());

        assertEquals(blob1, blob2);

        String clob1 = new String(lvo.getCLob());
        String clob2 = new String(lvo2.getCLob());

        assertEquals(clob1, clob2);

      } catch (Exception ie) {
        logger.error("error retrieving lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2.setBLob(testBlob2);
        lvo2.setCLob(testClob2);
        cpoAdapter.updateBean(LobValueObject.FG_UPDATE_UPDATELVO, lvo2);
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        String blob1 = new String(testBlob2);
        String blob2 = new String(lvo2.getBLob());

        assertEquals(blob1, blob2);

        String clob1 = new String(testClob2);
        String clob2 = new String(lvo2.getCLob());

        assertEquals(clob1, clob2);

      } catch (Exception ie) {
        logger.error("error updating lob", ie);
        fail(ie.getMessage());
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support BLOBs");
    }
  }

  @Test
  public void testTrxBlobInsertandDelete() {

    if (isSupportsBlobs) {

      testBlob = new byte[JdbcStatics.BLOB_SIZE];
      for (int i = 0; i < JdbcStatics.BLOB_SIZE; i++) {
        testBlob[i] = (byte) (((int) 'a') + (i % 26));
      }

      testBlob2 = new byte[JdbcStatics.BLOB_SIZE];
      for (int i = 0; i < JdbcStatics.BLOB_SIZE; i++) {
        testBlob2[i] = (byte) (((int) 'z') - (i % 26));
      }

      LobValueObject lvo = LobValueObjectFactory.createLobValueObject(1, testBlob, testClob);
      LobValueObject lvo2 = null;

      lvo.setBLob2(testBlob2);

      try {
        cpoAdapter.deleteBean(LobValueObject.FG_DELETE_DELETELVO, lvo);
      } catch (Exception ie) {
        logger.error("error deleting lob");
        fail(ie.getMessage());
      }

      try {
        cpoAdapter.insertBean(LobValueObject.FG_CREATE_CREATELVO, lvo);
      } catch (Exception ie) {
        logger.error("error inserting lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        String blob1 = new String(lvo.getBLob2());
        String blob2 = new String(lvo2.getBLob2());

        assertEquals(blob1, blob2);

      } catch (Exception ie) {
        logger.error("error retrieving lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2.setBLob2(testBlob);
        cpoAdapter.updateBean(LobValueObject.FG_UPDATE_UPDATELVO, lvo2);
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        String blob1 = new String(testBlob);
        String blob2 = new String(lvo2.getBLob2());

        assertEquals(blob1, blob2);

      } catch (Exception ie) {
        logger.error("error updating lob", ie);
        fail(ie.getMessage());
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support BLOBs");
    }
  }

  /*
   * public void testBlobLeakage(){ Connection c1_=null; JdbcCpoAdapter jca = null;
   *
   * if (hasBlobSupport){ // Load database driver if not already loaded try{ CpoClassLoader.forName(dbDriver_);
   *
   * c1_ = DriverManager.getConnection(dbUrl_, dbUser_, dbPassword_); c1_.setAutoCommit(false);
   *
   * jca = new JdbcCpoAdapter(c1_);
   *
   * } catch(Exception e){ fail(ExceptionHelper.getLocalizedMessage(e)); }
   *
   * testBlob = new byte[64999]; for (int j = 0; j< 40000; j++){ testBlob[j]= (byte)(((int)'a') + (j%26)); }
   *
   *
   * for (int i=2; i<1000; i++){ LobValueObject lvo = new LobValueObject(i,testBlob, testClob); al.add(lvo); try{
   * jca.insertObject("createLVO",lvo,c1_); c1_.commit(); jca.retrieveBean("retrieveLVO",lvo,c1_); } catch (Exception
   * ie){ logger.error("error inserting lob", ie); fail(ie.getMessage()); }
   *
   * }
   * }
   *
   * }
   */
  @Test
  public void testTrxEmptyGZipBlobInsertandDelete() {

    if (isSupportsBlobs) {

      testBlob = new byte[1];
      testBlob2 = new byte[1];

      LobValueObject lvo = LobValueObjectFactory.createLobValueObject(1, testBlob, testClob);
      LobValueObject lvo2 = null;

      lvo.setBLob2(testBlob2);

      try {
        cpoAdapter.deleteBean(LobValueObject.FG_DELETE_DELETELVO, lvo);
      } catch (Exception ie) {
        logger.error("error deleting lob");
        fail(ie.getMessage());
      }

      try {
        cpoAdapter.insertBean(LobValueObject.FG_CREATE_CREATELVO, lvo);
      } catch (Exception ie) {
        logger.error("error inserting lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        byte blob1[] = lvo.getBLob();
        byte blob2[] = lvo2.getBLob();

        assertNotNull(blob1);
        assertNotNull(blob2);

        assertTrue(isEqual(blob1, blob2));

      } catch (Exception ie) {
        logger.error("error retrieving lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2.setBLob(testBlob2);
        lvo2.setCLob(testClob2);
        cpoAdapter.updateBean(LobValueObject.FG_UPDATE_UPDATELVO, lvo2);
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);
        byte blob1[] = testBlob2;
        byte blob2[] = lvo2.getBLob();

        assertNotNull(blob1);
        assertNotNull(blob2);

        assertTrue(isEqual(blob1, blob2));

      } catch (Exception ie) {
        logger.error("error updating lob", ie);
        fail(ie.getMessage());
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support BLOBs");
    }
  }

  @Test
  public void testTrxNullGZipBlobInsertandDelete() {

    if (isSupportsBlobs) {

      LobValueObject lvo = LobValueObjectFactory.createLobValueObject(1, null, null);
      LobValueObject lvo2 = null;

      lvo.setBLob2(null);

      try {
        cpoAdapter.deleteBean(LobValueObject.FG_DELETE_DELETELVO, lvo);
      } catch (Exception ie) {
        logger.error("error deleting lob");
        fail(ie.getMessage());
      }

      try {
        cpoAdapter.insertBean(LobValueObject.FG_CREATE_CREATELVO, lvo);
      } catch (Exception ie) {
        logger.error("error inserting lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);

        assertNull(lvo2.getBLob());
        assertNull(lvo2.getBLob2());
        assertNull(lvo2.getCLob());

      } catch (Exception ie) {
        logger.error("error retrieving lob", ie);
        fail(ie.getMessage());
      }

      try {
        lvo2.setBLob(null);
        lvo2.setCLob(null);
        cpoAdapter.updateBean(LobValueObject.FG_UPDATE_UPDATELVO, lvo2);
        lvo2 = cpoAdapter.retrieveBean(LobValueObject.FG_RETRIEVE_RETRIEVELVO, lvo);

        assertNull(lvo2.getBLob());
        assertNull(lvo2.getBLob2());
        assertNull(lvo2.getCLob());

      } catch (Exception ie) {
        logger.error("error updating lob", ie);
        fail(ie.getMessage());
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support BLOBs");
    }
  }

  @AfterClass
  public void tearDown() {

    cpoAdapter = null;
  }

  private boolean isEqual(byte[] b1, byte[] b2) {

    if (b1 == b2) {
      return true;
    }

    if (b1 == null || b2 == null) {
      return false;
    }

    if (b1.length != b2.length) {
      return false;
    }

    for (int i = 0; i < b1.length; i++) {
      if (b1[i] != b2[i]) {
        return false;
      }
    }

    return true;
  }
}

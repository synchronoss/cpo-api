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
package org.synchronoss.cpo.cassandra;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;

import java.nio.ByteBuffer;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class BlobTest extends TestCase {

  private static final Logger logger = LoggerFactory.getLogger(BlobTest.class);
  private CassandraCpoMetaDescriptor metaDescriptor = null;
  private CpoAdapter cpoAdapter = null;
  private ByteBuffer testBlob = null;
  private ByteBuffer testBlob2 = null;

  public BlobTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: BlobTest.java,v 1.15 2006/02/15 18:34:19 dberry Exp $'
   */
  @Override
  public void setUp() {

    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
      metaDescriptor = (CassandraCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testTrxGZipBlobInsertandDelete() {

    testBlob = ByteBuffer.allocate(CassandraStatics.BLOB_SIZE);
    for (int i = 0; i < CassandraStatics.BLOB_SIZE; i++) {
      testBlob.put((byte) (((int) 'a') + (i % 26)));
    }

    testBlob2 = ByteBuffer.allocate(CassandraStatics.BLOB_SIZE);
    for (int i = 0; i < CassandraStatics.BLOB_SIZE; i++) {
      testBlob2.put((byte) (((int) 'z') - (i % 26)));
    }

    ValueObject lvo = new ValueObject(1, testBlob);
    ValueObject lvo2 = null;

    lvo.setAttrBlob2(testBlob2);

    try {
      cpoAdapter.deleteObject("deleteLVO", lvo);
    } catch (Exception ie) {
      logger.error("error deleting lob");
      fail(ie.getMessage());
    }

    try {
      cpoAdapter.insertObject("createLVO", lvo);
    } catch (Exception ie) {
      logger.error("error inserting lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = lvo.getAttrBlob();
      ByteBuffer blob2 = lvo2.getAttrBlob();

      assertTrue(blob1.equals(blob2));

    } catch (Exception ie) {
      logger.error("error retrieving lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2.setAttrBlob(testBlob2);
      cpoAdapter.updateObject("updateLVO", lvo2);
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = testBlob2;
      ByteBuffer blob2 = lvo2.getAttrBlob();

      assertTrue(blob1.equals(blob2));

    } catch (Exception ie) {
      logger.error("error updating lob", ie);
      fail(ie.getMessage());
    }
  }

  public void testTrxBlobInsertandDelete() {

    testBlob = ByteBuffer.allocate(CassandraStatics.BLOB_SIZE);
    for (int i = 0; i < CassandraStatics.BLOB_SIZE; i++) {
      testBlob.put((byte) (((int) 'a') + (i % 26)));
    }

    testBlob2 = ByteBuffer.allocate(CassandraStatics.BLOB_SIZE);
    for (int i = 0; i < CassandraStatics.BLOB_SIZE; i++) {
      testBlob2.put((byte) (((int) 'z') - (i % 26)));
    }

    ValueObject lvo = new ValueObject(1, testBlob);
    ValueObject lvo2 = null;

    lvo.setAttrBlob2(testBlob2);

    try {
      cpoAdapter.deleteObject("deleteLVO", lvo);
    } catch (Exception ie) {
      logger.error("error deleting lob");
      fail(ie.getMessage());
    }

    try {
      cpoAdapter.insertObject("createLVO", lvo);
    } catch (Exception ie) {
      logger.error("error inserting lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = lvo.getAttrBlob2();
      ByteBuffer blob2 = lvo2.getAttrBlob2();

      assertTrue(blob1.equals(blob2));
    } catch (Exception ie) {
      logger.error("error retrieving lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2.setAttrBlob2(testBlob);
      cpoAdapter.updateObject("updateLVO", lvo2);
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = testBlob;
      ByteBuffer blob2 = lvo2.getAttrBlob2();

      assertTrue(blob1.equals(blob2));
    } catch (Exception ie) {
      logger.error("error updating lob", ie);
      fail(ie.getMessage());
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
  public void testTrxEmptyGZipBlobInsertandDelete() {

    testBlob = ByteBuffer.allocate(0);
    testBlob2 = ByteBuffer.allocate(0);

    ValueObject lvo = new ValueObject(1, testBlob);
    ValueObject lvo2 = null;

    lvo.setAttrBlob2(testBlob2);

    try {
      cpoAdapter.deleteObject("deleteLVO", lvo);
    } catch (Exception ie) {
      logger.error("error deleting lob");
      fail(ie.getMessage());
    }

    try {
      cpoAdapter.insertObject("createLVO", lvo);
    } catch (Exception ie) {
      logger.error("error inserting lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = lvo.getAttrBlob();
      ByteBuffer blob2 = lvo2.getAttrBlob();

      assertNotNull(blob1);
      assertNotNull(blob2);

      assertTrue(blob1.equals(blob2));


    } catch (Exception ie) {
      logger.error("error retrieving lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2.setAttrBlob(testBlob2);
      cpoAdapter.updateObject("updateLVO", lvo2);
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);
      ByteBuffer blob1 = testBlob2;
      ByteBuffer blob2 = lvo2.getAttrBlob();

      assertNotNull(blob1);
      assertNotNull(blob2);

      assertTrue(blob1.equals(blob2));


    } catch (Exception ie) {
      logger.error("error updating lob", ie);
      fail(ie.getMessage());
    }
  }

  public void testTrxNullGZipBlobInsertandDelete() {

    ValueObject lvo = new ValueObject(1, null);
    ValueObject lvo2 = null;

    lvo.setAttrBlob2(null);

    try {
      cpoAdapter.deleteObject("deleteLVO", lvo);
    } catch (Exception ie) {
      logger.error("error deleting lob");
      fail(ie.getMessage());
    }

    try {
      cpoAdapter.insertObject("createLVO", lvo);
    } catch (Exception ie) {
      logger.error("error inserting lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);

      assertNull(lvo2.getAttrBlob());
      assertNull(lvo2.getAttrBlob2());

    } catch (Exception ie) {
      logger.error("error retrieving lob", ie);
      fail(ie.getMessage());
    }

    try {
      lvo2.setAttrBlob(null);
      cpoAdapter.updateObject("updateLVO", lvo2);
      lvo2 = cpoAdapter.retrieveBean("retrieveLVO", lvo);

      assertNull(lvo2.getAttrBlob());
      assertNull(lvo2.getAttrBlob2());

    } catch (Exception ie) {
      logger.error("error updating lob", ie);
      fail(ie.getMessage());
    }
  }

  @Override
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
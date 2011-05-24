/**
 * CachedPreparedStatementTest.java  
 * 
 *  Copyright (C) 2006  David E. Berry
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
 */
package org.synchronoss.cpo.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class CachedPreparedStatementTest extends TestCase {
  private static Logger logger = LoggerFactory.getLogger(CachedPreparedStatementTest.class.getName());
    private static final String PROP_FILE="jdbcCpoFactory";
    private static final String PROP_DBDRIVER="default.dbDriver";
    private static final String PROP_DB_CALLS_SUPPORTED="default.dbCallsSupported";
    private static final String stmt="select * from value_object";
    private static final String cstmt="{?= call power(3,3)}";
    private CpoAdapter jdbcIdo_=null;
    private String dbDriver_=null;
    private boolean hasCallSupport = true;
    

    /**
     * Creates a new CachedPreparedStatementTest object.
     *
     * @param name DOCUMENT ME!
     */
    public CachedPreparedStatementTest(String name) {
        super(name);
    }

    /**
     * <code>setUp</code> Load the datasource from the properties in the property file
     * jdbc_en_US.properties
     */
    public void setUp() {
        String method="setUp:";
        ResourceBundle b=PropertyResourceBundle.getBundle(PROP_FILE, Locale.getDefault(),
                this.getClass().getClassLoader());
        dbDriver_=b.getString(PROP_DBDRIVER).trim();
        hasCallSupport = new Boolean(b.getString(PROP_DB_CALLS_SUPPORTED).trim());
        
        try {
          jdbcIdo_ = JdbcCpoFactory.getCpoAdapter();
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            fail(method+e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown() {
        jdbcIdo_=null;
    }

    /**
     * DOCUMENT ME!
     */
    public void testPreparedStatement1() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt);
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt);
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }
    public void testPreparedStatement2() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt,Statement.RETURN_GENERATED_KEYS);
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }
    public void testPreparedStatement3() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt,new int[]{1});
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt,new int[]{1});
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }
    public void testPreparedStatement4() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }
    public void testPreparedStatement5() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.CLOSE_CURSORS_AT_COMMIT);
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.CLOSE_CURSORS_AT_COMMIT);
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }
    
    public void testPreparedStatement6() {
        try {
            Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps=c.prepareStatement(stmt,new String[]{"ID"});
            PreparedStatement ips=((JdbcPreparedStatement)ps).getPreparedStatement();
            ps.close();
            c.close();

            Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
            PreparedStatement ps2=c2.prepareStatement(stmt,new String[]{"ID"});
            PreparedStatement ips2=((JdbcPreparedStatement)ps2).getPreparedStatement();
            assertEquals(ips,ips2);
            ps2.close();
            c2.close();
        } catch(CpoException ce) {
            fail("Failed from CpoException"+ce.getMessage());
        } catch(SQLException se) {
            fail("Failed from SQLException"+se.getMessage());
        }
    }

   /**
     * DOCUMENT ME!
     */
    public void testCallableStatement1() {
    	if (hasCallSupport){
            try {
                Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs=c.prepareCall(cstmt);
                CallableStatement ics=((JdbcCallableStatement)cs).getCallableStatement();
                cs.close();
                c.close();

                Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs2=c2.prepareCall(cstmt);
                CallableStatement ics2=((JdbcCallableStatement)cs2).getCallableStatement();
                assertEquals(ics,ics2);
                cs2.close();
                c2.close();
            } catch(CpoException ce) {
                fail("Failed from CpoException"+ce.getMessage());
            } catch(SQLException se) {
                fail("Failed from SQLException"+se.getMessage());
            }
    	} else {
            logger.error(dbDriver_+" does not support CallableStatements");
      }
    }
    public void testCallableStatement2() {
    	if (hasCallSupport){
            try {
                Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs=c.prepareCall(cstmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                CallableStatement ics=((JdbcCallableStatement)cs).getCallableStatement();
                cs.close();
                c.close();

                Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs2=c2.prepareCall(cstmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
                CallableStatement ics2=((JdbcCallableStatement)cs2).getCallableStatement();
                assertEquals(ics,ics2);
                cs2.close();
                c2.close();
            } catch(CpoException ce) {
                fail("Failed from CpoException"+ce.getMessage());
            } catch(SQLException se) {
                fail("Failed from SQLException"+se.getMessage());
            }
    	} else {
    	  logger.error(dbDriver_+" does not support CallableStatements");
        }
    }
    public void testCallableStatement3() {
    	if (hasCallSupport){
            try {
                Connection c=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs=c.prepareCall(cstmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.CLOSE_CURSORS_AT_COMMIT);
                CallableStatement ics=((JdbcCallableStatement)cs).getCallableStatement();
                cs.close();
                c.close();

                Connection c2=((JdbcCpoAdapter)jdbcIdo_).getWriteConnection();
                CallableStatement cs2=c2.prepareCall(cstmt,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,ResultSet.CLOSE_CURSORS_AT_COMMIT);
                CallableStatement ics2=((JdbcCallableStatement)cs2).getCallableStatement();
                assertEquals(ics,ics2);
                cs2.close();
                c2.close();
            } catch(CpoException ce) {
                fail("Failed from CpoException"+ce.getMessage());
            } catch(SQLException se) {
                fail("Failed from SQLException"+se.getMessage());
            }
    	} else {
    	  logger.error(dbDriver_+" does not support CallableStatements");
        }
    }

}

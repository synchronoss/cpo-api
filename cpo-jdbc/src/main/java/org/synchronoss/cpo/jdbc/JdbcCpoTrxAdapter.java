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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

public class JdbcCpoTrxAdapter extends JdbcCpoAdapter implements CpoTrxAdapter {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  /**
   * DOCUMENT ME!
   */
  // Default Connection. Only used JdbcCpoTrxAdapter
  private Connection writeConnection_ = null;
  // map to keep track of busy connections
  private static final HashMap<Connection, Connection> busyMap_ = new HashMap<Connection, Connection>();

  @SuppressWarnings("unused")
  private JdbcCpoTrxAdapter() {
  }

  protected JdbcCpoTrxAdapter(JdbcCpoMetaDescriptor metaDescriptor, Connection c,
          boolean batchSupported, String dataSourceName) throws CpoException {
    super(metaDescriptor, batchSupported, dataSourceName);
    setStaticConnection(c);
  }

  @Override
  public void commit() throws CpoException {
    if (writeConnection_ != null) {
      try {
        writeConnection_.commit();
      } catch (SQLException se) {
        throw new CpoException(se.getMessage());
      }
    } else {
      throw new CpoException("Transaction Object has been Closed");
    }
  }

  @Override
  public void rollback() throws CpoException {
    if (writeConnection_ != null) {
      try {
        writeConnection_.rollback();
      } catch (Exception e) {
        throw new CpoException(e.getMessage());
      }
    } else {
      throw new CpoException("Transaction Object has been Closed");
    }
  }

  @Override
  public boolean isClosed() throws CpoException {
    boolean closed = false;

    try {
      closed = (writeConnection_ == null || writeConnection_.isClosed());
    } catch (Exception e) {
      throw new CpoException(e.getMessage());
    }
    return closed;
  }

  @Override
  public void close() throws CpoException {
    if (writeConnection_ != null) {
      try {
        try {
          writeConnection_.rollback();
        } catch (Exception e) {
        }
        try {
          writeConnection_.close();
        } catch (Exception e) {
        }
      } finally {
        setStaticConnection(null);
      }
    }
  }

  /**
   * DOCUMENT ME!
   */
  @Override
  protected void finalize() {
    try {
      super.finalize();
    } catch (Throwable e) {
    }
    try {
      if (writeConnection_ != null && !writeConnection_.isClosed()) {
        this.close();
      }
    } catch (Exception e) {
    }
  }

  @Override
  protected Connection getStaticConnection() throws CpoException {
    if (writeConnection_ != null) {
      if (isConnectionBusy(writeConnection_)) {
        throw new CpoException("Error Connection Busy");
      } else {
        setConnectionBusy(writeConnection_);
      }
    }
    return writeConnection_;
  }

  @Override
  protected boolean isStaticConnection(Connection c) {
    return (writeConnection_ == c);
  }

  @Override
  protected void setStaticConnection(Connection c) {
    writeConnection_ = c;
  }

  @Override
  protected boolean isConnectionBusy(Connection c) {
    synchronized (busyMap_) {
      Connection test = busyMap_.get(c);
      return test != null;
    }
  }

  @Override
  protected void setConnectionBusy(Connection c) {
    synchronized (busyMap_) {
      busyMap_.put(c, c);
    }
  }

  @Override
  protected void clearConnectionBusy(Connection c) {
    synchronized (busyMap_) {
      busyMap_.remove(c);
    }
  }
}

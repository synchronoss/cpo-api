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
package org.synchronoss.cpo.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoXaAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * A JdbcCpoXaAdapter is a JdbcCpoAdapter which is also an XAResource.
 *
 * The XAResource interface is a Java mapping of the industry standard XA interface based on the X/Open CAE Specification
 * (Distributed Transaction Processing: The XA Specification).
 *
 * The XA interface defines the contract between a Resource Manager and a Transaction Manager in a distributed transaction processing (DTP) environment.
 * A JDBC driver or a JMS provider implements this interface to support the association between a global transaction and a database or message service connection.
 *
 * The XAResource interface can be supported by any transactional resource that is intended to be used by application programs in an environment
 * where transactions are controlled by an external transaction manager. An example of such a resource is a database management system. An application
 * may access data through multiple database connections. Each database connection is enlisted with the transaction manager as a transactional resource.
 * The transaction manager obtains an XAResource for each connection participating in a global transaction. The transaction manager uses the start method to
 * associate the global transaction with the resource, and it uses the end method to disassociate the transaction from the resource. The resource manager is
 * responsible for associating the global transaction to all work performed on its data between the start and end method invocations.
 *
 *  At transaction commit time, the resource managers are informed by the transaction manager to prepare, commit, or rollback a transaction according to the
 *  two-phase commit protocol.
 *
 */
public class JdbcCpoXaAdapter extends JdbcCpoTrxAdapter implements CpoXaAdapter {

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoXaAdapter.class);

  // using this string as the MUTEX for all synchronizations within this class.
  // It will be used for coordinating local transactions and global transactions
  private static final String XA_MUTEX = "XA_MUTEX";

  // store all inUseXids
  private static final HashMap<Xid, Connection> inUseXidMap = new HashMap<>();

  // store all idleXids
  private  static final HashMap<Xid, Connection> idleXidMap = new HashMap<>();

  // The XA Transaction associated with this XAResource
  private Xid resourceXid = null;

  protected JdbcCpoXaAdapter(JdbcCpoAdapter jdbcCpoAdapter) throws CpoException {
    super(jdbcCpoAdapter);
  }

  /**
   * Commits the global transaction specified by xid.
   *
   * @param xid - A global transaction identifier
   * @param onePhase - If true, the resource manager should use a one-phase commit protocol to commit the work done on behalf of xid.
   * @throws XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR,
   * XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   *
   * If the resource manager did not commit the transaction and the parameter onePhase is set to true, the resource manager may throw
   * one of the XA_RB* exceptions. Upon return, the resource manager has rolled back the branch's work and has released all held resources.
   */
  @Override
  public void commit(Xid xid, boolean onePhase) throws XAException {
    Connection conn = getWriteConnection(resourceXid);

    try {
      conn.commit();
    } catch (SQLException se) {
      throw new XAException(XAException.XAER_RMERR);
    } catch (Exception e) {
      throw new XAException(XAException.XAER_RMFAIL);
    }
  }

  /**
   * Ends the work performed on behalf of a transaction branch. The resource manager disassociates the XA resource from the transaction branch
   * specified and lets the transaction complete.
   *
   * If TMSUSPEND is specified in the flags, the transaction branch is temporarily suspended in an incomplete state. The transaction context is
   * in a suspended state and must be resumed via the start method with TMRESUME specified.
   *
   * If TMFAIL is specified, the portion of work has failed. The resource manager may mark the transaction as rollback-only
   *
   * If TMSUCCESS is specified, the portion of work has completed successfully.
   *
   * @param xid - A global transaction identifier that is the same as the identifier used previously in the start method.
   * @param flags - One of TMSUCCESS, TMFAIL, or TMSUSPEND.
   * @throws XAException - An error has occurred. Possible XAException values are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, XAER_PROTO, or XA_RB*.
   */
  @Override
  public void end(Xid xid, int flags) throws XAException {
    throw new XAException(XAException.XAER_RMFAIL);
  }

  /**
   * Tells the resource manager to forget about a heuristically completed transaction branch.
   *
   * @param xid - A global transaction identifier.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public void forget(Xid xid) throws XAException {
    throw new XAException(XAException.XAER_RMFAIL);
  }

  /**
   * Obtains the current transaction timeout value set for this XAResource instance. If XAResource.setTransactionTimeout was not used prior to
   * invoking this method, the return value is the default timeout set for the resource manager; otherwise, the value used in the previous
   * setTransactionTimeout call is returned.
   *
   * @return - the transaction timeout value in seconds.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR and XAER_RMFAIL.
   */
  @Override
  public int getTransactionTimeout() throws XAException {
    return 0;
  }

  /**
   * This method is called to determine if the resource manager instance represented by the target object is the same as the resouce manager
   * instance represented by the parameter xares.
   *
   * @param xaResource - An XAResource object whose resource manager instance is to be compared with the resource manager instance of the target object.
   * @return - true if it's the same RM instance; otherwise false.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR and XAER_RMFAIL.
   */
  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    if (xaResource == null)
      throw new XAException(XAException.XAER_INVAL);

    if (xaResource instanceof JdbcCpoXaAdapter)
      return true;
    return false;
  }

  /**
   * Ask the resource manager to prepare for a transaction commit of the transaction specified in xid.
   *
   * @param xid - A global transaction identifier.
   * @return - A value indicating the resource manager's vote on the outcome of the transaction. The possible values are: XA_RDONLY or XA_OK.
   * If the resource manager wants to roll back the transaction, it should do so by raising an appropriate XAException in the prepare method.
   * @throws XAException - An error has occurred. Possible exception values are: XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public int prepare(Xid xid) throws XAException {
    if (inUseXidMap.get(xid) == null && idleXidMap.get(xid) == null)
      throw new XAException(XAException.XAER_DUPID);

    return XA_OK;
  }

  /**
   * Obtains a list of prepared transaction branches from a resource manager. The transaction manager calls this method during recovery to obtain the
   * list of transaction branches that are currently in prepared or heuristically completed states.
   *
   * @param flags - One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS. TMNOFLAGS must be used when no other flags are set in the parameter.
   * @return - The resource manager returns zero or more XIDs of the transaction branches that are currently in a prepared or heuristically completed state.
   * If an error occurs during the operation, the resource manager should throw the appropriate XAException.
   * @throws XAException - An error has occurred. Possible values are XAER_RMERR, XAER_RMFAIL, XAER_INVAL, and XAER_PROTO.
   */
  @Override
  public Xid[] recover(int flags) throws XAException {
    return new Xid[0];
  }

  /**
   * Informs the resource manager to roll back work done on behalf of a transaction branch.
   *
   * @param xid - A global transaction identifier.
   * @throws XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL, XAER_NOTA,
   * XAER_INVAL, or XAER_PROTO.
   *
   * If the transaction branch is already marked rollback-only the resource manager may throw one of the XA_RB* exceptions. Upon return, the resource manager
   * has rolled back the branch's work and has released all held resources.
   */
  @Override
  public void rollback(Xid xid) throws XAException {
    Connection conn = getWriteConnection(xid);

    try {
      conn.rollback();
    } catch (SQLException se) {
      throw new XAException(XAException.XAER_RMERR);
    } catch (Exception e) {
      throw new XAException(XAException.XAER_RMFAIL);
    }
  }

  /**
   * Sets the current transaction timeout value for this XAResource instance. Once set, this timeout value is effective until setTransactionTimeout is invoked
   * again with a different value. To reset the timeout value to the default value used by the resource manager, set the value to zero. If the timeout operation
   * is performed successfully, the method returns true; otherwise false. If a resource manager does not support explicitly setting the transaction timeout value,
   * this method returns false.
   *
   * @param seconds - The transaction timeout value in seconds.
   * @return - true if the transaction timeout value is set successfully; otherwise false.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR, XAER_RMFAIL, or XAER_INVAL.
   */
  @Override
  public boolean setTransactionTimeout(int seconds) throws XAException {
    return false;
  }

  /**
   * Starts work on behalf of a transaction branch specified in xid. If TMJOIN is specified, the start applies to joining a transaction previously seen by the
   * resource manager. If TMRESUME is specified, the start applies to resuming a suspended transaction specified in the parameter xid. If neither TMJOIN nor TMRESUME
   * is specified and the transaction specified by xid has previously been seen by the resource manager, the resource manager throws the XAException exception with
   * XAER_DUPID error code.
   *
   * @param xid - A global transaction identifier to be associated with the resource.
   * @param flags - One of TMNOFLAGS, TMJOIN, or TMRESUME.
   * @throws XAException - An error has occurred. Possible exceptions are XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
public void start(Xid xid, int flags) throws XAException {
    synchronized (XA_MUTEX) {
      if (resourceXid!=null)
        throw new XAException(XAException.XAER_PROTO);
      try {
        if (isConnectionBusy(getStaticConnection()))
          throw new XAException(XAException.XAER_OUTSIDE);
      } catch (CpoException ce) {
        throw new XAException(ExceptionHelper.getLocalizedMessage(ce));
      }

      switch (flags) {
        case TMNOFLAGS: // Starting a new transaction ID
          // if it is already in use then throw a dupe id error
          if (inUseXidMap.get(xid) != null || idleXidMap.get(xid) != null)
            throw new XAException(XAException.XAER_DUPID);
          try {
            Connection conn = super.getWriteConnection();
            inUseXidMap.put(xid, conn);
            resourceXid = xid;
          } catch (CpoException ce) {
            throw new XAException(XAException.XAER_RMERR);
          }
          break;
        case TMJOIN:
          if (inUseXidMap.get(xid) != null)
            resourceXid = xid;
          else if (idleXidMap.get(xid) != null) {
            Connection connection = idleXidMap.get(xid);
            if (connection != null)
              throw new XAException(XAException.XAER_PROTO);
            else {
              try {
                Connection conn = super.getWriteConnection();
                inUseXidMap.put(xid, conn);
                idleXidMap.remove(xid);
                resourceXid = xid;
              } catch (CpoException ce) {
                throw new XAException(XAException.XAER_RMERR);
              }
            }
          } else {
            throw new XAException(XAException.XAER_NOTA);
          }
          break;
        case TMRESUME:
          if (inUseXidMap.get(xid) != null)
            throw new XAException(XAException.XAER_PROTO);
          Connection connection = idleXidMap.get(xid);
          if (connection == null)
            throw new XAException(XAException.XAER_NOTA);
          else {
            idleXidMap.remove(xid);
            inUseXidMap.put(xid,connection);
          }
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  protected Connection getReadConnection() throws CpoException {
    return getWriteConnection();
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  protected Connection getWriteConnection() throws CpoException {
    try {
      return getWriteConnection(resourceXid);
    } catch (XAException xae) {
      throw new CpoException(xae);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected Connection getWriteConnection(Xid xid) throws XAException {

    synchronized (XA_MUTEX) {
      Connection connection;

      if (xid == null) {
          try {
            connection = getStaticConnection();
          } catch (CpoException ce) {
            throw new XAException(ExceptionHelper.getLocalizedMessage(ce));
          }
      } else {
        connection = inUseXidMap.get(xid);
      }

      setConnectionBusy(connection);

      return connection;
    }
  }

  /**
   *
   * Closes a local connection. A CpoTrxAdapter allows the user to manage the transaction so <code>closeLocalConnection</code>
   * does nothing but mark the connection as not busy.
   *
   * @param connection The connection to be closed
   */
  @Override
  protected void closeLocalConnection(Connection connection) {
    synchronized (XA_MUTEX) {
      clearConnectionBusy(connection);
    }
  }

  /**
   *
   * Commits a local connection. A CpoXaAdapter allows the user to manage both the local and the global transaction so <code>commitLocalConnection</code>
   * does nothing
   *
   * @param connection The connection to be committed
   */
  @Override
  protected void commitLocalConnection(Connection connection) {
  }

  /**
   *
   * Rollbacks a local connection. A CpoXaAdapter allows the user to manage both the local and the global transaction so <code>rollbackLocalConnection</code>
   * does nothing
   *
   * @param connection The connection to be rolled back
   */
  @Override
  protected void rollbackLocalConnection(Connection connection) {
  }

}

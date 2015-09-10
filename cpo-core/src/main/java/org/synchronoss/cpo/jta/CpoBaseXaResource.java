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
package org.synchronoss.cpo.jta;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dberry on 3/9/15.
 */
public abstract class CpoBaseXaResource<T> implements CpoXaResource {

  // Mutex used for assigning the statemap for the class
  private static final String XA_STATEMAP_MUTEX = "XA_STATEMAP_MUTEX";

  private static final HashMap<String,CpoXaStateMap<?>> cpoXaStateMapMap = new HashMap<>();

  private T localResource = null;

  private CpoXaStateMap<T> cpoXaStateMap = null;

  public CpoBaseXaResource(T localResource) {
    this.localResource = localResource;
    cpoXaStateMap = getCpoXaStateMap();
  }

  // Methods to be implemented by actual implementation
  protected abstract boolean isLocalResourceBusy() throws XAException;
  protected abstract  void prepareResource(T xaResource) throws XAException;
  protected abstract  void commitResource(T xaResource) throws XAException;
  protected abstract  void rollbackResource(T xaResource) throws XAException;
  protected abstract  T createNewResource() throws XAException;
  protected abstract  void closeResource(T xaResource) throws XAException;

  protected T getCurrentResource() {
    synchronized (cpoXaStateMap) {
      T currentResource = null;
      Xid associatedXid = cpoXaStateMap.getXaResourceMap().get(this);
      if ( associatedXid != null) {
        CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(associatedXid);
        currentResource = cpoXaState.getResource();
      }
      if (currentResource == null) {
        currentResource = localResource;
      }
      return currentResource;
    }
  }

  protected T getLocalResource() {
    return localResource;
  }

  /**
   * Closes the resource for the specified xid
   *
   * @param xid of the global transaction
   * @throws XAException
   */
  public void close(Xid xid) throws XAException {
    synchronized (cpoXaStateMap) {
      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      if (cpoXaState == null)
        throw new XAException(XAException.XAER_NOTA);

      // close the resource
      closeResource(cpoXaState.getResource());

      // unassociate
      cpoXaStateMap.getXaResourceMap().remove(cpoXaState.getAssignedResourceManager());

      // remove the xid reference
      cpoXaStateMap.getXidStateMap().remove(xid);
    }
  }

  /**
   * Commits the global transaction specified by xid.
   *
   * @param xid      - A global transaction identifier
   * @param onePhase - If true, the resource manager should use a one-phase commit protocol to commit the work done on behalf of xid.
   * @throws XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR,
   *                     XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   *                     <p>
   *                     If the resource manager did not commit the transaction and the parameter onePhase is set to true, the resource manager may throw
   *                     one of the XA_RB* exceptions. Upon return, the resource manager has rolled back the branch's work and has released all held resources.
   */
  @Override
  public void commit(Xid xid, boolean onePhase) throws XAException {
    synchronized (cpoXaStateMap) {
      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      if (cpoXaState == null)
        throw new XAException(XAException.XAER_NOTA);

      if (cpoXaState.getAssociation()==CpoXaState.XA_UNASSOCIATED) {
        if (onePhase) {
          if (!cpoXaState.isSuccess()) {
            rollbackResource(cpoXaState.getResource());
            cpoXaState.setSuccess(true);
            throw new XAException(XAException.XA_RBROLLBACK);
          }
          prepareResource(cpoXaState.getResource());
        }
        commitResource(cpoXaState.getResource());
        cpoXaState.setPrepared(false);
      } else {
        throw new XAException(XAException.XAER_PROTO);
      }
    }
  }

  /**
   * Ends the work performed on behalf of a transaction branch. The resource manager disassociates the XA resource from the transaction branch
   * specified and lets the transaction complete.
   * <p>
   * If TMSUSPEND is specified in the flags, the transaction branch is temporarily suspended in an incomplete state. The transaction context is
   * in a suspended state and must be resumed via the start method with TMRESUME specified.
   * <p>
   * If TMFAIL is specified, the portion of work has failed. The resource manager may mark the transaction as rollback-only
   * <p>
   * If TMSUCCESS is specified, the portion of work has completed successfully.
   *
   * @param xid   - A global transaction identifier that is the same as the identifier used previously in the start method.
   * @param flags - One of TMSUCCESS, TMFAIL, or TMSUSPEND.
   * @throws XAException - An error has occurred. Possible XAException values are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, XAER_PROTO, or XA_RB*.
   */
  @Override
  public void end(Xid xid, int flags) throws XAException {
    synchronized(cpoXaStateMap) {
      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      if (cpoXaState == null)
        throw new XAException(XAException.XAER_NOTA);

      // has this already been ended
      if (cpoXaState.getAssociation()==CpoXaState.XA_UNASSOCIATED)
        throw new XAException(XAException.XAER_PROTO);

      switch(flags) {
        case TMSUSPEND:
          // You can only suspend an associated transaction
          if (cpoXaState.getAssociation()==CpoXaState.XA_ASSOCIATED) {
            cpoXaStateMap.getXaResourceMap().remove(cpoXaState.getAssignedResourceManager());
            cpoXaState.setAssociation(CpoXaState.XA_SUSPENDED);
            cpoXaState.setAssignedResourceManager(null);
          } else {
            throw new XAException(XAException.XAER_PROTO);
          }
          break;

          //you can fail or succeed an associated or suspended trx
        case TMFAIL:
          // mark transaction as failed
          cpoXaStateMap.getXaResourceMap().remove(cpoXaState.getAssignedResourceManager());
          cpoXaState.setAssociation(CpoXaState.XA_UNASSOCIATED);
          cpoXaState.setAssignedResourceManager(null);
          cpoXaState.setSuccess(cpoXaState.isSuccess()&&false);
         break;

        case TMSUCCESS:
          // mark transaction as success
          cpoXaStateMap.getXaResourceMap().remove(cpoXaState.getAssignedResourceManager());
          cpoXaState.setAssociation(CpoXaState.XA_UNASSOCIATED);
          cpoXaState.setAssignedResourceManager(null);
          cpoXaState.setSuccess(cpoXaState.isSuccess()&&true);
          break;

        default:
          throw new XAException(XAException.XAER_INVAL);
      }

    }
  }

  /**
   * Tells the resource manager to forget about a heuristically completed transaction branch.
   *
   * @param xid - A global transaction identifier.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public void forget(Xid xid) throws XAException {
    close(xid);
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
   * Ask the resource manager to prepare for a transaction commit of the transaction specified in xid.
   *
   * @param xid - A global transaction identifier.
   * @return - A value indicating the resource manager's vote on the outcome of the transaction. The possible values are: XA_RDONLY or XA_OK.
   * If the resource manager wants to roll back the transaction, it should do so by raising an appropriate XAException in the prepare method.
   * @throws XAException - An error has occurred. Possible exception values are: XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public int prepare(Xid xid) throws XAException {
    synchronized (cpoXaStateMap) {
      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      if (cpoXaState == null)
        throw new XAException(XAException.XAER_NOTA);

      if (!cpoXaState.isSuccess()) {
        rollbackResource(cpoXaState.getResource());
        cpoXaState.setPrepared(false);
        cpoXaState.setSuccess(true);
        throw new XAException(XAException.XA_RBROLLBACK);
      }

      if (cpoXaState.getAssociation()==CpoXaState.XA_UNASSOCIATED){
        prepareResource(cpoXaState.getResource());
        cpoXaState.setPrepared(true);
      } else {
          throw new XAException(XAException.XAER_PROTO);
      }

      return XA_OK;
    }
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
    synchronized (cpoXaStateMap) {
      ArrayList<Xid> xids = new ArrayList<>();

      for (CpoXaState<T> cpoXaState : cpoXaStateMap.getXidStateMap().values()) {
        if (cpoXaState.isPrepared())
          xids.add(cpoXaState.getXid());
      }
      return xids.toArray(new Xid[0]);
    }
  }

  /**
   * Informs the resource manager to roll back work done on behalf of a transaction branch.
   *
   * @param xid - A global transaction identifier.
   * @throws XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM, XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL, XAER_NOTA,
   *                     XAER_INVAL, or XAER_PROTO.
   *                     <p>
   *                     If the transaction branch is already marked rollback-only the resource manager may throw one of the XA_RB* exceptions. Upon return, the resource manager
   *                     has rolled back the branch's work and has released all held resources.
   */
  @Override
  public void rollback(Xid xid) throws XAException {
    synchronized (cpoXaStateMap) {
      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      if (cpoXaState == null)
        throw new XAException(XAException.XAER_NOTA);

      if (cpoXaState.getAssociation()==CpoXaState.XA_UNASSOCIATED) {
        rollbackResource(cpoXaState.getResource());
        cpoXaState.setPrepared(false);
        cpoXaState.setSuccess(true);
      } else {
        throw new XAException(XAException.XAER_PROTO);
      }
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
   * @param xid   - A global transaction identifier to be associated with the resource.
   * @param flags - One of TMNOFLAGS, TMJOIN, or TMRESUME.
   * @throws XAException - An error has occurred. Possible exceptions are XA_RB*, XAER_RMERR, XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public void start(Xid xid, int flags) throws XAException {

    synchronized (cpoXaStateMap) {
      // see if we are already associated with a global transaction
      if (cpoXaStateMap.getXaResourceMap().get(this) != null)
        throw new XAException(XAException.XAER_PROTO);

      // see if we are not in the middle of doing something on the local transaction
      if (isLocalResourceBusy())
        throw new XAException(XAException.XAER_OUTSIDE);

      CpoXaState<T> cpoXaState = cpoXaStateMap.getXidStateMap().get(xid);

      switch (flags) {
        case TMNOFLAGS: // Starting a new transaction ID
          // if it is already in use then throw a dupe id error
          if (cpoXaState != null)
            throw new XAException(XAException.XAER_DUPID);

          cpoXaState = new CpoXaState<>(xid, createNewResource(), CpoXaState.XA_ASSOCIATED, this, true);
          cpoXaStateMap.getXidStateMap().put(xid, cpoXaState);
          cpoXaStateMap.getXaResourceMap().put(this, xid);
          break;

        case TMJOIN:
          if (cpoXaState == null)
            throw new XAException(XAException.XAER_NOTA);

          if (cpoXaState.getAssociation()==CpoXaState.XA_UNASSOCIATED) {
            cpoXaState.setAssociation(CpoXaState.XA_ASSOCIATED);
            cpoXaState.setAssignedResourceManager(this);
            cpoXaStateMap.getXaResourceMap().put(this, xid);
          } else {
            throw new XAException(XAException.XAER_PROTO);
          }
          break;

        case TMRESUME:
          if (cpoXaState == null)
            throw new XAException(XAException.XAER_NOTA);

          // you can only join a suspended transaction
          if (cpoXaState.getAssociation() == CpoXaState.XA_SUSPENDED) {
            cpoXaState.setAssociation(CpoXaState.XA_ASSOCIATED);
            cpoXaState.setAssignedResourceManager(this);
            cpoXaStateMap.getXaResourceMap().put(this, xid);
          } else {
            throw new XAException(XAException.XAER_PROTO);
          }
          break;

        default: // invalid arguments
          throw new XAException(XAException.XAER_INVAL);
      }
    }
  }


  private CpoXaStateMap<T> getCpoXaStateMap() {
    synchronized(XA_STATEMAP_MUTEX) {
      CpoXaStateMap<T> stateMap = (CpoXaStateMap<T>)cpoXaStateMapMap.get(this.getClass().getName());
      if (stateMap==null) {
        stateMap = new CpoXaStateMap<T>();
        cpoXaStateMapMap.put(this.getClass().getName(), stateMap);
      }
      return stateMap;
    }
  }


}

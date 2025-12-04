package org.synchronoss.cpo.jta;

/*-
 * [[
 * core
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

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import org.synchronoss.cpo.CpoException;

/** Created by dberry on 3/9/15. */
public abstract class CpoBaseXaResource<T> implements CpoXaResource {

  /**
   * Each XAResource, the class that extends this, needs to see the XID state for all similar
   * resources. So a StringBuilderXaResource needs to see all the XIDs that are in play with other
   * StringBuilderXaResource instances
   */
  private static final ConcurrentHashMap<String, ConcurrentHashMap<Xid, CpoXaState<?>>>
      cpoXaResourceStateMap = new ConcurrentHashMap<>();

  private volatile Xid associatedXid = null;

  private final T localResource;

  private final Semaphore semaphore = new Semaphore(1);

  private final ConcurrentHashMap<Xid, CpoXaState<?>> xidStateMap = getXidStateMap();

  public CpoBaseXaResource(T localResource) {
    this.localResource = Objects.requireNonNull(localResource);
  }

  protected abstract void prepareResource(T xaResource) throws XAException;

  protected abstract void commitResource(T xaResource) throws XAException;

  protected abstract void rollbackResource(T xaResource) throws XAException;

  protected abstract T createNewResource() throws XAException;

  protected abstract void closeResource(T xaResource) throws XAException;

  private T getResource() {
    @SuppressWarnings("unchecked")
    CpoXaState<T> cpoXaState =
        associatedXid == null ? null : (CpoXaState<T>) xidStateMap.get(associatedXid);
    return cpoXaState == null
        ? localResource
        : cpoXaState.getResource() == null ? localResource : cpoXaState.getResource();
  }

  public <R> R apply(Function<T, R> function) throws CpoException {
    semaphore.acquireUninterruptibly();
    try {
      return function.apply(getResource());
    } catch (RuntimeException e) {
      if (e.getCause() instanceof CpoException) throw (CpoException) e.getCause();
      throw e;
    } finally {
      semaphore.release();
    }
  }

  public void accept(Consumer<T> consumer) throws CpoException {
    semaphore.acquireUninterruptibly();
    try {
      consumer.accept(getResource());
    } catch (RuntimeException e) {
      if (e.getCause() instanceof CpoException) throw (CpoException) e.getCause();
      throw e;
    } finally {
      semaphore.release();
    }
  }

  /**
   * Closes the resource associated with this instance
   *
   * @throws XAException -
   */
  public void closeAssociated() throws XAException {
    if (associatedXid != null) {
      close(associatedXid);
    }
  }

  /**
   * Closes the resource for the specified xid
   *
   * @param xid of the global transaction
   * @throws XAException An error has occurred.
   */
  @SuppressWarnings("unchecked")
  public void close(Xid xid) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (cpoXaState == null)
              throw new RuntimeException(
                  CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));

            // unassociate
            if (cpoXaState.getAssignedResourceManager() != null) {
              cpoXaState.getAssignedResourceManager().associatedXid = null;
            }

            try {
              closeResource((T) cpoXaState.getResource());
            } catch (XAException e) {
              throw new RuntimeException(e);
            }

            return null;
          });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
  }

  /**
   * Commits the global transaction specified by xid.
   *
   * @param xid A global transaction identifier
   * @param onePhase If true, the resource manager should use a one-phase commit protocol to commit
   *     the work done on behalf of xid.
   * @throws XAException An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM,
   *     XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   *     <p>If the resource manager did not commit the transaction and the parameter onePhase is set
   *     to true, the resource manager may throw one of the XA_RB* exceptions. Upon return, the
   *     resource manager has rolled back the branch's work and has released all held resources.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void commit(Xid xid, boolean onePhase) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (cpoXaState == null)
              throw new RuntimeException(
                  CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));
            if (cpoXaState.getAssociation() != CpoXaState.XA_UNASSOCIATED)
              throw new RuntimeException(
                  CpoXaError.createXAException(
                      CpoXaError.XAER_PROTO, "Commit can only be called on an unassociated XID"));
            try {
              if (onePhase) {
                if (!cpoXaState.isSuccess()) {
                  rollbackResource((T) cpoXaState.getResource());
                  cpoXaState.setSuccess(true);
                  throw new RuntimeException(
                      CpoXaError.createXAException(
                          CpoXaError.XA_RBROLLBACK,
                          "Trying to commit an unsuccessful transaction. Transaction Rolled Back"));
                }
                prepareResource((T) cpoXaState.getResource());
              }
              commitResource((T) cpoXaState.getResource());
              cpoXaState.setPrepared(false);
            } catch (XAException e) {
              throw new RuntimeException(e);
            }
            return cpoXaState;
          });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
  }

  /**
   * Ends the work performed on behalf of a transaction branch. The resource manager disassociates
   * the XA resource from the transaction branch specified and lets the transaction complete.
   *
   * <p>If TMSUSPEND is specified in the flags, the transaction branch is temporarily suspended in
   * an incomplete state. The transaction context is in a suspended state and must be resumed via
   * the start method with TMRESUME specified.
   *
   * <p>If TMFAIL is specified, the portion of work has failed. The resource manager may mark the
   * transaction as rollback-only
   *
   * <p>If TMSUCCESS is specified, the portion of work has completed successfully.
   *
   * @param xid A global transaction identifier that is the same as the identifier used previously
   *     in the start method.
   * @param flags One of TMSUCCESS, TMFAIL, or TMSUSPEND.
   * @throws XAException An error has occurred. Possible XAException values are XAER_RMERR,
   *     XAER_RMFAIL, XAER_NOTA, XAER_INVAL, XAER_PROTO, or XA_RB*.
   */
  @Override
  public void end(Xid xid, int flags) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (cpoXaState == null)
              throw new RuntimeException(
                  CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));

            // has this already been ended
            if (cpoXaState.getAssociation() == CpoXaState.XA_UNASSOCIATED)
              throw new RuntimeException(
                  CpoXaError.createXAException(
                      CpoXaError.XAER_PROTO, "Cannot End an Unassociated XID"));

            switch (flags) {
              case TMSUSPEND:
                // You can only suspend an associated transaction
                if (cpoXaState.getAssociation() != CpoXaState.XA_ASSOCIATED)
                    throw new RuntimeException(
                            CpoXaError.createXAException(
                                    CpoXaError.XAER_PROTO, "You can only suspend an associated XID"));
                cpoXaState.setAssociation(CpoXaState.XA_SUSPENDED);
                break;

              // you can fail or succeed an associated or suspended trx
              case TMFAIL:
                // mark transaction as failed
                cpoXaState.setAssociation(CpoXaState.XA_UNASSOCIATED);
                cpoXaState.setSuccess(!cpoXaState.isSuccess());
                break;

              case TMSUCCESS:
                // mark transaction as success
                cpoXaState.setAssociation(CpoXaState.XA_UNASSOCIATED);
                cpoXaState.setSuccess(cpoXaState.isSuccess());
                break;

              default:
                throw new RuntimeException(
                    CpoXaError.createXAException(CpoXaError.XAER_INVAL, "Invalid flag for end()"));
            }

            if (cpoXaState.getAssignedResourceManager() != null) {
              cpoXaState.getAssignedResourceManager().associatedXid = null;
              cpoXaState.setAssignedResourceManager(null);
            }

            return cpoXaState;
          });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
  }

  /**
   * Tells the resource manager to forget about a heuristically completed transaction branch.
   *
   * @param xid A global transaction identifier.
   * @throws XAException An error has occurred. Possible exception values are XAER_RMERR,
   *     XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  public void forget(Xid xid) throws XAException {
    close(xid);
  }

  /**
   * Obtains the current transaction timeout value set for this XAResource instance. If
   * XAResource.setTransactionTimeout was not used prior to invoking this method, the return value
   * is the default timeout set for the resource manager; otherwise, the value used in the previous
   * setTransactionTimeout call is returned.
   *
   * @return the transaction timeout value in seconds.
   * @throws XAException An error has occurred. Possible exception values are XAER_RMERR and
   *     XAER_RMFAIL.
   */
  @Override
  public int getTransactionTimeout() throws XAException {
    return 0;
  }

  /**
   * Ask the resource manager to prepare for a transaction commit of the transaction specified in
   * xid.
   *
   * @param xid A global transaction identifier.
   * @return - A value indicating the resource manager's vote on the outcome of the transaction. The
   *     possible values are: XA_RDONLY or XA_OK. If the resource manager wants to roll back the
   *     transaction, it should do so by raising an appropriate XAException in the prepare method.
   * @throws XAException - An error has occurred. Possible exception values are: XA_RB*, XAER_RMERR,
   *     XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  @SuppressWarnings("unchecked")
  public int prepare(Xid xid) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (cpoXaState == null)
              throw new RuntimeException(
                  CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));
            if (cpoXaState.getAssociation() != CpoXaState.XA_UNASSOCIATED)
              throw new RuntimeException(
                  CpoXaError.createXAException(
                      CpoXaError.XAER_PROTO, "Prepare can only be called on an unassociated XID"));
            try {
              if (!cpoXaState.isSuccess()) {
                rollbackResource((T) cpoXaState.getResource());
                cpoXaState.setPrepared(false);
                cpoXaState.setSuccess(true);
                throw new RuntimeException(
                    CpoXaError.createXAException(
                        CpoXaError.XA_RBROLLBACK,
                        "Trying to prepare an unsuccessfull transaction. Rollback performed"));
              }
              prepareResource((T) cpoXaState.getResource());
              cpoXaState.setPrepared(true);
            } catch (XAException e) {
              throw new RuntimeException(e);
            }

            return cpoXaState;
          });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
    return XA_OK;
  }

  /**
   * Obtains a list of prepared transaction branches from a resource manager. The transaction
   * manager calls this method during recovery to obtain the list of transaction branches that are
   * currently in prepared or heuristically completed states.
   *
   * @param flags - One of TMSTARTRSCAN, TMENDRSCAN, TMNOFLAGS. TMNOFLAGS must be used when no other
   *     flags are set in the parameter.
   * @return - The resource manager returns zero or more XIDs of the transaction branches that are
   *     currently in a prepared or heuristically completed state. If an error occurs during the
   *     operation, the resource manager should throw the appropriate XAException.
   * @throws XAException - An error has occurred. Possible values are XAER_RMERR, XAER_RMFAIL,
   *     XAER_INVAL, and XAER_PROTO.
   */
  @Override
  public Xid[] recover(int flags) throws XAException {
    return switch (flags) {
      case TMSTARTRSCAN, TMENDRSCAN, TMSTARTRSCAN | TMENDRSCAN, TMNOFLAGS ->
          xidStateMap.values().stream()
              .filter(CpoXaState::isPrepared)
              .map(CpoXaState::getXid)
              .toList()
              .toArray(new Xid[0]);
      default ->
          throw CpoXaError.createXAException(CpoXaError.XAER_INVAL, "Invalid flag for recover()");
    };
  }

  /**
   * Informs the resource manager to roll back work done on behalf of a transaction branch.
   *
   * @param xid - A global transaction identifier.
   * @throws XAException - An error has occurred. Possible XAExceptions are XA_HEURHAZ, XA_HEURCOM,
   *     XA_HEURRB, XA_HEURMIX, XAER_RMERR, XAER_RMFAIL, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   *     <p>If the transaction branch is already marked rollback-only the resource manager may throw
   *     one of the XA_RB* exceptions. Upon return, the resource manager has rolled back the
   *     branch's work and has released all held resources.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void rollback(Xid xid) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (cpoXaState == null)
              throw new RuntimeException(
                  CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));
            if (cpoXaState.getAssociation() != CpoXaState.XA_UNASSOCIATED)
              throw new RuntimeException(
                  CpoXaError.createXAException(
                      CpoXaError.XAER_PROTO, "Rollback can only be called on an unassociated XID"));

            try {
              rollbackResource((T) cpoXaState.getResource());
              cpoXaState.setPrepared(false);
              cpoXaState.setSuccess(true);
            } catch (XAException e) {
              throw new RuntimeException(e);
            }
            return cpoXaState;
          });

    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
  }

  /**
   * Sets the current transaction timeout value for this XAResource instance. Once set, this timeout
   * value is effective until setTransactionTimeout is invoked again with a different value. To
   * reset the timeout value to the default value used by the resource manager, set the value to
   * zero. If the timeout operation is performed successfully, the method returns true; otherwise
   * false. If a resource manager does not support explicitly setting the transaction timeout value,
   * this method returns false.
   *
   * @param seconds - The transaction timeout value in seconds.
   * @return - true if the transaction timeout value is set successfully; otherwise false.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR,
   *     XAER_RMFAIL, or XAER_INVAL.
   */
  @Override
  public boolean setTransactionTimeout(int seconds) throws XAException {
    return false;
  }

  /**
   * Starts work on behalf of a transaction branch specified in xid. If TMJOIN is specified, the
   * start applies to joining a transaction previously seen by the resource manager. If TMRESUME is
   * specified, the start applies to resuming a suspended transaction specified in the parameter
   * xid. If neither TMJOIN nor TMRESUME is specified and the transaction specified by xid has
   * previously been seen by the resource manager, the resource manager throws the XAException
   * exception with XAER_DUPID error code.
   *
   * @param xid - A global transaction identifier to be associated with the resource.
   * @param flags - One of TMNOFLAGS, TMJOIN, or TMRESUME.
   * @throws XAException - An error has occurred. Possible exceptions are XA_RB*, XAER_RMERR,
   *     XAER_RMFAIL, XAER_DUPID, XAER_OUTSIDE, XAER_NOTA, XAER_INVAL, or XAER_PROTO.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void start(Xid xid, int flags) throws XAException {
    try {
      xidStateMap.compute(
          xid,
          (k, cpoXaState) -> {
            if (associatedXid != null)
              throw new RuntimeException(
                  CpoXaError.createXAException(
                      CpoXaError.XAER_PROTO, "Start can not be called on an associated XID"));

            switch (flags) {
              case TMNOFLAGS: // Starting a new transaction ID
                // if it is already in use then throw a dupe id error
                if (cpoXaState != null)
                  throw new RuntimeException(
                      CpoXaError.createXAException(CpoXaError.XAER_DUPID, "Duplicate XID"));

                try {
                  cpoXaState =
                      new CpoXaState<>(
                          xid, createNewResource(), CpoXaState.XA_ASSOCIATED, this, true);
                } catch (XAException e) {
                  throw new RuntimeException(e);
                }
                break;

              case TMJOIN:
                if (cpoXaState == null)
                  throw new RuntimeException(
                      CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));

                if (cpoXaState.getAssociation() != CpoXaState.XA_UNASSOCIATED) {
                  throw new RuntimeException(
                      CpoXaError.createXAException(
                          CpoXaError.XAER_PROTO,
                          "TMJOIN can only be used with an unassociated XID"));
                }
                break;

              case TMRESUME:
                if (cpoXaState == null)
                  throw new RuntimeException(
                      CpoXaError.createXAException(CpoXaError.XAER_NOTA, "Unknown XID"));

                // you can only resume a suspended transaction
                if (cpoXaState.getAssociation() != CpoXaState.XA_SUSPENDED) {
                  throw new RuntimeException(
                      CpoXaError.createXAException(
                          CpoXaError.XAER_PROTO, "TMRESUME can only be used with a suspended XID"));
                }
                break;

              default: // invalid arguments
                throw new RuntimeException(
                    CpoXaError.createXAException(CpoXaError.XAER_INVAL, "Invalid start() flag"));
            }

            cpoXaState.setAssociation(CpoXaState.XA_ASSOCIATED);
            ((CpoXaState<T>) cpoXaState).setAssignedResourceManager(this);
            associatedXid = xid;
            return cpoXaState;
          });
    } catch (RuntimeException e) {
      if (e.getCause() instanceof XAException) throw (XAException) e.getCause();
      throw e;
    }
  }

  private ConcurrentHashMap<Xid, CpoXaState<?>> getXidStateMap() {
    return cpoXaResourceStateMap.computeIfAbsent(
        this.getClass().getName(), (k) -> new ConcurrentHashMap<>());
  }
}

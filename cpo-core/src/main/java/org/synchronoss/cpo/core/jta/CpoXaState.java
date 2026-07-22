package org.synchronoss.cpo.core.jta;

/*-
 * [[
 * core
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

import javax.transaction.xa.Xid;

/**
 * Mutable state tracked for a single XA transaction branch ({@link Xid}): its association state
 * with a resource manager, its underlying resource, and its success/prepared flags.
 *
 * <p>Instances are held in the per-subclass state map maintained by {@link CpoBaseXaResource} and
 * mutated as the branch progresses through start/end/prepare/commit/rollback.
 *
 * @param <T> the type of the underlying resource associated with this transaction branch
 * @author dberry
 */
public class CpoXaState<T> {

  /** The transaction branch has no resource manager currently associated with it. */
  public static final int XA_UNASSOCIATED = 0;

  /** The transaction branch is currently associated with a resource manager. */
  public static final int XA_ASSOCIATED = 1;

  /** The transaction branch's association has been temporarily suspended. */
  public static final int XA_SUSPENDED = 2;

  private Xid xid;
  private T resource = null;
  private int association = XA_UNASSOCIATED;
  private boolean success = true;
  private boolean prepared = false;
  private CpoBaseXaResource<T> assignedResourceManager;

  private CpoXaState() {}

  /**
   * Constructs the state for a new transaction branch.
   *
   * @param xid the global transaction identifier for this branch
   * @param resource the underlying resource associated with this branch
   * @param state the initial association state; one of {@link #XA_UNASSOCIATED}, {@link
   *     #XA_ASSOCIATED}, or {@link #XA_SUSPENDED}
   * @param assignedResourceManager the resource manager instance currently associated with this
   *     branch, or {@code null} if none
   * @param success whether the work done on this branch so far is considered successful
   */
  public CpoXaState(
      Xid xid,
      T resource,
      int state,
      CpoBaseXaResource<T> assignedResourceManager,
      boolean success) {
    this.xid = xid;
    this.resource = resource;
    this.association = state;
    this.assignedResourceManager = assignedResourceManager;
    this.success = success;
  }

  /**
   * Gets the global transaction identifier for this branch.
   *
   * @return the global transaction identifier for this branch
   */
  public Xid getXid() {
    return xid;
  }

  /**
   * Gets the underlying resource associated with this branch.
   *
   * @return the underlying resource associated with this branch
   */
  public T getResource() {
    return resource;
  }

  /**
   * Sets the association state.
   *
   * @param association one of {@link #XA_UNASSOCIATED}, {@link #XA_ASSOCIATED}, or {@link
   *     #XA_SUSPENDED}
   */
  public void setAssociation(int association) {
    this.association = association;
  }

  /**
   * Gets the current association state.
   *
   * @return the current association state; one of {@link #XA_UNASSOCIATED}, {@link #XA_ASSOCIATED},
   *     or {@link #XA_SUSPENDED}
   */
  public int getAssociation() {
    return association;
  }

  /**
   * Sets whether the work done on this branch so far is considered successful.
   *
   * @param success {@code true} if successful, {@code false} if the branch has been marked failed
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Gets whether the work done on this branch so far is considered successful.
   *
   * @return {@code true} if the work done on this branch so far is considered successful
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets whether this branch has been prepared (voted to commit) as part of two-phase commit.
   *
   * @param prepared {@code true} if prepared, {@code false} otherwise
   */
  public void setPrepared(boolean prepared) {
    this.prepared = prepared;
  }

  /**
   * Gets whether this branch has been prepared as part of two-phase commit.
   *
   * @return {@code true} if this branch has been prepared as part of two-phase commit
   */
  public boolean isPrepared() {
    return prepared;
  }

  /**
   * Gets the resource manager instance currently associated with this branch.
   *
   * @return the resource manager instance currently associated with this branch, or {@code null} if
   *     none
   */
  public CpoBaseXaResource<T> getAssignedResourceManager() {
    return assignedResourceManager;
  }

  /**
   * Sets the resource manager instance currently associated with this branch.
   *
   * @param assignedResourceManager the resource manager to associate, or {@code null} to clear the
   *     association
   */
  public void setAssignedResourceManager(CpoBaseXaResource<T> assignedResourceManager) {
    this.assignedResourceManager = assignedResourceManager;
  }
}

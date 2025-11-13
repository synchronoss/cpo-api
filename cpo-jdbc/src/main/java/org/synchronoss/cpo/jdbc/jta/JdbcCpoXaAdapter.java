/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.jdbc.jta;

import org.synchronoss.cpo.*;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapterFactory;
import org.synchronoss.cpo.jdbc.JdbcCpoTrxAdapter;
import org.synchronoss.cpo.jta.CpoBaseXaResource;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A JdbcCpoXaAdapter wraps the XAResource processing around a JdbcCpoTrxAdapter.
 * </p><p>
 * It manages a local and global CpoTrxAdapter and manages the association of XIDs to transactions
 * </p><p>
 * The XAResource interface is a Java mapping of the industry standard XA interface based on the X/Open CAE Specification
 * (Distributed Transaction Processing: The XA Specification).
 * </p><p>
 * The XA interface defines the contract between a Resource Manager and a Transaction Manager in a distributed transaction processing (DTP) environment.
 * A JDBC driver or a JMS provider implements this interface to support the association between a global transaction and a database or message service connection.
 * </p><p>
 * The XAResource interface can be supported by any transactional resource that is intended to be used by application programs in an environment
 * where transactions are controlled by an external transaction manager. An example of such a resource is a database management system. An application
 * may access data through multiple database connections. Each database connection is enlisted with the transaction manager as a transactional resource.
 * The transaction manager obtains an XAResource for each connection participating in a global transaction. The transaction manager uses the start method to
 * associate the global transaction with the resource, and it uses the end method to disassociate the transaction from the resource. The resource manager is
 * responsible for associating the global transaction to all work performed on its data between the start and end method invocations.
 * </p><p>
 * At transaction commit time, the resource managers are informed by the transaction manager to prepare, commit, or rollback a transaction according to the
 * two-phase commit protocol.
 * </p>
 */
public class JdbcCpoXaAdapter extends CpoBaseXaResource<JdbcCpoAdapter> implements CpoTrxAdapter {
    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;

  private JdbcCpoAdapterFactory jdbcCpoAdapterFactory;

    /**
     * Construct a JdbcCpoXaAdapter
     *
     * @param jdbcCpoAdapterFactory  - The adapter factory to use to create the JdbcCpoXaAdapter
     * @throws CpoException - An error getting the JdbcCpoXaAdapter
     */
  public JdbcCpoXaAdapter(JdbcCpoAdapterFactory jdbcCpoAdapterFactory) throws CpoException {
    super((JdbcCpoAdapter) jdbcCpoAdapterFactory.getCpoAdapter());
    this.jdbcCpoAdapterFactory = jdbcCpoAdapterFactory;
  }

  /**
   * Commits the current transaction behind the CpoTrxAdapter
   */
  @Override
  public void commit() throws CpoException {
    JdbcCpoAdapter currentResource = getCurrentResource();
    if (currentResource != getLocalResource())
      ((JdbcCpoTrxAdapter)currentResource).commit();
  }

  /**
   * Rollback the current transaction behind the CpoTrxAdapter
   */
  @Override
  public void rollback() throws CpoException {
    JdbcCpoAdapter currentResource = getCurrentResource();
    if (currentResource != getLocalResource())
      ((JdbcCpoTrxAdapter)currentResource).rollback();
  }

  /**
   * Closes the current transaction behind the CpoTrxAdapter. All subsequent calls to the CpoTrxAdapter will throw an
   * exception.
   */
  @Override
  public void close() throws CpoException {
    try {
      super.closeAssociated();
    } catch (XAException xae) {
      throw new CpoException(xae);
    }
  }

  /**
   * Returns true if the TrxAdapter has been closed, false if it is still active
   */
  @Override
  public boolean isClosed() throws CpoException {
    JdbcCpoAdapter currentResource = getCurrentResource();
    if (currentResource != getLocalResource())
      return ((JdbcCpoTrxAdapter)currentResource).isClosed();
    else
      return true;
  }

  /**
   * Returns true if the TrxAdapter is processing a request, false if it is not
   */
  @Override
  public boolean isBusy() throws CpoException {
    JdbcCpoAdapter currentResource = getCurrentResource();
    if (currentResource != getLocalResource())
      return ((JdbcCpoTrxAdapter)currentResource).isBusy();
    else
      return false;
  }

  @Override
  public <T> long insertObject(T obj) throws CpoException {
    return getCurrentResource().insertObject(obj);
  }

  @Override
  public <T> long insertObject(String name, T obj) throws CpoException {
    return getCurrentResource().insertObject(name, obj);
  }

  @Override
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().insertObject(name, obj, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long insertObjects(Collection<T> coll) throws CpoException {
    return getCurrentResource().insertObjects(coll);
  }

  @Override
  public <T> long insertObjects(String name, Collection<T> coll) throws CpoException {
    return getCurrentResource().insertObjects(name, coll);
  }

  @Override
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().insertObjects(name, coll, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteObject(T obj) throws CpoException {
    return getCurrentResource().deleteObject(obj);
  }

  @Override
  public <T> long deleteObject(String name, T obj) throws CpoException {
    return getCurrentResource().deleteObject( name, obj);
  }

  @Override
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().deleteObject( name, obj, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteObjects(Collection<T> coll) throws CpoException {
    return  getCurrentResource().deleteObjects(coll);
  }

  @Override
  public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException {
    return  getCurrentResource().deleteObjects( name, coll);
  }

  @Override
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().deleteObjects( name, coll, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> T executeObject(T obj) throws CpoException {
    return getCurrentResource().executeObject(obj);
  }

  @Override
  public <T> T executeObject(String name, T object) throws CpoException {
    return getCurrentResource().executeObject( name, object );
  }

  @Override
  public <T, C> T executeObject(String name, C criteria, T result) throws CpoException {
    return getCurrentResource().executeObject( name, criteria, result );
  }

  @Override
  public <T> long existsObject(T obj) throws CpoException {
    return getCurrentResource().existsObject(obj);
  }

  @Override
  public <T> long existsObject(String name, T obj) throws CpoException {
    return getCurrentResource().existsObject( name, obj);
  }

  @Override
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException {
    return getCurrentResource().existsObject( name, obj, wheres);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException {
    return  getCurrentResource().newOrderBy(attribute, ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException {
    return  getCurrentResource().newOrderBy( marker,  attribute,  ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException {
    return getCurrentResource().newOrderBy(attribute,  ascending,  function);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function) throws CpoException {
    return getCurrentResource().newOrderBy( marker,  attribute,  ascending,  function);
  }

  @Override
  public CpoWhere newWhere() throws CpoException {
    return getCurrentResource().newWhere();
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value) throws CpoException {
    return getCurrentResource().newWhere( logical,  attr,  comp,  value);
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not) throws CpoException {
    return getCurrentResource().newWhere( logical,  attr,  comp,  value,  not);
  }

  @Override
  public <T> long persistObject(T bean) throws CpoException {
    return getCurrentResource().persistObject(bean);
  }

  @Override
  public <T> long persistObject(String name, T bean) throws CpoException {
    return getCurrentResource().persistObject(name, bean);
  }

  @Override
  public <T> long persistObjects(Collection<T> coll) throws CpoException {
    return getCurrentResource().persistObjects(coll);
  }

  @Override
  public <T> long persistObjects(String name, Collection<T> coll) throws CpoException {
    return getCurrentResource().persistObjects(name, coll);
  }

  @Override
  public <T> T retrieveBean(T bean) throws CpoException {
    return getCurrentResource().retrieveBean(bean);
  }

  @Override
  public <T> T retrieveBean(String name, T bean) throws CpoException {
    return getCurrentResource().retrieveBean(name, bean);
  }

  @Override
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().retrieveBean(name, bean, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBean(name, criteria, result, wheres, orderBy);
  }

  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().retrieveBean(name, criteria, result, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, where, orderBy);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, orderBy);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, wheres, orderBy);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, result);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, result, where, orderBy);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, result, wheres, orderBy);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, result, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T, C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, int queueSize) throws CpoException {
    return getCurrentResource().retrieveBeans(name, criteria, result, wheres, orderBy, nativeExpressions, queueSize);
  }

  @Override
  public <T> long updateObject(T obj) throws CpoException {
    return getCurrentResource().updateObject(obj);
  }

  @Override
  public <T> long updateObject(String name, T obj) throws CpoException {
    return getCurrentResource().updateObject(name, obj);
  }

  @Override
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().updateObject(name, obj, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long updateObjects(Collection<T> coll) throws CpoException {
    return getCurrentResource().updateObjects(coll);
  }

  @Override
  public <T> long updateObjects(String name, Collection<T> coll) throws CpoException {
    return getCurrentResource().updateObjects(name, coll);
  }

  @Override
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return getCurrentResource().updateObjects(name, coll, wheres, orderBy, nativeExpressions);
  }

  @Override
  public CpoMetaDescriptor getCpoMetaDescriptor() {
    return getCurrentResource().getCpoMetaDescriptor();
  }

  @Override
  public String getDataSourceName() {
    return getCurrentResource().getDataSourceName();
  }

  @Override
  public List<CpoAttribute> getCpoAttributes(String expression) throws CpoException {
    return getCurrentResource().getCpoAttributes(expression);
  }

   @Override
   public boolean isSameRM(XAResource xaResource) throws XAException {
     if (xaResource == null)
       throw CpoXaError.createXAException(CpoXaError.XAER_INVAL, "Invalid parameter. xaResource cannot be null.");

     return xaResource instanceof JdbcCpoXaAdapter;
   }

  @Override
  public boolean isLocalResourceBusy() throws XAException {
    return false;
//    boolean busy;
//    try {
//      busy = getLocalResource().isBusy();
//    } catch (CpoException ce) {
//      throw new XAException(XAException.XAER_RMERR);
//    }
//    return busy;
  }

  @Override
  protected void prepareResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {

  }

  @Override
  public void commitResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter)jdbcCpoAdapter).commit();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public void rollbackResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter)jdbcCpoAdapter).rollback();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public JdbcCpoTrxAdapter createNewResource() throws XAException {
    try {
      return (JdbcCpoTrxAdapter)jdbcCpoAdapterFactory.getCpoTrxAdapter();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(CpoXaError.XAER_RMFAIL, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public void closeResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter)jdbcCpoAdapter).close();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

}

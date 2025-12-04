package org.synchronoss.cpo.jdbc.jta;

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

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapterFactory;
import org.synchronoss.cpo.jdbc.JdbcCpoTrxAdapter;
import org.synchronoss.cpo.jta.CpoBaseXaResource;
import org.synchronoss.cpo.jta.CpoXaError;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * A JdbcCpoXaAdapter wraps the XAResource processing around a JdbcCpoTrxAdapter.
 *
 * <p>It manages a local and global CpoTrxAdapter and manages the association of XIDs to
 * transactions
 *
 * <p>The XAResource interface is a Java mapping of the industry standard XA interface based on the
 * X/Open CAE Specification (Distributed Transaction Processing: The XA Specification).
 *
 * <p>The XA interface defines the contract between a Resource Manager and a Transaction Manager in
 * a distributed transaction processing (DTP) environment. A JDBC driver or a JMS provider
 * implements this interface to support the association between a global transaction and a database
 * or message service connection.
 *
 * <p>The XAResource interface can be supported by any transactional resource that is intended to be
 * used by application programs in an environment where transactions are controlled by an external
 * transaction manager. An example of such a resource is a database management system. An
 * application may access data through multiple database connections. Each database connection is
 * enlisted with the transaction manager as a transactional resource. The transaction manager
 * obtains an XAResource for each connection participating in a global transaction. The transaction
 * manager uses the start method to associate the global transaction with the resource, and it uses
 * the end method to disassociate the transaction from the resource. The resource manager is
 * responsible for associating the global transaction to all work performed on its data between the
 * start and end method invocations.
 *
 * <p>At transaction commit time, the resource managers are informed by the transaction manager to
 * prepare, commit, or rollback a transaction according to the two-phase commit protocol.
 */
public class JdbcCpoXaAdapter extends CpoBaseXaResource<JdbcCpoAdapter> implements CpoTrxAdapter {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoXaAdapter.class);

  private JdbcCpoAdapterFactory jdbcCpoAdapterFactory;

  /**
   * Construct a JdbcCpoXaAdapter
   *
   * @param jdbcCpoAdapterFactory - The adapter factory to use to create the JdbcCpoXaAdapter
   * @throws CpoException - An error getting the JdbcCpoXaAdapter
   */
  public JdbcCpoXaAdapter(JdbcCpoAdapterFactory jdbcCpoAdapterFactory) throws CpoException {
    super((JdbcCpoAdapter) jdbcCpoAdapterFactory.getCpoAdapter());
    this.jdbcCpoAdapterFactory = jdbcCpoAdapterFactory;
  }

  /** Commits the current transaction behind the CpoTrxAdapter */
  @Override
  public void commit() throws CpoException {
    accept(
        (adapter) -> {
          try {
            if (adapter instanceof JdbcCpoTrxAdapter) ((JdbcCpoTrxAdapter) adapter).commit();
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /** Rollback the current transaction behind the CpoTrxAdapter */
  @Override
  public void rollback() throws CpoException {
    accept(
        (adapter) -> {
          try {
            if (adapter instanceof JdbcCpoTrxAdapter) ((JdbcCpoTrxAdapter) adapter).rollback();
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Closes the current transaction behind the CpoTrxAdapter. All subsequent calls to the
   * CpoTrxAdapter will throw an exception.
   */
  @Override
  public void close() throws CpoException {
    try {
      super.closeAssociated();
    } catch (XAException xae) {
      throw new CpoException(xae);
    }
  }

  /** Returns true if the TrxAdapter has been closed, false if it is still active */
  @Override
  public boolean isClosed() throws CpoException {
    return apply(
        (adapter) -> {
          try {
            if (adapter instanceof JdbcCpoTrxAdapter)
              return ((JdbcCpoTrxAdapter) adapter).isClosed();
            return true;
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBean(groupName, bean, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBeans(List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBeans(beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBeans(String groupName, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBeans(groupName, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBeans(
      String groupName,
      List<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBeans(groupName, beans, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBean(groupName, bean, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBeans(List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBeans(beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBeans(String groupName, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBeans(groupName, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBeans(
      String groupName,
      List<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBeans(groupName, beans, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T executeBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.executeBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T executeBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.executeBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> T executeBean(String groupName, C criteria, T result) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.executeBean(groupName, criteria, result);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long existsBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.existsBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long existsBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.existsBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long existsBean(String groupName, T bean, Collection<CpoWhere> wheres)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.existsBean(groupName, bean, wheres);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.newOrderBy(attribute, ascending);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.newOrderBy(marker, attribute, ascending);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.newOrderBy(attribute, ascending, function);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.newOrderBy(marker, attribute, ascending, function);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoWhere newWhere() throws CpoException {
    return apply(JdbcCpoAdapter::newWhere);
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value)
      throws CpoException {
    return apply((cpo) -> cpo.newWhere(logical, attr, comp, value));
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    return apply((cpo) -> cpo.newWhere(logical, attr, comp, value, not));
  }

  @Override
  public <T> long upsertBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long upsertBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long upsertBeans(List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBeans(beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long upsertBeans(String groupName, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBeans(groupName, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T retrieveBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T retrieveBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T retrieveBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(groupName, bean, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(groupName, criteria, result, wheres, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(
                groupName, criteria, result, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <C> Stream<C> retrieveBeans(String groupName, C criteria) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <C> Stream<C> retrieveBeans(
      String groupName, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, where, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <C> Stream<C> retrieveBeans(String groupName, C criteria, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <C> Stream<C> retrieveBeans(
      String groupName, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, wheres, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(String groupName, C criteria, T result)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, result);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(
      String groupName, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, result, where, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(groupName, criteria, result, wheres, orderBy);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(
                groupName, criteria, result, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBean(T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBean(bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBean(String groupName, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBean(groupName, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBean(groupName, bean, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBeans(List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBeans(beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBeans(String groupName, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBeans(groupName, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBeans(
      String groupName,
      List<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBeans(groupName, beans, wheres, orderBy, nativeExpressions);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public CpoMetaDescriptor getCpoMetaDescriptor() {
    try {
      return apply(JdbcCpoAdapter::getCpoMetaDescriptor);
    } catch (CpoException e) {
      return null;
    }
  }

  @Override
  public String getDataSourceName() {
    try {
      return apply(JdbcCpoAdapter::getDataSourceName);
    } catch (CpoException e) {
      return null;
    }
  }

  @Override
  public int getFetchSize() {
    try {
      return apply(JdbcCpoAdapter::getFetchSize);
    } catch (CpoException e) {
      return 0;
    }
  }

  @Override
  public void setFetchSize(int fetchSize) {
    try {
      accept((cpo) -> cpo.setFetchSize(fetchSize));
    } catch (CpoException e) {
      logger.error("Could not set fetch size", e);
    }
  }

  @Override
  public int getBatchSize() {
    try {
      return apply(JdbcCpoAdapter::getBatchSize);
    } catch (CpoException e) {
      return 0;
    }
  }

  @Override
  public void setBatchSize(int batchSize) {
    try {
      accept((cpo) -> cpo.setBatchSize(batchSize));
    } catch (CpoException e) {
      logger.error("Could not set batch size", e);
    }
  }

  @Override
  public List<CpoAttribute> getCpoAttributes(String expression) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.getCpoAttributes(expression);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    if (xaResource == null)
      throw CpoXaError.createXAException(
          CpoXaError.XAER_INVAL, "Invalid parameter. xaResource cannot be null.");

    return xaResource instanceof JdbcCpoXaAdapter;
  }

  @Override
  protected void prepareResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {}

  @Override
  public void commitResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter) jdbcCpoAdapter).commit();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(
          CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public void rollbackResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter) jdbcCpoAdapter).rollback();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(
          CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public JdbcCpoTrxAdapter createNewResource() throws XAException {
    try {
      return (JdbcCpoTrxAdapter) jdbcCpoAdapterFactory.getCpoTrxAdapter();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(
          CpoXaError.XAER_RMFAIL, ExceptionHelper.getLocalizedMessage(ce));
    }
  }

  @Override
  public void closeResource(JdbcCpoAdapter jdbcCpoAdapter) throws XAException {
    try {
      ((JdbcCpoTrxAdapter) jdbcCpoAdapter).close();
    } catch (CpoException ce) {
      throw CpoXaError.createXAException(
          CpoXaError.XAER_RMERR, ExceptionHelper.getLocalizedMessage(ce));
    }
  }
}

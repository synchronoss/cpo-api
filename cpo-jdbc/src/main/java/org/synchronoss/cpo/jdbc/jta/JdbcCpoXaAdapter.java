package org.synchronoss.cpo.jdbc.jta;

/*-
 * [[
 * jdbc
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

import java.util.List;
import java.util.stream.Stream;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.*;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.jta.CpoBaseXaResource;
import org.synchronoss.cpo.core.jta.CpoXaError;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapterFactory;
import org.synchronoss.cpo.jdbc.JdbcCpoTrxAdapter;

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

  /** The factory used to create/retrieve the underlying {@link JdbcCpoAdapter}. */
  private transient JdbcCpoAdapterFactory jdbcCpoAdapterFactory;

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

  // Every CpoAdapter operation is routed through apply() so the XA state machine picks the
  // correct resource manager for the calling thread. Only the canonical CpoQuery forms are
  // delegated; the convenience overloads are interface defaults that funnel through these.
  @Override
  public <T> long insertBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long insertBeans(CpoQuery query, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.insertBeans(query, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long deleteBeans(CpoQuery query, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.deleteBeans(query, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long updateBeans(CpoQuery query, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.updateBeans(query, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long upsertBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long upsertBeans(CpoQuery query, List<T> beans) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.upsertBeans(query, beans);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> long existsBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.existsBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> T executeBean(CpoQuery query, C criteria, T result) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.executeBean(query, criteria, result);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T> T retrieveBean(CpoQuery query, T bean) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(query, bean);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> T retrieveBean(CpoQuery query, C criteria, T result) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBean(query, criteria, result);
          } catch (CpoException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(CpoQuery query, C criteria, T result) throws CpoException {
    return apply(
        (cpo) -> {
          try {
            return cpo.retrieveBeans(query, criteria, result);
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

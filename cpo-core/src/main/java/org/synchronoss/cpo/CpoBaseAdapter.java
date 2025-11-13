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
package org.synchronoss.cpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cache.CpoAdapterCache;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Crud;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 09:00 AM
 * To change this template use File | Settings | File Templates.
 * @param <D> The type of the Datasource
 */
public abstract class CpoBaseAdapter<D> extends CpoAdapterCache implements CpoAdapter {
    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(CpoBaseAdapter.class);
  // DataSource Information

  /**
   * The datasource where read queries are executed against
   */
  private D readDataSource = null;

  /**
   * The datasource where write queries are executed against
   */
  private D writeDataSource = null;

  /**
   * The name of the datasource
   */
  private String dataSourceName = null;

  protected D getReadDataSource() {
    return readDataSource;
  }

  protected void setReadDataSource(D readDataSource) {
    this.readDataSource = readDataSource;
  }

  protected D getWriteDataSource() {
    return writeDataSource;
  }

  protected void setWriteDataSource(D writeDataSource) {
    this.writeDataSource = writeDataSource;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  protected void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  @Override
  public <T> long insertObject(T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.CREATE, null, null, null, null);
  }

  @Override
  public <T> long insertObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.CREATE, name, null, null, null);
  }

  @Override
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, Crud.CREATE, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long insertObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.CREATE, null, null, null, null);
  }

  @Override
  public <T> long insertObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.CREATE, name, null, null, null);
  }

  @Override
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, Crud.CREATE, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteObject(T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.DELETE, null, null, null, null);
  }

  @Override
  public <T> long deleteObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.DELETE, name, null, null, null);
  }

  @Override
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, Crud.DELETE, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.DELETE, null, null, null, null);
  }

  @Override
  public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.DELETE, name, null, null, null);
  }

  @Override
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, Crud.DELETE, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> T executeObject(T object) throws CpoException {
    return processExecuteGroup(null, object, object);
  }

  @Override
  public <T> T executeObject(String name, T object) throws CpoException {
    return processExecuteGroup(name, object, object);
  }

  @Override
  public <T, C> T executeObject(String name, C criteria, T result) throws CpoException {
    return processExecuteGroup(name, criteria, result);
  }

  @Override
  public <T> long existsObject(T obj) throws CpoException {
    return this.existsObject(null, obj);
  }

  @Override
  public <T> long existsObject(String name, T obj) throws CpoException {
    return this.existsObject(name, obj, null);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending, function);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function) throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending, function);
  }

  @Override
  public CpoWhere newWhere() throws CpoException {
    return new BindableCpoWhere();
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value) throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value);
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not) throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value, not);
  }

  @Override
  public <T> long persistObject(T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.PERSIST, null, null, null, null);
  }

  @Override
  public <T> long persistObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.PERSIST, name, null, null, null);
  }

  @Override
  public <T> long persistObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.PERSIST, null, null, null, null);
  }

  @Override
  public <T> long persistObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.PERSIST, name, null, null, null);
  }

  @Override
  public <T> T retrieveBean(T bean) throws CpoException {
    return processSelectGroup(bean, null, null, null, null);
  }

  @Override
  public <T> T retrieveBean(String name, T bean) throws CpoException {
    return processSelectGroup(bean, name, null, null, null);
  }

  @Override
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processSelectGroup(bean, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return retrieveBean(name, criteria, result, wheres, orderBy, null);
  }

  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Iterator<T> it = processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, true).iterator();
    if (it.hasNext()) {
      return it.next();
    } else {
      return null;
    }
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, null, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException {
    return processSelectGroup(name, criteria, result, null, null, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, false);
  }

  @Override
  public <T, C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, int queueSize) {
    CpoBlockingResultSet<T> resultSet = new CpoBlockingResultSet<>(queueSize);
    RetrieverThread<T, C> retrieverThread = new RetrieverThread<T,C>(name, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);

    retrieverThread.start();
    return resultSet;
  }

  @Override
  public <T> long updateObject(T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.UPDATE, null, null, null, null);
  }

  @Override
  public <T> long updateObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, Crud.UPDATE, name, null, null, null);
  }

  @Override
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, Crud.UPDATE, name, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long updateObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.UPDATE, null, null, null, null);
  }

  @Override
  public <T> long updateObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, Crud.UPDATE, name, null, null, null);
  }

  @Override
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, Crud.UPDATE, name, wheres, orderBy, nativeExpressions);
  }

  protected abstract <T> long processUpdateGroup(T obj, Crud crud, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  protected abstract <T> long processUpdateGroup(Collection<T> coll, Crud crud, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  protected abstract <T, C> T processExecuteGroup(String name, C criteria, T result) throws CpoException;

  public abstract <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException;

  protected abstract <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  protected abstract <T, C> List<T> processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                              boolean useRetrieve) throws CpoException;

  protected abstract <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                             boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException;

  protected class RetrieverThread<T, C> extends Thread {

    String name;
    C criteria;
    T result;
    Collection<CpoWhere> wheres;
    Collection<CpoOrderBy> orderBy;
    Collection<CpoNativeFunction> nativeExpressions;
    boolean useRetrieve;
    CpoBlockingResultSet<T> resultSet;
    Thread callingThread = null;

    public RetrieverThread(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, boolean useRetrieve, CpoBlockingResultSet<T> resultSet) {
      this.name = name;
      this.criteria = criteria;
      this.result = result;
      this.wheres = wheres;
      this.orderBy = orderBy;
      this.useRetrieve = useRetrieve;
      this.resultSet = resultSet;
      this.nativeExpressions = nativeExpressions;
      callingThread = Thread.currentThread();
    }

    @Override
    public void run() {
      try {
        processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);
      } catch (CpoException e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
      } finally {
        //wait until the calling thread is finished processing the records
        while (resultSet.size() > 0) {
          Thread.yield();
        }
        //Tell the calling thread that it should not wait on the blocking queue any longer.
        callingThread.interrupt();
      }
    }
  }
}

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
package org.synchronoss.cpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cache.CpoAdapterCache;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 09:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CpoBaseAdapter<T> extends CpoAdapterCache implements CpoAdapter {
  private static final Logger logger = LoggerFactory.getLogger(CpoBaseAdapter.class);
  // DataSource Information

  /**
   * The datasource where read queries are executed against
   */
  private T readDataSource = null;
  /**
   * The datasource where write queries are executed against
   */
  private T writeDataSource = null;
  /**
   * The name of the datasource
   */
  private String dataSourceName = null;

  protected T getReadDataSource() {
    return readDataSource;
  }

  protected void setReadDataSource(T readDataSource) {
    this.readDataSource = readDataSource;
  }

  protected T getWriteDataSource() {
    return writeDataSource;
  }

  protected void setWriteDataSource(T writeDataSource) {
    this.writeDataSource = writeDataSource;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  protected void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

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

package org.synchronoss.cpo.jdbc;

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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;

/**
 * Executes a single-function update over a list of beans as JDBC batches. This class owns the
 * batching mechanics — chunking by batch size, accumulating driver update counts, and surfacing
 * per-statement failures — while {@link JdbcCpoAdapter} remains responsible for meta lookup,
 * statement creation, logging, and transaction control.
 *
 * @author david berry
 */
class JdbcBatchExecutor {

  private static final Logger logger = LoggerFactory.getLogger(JdbcBatchExecutor.class);

  private final int batchSize;

  /**
   * Constructs a JdbcBatchExecutor
   *
   * @param batchSize The number of statements to accumulate before executing a batch; zero or
   *     negative executes everything as one batch
   */
  JdbcBatchExecutor(int batchSize) {
    this.batchSize = batchSize;
  }

  /**
   * Adds every bean to the statement factory's prepared statement as a batch, executing each time
   * the batch size is reached, and closes the statement when done. The factory must already be
   * bound to the first bean.
   *
   * @param <T> The bean type
   * @param jpsf The statement factory, bound to the first bean
   * @param cpoFunction The single function being batched
   * @param beans The beans to update
   * @return The number of records updated
   * @throws CpoException An error occurred binding a bean's values
   * @throws SQLException An error occurred executing a batch
   */
  <T> long executeBatchedUpdates(
      JdbcPreparedStatementFactory jpsf, CpoFunction cpoFunction, List<T> beans)
      throws CpoException, SQLException {
    PreparedStatement ps = jpsf.getPreparedStatement();
    try {
      long updateCount = 0;
      ps.addBatch();
      int batchCount = 1;
      for (T bean : beans.subList(1, beans.size())) {
        jpsf.setBindValues(jpsf.getBindValues(cpoFunction, bean));
        ps.addBatch();
        if (batchSize > 0 && ++batchCount % batchSize == 0) {
          updateCount += executeBatch(ps);
        }
      }
      updateCount += executeBatch(ps);
      return updateCount;
    } finally {
      try {
        ps.close();
      } catch (Exception e) {
        if (logger.isTraceEnabled()) {
          logger.trace(e.getMessage());
        }
      }
    }
  }

  private long executeBatch(PreparedStatement ps) throws SQLException {
    long updateCount = 0;
    int failedCount = 0;
    int[] updates = ps.executeBatch();
    for (int update : updates) {
      if (update == PreparedStatement.SUCCESS_NO_INFO) {
        // something updated but we do not know what or how many so default to one.
        updateCount++;
      } else if (update == PreparedStatement.EXECUTE_FAILED) {
        // some drivers report per-statement failure here instead of throwing
        // BatchUpdateException
        failedCount++;
      } else {
        updateCount += update;
      }
    }
    if (failedCount > 0) {
      throw new SQLException(
          failedCount + " of " + updates.length + " statements in the batch failed to execute");
    }
    return updateCount;
  }
}

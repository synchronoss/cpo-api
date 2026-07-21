# Performance Tuning

CPO executes your native expressions through the standard driver APIs, so most throughput tuning
happens in the JDBC driver configuration rather than in CPO itself.

- Enable driver-side batch rewriting. `insertBeans`/`deleteBeans`/`updateBeans` use JDBC batching,
  but several drivers still send each batched statement as its own network round trip unless told
  otherwise:
  - MySQL: `rewriteBatchedStatements=true`
  - MariaDB: `useBulkStmts=true`
  - PostgreSQL: `reWriteBatchedInserts=true` (rewrites inserts only)
- Enable your driver's prepared-statement cache. CPO deliberately does not cache
  `PreparedStatement`s (see below) — use the driver or pool cache instead:
  - MySQL/MariaDB: `cachePrepStmts=true&useServerPrepStmts=true&prepStmtCacheSize=250&prepStmtCacheSqlLimit=2048`
  - PostgreSQL: built in (`prepareThreshold`, server-prepares after 5 uses)
  - Oracle: `oracle.jdbc.implicitStatementCacheSize=<n>`
  - SQL Server: `disableStatementPooling=false;statementPoolingCacheSize=<n>`
  - H2: caches parsed statements per session automatically
- Raise the batch size for bulk operations. `CpoAdapter.setBatchSize` defaults to 100; large
  `insertBeans`/`deleteBeans` calls benefit from batch sizes in the thousands.

## Why CPO does not cache PreparedStatements

A framework-level cache would sit above the connection pool, where connections are proxy objects
— it would mis-key on the physical connection, would have to intercept close and pool eviction,
and at best would save the microsecond that a driver-cache hit already costs, while the execute
and commit round trips dominate every operation. Statement caching belongs to the driver, which
implements it correctly one layer down. (This is the same reasoning HikariCP gives for not
shipping statement pooling.)

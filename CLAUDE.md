# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CPO (Class Persistence Object) API is a Java persistence library that maps JavaBean objects to datastore operations (CRUD). Unlike JPA/ORM tools, CPO lets you use native datastore syntax (SQL, CQL) stored externally in XML configuration files rather than annotations or code.

## Build Commands

**Prerequisites**: Java 21+, Maven 3.9+, Docker (for integration tests via Testcontainers)

```bash
# Full build including integration tests
mvn install

# Build without tests (skips Docker-dependent integration tests)
mvn install -DskipTests

# Build a specific module
mvn install -pl cpo-core
mvn install -pl cpo-jdbc
mvn install -pl cpo-cassandra

# Run tests for a specific module
mvn test -pl cpo-jdbc

# Run a single test class
mvn test -pl cpo-jdbc -Dtest=InsertObjectTest

# Apply code formatting (Google Java Format via Spotless)
mvn spotless:apply

# Check formatting without applying
mvn spotless:check

# Run PMD/CPD static analysis only
mvn pmd:check pmd:cpd-check -pl cpo-core

# Generate the aggregate JaCoCo coverage report (all modules combined)
mvn verify   # report lands in cpo-coverage/target/site/jacoco-aggregate/index.html

# Generate a per-module JaCoCo report (informational only)
mvn test jacoco:report -pl cpo-jdbc
```

## Code Quality Enforcement

The build enforces these checks at compile/test phases — violations fail the build:

- **Spotless** (Google Java Format, `spotless:check` at the `process-sources` phase): the build FAILS on unformatted Java — it does not auto-format. Run `mvn spotless:apply` to fix formatting before building/committing.
- **PMD + CPD** (`compile` phase): static analysis and copy-paste detection. Minimum token threshold is 55.
- **JaCoCo** (`verify` phase, `cpo-coverage` module): coverage is measured and enforced on the **aggregate** of all module tests, not per module — cpo-core is mostly interfaces, so its coverage comes from the cpo-jdbc/cpo-cassandra tests that exercise it. Minimums (80% instruction, line, and branch) are set by the `coverage.instruction.minimum`, `coverage.line.minimum`, and `coverage.branch.minimum` properties in the root pom. JAXB-generated packages (`cpoconfig`, `cpometa`, `cpoutilconfig`) are excluded from coverage.
- **License headers**: `license-maven-plugin` enforces LGPL v3 headers in all Java files. Headers use `[[` / `]]` delimiters and `==` section separator (not the standard `%L` / `%%`).

## Module Architecture

```
cpo-core        — Persistence-agnostic interfaces, meta model, config, cache
cpo-jdbc        — JDBC implementation of cpo-core
cpo-cassandra   — Cassandra 3.x native driver implementation
cpo-plugin      — Maven plugin that code-generates CPO interfaces/beans at build time
cpo-coverage    — Build-internal module (built last): merges all modules' JaCoCo data,
                  produces the aggregate report, and enforces coverage minimums. Never published.
```

### Core Abstractions (cpo-core)

- **`CpoAdapter`** — primary interface for CRUD operations (`insertBean`, `retrieveBean`, `updateBean`, `deleteBean`, `upsertBean`, `existsBean`, `executeBean`, `retrieveBeans` returning `Stream<T>`). Entry point for application code. Every operation has a canonical form taking a `CpoQuery` plus 1-2 arg convenience defaults; there are no clause-bearing positional overloads.
- **`CpoQuery`** — immutable parameter object carrying a function group name plus run-time where/order-by/native-expression clauses (`CpoQuery.group("g").where(w).orderBy(o)`). Built once, shareable across threads.
- **`CpoTrxAdapter`** — extends `CpoAdapter` with explicit transaction control; obtained from `CpoAdapter.getCpoTrxAdapter()`.
- **`CpoAdapterFactory`** — creates `CpoAdapter` instances from a named config context.
- **`CpoAdapterFactoryManager`** — singleton cache that loads `cpoConfig.xml` from the classpath (env var `CPO_CONFIG` overrides path) and vends `CpoAdapterFactory` instances by config name.
- **`CpoMetaDescriptor`** — holds the JavaBean-to-datastore mapping (attributes, function groups, SQL/CQL expressions) loaded from meta XML files.
- **`CpoWhere` / `CpoOrderBy`** — programmatic query clause builders. Prefer `CpoAdapter.whereBuilder()`
  (returns a `CpoWhereBuilder`) over hand-assembling a `CpoWhere` tree with `newWhere`/`addWhere`/
  `setLogical` — the fluent `.where(attr, comp, val).and(...)`/`.or(group -> ...)` chain derives
  correct `Logical` placement, including nested AND/OR groups via `CpoWhereGroup`, instead of
  requiring it be set by hand on both leaves and group containers.
- **`CpoStatementFactory`** — builds datastore-specific prepared statements from `CpoMetaDescriptor` data.
- **`DataTypeMapper` / `MethodMapper`** — reflective bridges that map Java types and getter/setter methods to datastore types.

### Configuration System (two-layer XML)

1. **`cpoConfig.xml`** (loaded at startup): declares named `dataConfig` entries (JDBC or Cassandra connection details) and named `metaConfig` entries pointing to one or more meta XML files.
2. **Meta XML files** (e.g., `ValueMetaData.xml`): map JavaBean classes to function groups (INSERT, RETRIEVE, UPDATE, DELETE, EXIST, EXECUTE) and their native expressions (SQL/CQL), plus attribute-to-column bindings.

Multiple meta XML files are merged per `metaConfig`, enabling the polymorphic override pattern — a program can import a library's meta config and override class-level mappings.

### JDBC Implementation (cpo-jdbc)

- `JdbcCpoAdapter` / `JdbcCpoTrxAdapter` — concrete JDBC implementations. The adapter delegates
  cross-cutting concerns to package-private collaborators: `JdbcConnectionStrategy` (connection
  lifecycle — `JdbcPooledConnectionStrategy` per-call checkout vs `JdbcPinnedConnectionStrategy`
  one pinned connection per transaction, injected by `JdbcCpoTrxAdapter`),
  `JdbcDatabaseCapabilities` (DatabaseMetaData capability flags probed once at construction), and
  `JdbcBatchExecutor` (batch chunking and update-count mechanics). `CassandraSessionStrategy`
  mirrors the session-acquisition shape in cpo-cassandra (concrete class, no per-call release —
  Cassandra sessions are long-lived). The lifecycle seam is deliberately NOT generalized into
  cpo-core: the JDBC and Cassandra semantics differ too much (transactional release vs none), and
  cpo-core never touches connections itself. Revisit if a third datastore is added.
- `JdbcCpoMetaDescriptor` — JDBC-specific meta with `JdbcMethodMapper` for SQL type mappings.
- `JdbcPreparedStatementFactory` / `JdbcCallableStatementFactory` — build `PreparedStatement`/`CallableStatement` from meta.
- Single-object CRUD builds and prepares its statement on every call. There is deliberately NO
  CPO-level `PreparedStatement` cache (decided 2026-07): statement caching belongs to the
  driver/pool layer, which keys on the physical connection below the pool — a framework cache above
  pooled connection proxies mis-keys and must intercept close/eviction, and a hit would only save
  the ~µs a driver-cache hit already costs while execute/commit round trips dominate each op.
  Recommend driver cache/batch flags instead (see README "Performance Tuning"). If profiling ever
  shows `buildSql` cost, cache the built SQL string on `CpoFunction` (static case — no dynamic
  wheres/orderBy/native expressions), not statements.
- `JdbcCpoTransform` — interface for custom type transforms; built-ins: `TransformClob`, `TransformGZipBytes`, `TransformTimestampToCalendar`, etc.
- DataSource configuration supports three modes: `dataSourceClassName` (connection pool), `driverClassName` (raw JDBC), `jndiName` (JNDI lookup). Both `readWriteConfig` (single pool) and separate `readConfig`/`writeConfig` (read/write split) are supported.

### Cassandra Implementation (cpo-cassandra)

- `CassandraCpoAdapter` — uses the Cassandra 3.x native driver; `ClusterDataSource` wraps the `Cluster` object.
- Follows the same meta XML + cpoConfig.xml pattern using CQL instead of SQL.

### Cache Layer (cpo-core)

- `CpoAdapterFactoryCache` — singleton backing store for `CpoAdapterFactory` instances, keyed by config name.
- `CpoMetaDescriptorCache` — caches parsed meta descriptors; supports hot-deploy (runtime reload).
- `CpoAdapterCache` — pools `CpoAdapter` instances.

## Testing

- Tests use **TestNG** (not JUnit). Test suites are defined in `testng.xml` per database variant (h2, mariadb, oracle, postgres, mysql, cassandra).
- Integration tests start real databases via **Testcontainers** — Docker must be running.
- H2 is the default in-process DB for fast local development; other databases require running containers.
- Test resources follow the pattern `src/test/resources/<db>/cpoConfig.xml` with filtered Maven properties (`${h2.url}`, etc.) resolved at build time.
- `JdbcSuiteListener` / `CassandraSuiteListener` handle container lifecycle (start/stop) around the test suite.

## Key Conventions

- All source files must carry the LGPL v3 license header using `[[` / `]]` delimiters; the license plugin enforces this at build time. Add headers with `mvn license:update-file-header`.
- Java 21 language level; `--release 21` in compiler config (kept at the older LTS for broader community support).
- JAXB-generated classes under `org.synchronoss.cpo.*config.*` and `org.synchronoss.cpo.*meta.*` subpackages (the generated ones) are excluded from Javadoc and should not be edited by hand.
- The `cpo-plugin` module generates Java interfaces and bean classes from CPO meta XML at build time — edit the XML config, not the generated sources.

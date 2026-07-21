cpo-api
=======

[![CI](https://github.com/synchronoss/cpo-api/actions/workflows/mvn-linux.yml/badge.svg)](https://github.com/synchronoss/cpo-api/actions/workflows/mvn-linux.yml)
![License](https://img.shields.io/github/license/synchronoss/cpo-api.svg)
[![Maven Central](https://img.shields.io/maven-central/v/org.synchronoss.cpo/cpo-core.svg)](https://central.sonatype.com/artifact/org.synchronoss.cpo/cpo-core)
[![Documentation](https://img.shields.io/badge/docs-website-blue.svg)](https://synchronoss.github.io/cpo-api/)

Class Persistence Object (CPO) Application Programming Interface (API).

CPO is a java api that maps JavaBean objects and interfaces to persistence functions.
Unlike most ORM technologies and the java persistence api (JPA), CPO is designed to allow you to fully utilize the native syntax and
functionality of your datastore.

CPO supports databases with JDBC drivers and Cassandra's native 3.x driver. The following databases are included in the integration tests for CPO.

* MySQL
* MariaDB
* Oracle
* Postgres
* H2
* Cassandra

CPO exploits the fact that datastores have a language that is used to manipulate data in that datastore. SQL is used for Relational Databases,
CQL is used for Cassandra, and XPath is used for XML. Each language consists of a defined syntax that allows you to build expressions to access
data in the datastore. CPO provides the means to map JavaBean objects and interfaces into the parameters of these expressions prior to execution and then
map the results of the executed expressions into one JavaBean or a java.util.stream.Stream of JavaBeans

CPO stores all queries outside of your code providing the following benefits: runtime reloading, improved versioning, improved searching,
and improved re-use. It also allows you to use the same JavaBean across tables or databases as the JavaBean has no direct linkage to a datastore.
In fact, the same JavaBean can be used directly by jaxb as well.

CPO supports a polymorphic configuration system which allows programs to override cpo configurations in imported libraries.
The program will be able to include the libraries' cpo configuration into its own and can override class level configurations.
This allows library writers to provide default queries that can be overridden by the programmer that imports the library.

cpo-api has a companion project cpo-util which is a utility program for managing the xml configuration and meta files for cpo.
It provides a graphical user interface for configuring CPO. It also provides tools to automatically generate configuration files from an existing datastore.

CPO also comes with a maven plugin which will generate the cpo interfaces and/or beans at build time. This allows the developer
to only have to keep the configuration information up to date. Cpo-plugin will then manage the classes.

---
Modules
=

| Module | Description |
| --- | --- |
| [cpo-core](cpo-core) | Persistence-agnostic interfaces, meta model, config, cache. Start here — `CpoAdapter` is the primary entry point for application code. |
| [cpo-jdbc](cpo-jdbc) | JDBC implementation of cpo-core, for any relational database with a JDBC driver. |
| [cpo-cassandra](cpo-cassandra) | Cassandra 3.x native driver implementation. |
| [cpo-plugin](cpo-plugin) | Maven plugin that code-generates CPO interfaces/beans from meta XML at build time. |

Full API and per-module reports (Javadoc, coverage, static analysis) are published at
[synchronoss.github.io/cpo-api](https://synchronoss.github.io/cpo-api/).

---
Example Usage
=
```java
// Obtain the CpoAdapter for the default context, as declared in cpoConfig.xml
CpoAdapter cpo = CpoAdapterFactoryManager.getCpoAdapter();

// Insert a new bean
Employee employee = new Employee();
employee.setId(UUID.randowUUID());
employee.setName("Ada Lovelace");
employee.setDepartment("Engineering");
cpo.insertBean(employee);

// Retrieve it back by primary key
Employee criteria = new Employee();
criteria.setId(employee.getId());
Employee found = cpo.retrieveBean(criteria);

// Stream every bean matching a named function group
try (Stream<Employee> engineers = cpo.retrieveBeans("ByDepartment", criteria)) {
  engineers.forEach(System.out::println);
}
```

`Employee` here is a plain JavaBean; the mapping between its properties and your datastore's native
SQL/CQL expressions lives entirely in an external meta XML file (or is generated for you by
cpo-plugin), never in annotations on the bean itself. For complete, config-wired examples backed by
a real database, see the test suites under `cpo-jdbc/src/test` (H2) and `cpo-cassandra/src/test`.

---
Performance Tuning
=
See [Performance Tuning](https://synchronoss.github.io/cpo-api/performance-tuning.html) on the
project site for driver batching/statement-cache settings and why CPO deliberately does not cache
`PreparedStatement`s itself.

---
Building CPO
=
- Install Java 21+
- Install Maven 3.9.0+
- Install Docker Desktop or Rancher Desktop
  - The integration tests use testcontainers which run in a local vm.
- Clone the repository
  - ```git clone git@github.com:synchronoss/cpo-api.git```
- Run the build
  - mvn install
- If all goes well:
```[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for cpo 6.0.0-SNAPSHOT:
[INFO]
[INFO] cpo ................................................ SUCCESS [  4.503 s]
[INFO] core ............................................... SUCCESS [  9.438 s]
[INFO] plugin ............................................. SUCCESS [  2.360 s]
[INFO] jdbc ............................................... SUCCESS [02:34 min]
[INFO] cassandra .......................................... SUCCESS [ 26.797 s]
[INFO] cpo-coverage ....................................... SUCCESS [  3.912 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  03:21 min
[INFO] ------------------------------------------------------------------------
```


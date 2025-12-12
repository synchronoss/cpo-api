cpo-api
=======

[![CI](https://github.com/synchronoss/cpo-api/actions/workflows/mvn-linux.yml/badge.svg)](https://github.com/synchronoss/cpo-api/actions/workflows/mvn-linux.yml)
![License](https://img.shields.io/github/license/synchronoss/cpo-api.svg)

Class Persistence Object (CPO) Application Programming Interface (API).

CPO is a java api that maps JavaBean objects and interfaces to persistence functions. 
Unlike most ORM technologies and the java persistence api (JPA), CPO is designed to allow you to fully utilize the native syntax and 
functionality of your datastore.

CPO supports databases with JDBC drivers and Cassandra's native 3.x driver. The following databases are included in the integration tests for CPO.

*MySQL
*MariaDB
*Oracle
*Postgres
*H2
*Cassandra

CPO exploits that fact that datastores have a language that is used to manipulate data in that datastore. SQL is used for Relational Databases, 
CQL is used for Cassandra, and XPath is used for XML. Each language consists of a defined syntax that allows you to build expressions to access 
data in the datastore. CPO provides the means to map JavaBean objects and interfaces into the parameters of these expressions prior to execution and then 
map the results of the executed expressions into one JavaBean or a java.util.stream.Stream of JavaBeans

CPO stores all queries outside of your code providing the following benefits: runtime reloading, improved versioning, improved searching, 
and improved re-use. It also allows you to use the same JavaBean across tables or databases as the JavaBean has no direct linkage to a datastore. 
In fact, the same JavaBean can be used directly by jaxb as well.

CPO supports a polymorphic configuration system which allows programs to override cpo configurations in imported libraries. 
The program will be able to include the libraries' cpo configuration into its own and can override class level configurations. 
This allows library writers to provide default queries that can be overridden by the programmer that imports the library

cpo-api has a companion project cpo-util which is a utility program for managing the xml configuration and meta files for cpo. 
It provides a graphical user interface for configuring CPO. It also provides tools to automatically generate configuration files from an existing datastore. 

CPO also comes with a maven plugin which will generate the cpo interfaces and/or beans at build time. This allows the developer 
to only have to keep the configuration information up to date. Cpo-plugin will then manage the classes.
---
Building CPO
=
- Install Docker Desktop or Ranchr Desktop
  - The integration tests use testcontainers which run in a vm. 
- Clone the repository
  - <code>git clone git@github.com:synchronoss/cpo-api.git</code>
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
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  03:18 min
[INFO] Finished at: 2025-12-12T07:57:25-05:00
[INFO] ------------------------------------------------------------------------
```


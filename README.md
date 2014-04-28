cpo-api
=======

Class Persistence Object (CPO) Application Programming Interface (API).

CPO 5.x is a java api that maps plain old java objects to persistence functions. Unlike most ORM technologies and the java persistence api (JPA),
CPO is designed to allow you to fully utilize the native syntax and functionality of your datastore.

CPO supports the following datastores:

MySQL
MariaDB
Oracle
Postgres sans BLOBS
HSqlDB
Cassandra

CPO exploits that fact that datastores have a language that is used to manipulate data in that datastore. SQL is used for Relational Databases,
CQL is used for Cassandra, and XPath is used for XML. Each language consists of a defined syntax that allows you to build expressions to access
data in the datastore. CPO provides the means to map POJOs into the parameters of these expressions prior to execution and then map the results
of the executed expressions into one POJO or a java.util.List of POJOs

cpo-api has a companion project cpo-org/cpo-util which is a utility program for managing cpo's xml configuration file.

CPO is well suited for use in libraries that are later included in larger programs. The larger program will be able to refer to include the
libraries' cpo configuration into its own and can overwrite class level configurations by taking advantage of CPO's polymorphic configuration
capabilities.


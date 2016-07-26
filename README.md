cpo-api
=======

Class Persistence Object (CPO) Application Programming Interface (API).

Documentation can be found here: http://synchronoss.github.io/cpo-api/site/

CPO is a java api that maps plain old java objects (POJOs) and interfaces to persistence functions. 
Unlike most ORM technologies and the java persistence api (JPA), CPO is designed to allow you to fully utilize the native syntax and 
functionality of your datastore.

CPO supports the following datastores:

*MySQL
*MariaDB
*Oracle
*Postgres sans BLOBS
*HSqlDB
*Cassandra

CPO exploits that fact that datastores have a language that is used to manipulate data in that datastore. SQL is used for Relational Databases, 
CQL is used for Cassandra, and XPath is used for XML. Each language consists of a defined syntax that allows you to build expressions to access 
data in the datastore. CPO provides the means to map POJOs and interfaces into the parameters of these expressions prior to execution and then 
map the results of the executed expressions into one POJO or a java.util.List of POJOs

CPO stores all queries outside of your code providing the following benefits: runtime reloading, improved versioning, improved searching, 
and improved re-use. It also allows you to use the same POJO across tables or databases as the POJO has no direct linkage to a datastore. 
In fact, the same POJO can be used directly by jaxb as well.

CPO supports a polymorphic configuration system which allows programs to override cpo configurations in imported libraries. 
The program will be able to include the libraries' cpo configuration into its own and can override class level configurations. 
This allows library writers to provide default queries that can be overridden by the programmer that imports the library

cpo-api has a companion project cpo-util which is a utility program for managing cpo's xml configuration file. 
It provides a graphical user interface for configuring CPO. It also provides tools for automatically generate configuration file from an existing datastore. 

CPO also comes with a maven plugin which will generate the cpo interfaces and/or pojos at build time. This allows the developer 
to only have to keep the configuration information up to date. Cpo-plugin will then manage the classes.


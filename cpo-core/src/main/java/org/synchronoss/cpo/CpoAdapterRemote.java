/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */

package org.synchronoss.cpo;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * CpoAdapter is an interface for a set of routines that are responsible for 
 * Creating, Retrieving, Updating, and Deleting (CRUD) value objects within a 
 * datasource.
 * 
 * CpoAdapter is an interface that acts as a common facade for different datasources.
 * It is conceivable that an CpoAdapter can be implemented for JDBC, CSV, XML, LDAP, and
 * more datasources producing classes such as JdbcCpoAdapter, CsvCpoAdapter, XmlCpoAdapter, 
 * LdapCpoAdapter, etc.
 * 
 * @author david berry
 */
public interface CpoAdapterRemote extends java.io.Serializable {
    static final int CREATE=0;
    static final int INSERT=0;
    static final int UPDATE=1;
    static final int DELETE=2;
    static final int RETRIEVE=3;
    static final int LIST=4;
    static final int PERSIST=5;
    static final int EXIST=6;
    static final int EXECUTE=7;

    /**
     * Clears the metadata for the specified object. The metadata will be reloaded
     * the next time that this object is loaded by dataPersist.
     *
     * @param obj The object whose metadata must be cleared
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public void clearMetaClass(Object obj) throws CpoException, RemoteException;

    /**
     * Clears the metadata for the specified fully qualifed class name. The metadata 
     * will be reloaded the next time that this class is loaded by dataPersist.
     *
     * @param className The fully qualified class name for the class that needs its
     *                  metadata cleared.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public void clearMetaClass(String className) throws CpoException, RemoteException;

    /**
     * Clears the metadata for all classes. The metadata will be lazy-loaded from 
     * the metadata repository as classes are accessed.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
    */
    public void clearMetaClass() throws CpoException, RemoteException;
    
    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long insertObject(T obj) throws CpoException, RemoteException;

    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long insertObject(String name, T obj) throws CpoException, RemoteException;

    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
     * 
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long insertObjects(Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long insertObjects(String name, Collection<T> coll)
        throws CpoException, RemoteException;


    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long deleteObject(T obj) throws CpoException, RemoteException;

    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long deleteObject(String name, T obj) throws CpoException, RemoteException;

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     *
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long deleteObjects(Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long deleteObjects(String name, Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Executes an Object that represents an executable object within the datasource. 
     * It is assumed that the object exists in the datasource. If the object does not exist, an exception will be thrown
     * @param obj This is an <code>Object</code> that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to executed the datasource object.
     *        
     *        An object of this type will be created and filled with the returned data from the value_object. 
     *        This newly created object will be returned from this method.
     *
     * @return An object populated with the data returned from the executable object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> T executeObject(T obj)
        throws CpoException, RemoteException;

    /**
     * Executes an Object whose MetaData contains a stored procedure. An assumption is that the
     * object exists in the datasource.
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be populated.
     *
     * @param object This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to retrieve the collection of objects.
     *        This object defines the object type that will be returned in the collection and 
     *        contain the result set data or the OUT Parameters.
     * @return A result object populate with the OUT parameters
     *
     * @throws CpoException DOCUMENT ME!
     */
    public <T> T executeObject(String name, T object)
    throws CpoException,RemoteException;

    /**
     * Executes an Object that represents an executable object within the datasource. 
     * It is assumed that the object exists in the datasource. If the object does not exist, an exception will be thrown
     * @param name The <code>String</code> name of the EXECUTE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object defines  the
     *        object type that will be created, filled with the return data and returned from this
     *        method.
     * @return An object populated with the out parameters
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T,C> T executeObject(String name, C criteria, T result)
        throws CpoException, RemoteException;

    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.

     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long existsObject(T obj) throws CpoException, RemoteException;
    
    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * @param name The <code>String</code> name of the EXISTS Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long existsObject(String name, T obj) throws CpoException, RemoteException;


    /**
     * DOCUMENT ME!
     *
     * @param attribute DOCUMENT ME!
     * @param ascending DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public CpoOrderBy newOrderBy(String attribute, boolean ascending)
        throws CpoException, RemoteException;

    /**
     * DOCUMENT ME!
     *
     * @param attribute DOCUMENT ME!
     * @param ascending DOCUMENT ME!
     * @param function DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> CpoOrderBy newOrderBy(String attribute, boolean ascending, String function)
        throws CpoException, RemoteException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public CpoWhere newWhere() throws CpoException, RemoteException;

    /**
     * DOCUMENT ME!
     *
     * @param logical DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param comp DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value)
        throws CpoException, RemoteException;

    /**
     * DOCUMENT ME!
     *
     * @param logical DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param comp DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param not DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not)
        throws CpoException, RemoteException;


    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObject(T obj)
        throws CpoException, RemoteException;

    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObject(String name, T obj)
        throws CpoException, RemoteException;

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObjects(Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObjects(String name, Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> T retrieveObject(T obj)
        throws CpoException, RemoteException;

     /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     * @param name DOCUMENT ME!
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> T retrieveObject(String name, T obj)
        throws CpoException, RemoteException;
    
    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     * @param name DOCUMENT ME!
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */    
    public <T,C> T retrieveObject(String name, C criteria, T result, CpoWhere where,
        Collection<? extends CpoOrderBy> orderBy) throws CpoException, RemoteException;
    
    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be  populated.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to specify the parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object is used to specify
     *        the object type that will be returned in the  collection.
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     * @return A collection of objects will be returned that meet the criteria  specified by obj.
     *         The objects will be of the same type as the Object  that was passed in. If no
     *         objects match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T,C> T retrieveObjects(String name, C criteria, T result, CpoWhere where,
        Collection<? extends CpoOrderBy> orderBy) throws CpoException, RemoteException;

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *        
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long updateObject(T obj) throws CpoException, RemoteException;

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long updateObject(String name, T obj) throws CpoException, RemoteException;

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.

     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long updateObjects(Collection<T> coll)
        throws CpoException, RemoteException;

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     * @throws RemoteException Thrown if using dataPersist as an EJB and an Error
     *                         occurs.
     */
    public <T> long updateObjects(String name, Collection<T> coll)
        throws CpoException, RemoteException;
}

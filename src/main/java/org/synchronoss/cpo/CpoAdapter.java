/**
 * CpoAdapter.java  
 * 
 *  Copyright (C) 2006  David E. Berry
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
public interface CpoAdapter extends java.io.Serializable {
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. CREATE signifies that the CpoObject will try to add 
     * the object to the datasource.
     *
     * @see CpoObject
     */
    static final int CREATE=0;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. INSERT signifies that the CpoObject will try to add 
     * the object to the datasource.
     *
     * @see CpoObject
     */
    static final int INSERT=0;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. UPDATE signifies that the CpoObject will try to update 
     * the object in the datasource.
     *
     * @see CpoObject
     */
    static final int UPDATE=1;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. DELETE signifies that the CpoObject will try to delete 
     * the object in the datasource.
     *
     * @see CpoObject
     */
    static final int DELETE=2;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. RETRIEVE signifies that the CpoObject will try to  
     * retrieve a single object from the datasource.
     *
     * @see CpoObject
     */
    static final int RETRIEVE=3;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. LIST signifies that the CpoObject will try to retrieve 
     * one or more objects from the datasource.
     *
     * @see CpoObject
     */
    static final int LIST=4;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. PERSIST signifies that the CpoObject will try to add 
     * or update the object in the datasource.
     *
     * @see CpoObject
     */
    static final int PERSIST=5;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. EXIST signifies that the CpoObject will check to see 
     * if the object exists in  the datasource.
     *
     * @see CpoObject
     */
    static final int EXIST=6;
    /**
     * Static integer to be used with the <code>CpoObject</code>. It identifies the operation
     * to be processed by the CpoObject. EXECUTE signifies that the CpoObject will try to execute 
     * a function or procedure in the datasource.
     *
     * @see CpoObject
     */
    static final int EXECUTE=7;

    /**
     * Clears the metadata for the specified object. The metadata will be reloaded
     * the next time that CPO is called to access this object
     *
     * @param obj The object whose metadata must be cleared
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(Object obj) throws CpoException;

    /**
     * Clears the metadata for the specified fully qualifed class name. The metadata 
     * will be reloaded the next time CPO is called to access this class.
     *
     * @param className The fully qualified class name for the class that needs its
     *                  metadata cleared.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(String className) throws CpoException;

    /**
     * Clears the metadata for all classes. The metadata will be lazy-loaded from 
     * the metadata repository as classes are accessed.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public void clearMetaClass() throws CpoException;
    
    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource.<br>
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.insertObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long insertObject(T obj) throws CpoException;

    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.insertObject("IDNameInsert",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used which is
     *             equivalent to <code>insertObject(Object obj);</code>
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long insertObject(String name, T obj) throws CpoException;


    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter will stop
     * processing the remainder of the collection and rollback all the objects created thus far. Rollback is
     * on the underlying datasource's support of rollback.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.insertObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long insertObjects(Collection<T> coll)
        throws CpoException;

    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.insertObjects("IdNameInsert",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long insertObjects(String name, Collection<T> coll)
        throws CpoException;


    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.deleteObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long deleteObject(T obj) throws CpoException;

    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.deleteObject("DeleteById",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public <T> long deleteObject(String name, T obj) throws CpoException;

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.deleteObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long deleteObjects(Collection<T> coll)
        throws CpoException;

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.deleteObjects("IdNameDelete",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long deleteObjects(String name, Collection<T> coll)
        throws CpoException;

    /**
     * Executes an Object whose metadata will call an executable within the datasource. 
     * It is assumed that the executable object exists in the metadatasource. If the executable does not exist, 
     * an exception will be thrown.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.executeObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an <code>Object</code> that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to executed the datasource object.
     *        
     *        An object of this type will be created and filled with the returned data from the value_object. 
     *        This newly created object will be returned from this method.
     *
     * @return An object populated with the OUT parameters returned from the executable object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> T executeObject(T obj)
        throws CpoException;

    /**
     * Executes an Object whose metadata will call an executable within the datasource. 
     * It is assumed that the executable object exists in the metadatasource. If the executable does not exist, 
     * an exception will be thrown.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.executeObject("execNotifyProc",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
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
    throws CpoException;

    /**
     * Executes an Object that represents an executable object within the datasource. 
     * It is assumed that the object exists in the datasource. If the object does not exist, an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class SomeResult sr = new SomeResult();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				sr = (SomeResult)cpo.executeObject("execNotifyProc",so, sr);
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
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
     */
    public <T,C> T executeObject(String name, C criteria, T result)
        throws CpoException;


    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * long count = 0;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				count = cpo.existsObject(so);<br>
     * 				if (count>0) {<br>
     * 					</code><ul><code>
     *              	// object exists<br>
     * 					</code></ul><code>
     *              } else {<br>
     * 					</code><ul><code>
     *              	// object does not exist<br>
     * 					</code></ul><code>
     *              }<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.
	 *
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long existsObject(T obj) throws CpoException;
    
    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * long count = 0;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				count = cpo.existsObject("SomeExistCheck",so);<br>
     * 				if (count>0) {<br>
     * 					</code><ul><code>
     *              	// object exists<br>
     * 					</code></ul><code>
     *              } else {<br>
     * 					</code><ul><code>
     *              	// object does not exist<br>
     * 					</code></ul><code>
     *              }<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the EXISTS Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long existsObject(String name, T obj) throws CpoException;

    /**
     * <code>newOrderBy</code> allows you to dynamically change the order of the objects in the resulting 
     * collection. This allows you to apply user input in determining the order of the collection
     *
     * @param attribute The name of the attribute from the pojo that will be sorted.
     * @param ascending If true, sort ascending. If false sort descending.
     *
     * @return A CpoOrderBy object to be passed into retrieveObjects.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public CpoOrderBy newOrderBy(String attribute, boolean ascending)
        throws CpoException;

    /**
     * <code>newOrderBy</code> allows you to dynamically change the order of the objects in the resulting 
     * collection. This allows you to apply user input in determining the order of the collection
     *
     * @param attribute The name of the attribute from the pojo that will be sorted.
     * @param ascending If true, sort ascending. If false sort descending.
     * @param function A string which represents a datasource function that will be called on the attribute.
     * 					must be contained in the function string. The attribute name will be replaced at run-time with its 
     * 					datasource counterpart
     *
     * @return A CpoOrderBy object to be passed into retrieveObjects.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function)
        throws CpoException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public CpoWhere newWhere() throws CpoException;

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
     */
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value)
        throws CpoException;

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
     */
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not)
        throws CpoException;


    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource. This method uses the default EXISTS, CREATE, and UPDATE query groups specified 
     * for this object.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.persistObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObject(T obj)
        throws CpoException;

    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource.<br>
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.persistObject("persistSomeObject",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObject(String name, T obj)
        throws CpoException;

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.persistObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObjects(Collection<T> coll)
        throws CpoException;

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.persistObjects("myPersist",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public <T> long persistObjects(String name, Collection<T> coll)
        throws CpoException;

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
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> T retrieveObject(T obj)
        throws CpoException;

     /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     * @param name DOCUMENT ME!
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> T  retrieveObject(String name, T obj)
        throws CpoException;
    
    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
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
     * @param where The <code>CpoWhere</code> object that defines the constraints that should be
     *              used when retrieving objects
     * @param orderBy The <code>CpoOrderBy</code> object that defines the order in which objects
     *                should be returned
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */    
    public <T,C> T  retrieveObject(String name, C criteria, T result, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException;
    

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
     * @param where The <code>CpoWhere</code> object that defines the constraints that should be
     *              used when retrieving objects
     * @param orderBy The <code>CpoOrderBy</code> object that defines the order in which objects
     *                should be returned
     * @return A collection of objects will be returned that meet the criteria  specified by obj.
     *         The objects will be of the same type as the Object  that was passed in. If no
     *         objects match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T,C> Collection<T> retrieveObjects(String name, C criteria, T result, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException;

    /**
     * Allows you to perform a series of object interactions with the database. This method
     * pre-dates CpoTrxAdapter and can be used without a programmer needing to remember to call
     * <code>commit()</code> or <code>rollback()</code>. 
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			so = new SomeObject();<br>
     *	 		so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     *                  CpoObject cobj = new CpoObject(CpoAdapter.CREATE,"MyCreate",so);<br>
     * 			al.add(cobj);<br>
     *                  so = new SomeObject();<br>
     *	 		so.setId(3);<br>
     * 			so.setName("New Name");<br>
     *                  CpoObject cobj = new CpoObject(CpoAdapter.PERSIST,"MyPersist",so);<br>
     * 			al.add(cobj);<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.transactObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * 
     * @param coll This is a collection of <code>CpoObject</code> objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.

     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
   public <T> long transactObjects(Collection<CpoObject<T>> coll) throws CpoException;

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.updateObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *        
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long updateObject(T obj) throws CpoException;

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.updateObject("updateSomeObject",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long updateObject(String name, T obj) throws CpoException;

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.updateObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.

     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long updateObjects(Collection<T> coll)
        throws CpoException;

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.updateObjects("myUpdate",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long updateObjects(String name, Collection<T> coll)
        throws CpoException;

    /**
     * Provides a mechanism for the user to obtain a CpoTrxAdapter object. This object allows the
     * to control when commits and rollbacks occur on CPO.
     *   
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * class CpoTrxAdapter cpoTrx = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     *                  cpoTrx = cpo.getCpoTrxAdapter();<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     *	 		try{<br>
     *                      </code><ul><code>
     *                      for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				cpo.updateObject("myUpdate",so);<br>
     *                                  
     * 				</code></ul><code>
     *                      }<br>
     *                      cpoTrx.commit();<br>
     * 			    </code></ul><code>
     *	 		} catch (CpoException ce) {<br>
     *                      </code><ul><code>
     *                      // Handle the error<br>
     *                      cpoTrx.rollback();<br>
     *                      </code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @return A CpoTrxAdapter to manage the transactionality of CPO
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see CpoTrxAdapter
     */

    public CpoTrxAdapter getCpoTrxAdapter() throws CpoException;
}

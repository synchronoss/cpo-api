/**
 * CpoManagerBean.java
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

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.Context;
import javax.naming.InitialContext;

//import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;


public class CpoAdapterBean 
    implements CpoAdapter, SessionBean{

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    

    private static final String PROP_FILE = "org.synchronoss.cpo";
    private static final String PROP_ENV  = "environment";
    private static final String PROP_FACTORYCLASS = "factoryClassName";
    private static final String PROP_LOADERROR = "error.loading.cpofactory";


    private SessionContext ctx_=null;

    private CpoAdapter adapter_ = null;
    
    public CpoAdapterBean(CpoAdapter cpo){
    	adapter_=cpo;
    }

    public void ejbCreate() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
    }

    public void setSessionContext(SessionContext A) {
        this.ctx_=A;
    }

    protected CpoAdapter getAdapter()
        throws CpoException{

        if(adapter_==null) {
            adapter_ = loadAdapter();
        }

        return adapter_;
    }

    public Class getAdapterClass()
        throws CpoException{

        return getAdapter().getClass();
    }

    public Object executeAdapterMethod(String name, Class[] parameterTypes, Object[] args)
        throws CpoException{
        Method meth = null;
        Object obj = null;

        try{
            meth =  getAdapter().getClass().getMethod(name, parameterTypes);
            obj = meth.invoke(getAdapter(),args);
        } catch(Exception e) {
            throw new CpoException(e);
        }

        return obj;
    }

    protected CpoAdapter loadAdapter()
        throws CpoException {

        String factoryClassName = null;
        Class factory = null;
        CpoFactory cpoFactory = null;
        CpoAdapter cpoAdapter = null;
        ResourceBundle b = null;

        try{
            b = PropertyResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), this.getClass().getClassLoader());

            Context ctx = new InitialContext();
            
            Context myEnv = (Context)ctx.lookup(b.getString(PROP_ENV));
            factoryClassName = (String)myEnv.lookup(b.getString(PROP_FACTORYCLASS));
            factory = Class.forName(factoryClassName);
            cpoFactory = (CpoFactory) factory.newInstance();
            cpoAdapter = cpoFactory.newCpoAdapter();
        } catch (Exception e) {
            throw new CpoException(b.getString(PROP_LOADERROR),e);
        }

        return cpoAdapter;

    }

    /**
     * Creates the Object and stores it in the datasource. The assumption
     * is that the object does not exist in the datasource.
     * 
     * This method creates and stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long insertObject(Object obj) throws CpoException{
        return getAdapter().insertObject(null,obj);
    }

    /**
     * Creates the Object and stores it in the datasource. The assumption
     * is that the object does not exist in the datasource.
     * 
     * This method creates and stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long insertObject(String name, Object obj) throws CpoException{
        return getAdapter().insertObject(name,obj);
    }

    /**
     * Iterates through a collection of Objects, creates them and stores them in the datasource. 
     * The assumption is that the objects contained in the collection do not exist in the 
     * datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being created in the datasource then the entire collection
     * will be rolled back
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long insertObjects(Collection coll) throws CpoException{
        return getAdapter().insertObjects(null,coll);
    }

    /**
     * Iterates through a collection of Objects, creates them and stores them in the datasource. 
     * The assumption is that the objects contained in the collection do not exist in the 
     * datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being created in the datasource then the entire collection
     * will be rolled back
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long insertObjects(String name, Collection coll) throws CpoException{
        return getAdapter().insertObjects(name,coll);
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist
     *               in the datasource, an exception will be thrown. The input 
     *               object is used to specify the search criteria, the output 
     *               object is populated with the results of the query.
     */
    public Object retrieveObject(Object obj) throws CpoException{
        return(getAdapter().retrieveObject(null,obj));
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist
     *               in the datasource, an exception will be thrown. The input 
     *               object is used to specify the search criteria, the output 
     *               object is populated with the results of the query.
     */
    public Object retrieveObject(String name, Object obj) throws CpoException{
        return(getAdapter().retrieveObject(name,obj));
    }


    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist
     *               in the datasource, an exception will be thrown. The input 
     *               object is used to specify the search criteria, the output 
     *               object is populated with the results of the query.
     */
    public Object retrieveObject(String name, Object criteria, Object result, CpoWhere where, Collection orderBy) throws CpoException{
      return getAdapter().retrieveObject(name,criteria,result,where, orderBy);
    }
    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * @param name     The filter name which tells the datasource which objects should be
     *                 returned. The name also signifies what data in the object will be 
     *                 populated.
     * @param criteria This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object
     *                 is used to specify the parameters used to retrieve the 
     *                 collection of objects.
     * @param result   This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object
     *                 is used to specify the object type that will be returned in the 
     *                 collection.
     * @return         A collection of objects will be returned that meet the criteria 
     *                 specified by obj. The objects will be of the same type as the Object 
     *                 that was passed in. If no objects match the criteria, an empty
     *                 collection will be returned
     */
    public Collection retrieveObjects(String name, Object criteria, Object result, CpoWhere where, Collection orderBy)  throws CpoException {
        return getAdapter().retrieveObjects(name,criteria,result,where, orderBy);
    }

    /**
     * Persists the Object into the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * This method stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long updateObject(Object obj) throws CpoException{
        return getAdapter().updateObject(null,obj);
    }

    /**
     * Persists the Object into the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * This method stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long updateObject(String name, Object obj) throws CpoException{
        return getAdapter().updateObject(name,obj);
    }

    /**
     * Persists a collection of Objects into the datasource. The assumption
     * is that the objects contained in the collection exist in the datasource.
     * 
     * This method stores the object in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being updated in the datasource then the entire collection
     * will be rolled back.
     * 
     * If no rows are updated, no exception is thrown
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long updateObjects(Collection coll) throws CpoException{
        return getAdapter().updateObjects(null,coll);
    }
    
    /**
     * Persists a collection of Objects into the datasource. The assumption
     * is that the objects contained in the collection exist in the datasource.
     * 
     * This method stores the object in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being updated in the datasource then the entire collection
     * will be rolled back.
     * 
     * If no rows are updated, no exception is thrown
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long updateObjects(String name, Collection coll) throws CpoException{
        return getAdapter().updateObjects(name,coll);
    }

    /**
     * Removes the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * This method stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist in
     *               the datasource an exception will be thrown.
     */
    public long deleteObject(Object obj) throws CpoException{
        return getAdapter().deleteObject(null,obj);
    }

    /**
     * Removes the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * 
     * This method stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist in
     *               the datasource an exception will be thrown.
     */
    public long deleteObject(String name, Object obj) throws CpoException{
        return getAdapter().deleteObject(name,obj);
    }

    /**
     * Removes the Objects contained in the collection from the datasource. The 
     * assumption is that the object exists in the datasource.
     * 
     * This method stores the objects contained in the collection in the datasource.
     * The objects in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being deleted from the datasource then the entire collection
     * will be rolled back.
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long deleteObjects(Collection coll) throws CpoException{
        return getAdapter().deleteObjects(null,coll);
    }

    /**
     * Removes the Objects contained in the collection from the datasource. The 
     * assumption is that the object exists in the datasource.
     * 
     * This method stores the objects contained in the collection in the datasource.
     * The objects in the collection will be treated as one transaction, meaning that if one 
     * of the objects fail being deleted from the datasource then the entire collection
     * will be rolled back.
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public long deleteObjects(String name, Collection coll) throws CpoException{
        return getAdapter().deleteObjects(name,coll);
    }

    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this
     * object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.
     * 
     * This method stores the object in the datasource
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object. 
     * 
     * @see          #existsObject
     * @see          #insertObject
     * @see          #updateObject
     * 
     * @exception    An exception is thrown if existsObject() returns a value > 1
     */
    public long persistObject(Object obj) throws CpoException{
        return getAdapter().persistObject(null,obj);
    }
    
    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this
     * object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.
     * 
     * This method stores the object in the datasource
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object. 
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @see          #existsObject
     * @see          #insertObject
     * @see          #updateObject
     * 
     * @exception    An exception is thrown if existsObject() returns a value > 1
     */
    public long persistObject(String name, Object obj) throws CpoException{
        return getAdapter().persistObject(name,obj);
    }
    
    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this
     * object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.
     * 
     * This method stores the object in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one
     * of the objects fail being inserted or updated in the datasource then the entire collection
     * will be rolled back.
     * @param coll   This is a collection of objects that have been defined within
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object.
     * 
     * @return       A count of the number of objects persisted
     * @exception An     exception is thrown if existsObject() returns a value > 1
     * @exception CpoException
     * @exception RemoteException
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObjects(Collection coll) throws CpoException{
        return getAdapter().persistObjects(null,coll);
    }

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this
     * object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.
     * 
     * This method stores the object in the datasource. The objects
     * in the collection will be treated as one transaction, meaning that if one
     * of the objects fail being inserted or updated in the datasource then the entire collection
     * will be rolled back.
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object.
     * @param coll   This is a collection of objects that have been defined within
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @return       A count of the number of objects persisted
     * @exception An     exception is thrown if existsObject() returns a value > 1
     * @exception CpoException
     * @exception RemoteException
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObjects(String name, Collection coll) throws CpoException{
        return getAdapter().persistObjects(name,coll);
    }

    /**
     * The CpoAdapter will check to see if this object exists in the datasource. 
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object. 
     *
     * @return       The int value of the first column returned in the record set
     * 
     * @exception    An exception will be thrown if the Query Group has a query count != 1
     * @exception    An exception will be thrown if the resultset has a record count != 1
     * @exception    An exception will be thrown if there is a column count != 1
     * @exception    An exception will be thrown if the column returned  cannot be converted to an int
     */
    public long existsObject(Object obj) throws CpoException{
        return getAdapter().existsObject(null,obj);
    }
    
    /**
     * The CpoAdapter will check to see if this object exists in the datasource. 
     * @param name   The name which identifies which EXISTS, INSERT, and UPDATE Query groups
     *               to execute to persist the object. 
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     * @return       The int value of the first column returned in the record set
     * 
     * @exception    An exception will be thrown if the Query Group has a query count != 1
     * @exception    An exception will be thrown if the resultset has a record count != 1
     * @exception    An exception will be thrown if there is a column count != 1
     * @exception    An exception will be thrown if the column returned  cannot be converted to an int
     */
    public long existsObject(String name, Object obj) throws CpoException{
        return getAdapter().existsObject(name,obj);
    }
    
    
    /**
     * Executes an Object whose MetaData contains a stored procedure. An assumption is that
     * the object exists in the datasource.
     * @param criteria This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object
     *                 is used to populate the IN parameters used to retrieve the 
     *                 collection of objects.
     * @param result   This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object defines 
     *                 the object type that will be returned in the 
     *                 collection and contain the result set data or the OUT Parameters.
     * @param name     The filter name which tells the datasource which objects should be
     *                 returned. The name also signifies what data in the object will be 
     *                 populated.
     * 
     * @return         A result object populate with the OUT parameters
     */
    public Object executeObject(Object object)  throws CpoException
    {
        return getAdapter().executeObject(null,object, object);    
    }

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
    public Object executeObject(String name, Object object)  throws CpoException
    {
        return getAdapter().executeObject(name,object, object);    
    }

     /**
     * Executes an Object whose MetaData contains a stored procedure. An assumption is that
     * the object exists in the datasource.
     * @param name     The filter name which tells the datasource which objects should be
     *                 returned. The name also signifies what data in the object will be 
     *                 populated.
     * @param criteria This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object
     *                 is used to populate the IN parameters used to retrieve the 
     *                 collection of objects.
     * @param result   This is an object that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the object does not exist
     *                 in the datasource, an exception will be thrown. This object defines 
     *                 the object type that will be returned in the 
     *                 collection and contain the result set data or the OUT Parameters.
     * @return         A result object populate with the OUT parameters
     */
    public Object executeObject(String name, Object criteria, Object result)  throws CpoException
    {
        return getAdapter().executeObject(name,criteria, result);    
    }

    
    public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException{
        return getAdapter().newOrderBy(attribute,ascending);
    }
    
    public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException{
        return getAdapter().newOrderBy(attribute,ascending,function);
    }

    public CpoWhere newWhere() throws CpoException{
        return getAdapter().newWhere();
    }
    public CpoWhere newWhere(int logical, String attr, int comp, Object value) throws CpoException{
        return getAdapter().newWhere(logical, attr,comp,value);
    }
    public CpoWhere newWhere(int logical, String attr, int comp, Object value, boolean not) throws CpoException{
        return getAdapter().newWhere(logical, attr,comp,value, not);
    }

    public void clearMetaClass(Object obj) throws CpoException{
        getAdapter().clearMetaClass(obj);
    }
    public void clearMetaClass(String className) throws CpoException{
        getAdapter().clearMetaClass(className);
    }
    public void clearMetaClass() throws CpoException{
        getAdapter().clearMetaClass();
    }

    public long transactObjects(Collection coll) throws CpoException{
        return getAdapter().transactObjects(coll);
    }
    public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    	throw new CpoException("Not Supported in Session Bean");
    }
    
}
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
import java.util.Collection;
import java.util.List;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.synchronoss.cpo.jdbc.JdbcCpoFactory;

//import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;


public class CpoAdapterBean 
    implements CpoAdapter, SessionBean{

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
    private CpoAdapter adapter_ = null;
    
    private SessionContext ctx_=null;
    
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

    public Class<?> getAdapterClass()
        throws CpoException{

        return JdbcCpoFactory.getCpoAdapter().getClass();
    }

    public Object executeAdapterMethod(String name, Class<?>[] parameterTypes, Object[] args)
        throws CpoException{
        Method meth = null;
        Object obj = null;

        try{
            meth =  JdbcCpoFactory.getCpoAdapter().getClass().getMethod(name, parameterTypes);
            obj = meth.invoke(JdbcCpoFactory.getCpoAdapter(),args);
        } catch(Exception e) {
            throw new CpoException(e);
        }

        return obj;
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
    public <T> long insertObject(T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().insertObject(null,obj);
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
    public <T> long insertObject(String name, T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().insertObject(name,obj);
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
    public <T> long insertObjects(Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().insertObjects(null,coll);
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
     * @param name   The the context name for which group of operations will be called to
     *               process this insert.
     * @param coll   This is a collection of objects that have been defined within 
     *               the metadata of the datasource. If the class is not defined
     *               an exception will be thrown.
     */
    public <T> long insertObjects(String name, Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().insertObjects(name,coll);
    }

    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * 
     * If the retrieve query defined for this beans returns more than one row,
     * an exception will be thrown.
     * @param bean    This is an bean that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the bean does not exist
     *               in the datasource, an exception will be thrown. The input 
     *               bean is used to specify the search criteria, the output
     *               bean is populated with the results of the query.
     */
    public <T> T retrieveBean(T bean) throws CpoException{
        return(JdbcCpoFactory.getCpoAdapter().retrieveBean(null,bean));
    }

    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * 
     * If the retrieve query defined for this beans returns more than one row,
     * an exception will be thrown.
     * @param name   The the context name for which group of operations will be called to
     *               process this retrieve.
     * @param bean    This is an bean that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the bean does not exist
     *               in the datasource, an exception will be thrown. The input 
     *               bean is used to specify the search criteria, the output
     *               bean is populated with the results of the query.
     */
    public <T> T retrieveBean(String name, T bean) throws CpoException{
        return(JdbcCpoFactory.getCpoAdapter().retrieveBean(name,bean));
    }

    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * 
     * If the retrieve query defined for this beans returns more than one row,
     * an exception will be thrown.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
     * @param criteria This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the parameters used to retrieve the 
     *                 collection of beans.
     * @param result   This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the bean type that will be returned in the
     *                 collection.
     */
    public <T,C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException{
      return JdbcCpoFactory.getCpoAdapter().retrieveBean(name,criteria,result,wheres, orderBy);
    }
    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * 
     * If the retrieve query defined for this beans returns more than one row,
     * an exception will be thrown.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
     * @param criteria This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the parameters used to retrieve the 
     *                 collection of beans.
     * @param result   This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the bean type that will be returned in the
     *                 collection.
     */
    public <T,C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery) throws CpoException{
      return JdbcCpoFactory.getCpoAdapter().retrieveBean(name,criteria,result,wheres, orderBy,nativeQuery);
    }
    
    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param where A CpoWhere bean that defines the constraints that should be
     *           used when retrieving beans
     * @param orderBy The CpoOrderBy bean that defines the order in which beans
     *             should be returned
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,where, orderBy);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param orderBy The CpoOrderBy bean that defines the order in which beans
     *             should be returned
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria, orderBy);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param wheres A collection of CpoWhere beans that define the constraints that should be
     *           used when retrieving beans
     * @param orderBy The CpoOrderBy bean that defines the order in which beans
     *             should be returned
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,wheres, orderBy);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param result This is an bean that has been defined within the metadata of the datasource.
     *     If the class is not defined an exception will be thrown. If the bean does not
     *     exist in the datasource, an exception will be thrown. This bean is used to specify
     *     the bean type that will be returned in the  collection.
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param result This is an bean that has been defined within the metadata of the datasource.
     *     If the class is not defined an exception will be thrown. If the bean does not
     *     exist in the datasource, an exception will be thrown. This bean is used to specify
     *     the bean type that will be returned in the  collection.
     * @param where A CpoWhere bean that defines the constraints that should be
     *           used when retrieving beans
     * @param orderBy The CpoOrderBy bean that defines the order in which beans
     *             should be returned
     * @return A collection of beans will be returned that meet the criteria  specified by obj.
     *      The beans will be of the same type as the bean  that was passed in. If no
     *      beans match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result,where, orderBy);
    }


    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
     * @param criteria This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the parameters used to retrieve the 
     *                 collection of beans.
     * @param result   This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the bean type that will be returned in the
     *                 collection.
     * @return         A collection of beans will be returned that meet the criteria
     *                 specified by obj. The beans will be of the same type as the bean
     *                 that was passed in. If no beans match the criteria, an empty
     *                 collection will be returned
     */
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy)  throws CpoException {
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result, wheres, orderBy);
    }

    /**
     * Retrieves the bean from the datasource. The assumption
     * is that the bean exists in the datasource.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
     * @param criteria This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the parameters used to retrieve the 
     *                 collection of beans.
     * @param result   This is an bean that has been defined within the
     *                 metadata of the datasource. If the class is not defined
     *                 an exception will be thrown. If the bean does not exist
     *                 in the datasource, an exception will be thrown. This bean
     *                 is used to specify the bean type that will be returned in the
     *                 collection.
     * @return         A collection of beans will be returned that meet the criteria
     *                 specified by obj. The beans will be of the same type as the bean
     *                 that was passed in. If no beans match the criteria, an empty
     *                 collection will be returned
     */
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery)  throws CpoException {
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result,wheres, orderBy, nativeQuery);
    }

    /**
     * Retrieves the bean from the datasource. The assumption is that the bean exists in the
     * datasource.
     * @param name The filter name which tells the datasource which beans should be returned. The
     *     name also signifies what data in the bean will be  populated.
     * @param criteria This is an bean that has been defined within the metadata of the
     *     datasource. If the class is not defined an exception will be thrown. If the bean
     *     does not exist in the datasource, an exception will be thrown. This bean is used
     *     to specify the parameters used to retrieve the  collection of beans.
     * @param result This is an bean that has been defined within the metadata of the datasource.
     *     If the class is not defined an exception will be thrown. If the bean does not
     *     exist in the datasource, an exception will be thrown. This bean is used to specify
     *     the bean type that will be returned in the  collection.
     * @param wheres A collection of CpoWhere beans that define the constraints that should be
     *           used when retrieving beans
     * @param orderBy The CpoOrderBy bean that defines the order in which beans
     *             should be returned
     * @param nativeQueries Native query text that will be used to augment the query text stored in
     *             the meta data. This text will be embedded at run-time
     * @param the queue size of the buffer that it uses to send the beans from the producer to the
     *        consumer.
     * @return A CpoResultSet that can be iterated through
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T,C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, int queueSize) throws CpoException{
			  return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result,wheres, orderBy, nativeQueries, queueSize);
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
		 *
		 * @deprecated use retrieveBean
     */
		@Deprecated
    public <T> T retrieveObject(T obj) throws CpoException{
        return(JdbcCpoFactory.getCpoAdapter().retrieveBean(null,obj));
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     *
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param name   The the context name for which group of operations will be called to
     *               process this retrieve.
     * @param obj    This is an object that has been defined within the
     *               metadata of the datasource. If the class is not defined
     *               an exception will be thrown. If the object does not exist
     *               in the datasource, an exception will be thrown. The input
     *               object is used to specify the search criteria, the output
     *               object is populated with the results of the query.
		 *
		 * @deprecated use retrieveBean
     */
		@Deprecated
    public <T> T retrieveObject(String name, T obj) throws CpoException{
        return(JdbcCpoFactory.getCpoAdapter().retrieveBean(name,obj));
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     *
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
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
		 *
		 * @deprecated use retrieveBean
     */
		@Deprecated
    public <T,C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException{
      return JdbcCpoFactory.getCpoAdapter().retrieveBean(name,criteria,result,wheres, orderBy);
    }
    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     *
     * If the retrieve query defined for this objects returns more than one row,
     * an exception will be thrown.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
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
		 *
		 * @deprecated use retrieveBean
     */
		@Deprecated
    public <T,C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery) throws CpoException{
      return JdbcCpoFactory.getCpoAdapter().retrieveBean(name,criteria,result,wheres, orderBy,nativeQuery);
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
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
		 *
		 * @deprecated use retrieveBeans
     */
		@Deprecated
    public <T,C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, T result)  throws CpoException {
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result,wheres, orderBy);
    }

    /**
     * Retrieves the Object from the datasource. The assumption
     * is that the object exists in the datasource.
     * @param name     The the context name for which group of operations will be called to
     *                 process this retrieve.
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
		 *
		 * @deprecated use retrieveBeans
     */
		@Deprecated
    public <T,C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery, T result)  throws CpoException {
        return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria,result,wheres, orderBy, nativeQuery);
    }

  /**
   * Retrieves the Object from the datasource. This method returns an Iterator immediately. The
   * iterator will get filled asynchronously by the cpo framework. The framework will stop supplying
   * the iterator with objects if the objectBufferSize is reached.
   *
   * If the consumer of the iterator is processing records faster than the framework is filling it,
   * then the iterator will wait until it has data to provide.
   *
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
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects
   *                should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in
   *             the meta data. This text will be embedded at run-time
   * @param objectBufferSize the maximum number of objects that the Iterator is allowed to cache.
   *        Once reached, the CPO framework will halt processing records from the datasource.
   *
   * @return An iterator that will be fed objects from the CPO framework.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
	 *
	 * @deprecated use retrieveBeans
   */
    @Deprecated
    public <T,C> CpoResultSet<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery, T result, int queueSize) throws CpoException {
      return JdbcCpoFactory.getCpoAdapter().retrieveBeans(name,criteria, result, wheres, orderBy, nativeQuery, queueSize);
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
    public <T> long updateObject(T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().updateObject(null,obj);
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
    public <T> long updateObject(String name, T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().updateObject(name,obj);
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
    public <T> long updateObjects(Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().updateObjects(null,coll);
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
    public <T> long updateObjects(String name, Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().updateObjects(name,coll);
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
    public <T> long deleteObject(T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().deleteObject(null,obj);
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
    public <T> long deleteObject(String name, T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().deleteObject(name,obj);
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
    public <T> long deleteObjects(Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().deleteObjects(null,coll);
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
    public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().deleteObjects(name,coll);
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
    public <T> long persistObject(T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().persistObject(null,obj);
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
    public <T> long persistObject(String name, T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().persistObject(name,obj);
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
    public <T> long persistObjects(Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().persistObjects(null,coll);
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
    public <T> long persistObjects(String name, Collection<T> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().persistObjects(name,coll);
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
    public <T> long existsObject(T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().existsObject(null,obj);
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
    public <T> long existsObject(String name, T obj) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().existsObject(name,obj);
    }
    
    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * 
     * <pre>Example:<code>
     * 
     * class SomeObject so = new SomeObject();
     * long count = 0;
     * class CpoAdapter cpo = null;
     * 
     *  
     *  try {
     *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
     *  } catch (CpoException ce) {
     *    // Handle the error
     *    cpo = null;
     *  }
     *  
     *  if (cpo!=null) {
     *    so.setId(1);
     *    so.setName("SomeName");
     *    try{
     *      CpoWhere where = cpo.newCpoWhere(CpoWhere.LOGIC_NONE, id, CpoWhere.COMP_EQ);
     *      count = cpo.existsObject("SomeExistCheck",so, where);
     *      if (count>0) {
     *        // object exists
     *      } else {
     *        // object does not exist
     *      }
     *    } catch (CpoException ce) {
     *      // Handle the error
     *    }
     *  }
     *</code>
     *</pre>
     * 
     * @param name The String name of the EXISTS Query group that will be used to create the object
     *          in the datasource. null signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *     the class is not defined an exception will be thrown. This object will be searched for inside the
     *     datasource.
     * @param wheres A Collection of CpoWhere objects that pass in run-time constraints to the query that performs the 
     *      the exist
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().existsObject(name,obj, wheres);
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
    public <T> T executeObject(T object)  throws CpoException
    {
        return JdbcCpoFactory.getCpoAdapter().executeObject(null,object, object);    
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
    public <T> T executeObject(String name, T object)  throws CpoException
    {
        return JdbcCpoFactory.getCpoAdapter().executeObject(name,object, object);    
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
    public <T,C> T executeObject(String name, C criteria, T result)  throws CpoException
    {
        return JdbcCpoFactory.getCpoAdapter().executeObject(name,criteria, result);    
    }
    
    public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().newOrderBy(attribute,ascending);
    }
    
    public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().newOrderBy(attribute,ascending,function);
    }

    public CpoWhere newWhere() throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().newWhere();
    }
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().newWhere(logical, attr,comp,value);
    }
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().newWhere(logical, attr,comp,value, not);
    }

    public void clearMetaClass(Object obj) throws CpoException{
        JdbcCpoFactory.getCpoAdapter().clearMetaClass(obj);
    }
    public void clearMetaClass(String className) throws CpoException{
        JdbcCpoFactory.getCpoAdapter().clearMetaClass(className);
    }
    public void clearMetaClass() throws CpoException{
        JdbcCpoFactory.getCpoAdapter().clearMetaClass();
    }
    public void clearMetaClass(boolean all) throws CpoException{
      JdbcCpoFactory.getCpoAdapter().clearMetaClass(all);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> long transactObjects(Collection<CpoObject<T>> coll) throws CpoException{
        return JdbcCpoFactory.getCpoAdapter().transactObjects(coll);
    }
    public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    	throw new CpoException("Not Supported in Session Bean");
    }
    
}

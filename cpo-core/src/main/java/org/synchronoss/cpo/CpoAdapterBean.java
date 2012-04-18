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


public class CpoAdapterBean 
    implements CpoAdapter, SessionBean{

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
    private CpoAdapter cpoAdapter = null;
    
    private SessionContext ctx_=null;
    
    public CpoAdapterBean(CpoAdapterFactory cpoFactory) throws CpoException {
    	this.cpoAdapter = cpoFactory.getCpoAdapter();
    }

    public CpoAdapterBean(CpoAdapter cpoAdapter) throws CpoException {
    	this.cpoAdapter = cpoAdapter;
    }

    public void ejbCreate() {
    }

  @Override
    public void ejbActivate() {
    }

  @Override
    public void ejbPassivate() {
    }

  @Override
    public void ejbRemove() {
    }

  @Override
    public void setSessionContext(SessionContext A) {
        this.ctx_=A;
    }

    public Class<?> getAdapterClass()
        throws CpoException{

        return cpoAdapter.getClass();
    }

    public Object executeAdapterMethod(String name, Class<?>[] parameterTypes, Object[] args)
        throws CpoException{
        Method meth = null;
        Object obj = null;

        try{
            meth =  cpoAdapter.getClass().getMethod(name, parameterTypes);
            obj = meth.invoke(cpoAdapter,args);
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
  @Override
    public <T> long insertObject(T obj) throws CpoException{
        return cpoAdapter.insertObject(null,obj);
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
  @Override
    public <T> long insertObject(String name, T obj) throws CpoException{
        return cpoAdapter.insertObject(name,obj);
    }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in
   * the datasource.  This method creates and stores the object in the datasource
   *
   * @param name The String name of the CREATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used which is
   *             equivalent to insertObject(Object obj);
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be
   *                 used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException{
        return cpoAdapter.insertObject(name,obj, wheres, orderBy, nativeQueries);
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
  @Override
    public <T> long insertObjects(Collection<T> coll) throws CpoException{
        return cpoAdapter.insertObjects(null,coll);
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
  @Override
    public <T> long insertObjects(String name, Collection<T> coll) throws CpoException{
        return cpoAdapter.insertObjects(name,coll);
    }

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
   * @param name The String name of the CREATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be
   *                 used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException{
        return cpoAdapter.insertObject(name,coll, wheres, orderBy, nativeQueries);
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
  @Override
    public <T> T retrieveBean(T bean) throws CpoException{
        return(cpoAdapter.retrieveBean(null,bean));
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
  @Override
    public <T> T retrieveBean(String name, T bean) throws CpoException{
        return(cpoAdapter.retrieveBean(name,bean));
    }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.  If the retrieve query defined for this beans returns more than one row, an
   * exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param bean  This is an bean that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown. If the bean does not exist
   *             in the datasource, an exception will be thrown. The input  bean is used to specify
   *             the search criteria, the output  bean is populated with the results of the query.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be
   *                 used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return An bean of the same type as the result parameter that is filled in as specified
   *         the metadata for the retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
      throws CpoException{
        return(cpoAdapter.retrieveBean(name,bean, wheres, orderBy, nativeQueries));
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
  @Override
    public <T,C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException{
      return cpoAdapter.retrieveBean(name,criteria,result,wheres, orderBy);
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
  @Override
    public <T,C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery) throws CpoException{
      return cpoAdapter.retrieveBean(name,criteria,result,wheres, orderBy,nativeQuery);
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
  @Override
    public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria);
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
  @Override
    public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria,where, orderBy);
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
  @Override
    public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria, orderBy);
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
  @Override
    public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria,wheres, orderBy);
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
  @Override
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria,result);
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
  @Override
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where,
        Collection<CpoOrderBy> orderBy) throws CpoException{
        return cpoAdapter.retrieveBeans(name,criteria,result,where, orderBy);
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
  @Override
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy)  throws CpoException {
        return cpoAdapter.retrieveBeans(name,criteria,result, wheres, orderBy);
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
  @Override
    public <T,C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery)  throws CpoException {
        return cpoAdapter.retrieveBeans(name,criteria,result,wheres, orderBy, nativeQuery);
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
  @Override
    public <T,C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, int queueSize) throws CpoException{
			  return cpoAdapter.retrieveBeans(name,criteria,result,wheres, orderBy, nativeQueries, queueSize);
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
  @Override
    public <T> T retrieveObject(T obj) throws CpoException{
        return(cpoAdapter.retrieveBean(null,obj));
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
  @Override
    public <T> T retrieveObject(String name, T obj) throws CpoException{
        return(cpoAdapter.retrieveBean(name,obj));
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
  @Override
    public <T,C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException{
      return cpoAdapter.retrieveBean(name,criteria,result,wheres, orderBy);
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
  @Override
    public <T,C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery) throws CpoException{
      return cpoAdapter.retrieveBean(name,criteria,result,wheres, orderBy,nativeQuery);
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
  @Override
    public <T,C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, T result)  throws CpoException {
        return cpoAdapter.retrieveBeans(name,criteria,result,wheres, orderBy);
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
  @Override
    public <T,C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery, T result)  throws CpoException {
        return cpoAdapter.retrieveBeans(name,criteria,result,wheres, orderBy, nativeQuery);
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
  @Override
    public <T,C> CpoResultSet<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQuery, T result, int queueSize) throws CpoException {
      return cpoAdapter.retrieveBeans(name,criteria, result, wheres, orderBy, nativeQuery, queueSize);
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
  @Override
    public <T> long updateObject(T obj) throws CpoException{
        return cpoAdapter.updateObject(null,obj);
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
  @Override
    public <T> long updateObject(String name, T obj) throws CpoException{
        return cpoAdapter.updateObject(name,obj);
    }

/**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object
   * exists in the datasource. If it exists then the object will be updated. If it does not exist,
   * an exception will be thrown
   * 
   * @param name The String name of the UPDATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown.
   * @param wheres    A collection of CpoWhere objects to be used by the query
   * @param orderBy   A collection of CpoOrderBy objects to be used by the query
   * @param nativeQueries A collection of CpoNativeQuery objects to be used by the query
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, 
      Collection<CpoNativeQuery> nativeQueries) throws CpoException{
        return cpoAdapter.updateObject(name,obj,wheres, orderBy, nativeQueries);
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
  @Override
    public <T> long updateObjects(Collection<T> coll) throws CpoException{
        return cpoAdapter.updateObjects(null,coll);
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
  @Override
    public <T> long updateObjects(String name, Collection<T> coll) throws CpoException{
        return cpoAdapter.updateObjects(name,coll);
    }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects
   * contained in the collection exist in the datasource.  This method stores the object in the
   * datasource. The objects in the collection will be treated as one transaction, meaning that
   * if one of the objects fail being updated in the datasource then the entire collection will
   * be rolled back, if supported by the datasource.
   * 
   * @param name The String name of the UPDATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @param wheres    A collection of CpoWhere objects to be used by the query
   * @param orderBy   A collection of CpoOrderBy objects to be used by the query
   * @param nativeQueries A collection of CpoNativeQuery objects to be used by the query
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
      throws CpoException{
        return cpoAdapter.updateObject(name,coll,wheres, orderBy, nativeQueries);
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
  @Override
    public <T> long deleteObject(T obj) throws CpoException{
        return cpoAdapter.deleteObject(null,obj);
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
  @Override
    public <T> long deleteObject(String name, T obj) throws CpoException{
        return cpoAdapter.deleteObject(name,obj);
    }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the
   * datasource.  This method stores the object in the datasource
   * 
   * @param name The String name of the DELETE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown. If the object does not exist
   *             in the datasource an exception will be thrown.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be
   *                 used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException{
        return cpoAdapter.deleteObject(name,obj, wheres, orderBy, nativeQueries);
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
  @Override
    public <T> long deleteObjects(Collection<T> coll) throws CpoException{
        return cpoAdapter.deleteObjects(null,coll);
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
  @Override
    public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException{
        return cpoAdapter.deleteObjects(name,coll);
    }

  /**
   * Removes the Objects contained in the collection from the datasource. The  assumption is that
   * the object exists in the datasource.  This method stores the objects contained in the
   * collection in the datasource. The objects in the collection will be treated as one transaction,
   * assuming the datasource supports transactions.
   * 
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   * 
   * @param name The String name of the DELETE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be
   *                 used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
      throws CpoException{
        return cpoAdapter.deleteObject(name, coll, wheres, orderBy, nativeQueries);
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
  @Override
    public <T> long persistObject(T obj) throws CpoException{
        return cpoAdapter.persistObject(null,obj);
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
  @Override
    public <T> long persistObject(String name, T obj) throws CpoException{
        return cpoAdapter.persistObject(name,obj);
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
  @Override
    public <T> long persistObjects(Collection<T> coll) throws CpoException{
        return cpoAdapter.persistObjects(null,coll);
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
  @Override
    public <T> long persistObjects(String name, Collection<T> coll) throws CpoException{
        return cpoAdapter.persistObjects(name,coll);
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
  @Override
    public <T> long existsObject(T obj) throws CpoException{
        return cpoAdapter.existsObject(null,obj);
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
  @Override
    public <T> long existsObject(String name, T obj) throws CpoException{
        return cpoAdapter.existsObject(name,obj);
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
  @Override
    public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException{
        return cpoAdapter.existsObject(name,obj, wheres);
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
  @Override
    public <T> T executeObject(T object)  throws CpoException
    {
        return cpoAdapter.executeObject(null,object, object);    
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
  @Override
    public <T> T executeObject(String name, T object)  throws CpoException
    {
        return cpoAdapter.executeObject(name,object, object);    
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
  @Override
    public <T,C> T executeObject(String name, C criteria, T result)  throws CpoException
    {
        return cpoAdapter.executeObject(name,criteria, result);    
    }
    
  @Override
    public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException{
        return cpoAdapter.newOrderBy(attribute,ascending);
    }
    
  @Override
    public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException{
        return cpoAdapter.newOrderBy(attribute,ascending,function);
    }

  @Override
    public CpoWhere newWhere() throws CpoException{
        return cpoAdapter.newWhere();
    }
  @Override
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value) throws CpoException{
        return cpoAdapter.newWhere(logical, attr,comp,value);
    }
  @Override
    public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException{
        return cpoAdapter.newWhere(logical, attr,comp,value, not);
    }

    /**
     * @deprecated
     */
    @Deprecated
  @Override
    public <T> long transactObjects(Collection<CpoObject<T>> coll) throws CpoException{
        return cpoAdapter.transactObjects(coll);
    }
    
  @Override
    public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    	throw new CpoException("Not Supported in Session Bean");
    }
    
}

/**
 * CpoObject.java
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



/**
 * CpoObject is used in conjunction with the <code>transactObjects</method> to perform a 
 * transactions without having to deal with commit, or rollback.
 *
 * A series of CpoObjects are defined and stored in a collection and then the collection 
 * is passed into <code>transactObjects</code>.
 *
 *<br>
 * Example<br>
 *      * <code><br>
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
 * @author david berry
 */

public class CpoObject<T> extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

//    private static Logger logger = Logger.getLogger(CpoObject.class.getName());

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    

    private int operation_ = -1;
    private T object_ = null;
    private String   name_ = null;

    @SuppressWarnings("unused")
    private CpoObject(){}

    /**
     * 
     * @param operation One of the following constants
     *    CpoAdapter.CREATE - performs an <code>insertObject</code> as part of a bigger transaction 
     *    CpoAdapter.INSERT - performs an <code>insertObject</code> as part of a bigger transaction
     *    CpoAdapter.UPDATE - performs an <code>updateObject</code> as part of a bigger transaction
     *    CpoAdapter.DELETE - performs an <code>deleteObject</code> as part of a bigger transaction
     *    CpoAdapter.RETRIEVE - performs a <code>retrieveObject</code> as part of a bigger transaction
     *    CpoAdapter.LIST - performs an <code>retrieveObjects</code> as part of a bigger transaction
     *    CpoAdapter.PERSIST - performs a <code>persistObject</code> as part of a bigger transaction
     *    CpoAdapter.EXIST - performs an <code>existsObject</code> as part of a bigger transaction
     *    CpoAdapter.EXECUTE - performs an <code>executeObject</code> as part of a bigger transaction
     * 
     * @param name - The context name of that identifies the query group associated with the operation.
     *
     * @param object - The populated object for which CPO will be called. 
     */
    public CpoObject(int operation, String name, T object){
        setOperation(operation);
        setName(name);
        setObject(object);
    }

    protected void setOperation(int operation){
        operation_ = operation;
    }

    protected void setName(String name){
        name_ = name;
    }

    protected void setObject(T object){
        object_ = object;
    }

    /**
     * Gets the integer that represents the type of operation that will be 
     * called on the pojo
     *
     * @return One of the following:
     *    CpoAdapter.CREATE - performs an <code>insertObject</code> as part of a bigger transaction 
     *    CpoAdapter.INSERT - performs an <code>insertObject</code> as part of a bigger transaction
     *    CpoAdapter.UPDATE - performs an <code>updateObject</code> as part of a bigger transaction
     *    CpoAdapter.DELETE - performs an <code>deleteObject</code> as part of a bigger transaction
     *    CpoAdapter.RETRIEVE - performs a <code>retrieveObject</code> as part of a bigger transaction
     *    CpoAdapter.LIST - performs an <code>retrieveObjects</code> as part of a bigger transaction
     *    CpoAdapter.PERSIST - performs a <code>persistObject</code> as part of a bigger transaction
     *    CpoAdapter.EXIST - performs an <code>existsObject</code> as part of a bigger transaction
     *    CpoAdapter.EXECUTE - performs an <code>executeObject</code> as part of a bigger transaction
     */
    public int getOperation(){
        return operation_;
    }

    /**
     * Gets the context name that will be used to identify the query group that is associated with
     * the operation on the specific object
     *
     * @return The context name
     */
    public String getName(){
        return name_;
    }

    /**
     * Gets the object that was passed in when creating this CpoObject.
     *
     * @return The populated object for which CPO will be called.  
     */
    public T getObject(){
        return object_;
    }


}
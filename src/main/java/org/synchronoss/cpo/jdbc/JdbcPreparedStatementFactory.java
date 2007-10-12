/**
 * JdbcPreparedStatementFactory.java    
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
package org.synchronoss.cpo.jdbc;


import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoReleasible;
import org.synchronoss.cpo.CpoWhere;


/**
 * JdbcPreparedStatementFactory is the object that encapsulates the creation of the actual
 * PreparedStatement for the JDBC driver. 
 *
 * @author david berry
 */
public class JdbcPreparedStatementFactory implements CpoReleasible {
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;

    /** DOCUMENT ME! */
    private static Logger logger=Logger.getLogger(JdbcPreparedStatementFactory.class.getName());
    
    private Logger localLogger=null;
    
    private PreparedStatement ps_ = null;
    
    private JdbcPreparedStatementFactory(){}
    
    private ArrayList releasibles = new ArrayList();
    
    private JdbcQuery jq_ = null;
    
    private Collection bindValues_=null;
    
    private static final String WHERE_MARKER = "__CPO_WHERE__";
    private static final String ORDERBY_MARKER = "__CPO_ORDERBY__";

    /**
     * Used to build the PreparedStatement that is used by CPO to create the 
     * actual JDBC PreparedStatement.
     *
     * The constructor is called by the internal CPO framework. This is not to be used by
     * users of CPO. Programmers that build Transforms may need to use this object to get access
     * to the actual connection. 
     * 
     * @param conn The actual jdbc connection that will be used to create the callable statement.
     * @param jca The JdbcCpoAdapter that is controlling this transaction 
     * @param jq The JdbcQuery that is being executed
     * @param obj The pojo that is being acted upon
    *
     * @throws CpoException if a CPO error occurs
     * @throws SQLException if a JDBC error occurs
     */
    public JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcMetaClass jmcCriteria, JdbcQuery jq, Object obj) throws CpoException{
        this(conn, jca, jmcCriteria, jq, obj, null, null, null);
    }

    /**
     * Used to build the PreparedStatement that is used by CPO to create the 
     * actual JDBC PreparedStatement.
     *
     * The constructor is called by the internal CPO framework. This is not to be used by
     * users of CPO. Programmers that build Transforms may need to use this object to get access
     * to the actual connection. 
     * 
     * @param conn The actual jdbc connection that will be used to create the callable statement.
     * @param jca The JdbcCpoAdapter that is controlling this transaction 
     * @param jq The JdbcQuery that is being executed
     * @param obj The pojo that is being acted upon
     * @param where a cpoWhere to be added to the queryText from the query group
     * @param orderBy an orderBy to be added to the queryText from the query group
     *
     * @throws CpoException if a CPO error occurs
     * @throws SQLException if a JDBC error occurs
     */
    public JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcMetaClass jmcCriteria, JdbcQuery jq, Object obj, CpoWhere where, Collection orderBy) throws CpoException{
        this(conn, jca, jmcCriteria, jq, obj, where, orderBy, null);
    }

    /**
     * Used to build the PreparedStatement that is used by CPO to create the 
     * actual JDBC PreparedStatement.
     *
     * The constructor is called by the internal CPO framework. This is not to be used by
     * users of CPO. Programmers that build Transforms may need to use this object to get access
     * to the actual connection. 
     * 
     * @param conn The actual jdbc connection that will be used to create the callable statement.
     * @param jca The JdbcCpoAdapter that is controlling this transaction 
     * @param jq The JdbcQuery that is being executed
     * @param obj The pojo that is being acted upon
     * @param additionalSql Additional sql to be appended to the JdbcQuery sql that is used to create the 
     *        actual JDBC PreparedStatement
     * @param bindValues additional bind values from a dynamic where statement
     *
     * @throws CpoException if a CPO error occurs
     * @throws SQLException if a JDBC error occurs
     */
    public JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcMetaClass jmcCriteria, JdbcQuery jq, Object obj,
    		CpoWhere where, Collection orderBy, Collection bindValues) throws CpoException {
      String sql=buildSql(jmcCriteria, jq.getText(), where, orderBy, bindValues);
      
       localLogger = obj==null?logger:Logger.getLogger(obj.getClass().getName());


       localLogger.info("JdbcQuery SQL = <"+sql+">");

        PreparedStatement pstmt = null;
        
        try {
            pstmt=conn.prepareStatement(sql);
        } catch (SQLException se){
        	localLogger.error("Error Instantiating JdbcPreparedStatementFactory SQL=<"+sql+">"+se.getLocalizedMessage());
          	throw new CpoException(se);
        }
        setPreparedStatement(pstmt);
        setJdbcQuery(jq);
        setBindValues(bindValues);

        bindParameters(obj);

    }
    /**
     * DOCUMENT ME!
     *
     * @param jmc DOCUMENT ME!
     * @param sql DOCUMENT ME!
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     * @param bindValues DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private String buildSql(JdbcMetaClass jmc, String sql, CpoWhere where, Collection orderBy,
        Collection bindValues) throws CpoException {
        StringBuffer sqlText=new StringBuffer();

        Iterator obIt=null;
        JdbcCpoOrderBy ob=null;
        JdbcWhereBuilder jwb=new JdbcWhereBuilder(jmc);
        JdbcCpoWhere jcw=(JdbcCpoWhere) where;

        sqlText.append(sql);

        // do the where stuff here when ready
        if(jcw!=null) {
            try{
                jcw.acceptDFVisitor(jwb);
            } catch (Exception e){
                throw new CpoException("Unable to build WHERE clause",e);
            }
            
            if (sqlText.indexOf(WHERE_MARKER)==-1)
                sqlText.append(jwb.getWhereClause());
            else 
                sqlText = replaceMarker(sqlText, WHERE_MARKER,jwb.getWhereClause());
            
            bindValues.addAll(jwb.getBindValues());
        } else {
        	sqlText = replaceMarker(sqlText, WHERE_MARKER,"");
        }

        // do the order by stuff now
        if(orderBy!=null) {
            StringBuffer obBuff = new StringBuffer();
            obIt=orderBy.iterator();
            
            try {
	            if(obIt.hasNext()) {
	                obBuff.append(" ORDER BY ");
	                ob=(JdbcCpoOrderBy) obIt.next();
	                obBuff.append(ob.toString(jmc));
	            }
	
	            while(obIt.hasNext()) {
	                obBuff.append(", ");
	                ob=(JdbcCpoOrderBy) obIt.next();
	                obBuff.append(ob.toString(jmc));
	            }
            } catch (CpoException ce) {
            	throw new CpoException("Error Processing OrderBy Attribute<"+ce.getLocalizedMessage()+"> not Found. JDBC Query=<"+sqlText.toString()+obBuff.toString()+">");
            }
            if (sqlText.indexOf(ORDERBY_MARKER)==-1){
                sqlText.append(obBuff);
            }
            else {
                sqlText=replaceMarker(sqlText, ORDERBY_MARKER, obBuff.toString());
            }
        } else {
            sqlText=replaceMarker(sqlText, ORDERBY_MARKER, "");
        }
        
        return sqlText.toString();
    }
    
    protected StringBuffer replaceMarker(StringBuffer source, String marker, String replace){
      int attrOffset = 0;
      int fromIndex = 0;
      int mLength=marker.length();
      int rLength=replace.length();
      
      //OUT.debug("starting string <"+source.toString()+">");
      if(source!=null && source.length()>0) {
          while((attrOffset=source.indexOf(marker, fromIndex))!=-1){
                   source.replace(attrOffset,attrOffset+mLength, replace);
                   fromIndex=attrOffset+rLength;
          }
      }
      //OUT.debug("ending string <"+source.toString()+">");

      return source;

  }

   
    /**
     * Returns the jdbc prepared statment associated with this 
     * object
     */
     public PreparedStatement getPreparedStatement(){
        return ps_;
    }
    protected void setPreparedStatement(PreparedStatement ps){
        ps_ = ps;
    }
    
    /**
     * Adds a releasible object to this object. The release method
     * on the releasible will be called when the PreparedStatement 
     * is executed.
     * 
     */
    public void AddReleasible(CpoReleasible releasible){
        if (releasible!=null)
            releasibles.add(releasible);
        
    }

    /**
     * Called by the CPO framework. This method calls the <code>release</code>
     * on all the CpoReleasible associated with this object
     */
    public void release() throws CpoException{
        Iterator it = releasibles.iterator();
        while (it.hasNext()){
            try{
                ((CpoReleasible)it.next()).release();
            } catch(CpoException ce) {
                localLogger.error("Error Releasing Prepared Statement Transform Object",ce);
                throw ce;
            }
        }
    }
    
    /**
     * Called by the CPO Framework. Binds all the attibutes from the class 
     * for the CPO meta parameters and the parameters from the dynamic where.
     *
     */
    public void bindParameters(Object obj) throws CpoException {
    	int j=0;
        ArrayList parameters=getJdbcQuery().getParameterList();
        JdbcParameter parameter=null;
        JdbcAttribute attribute=null;
        int preparedStatementArgNum=0;
        Collection bindValues = getBindValues();
        
        for(j=0; j<parameters.size(); j++) {
            preparedStatementArgNum++;
            parameter=(JdbcParameter) parameters.get(j);

            if(parameter==null) {
                throw new CpoException("JdbcParameter is null!");
            }

            attribute=parameter.getAttribute();
            

            attribute.invokeGetter(this, obj, preparedStatementArgNum);
        }

        j++;

        if(bindValues!=null) {
            Iterator valuesIt=bindValues.iterator();

            if(valuesIt!=null) {
                while(valuesIt.hasNext()) {
                    BindAttribute bindAttr=(BindAttribute)valuesIt.next();
                    Object bindObject = bindAttr.getBindObject();
                    JdbcAttribute ja = bindAttr.getJdbcAttribute();

                    
                    // check to see if we are getting a cpo value object or an object that can be put directly in the statement (String, BigDecimal, etc)
                    JavaSqlMethod jsm = JavaSqlMethods.getJavaSqlMethod(bindObject.getClass());
                    if (jsm != null){
                        try{
                        	if (ja==null)
                        		localLogger.debug(bindAttr.getName()+"="+bindObject);
                        	else
                        		localLogger.debug(ja.getDbName()+"="+bindObject);
                            jsm.getPsSetter().invoke(this.getPreparedStatement(), new Object[]{new Integer(j++),bindObject});
                        } catch (IllegalAccessException iae){
                        	localLogger.error("Error Accessing Prepared Statement Setter: "+iae.getLocalizedMessage());
                            throw new CpoException(iae);
                        } catch (InvocationTargetException ite){
                        	localLogger.error("Error Invoking Prepared Statement Setter: "+ite.getCause().getLocalizedMessage());
                            throw new CpoException(ite.getCause());
                        }
                    } else {
                        ja.invokeGetter(this, bindObject, j++);
                    }

                }
            }
        }
   	
    }

	/**
	 * @return Returns the bindValues_.
	 */
	protected Collection getBindValues() {
		return bindValues_;
	}

	/**
	 * @param bindValues_ The bindValues_ to set.
	 */
	protected void setBindValues(Collection bindValues_) {
		this.bindValues_ = bindValues_;
	}

	/**
	 * @return Returns the jq_.
	 */
	protected JdbcQuery getJdbcQuery() {
		return jq_;
	}

	/**
	 * @param jq_ The jq_ to set.
	 */
	protected void setJdbcQuery(JdbcQuery jq_) {
		this.jq_ = jq_;
	}

}

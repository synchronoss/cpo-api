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
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoNativeQuery;
import org.synchronoss.cpo.CpoOrderBy;
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
    
    @SuppressWarnings("unused")
    private JdbcPreparedStatementFactory(){}
    
    private ArrayList<CpoReleasible> releasibles = new ArrayList<CpoReleasible>();
    
    private JdbcQuery jq_ = null;
        
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
    public <T> JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcMetaClass<T> jmcCriteria, JdbcQuery jq, T obj) throws CpoException{
        this(conn, jca, jmcCriteria, jq, obj, null, null, null);
    }


    /**
   * Used to build the PreparedStatement that is used by CPO to create the
   * actual JDBC PreparedStatement.
   * 
   * The constructor is called by the internal CPO framework. This is not to be
   * used by users of CPO. Programmers that build Transforms may need to use
   * this object to get access to the actual connection.
   * 
   * @param conn
   *          The actual jdbc connection that will be used to create the
   *          callable statement.
   * @param jca
   *          The JdbcCpoAdapter that is controlling this transaction
   * @param jq
   *          The JdbcQuery that is being executed
   * @param obj
   *          The pojo that is being acted upon
   * @param additionalSql
   *          Additional sql to be appended to the JdbcQuery sql that is used to
   *          create the actual JDBC PreparedStatement
   * 
   * @throws CpoException
   *           if a CPO error occurs
   * @throws SQLException
   *           if a JDBC error occurs
   */
  public <T> JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcMetaClass<T> jmcCriteria,
      JdbcQuery jq, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeQuery> nativeQueries) throws CpoException {

    // get the list of bindValues from the query parameters
    Collection<BindAttribute> bindValues = getBindValues(jq, obj);

    String sql = buildSql(jmcCriteria, jq.getText(), wheres, orderBy, nativeQueries, bindValues);

    localLogger = obj == null ? logger : Logger.getLogger(obj.getClass().getName());

    localLogger.info("JdbcQuery SQL = <" + sql + ">");

    PreparedStatement pstmt = null;

    try {
      pstmt = conn.prepareStatement(sql);
    } catch (SQLException se) {
      localLogger
          .error("Error Instantiating JdbcPreparedStatementFactory SQL=<" + sql + ">" + se.getLocalizedMessage());
      throw new CpoException(se);
    }
    setPreparedStatement(pstmt);
    
    setBindValues(bindValues);

  }
    /**
     * DOCUMENT ME!
     *
     * @param jmc DOCUMENT ME!
     * @param sql DOCUMENT ME!
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private <T> String buildSql(JdbcMetaClass<T> jmc, String sql, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Collection<BindAttribute> bindValues) throws CpoException {
        StringBuilder sqlText=new StringBuilder();

        sqlText.append(sql);

        if (wheres != null){
          for (CpoWhere where: wheres){
            JdbcWhereBuilder<T> jwb=new JdbcWhereBuilder<T>(jmc);
            JdbcCpoWhere jcw=(JdbcCpoWhere) where;
  
          // do the where stuff here when ready
            try{
                jcw.acceptDFVisitor(jwb);
            } catch (Exception e){
                throw new CpoException("Unable to build WHERE clause",e);
            }
            
            if (sqlText.indexOf(jcw.getName())==-1)
                sqlText.append(jwb.getWhereClause());
            else 
                sqlText = replaceMarker(sqlText, jcw.getName(),jwb.getWhereClause());
            
            bindValues.addAll(jwb.getBindValues());
          }
        }

        // do the order by stuff now
        if(orderBy!=null) {
          HashMap<String, StringBuilder> mapOrderBy = new HashMap<String, StringBuilder>();
            try {
              for (CpoOrderBy ob : orderBy){
                StringBuilder sb = mapOrderBy.get(ob.getName());
                if (sb==null){
                  sb = new StringBuilder(" ORDER BY ");
                  mapOrderBy.put(ob.getName(),sb);
                }
                sb.append(((JdbcCpoOrderBy)ob).toString(jmc));
              }
            } catch (CpoException ce) {
            	throw new CpoException("Error Processing OrderBy Attribute<"+ce.getLocalizedMessage()+"> not Found. JDBC Query=<"+sqlText.toString()+">");
            }
            
            Set<Entry<String, StringBuilder>> entries = mapOrderBy.entrySet();
            for(Entry<String, StringBuilder> entry: entries){
              if (sqlText.indexOf(entry.getKey())==-1){
                  sqlText.append(entry.getValue().toString());
              }
              else {
                  sqlText=replaceMarker(sqlText, entry.getKey(), entry.getValue().toString());
              }
            }
        }
        
        if (nativeQueries != null){
          for (CpoNativeQuery cnq : nativeQueries){
            if (cnq.getMarker()==null || sqlText.indexOf(cnq.getMarker())==-1){
              sqlText.append(" ");
              sqlText.append(cnq.getNativeText());
            } else {
              sqlText=replaceMarker(sqlText, cnq.getMarker(), cnq.getNativeText());
            }
          }
        }
        
        // left for backwards compatibility
        sqlText = replaceMarker(sqlText, WHERE_MARKER,"");
        sqlText=replaceMarker(sqlText, ORDERBY_MARKER, "");
        
        return sqlText.toString();
    }
    
    protected StringBuilder replaceMarker(StringBuilder source, String marker, String replace){
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
    	for(CpoReleasible releasible:releasibles){
            try{
            	releasible.release();
            } catch(CpoException ce) {
                localLogger.error("Error Releasing Prepared Statement Transform Object",ce);
                throw ce;
            }
        }
    }
    
    /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the
   * CPO meta parameters and the parameters from the dynamic where.
   * 
   */
    protected Collection<BindAttribute> getBindValues(JdbcQuery jq, Object obj) throws CpoException {
    Collection<BindAttribute> bindValues = new ArrayList<BindAttribute>();
    ArrayList<JdbcParameter> parameters = jq.getParameterList();
    JdbcParameter parameter = null;
    for (int j = 0; j < parameters.size(); j++) {
      parameter = (JdbcParameter) parameters.get(j);
      if (parameter == null) {
        throw new CpoException("JdbcParameter is null!");
      }
      bindValues.add(new BindAttribute(parameter.getAttribute(), obj));
    }
    return bindValues;
  }

  /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the
   * CPO meta parameters and the parameters from the dynamic where.
   * 
   */
  protected void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {

    if (bindValues != null) {
      int index=1;
      
      //runs through the bind attributes and binds them to the prepared statement
      // They must be in correct order.
      for (BindAttribute bindAttr : bindValues) {
        Object bindObject = bindAttr.getBindObject();
        JdbcAttribute ja = bindAttr.getJdbcAttribute();

        // check to see if we are getting a cpo value object or an object that
        // can be put directly in the statement (String, BigDecimal, etc)
        JavaSqlMethod<?> jsm = JavaSqlMethods.getJavaSqlMethod(bindObject.getClass());
        if (jsm != null) {
          try {
            if (ja == null)
              localLogger.info(bindAttr.getName() + "=" + bindObject);
            else
              localLogger.info(ja.getDbName() + "=" + bindObject);
            jsm.getPsSetter().invoke(this.getPreparedStatement(), new Object[] { index++, bindObject });
          } catch (IllegalAccessException iae) {
            localLogger.error("Error Accessing Prepared Statement Setter: " + iae.getLocalizedMessage());
            throw new CpoException(iae);
          } catch (InvocationTargetException ite) {
            localLogger.error("Error Invoking Prepared Statement Setter: " + ite.getCause().getLocalizedMessage());
            throw new CpoException(ite.getCause());
          }
        } else {
          ja.invokeGetter(this, bindObject, index++);
        }
      }
    }

  }


}

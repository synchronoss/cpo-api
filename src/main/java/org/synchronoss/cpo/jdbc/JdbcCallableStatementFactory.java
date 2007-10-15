/**
 * JdbcCallableStatementFactory.java    
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


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoReleasible;

/**
 * JdbcCallableStatementFactory is the object that encapsulates the creation of the actual
 * CallableStatement for the JDBC driver. 
 *
 * @author david berry
 */
public class JdbcCallableStatementFactory implements CpoReleasible {
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;

    /** DOCUMENT ME! */
    private static Logger logger=Logger.getLogger(JdbcCallableStatementFactory.class.getName());
    
    private CallableStatement cs_ = null;
    
    private JdbcCallableStatementFactory(){}
    
    private ArrayList<CpoReleasible> releasibles = new ArrayList<CpoReleasible>();
    
    private ArrayList<JdbcParameter> outParameters = new ArrayList<JdbcParameter>();
 

    /**
     * Used to build the CallableStatement that is used by CPO to create the 
     * actual JDBC CallableStatement.
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
    public JdbcCallableStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcQuery jq, Object obj) throws CpoException {
        CallableStatement cstmt = null;
        JdbcAttribute attribute = null;
        Logger localLogger = obj==null?logger:Logger.getLogger(obj.getClass().getName());
        
        try {
            outParameters=jq.getParameterList();

            localLogger.debug("SQL = <"+jq.getText()+">");

            // prepare the Callable Statement
            cstmt=conn.prepareCall(jq.getText());
            setCallableStatement(cstmt);
            
            int j=1;
            for(JdbcParameter parameter:outParameters) {
                attribute=parameter.getAttribute();

                if(parameter.isInParameter()) {
                    attribute.invokeGetter(this, obj, j);
                }

                if(parameter.isOutParameter()) {
                	localLogger.debug("Setting OUT parameter "+j+" as Type "+attribute.getJavaSqlType());
                    cstmt.registerOutParameter(j, attribute.getJavaSqlType());
                }
                j++;
            }
    
        } catch (Exception e){
        	localLogger.error("Error Instantiating JdbcCallableStatementFactory"+e.getLocalizedMessage());
            throw new CpoException(e);
        }

    }
    
    /**
     * returns the jdbc callable statment associated with this 
     * object
     */
    public CallableStatement getCallableStatement(){
        return cs_;
    }
    
    protected void setCallableStatement(CallableStatement cs){
        cs_= cs;
    }
    
    /**
     * returns the Out parameters from the callable statement
     * 
     */
    public ArrayList<JdbcParameter> getOutParameters(){
        return outParameters;
    }

    /**
     * Adds a releasible object to this object. The release method
     * on the releasible will be called when the callableStatement 
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
     public void release() throws CpoException {
        for (CpoReleasible releasible:releasibles){
            try{
            	releasible.release();
            } catch(CpoException ce) {
                logger.error("Error Releasing Callable Statement Transform Object",ce);
                throw ce;
            }
        }
    }

}

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
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoReleasible;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value
 * objects from a datasource.
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
    
    private ArrayList releasibles = new ArrayList();
    
    private ArrayList outParameters = new ArrayList();
 

    /**
     * DOCUMENT ME!
     *
     * @param conn DOCUMENT ME!
     * @param jca
     * @param jq DOCUMENT ME!
     * @param obj DOCUMENT ME!
     *
    * @throws CpoException DOCUMENT ME!
     * @throws SQLException DOCUMENT ME!
     */
    public JdbcCallableStatementFactory(Connection conn, JdbcCpoAdapter jca, JdbcQuery jq, Object obj) throws CpoException {
        CallableStatement cstmt = null;
        JdbcParameter parameter = null;
        JdbcAttribute attribute = null;
        
        try {
            outParameters=jq.getParameterList();

            logger.debug("SQL = <"+jq.getText()+">");

            // prepare the Callable Statement
            cstmt=conn.prepareCall(jq.getText());
            setCallableStatement(cstmt);

            for(int j=0; j<outParameters.size(); j++) {
                parameter=(JdbcParameter) outParameters.get(j);
                attribute=parameter.getAttribute();

                if(parameter.isInParameter()) {
                    attribute.invokeGetter(this, obj, j+1);
                }

                if(parameter.isOutParameter()) {
                    logger.debug("Setting OUT parameter "+j+" as Type "+attribute.getJavaSqlType());
                    cstmt.registerOutParameter(j+1, attribute.getJavaSqlType());
                }
            }
    
        } catch (Exception e){
            logger.error("Error Instantiating JdbcCallableStatementFactory"+e.getLocalizedMessage());
            throw new CpoException(e);
        }

    }
    
    public CallableStatement getCallableStatement(){
        return cs_;
    }
    public void setCallableStatement(CallableStatement cs){
        cs_= cs;
    }
    public ArrayList getOutParameters(){
        return outParameters;
    }

    public void AddReleasible(CpoReleasible releasible){
        if (releasible!=null)
            releasibles.add(releasible);
        
    }
    public void release() throws CpoException {
        Iterator it = releasibles.iterator();
        while (it.hasNext()){
            try{
                ((CpoReleasible)it.next()).release();
            } catch(CpoException ce) {
                logger.error("Error Releasing Callable Statement Transform Object",ce);
                throw ce;
            }
        }
    }

}

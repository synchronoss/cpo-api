/*
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
 */

package org.synchronoss.cpo.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;

public class JdbcCpoTrxAdapter extends JdbcCpoAdapter implements CpoTrxAdapter {
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;
    
    private JdbcCpoTrxAdapter(){}
    
    protected JdbcCpoTrxAdapter(DataSource metaSource, String metaSourceName, Connection c, 
                    boolean batchSupported) throws CpoException {
            super(metaSource, metaSourceName, c, batchSupported);
            // TODO Auto-generated constructor stub
    }

    public void commit() throws CpoException {
    	Connection writeConnection = getStaticConnection();
    	if (writeConnection!=null){
	    	try {
	    		writeConnection.commit();
	    	} catch (SQLException se) {
	    		throw new CpoException (se.getMessage());
	    	}
    	}else{
    		throw new CpoException ("Transaction Object has been Closed");
    	}
    }
    
    public void rollback() throws CpoException {
    	Connection writeConnection = getStaticConnection();
    	if (writeConnection!=null){
	    	try {
	    		writeConnection.rollback();
	    	} catch (Exception e) {
	    		throw new CpoException (e.getMessage());
	    	}
    	}else{
    		throw new CpoException ("Transaction Object has been Closed");
    	}
    }
    
    public void close() {
    	Connection writeConnection = getStaticConnection();
    	if (writeConnection != null) {
    		try {
    			writeConnection.rollback();
    		} catch (Exception e) {}
    		try {
    			writeConnection.close();
    			setStaticConnection(null);
    		} catch (Exception e) {}
    	}
    }
    
    /**
     * DOCUMENT ME!
     */
    protected void finalize() {
    	Connection writeConnection = getStaticConnection();

        try {
        	super.finalize();
        } catch (Throwable e) {}
        try {
        	if (writeConnection!=null && !writeConnection.isClosed()) {
        		this.rollback();
        	}
        } catch (Exception e) {}
        try {
        	if (writeConnection != null && !writeConnection.isClosed()) {
        		this.close();
        	}
        } catch (Exception e) {}
    }

}

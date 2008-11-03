/*
 *  OracleTemporaryClob.java
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
 */
package org.synchronoss.cpo.transform.jdbc;

import oracle.sql.CLOB;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoReleasible;

/**
 * @author david.berry
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OracleTemporaryClob implements CpoReleasible {
	private CLOB clob_=null;

  @SuppressWarnings("unused")
	private OracleTemporaryClob(){}
	
	public OracleTemporaryClob(CLOB clob){
		clob_=clob;
	}
	/* (non-Javadoc)
	 * @see org.synchronoss.cpo.CpoReleasible#release()
	 */
	public void release() throws CpoException {
		// TODO Auto-generated method stub
		try{
			if (clob_!=null){
				clob_.freeTemporary();
				clob_=null;
			}
		} catch (Exception e){
			throw new CpoException("Error releasing Oracle Temporary Blob",e);
		}
	}

}

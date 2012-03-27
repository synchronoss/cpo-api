/**
 * TransformTimestampToCalendar.java
 * 
 *  Copyright (C) 2006-  David E. Berry
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
 
package org.synchronoss.cpo.transform.jdbc;

import java.sql.Timestamp;
import java.util.Calendar;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;
import org.synchronoss.cpo.transform.Transform;

/**
 * This is an example of a transform that does nothing. It is used to test the mechanics 
 * of the transform logic within CPO.
 * 
 * @author david berry
 */

public class TransformTimestampToCalendar implements Transform<Timestamp, Calendar> {

    public TransformTimestampToCalendar(){}

    /**
     * Transforms the <code>java.sql.Timestamp</code> returned from JDBC into
		 * a <code>java.util.Calendar</code> to be used by the class.
     * 
     * @param ts The Timestamp from JDBC.
     * @return A Calendar Object
     * @throws CpoException
     */
    public Calendar transformIn(Timestamp ts)
    throws CpoException {
			Calendar cal = null;
      if (ts!=null){
				cal = Calendar.getInstance();
				cal.setTimeInMillis(ts.getTime());
			}
      return cal;
    }

    /**
     * Transforms a <code>java.util.Calendar</code> from the CPO Bean into
		 * a <code>java.sql.Timestamp</code> to be stored by JDBC
     *
     * @param jcsf a reference to the JdbcCallableStatementFactory. This is necessary as some
     *        DBMSs (ORACLE !#$%^&!) that require access to the connection to deal with certain 
     *        datatypes. 
     * @param A Calendar instance
     * @return A Timestamp object to be stored in the database.
     * @throws CpoException
     */
    public Timestamp transformOut(JdbcCallableStatementFactory jcsf, Calendar cal)
    throws CpoException {
			Timestamp ts = null;
			if (cal!=null){
				ts=new Timestamp(cal.getTimeInMillis());
			}
      return ts;
    }

    /**
     * Transforms a <code>java.util.Calendar</code> from the CPO Bean into
		 * a <code>java.sql.Timestamp</code> to be stored by JDBC
     *
     * @param jpsf a reference to the JdbcPreparedStatementFactory. This is necessary as some
     *        DBMSs (ORACLE !#$%^&!) that require access to the connection to deal with certain 
     *        datatypes. 
     * @param A Calendar instance
     * @return A Timestamp object to be stored in the database.
     * @throws CpoException
     */
    public Timestamp transformOut(JdbcPreparedStatementFactory jpsf, Calendar cal)
    throws CpoException {
			Timestamp ts = null;
			if (cal!=null){
				ts=new Timestamp(cal.getTimeInMillis());
			}
      return ts;
    }
}

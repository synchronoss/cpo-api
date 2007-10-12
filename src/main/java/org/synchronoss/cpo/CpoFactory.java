/**
 * CpoFactory.java
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
 * CpoFactory is an interface for a set of routines
 * that are responsible for instantiating a CpoAdapter to be used
 * by the CpoManagerBean.
 * 
 * This interface should be implemented by anyone wanting to use the CpoManagerBean.
 * It allows a person to implement the Type of CpoAdapter they want and handle all the 
 * arguments for Creating the CpoAdapter
 * 
 * The class that implements the CpoAdapter should be specified in the ejb-jar.xml that includes CpoManagerBean
 * i.e.
 * 
 * <?xml version = '1.0' encoding = 'UTF-8'?>
 * <!DOCTYPE ejb-jar PUBLIC "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN" "http://java.sun.com/j2ee/dtds/ejb-jar_1_1.dtd">
 * <ejb-jar>
 *    <enterprise-beans>
 *       <session>
 *           <description>Stateless Session Bean for Class Persistence</description>
 *           <display-name>CpoAdapter</display-name>
 *           <ejb-name>ejbCpoAdapter</ejb-name>
 *           <home>org.synchronoss.cpo.CpoAdapterHome</home>
 *           <remote>org.synchronoss.cpo.CpoAdapter</remote>
 *           <ejb-class>org.synchronoss.cpo.CpoAdapterBean</ejb-class>
 *           <session-type>Stateless</session-type>
 *           <transaction-type>Container</transaction-type>
 *           <env-entry>
 *               <description>The fully qualified path name of a class that has a public default constructor and implements the cpoFactory interface 
 *                </description>
 *               <env-entry-name>factoryClass</env-entry-name>
 *               <env-entry-type>java.lang.String</env-entry-type>
 *               <env-entry-value>org.synchronoss.cpo.jdbc.JdbcCpoFactory</env-entry-value>            
 *            </env-entry>
 *       </session>
 * 
 *    </enterprise-beans>
 * </ejb-jar>
 * 
 * @author david berry
 */

public interface CpoFactory {

    /**
     * Creates a new CpoAdapter.
     * 
     * This method is called by the CpoManagerBean to get the CpoAdapter that will
     * be used to implement its public methods. 
     * 
     */
    public CpoAdapter newCpoAdapter() throws CpoException;

}

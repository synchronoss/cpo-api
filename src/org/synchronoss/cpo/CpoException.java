/**
 * CpoException.java
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
 * A <code>CpoException</code> is the common superclass for any number of
 * CPO related exceptions that may occur during the execution of a
 * business task.
 * @author David E. Berry
 */
public class CpoException extends Exception  {

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Nested Exception to hold wrapped exception.
     *
     * @serial
     */
    public Throwable detail;

    /**
     * Constructs a <code>CpoException</code> with no specified
     * detail message.
     */
    public CpoException() {
    }

    /**
     * Constructs a <code>CpoException</code> with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public CpoException(String s) {
        super(s);
    }

    /**
     * Constructs a <code>CpoException</code> with the specified
     * detail message and nested exception.
     *
     * @param s the detail message
     * @param ex the nested exception
     */
    public CpoException(String s, Throwable ex) {
        super(s);
        detail = ex;
    }
    /**
     * Constructs a <code>CpoException</code> with the specified
     * detail message and nested exception.
     *
     * @param ex the nested exception
     */
    public CpoException(Throwable ex) {
        super();
        detail = ex;
    }
    /**
     * Returns the detail message, including the message from the nested
     * exception if there is one.
     */
    public String getMessage() {
        StringBuffer msg=new StringBuffer("\n");

        msg.append(super.getMessage());
        if(detail != null) {
            msg.append("\n");
            msg.append(detail.getMessage());
        }
        return msg.toString();
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified stream <code>ps</code>.
     * @param ps the print stream
     */
    public void printStackTrace(java.io.PrintStream ps) {
        synchronized(ps) {
            if(detail != null) {
                detail.printStackTrace(ps);
            }
            super.printStackTrace(ps);
        }
    }

    /**
     * Prints the composite message to <code>System.err</code>.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the composite message and the embedded stack trace to
     * the specified print writer <code>pw</code>.
     * @param pw the print writer
     */
    public void printStackTrace(java.io.PrintWriter pw) {
        synchronized(pw) {
            if(detail != null) {
                detail.printStackTrace(pw);
            }
            super.printStackTrace(pw);
        }
    }

}

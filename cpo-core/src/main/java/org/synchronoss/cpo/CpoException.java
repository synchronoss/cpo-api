package org.synchronoss.cpo;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * --------------------------------------------------------------------------]
 */

/**
 * A <code>CpoException</code> is the common superclass for any number of CPO related exceptions
 * that may occur during the execution of a business task.
 *
 * @author David E. Berry
 */
public class CpoException extends Exception {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /**
   * Nested Exception to hold wrapped exception.
   *
   * @serial
   */
  private Throwable detail;

  /** Constructs a <code>CpoException</code> with no specified detail message. */
  public CpoException() {}

  /**
   * Constructs a <code>CpoException</code> with the specified detail message.
   *
   * @param s the detail message
   */
  public CpoException(String s) {
    super(s);
  }

  /**
   * Constructs a <code>CpoException</code> with the specified detail message and nested exception.
   *
   * @param s the detail message
   * @param ex the nested exception
   */
  public CpoException(String s, Throwable ex) {
    super(s);
    detail = ex;
  }

  /**
   * Constructs a <code>CpoException</code> with the specified detail message and nested exception.
   *
   * @param ex the nested exception
   */
  public CpoException(Throwable ex) {
    super();
    detail = ex;
  }

  /**
   * Returns the detail message, including the message from the nested exception if there is one.
   */
  @Override
  public String getMessage() {
    StringBuilder msg = new StringBuilder("\n");

    msg.append(super.getMessage());

    if (detail != null) {
      msg.append("\n");
      msg.append(detail.getMessage());
      if (detail.getCause() != null) {
        msg.append(detail.getCause().getMessage());
      }
    }
    return msg.toString();
  }

  /**
   * Returns the detail message, including the message from the nested exception if there is one.
   */
  @Override
  public String getLocalizedMessage() {
    StringBuilder msg = new StringBuilder("\n");

    msg.append(super.getLocalizedMessage());

    if (detail != null) {
      msg.append("\n");
      msg.append(detail.getLocalizedMessage());
      if (detail.getCause() != null) {
        msg.append(detail.getCause().getLocalizedMessage());
      }
    }
    return msg.toString();
  }

  /**
   * Prints the composite message and the embedded stack trace to the specified stream <code>ps
   * </code>.
   *
   * @param ps the print stream
   */
  @Override
  public void printStackTrace(java.io.PrintStream ps) {
    synchronized (ps) {
      if (detail != null) {
        detail.printStackTrace(ps);
      }
      super.printStackTrace(ps);
    }
  }

  /** Prints the composite message to <code>System.err</code>. */
  @Override
  public void printStackTrace() {
    printStackTrace(System.err);
  }

  /**
   * Prints the composite message and the embedded stack trace to the specified print writer <code>
   * pw</code>.
   *
   * @param pw the print writer
   */
  @Override
  public void printStackTrace(java.io.PrintWriter pw) {
    synchronized (pw) {
      if (detail != null) {
        detail.printStackTrace(pw);
      }
      super.printStackTrace(pw);
    }
  }
}

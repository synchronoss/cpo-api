/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.meta.domain;

import org.slf4j.*;
import org.synchronoss.cpo.meta.bean.CpoFunctionBean;

import java.util.*;

public class CpoFunction extends CpoFunctionBean {

  private static final Logger logger = LoggerFactory.getLogger(CpoFunction.class);
  List<CpoArgument> arguments = new ArrayList<CpoArgument>();

  public CpoFunction() {
  }

  public List<CpoArgument> getArguments() {
    return arguments;
  }

  public void addArgument(CpoArgument argument) {
    if (argument != null) {
      arguments.add(argument);
    }
  }

  public boolean removeArgument(CpoArgument argument) {
    if (argument != null) {
      return arguments.remove(argument);
    }
    return false;
  }

  public boolean removeArgument(int index) {
    if (index > 0 && index < arguments.size()) {
      return (arguments.remove(index) != null);
    }
    return false;
  }

  /**
   * DOCUMENT ME!
   *
   * @param function DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  public String parameterToString(CpoFunction function) {
    List<CpoArgument> args;
    int j;
    CpoArgument argument;
    CpoAttribute attribute;
    int type = 0;
    Class<?> c;
    StringBuilder sb = new StringBuilder("Parameter list for ");

    if (function == null) {
      return " null function.";
    }

    // TODO: make uncomment the following line and make work
//    sb.append(jq.getName() + " " + jq.getType());
    args = function.getArguments();

    for (j = 1; j <= args.size(); j++) {
      argument = args.get(j - 1);

      if (argument != null) {
        try {
          attribute = argument.getAttribute();
          c = attribute.getGetter().getReturnType();
          // TODO: make uncomment the following line and make work
//          type = attribute.getJavaSqlType();
          if (c != null) {
            sb.append(" col" + j + ":" + c.getName() + " type:"
                    + type + " ");
          } else {
            sb.append(j + ":null type:" + type + " ");
          }
        } catch (Exception e) {
          String msg = "parameterToString() Failed:";
          logger.error(msg);
        }
      }
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public String toStringFull() {
    return super.toString();
  }
}

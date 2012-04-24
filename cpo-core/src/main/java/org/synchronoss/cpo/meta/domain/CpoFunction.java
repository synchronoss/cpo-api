/*
 *  Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.meta.domain;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.meta.bean.CpoFunctionBean;

public class CpoFunction extends CpoFunctionBean {

  private static Logger logger = LoggerFactory.getLogger(CpoFunction.class.getName());
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

  /**
   * DOCUMENT ME!
   *
   * @param jq DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  public String parameterToString(CpoFunction jq) {
    List<CpoArgument> arguments;
    int j;
    CpoArgument argument;
    CpoAttribute attribute;
    int type = 0;
    Class<?> c;
    StringBuilder sb = new StringBuilder("Parameter list for ");

    if (jq == null) {
      return " null query.";
    }

    // TODO: make uncomment the following line and make work
//    sb.append(jq.getName() + " " + jq.getType());
    arguments = jq.getArguments();

    for (j = 1; j <= arguments.size(); j++) {
      argument = arguments.get(j - 1);

      if (argument != null) {
        try {
          attribute = argument.getAttribute();
          c = attribute.getGetters().get(0).getReturnType();
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
}

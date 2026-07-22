package org.synchronoss.cpo.core.meta.domain;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
 * ==
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
 * ]]
 */

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.meta.bean.CpoFunctionBean;

/**
 * Runtime metadata for a single CPO function: extends {@link CpoFunctionBean}'s name/expression/
 * description with the ordered list of {@link CpoArgument}s bound to the function's positional bind
 * markers.
 *
 * @author dberry
 */
public class CpoFunction extends CpoFunctionBean {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoFunction.class);

  /** The arguments bound to this function's bind markers, in bind-marker order. */
  List<CpoArgument> arguments = new ArrayList<>();

  /** Creates an empty instance. */
  public CpoFunction() {}

  /**
   * Gets the arguments bound to this function's bind markers, in bind-marker order.
   *
   * @return the bound arguments
   */
  public List<CpoArgument> getArguments() {
    return arguments;
  }

  /**
   * Appends an argument to this function's argument list. A no-op if {@code argument} is {@code
   * null}.
   *
   * @param argument the argument to add
   */
  public void addArgument(CpoArgument argument) {
    if (argument != null) {
      arguments.add(argument);
    }
  }

  /**
   * Removes an argument from this function's argument list.
   *
   * @param argument the argument to remove
   * @return {@code true} if the argument was found and removed, {@code false} otherwise
   */
  public boolean removeArgument(CpoArgument argument) {
    if (argument != null) {
      return arguments.remove(argument);
    }
    return false;
  }

  /**
   * Removes the argument at the given position in this function's argument list.
   *
   * @param index the position of the argument to remove
   * @return {@code true} if {@code index} was in range and the argument was removed, {@code false}
   *     otherwise
   */
  public boolean removeArgument(int index) {
    if (index >= 0 && index < arguments.size()) {
      return arguments.remove(index) != null;
    }
    return false;
  }

  /**
   * Builds a diagnostic string describing {@code function}'s argument list: each argument's bound
   * attribute Java type, for debugging/logging purposes.
   *
   * @param function the function whose arguments should be described, may be {@code null}
   * @return a diagnostic description of the argument list, or {@code " null function."} if {@code
   *     function} is {@code null}
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

    // TODO make uncomment the following line and make work
    //    sb.append(jq.getName() + " " + jq.getType());
    args = function.getArguments();

    for (j = 1; j <= args.size(); j++) {
      argument = args.get(j - 1);

      if (argument != null) {
        try {
          attribute = argument.getAttribute();
          c = attribute.getGetter().getReturnType();
          // TODO make uncomment the following line and make work
          //          type = attribute.getJavaSqlType();
          if (c != null) {
            sb.append(" col" + j + ":" + c.getName() + " type:" + type + " ");
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

  /**
   * {@inheritDoc}
   *
   * <p>Returns this function's {@link #getName() name}.
   */
  @Override
  public String toString() {
    return this.getName();
  }

  /**
   * Gets the full field-by-field string representation of this function, as produced by {@link
   * CpoFunctionBean#toString()}.
   *
   * @return the full string representation
   */
  public String toStringFull() {
    return super.toString();
  }
}

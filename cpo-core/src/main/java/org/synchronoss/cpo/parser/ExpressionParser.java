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
package org.synchronoss.cpo.parser;

import java.text.ParseException;
import java.util.List;

/**
 * @author Michael Bellomo
 */
public interface ExpressionParser {

  /**
   * Returns the expression used by this parser
   *
   * @return The expression
   */
  public String getExpression();

  /**
   * Sets the expression to be used for this parser
   *
   * @param expression The expression
   */
  public void setExpression(String expression);

  /**
   * Returns the count of the bind markers in the expression
   *
   * @return the number of bind markers
   */
  public int countArguments();

  /**
   * Returns a list of columns from the expression for each bind marker
   *
   * @return List of Strings for the columns for the bind markers
   * @throws ParseException thrown if the expression cannot be parsed
   */
  public List<String> parse() throws ParseException;
}

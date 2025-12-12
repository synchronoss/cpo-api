package org.synchronoss.cpo.core.parser;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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
  String getExpression();

  /**
   * Sets the expression to be used for this parser
   *
   * @param expression The expression
   */
  void setExpression(String expression);

  /**
   * Returns the count of the bind markers in the expression
   *
   * @return the number of bind markers
   */
  int countArguments();

  /**
   * Returns a list of columns from the expression for each bind marker
   *
   * @return List of Strings for the columns for the bind markers
   * @throws ParseException thrown if the expression cannot be parsed
   */
  List<String> parse() throws ParseException;
}

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

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Bellomo
 * @since 10/20/2008
 */
public class BoundExpressionParser implements ExpressionParser {

  private static final Logger logger = LoggerFactory.getLogger(BoundExpressionParser.class);

  private static final String COMPARE_CHARS = " =<>!()";
  private static final String SEPARATOR_CHARS = " .,()\n";

  private String expression;

  public BoundExpressionParser() {}

  @Override
  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  /**
   * Returns the count of the bind markers in the expression
   *
   * @return the number of bind markers
   */
  @Override
  public int countArguments() {
    return getBindMarkerIndexes(expression).size();
  }

  public static Collection<Integer> getBindMarkerIndexes(String source) {
    Collection<Integer> indexes = new ArrayList<>();

    if (source == null || source.length() == 0) return indexes;

    StringReader reader = new StringReader(source);

    try {

      int idx = 0;
      int rc;
      boolean inDoubleQuotes = false;
      boolean inSingleQuotes = false;

      do {
        rc = reader.read();
        if (((char) rc) == '\'') {
          inSingleQuotes = !inSingleQuotes;
        } else if (((char) rc) == '"') {
          inDoubleQuotes = !inDoubleQuotes;
        } else if (!inSingleQuotes && !inDoubleQuotes && ((char) rc) == '?') {
          indexes.add(idx);
        }
        idx++;
      } while (rc != -1);
    } catch (Exception e) {
      logger.error("error counting bind markers");
    }
    return indexes;
  }

  /**
   * Returns a list of columns from the expression for each bind marker
   *
   * @return List of Strings for the columns for the bind markers
   * @throws ParseException thrown if the expression cannot be parsed
   */
  @Override
  public List<String> parse() throws ParseException {

    if (expression == null) throw new ParseException("The expression is null", -1);

    if (logger.isDebugEnabled()) logger.debug("Expression: " + expression);

    // expression is empty, nothing we can do
    if (expression.length() < 1) return null;

    // no question marks, nothing to do
    if (!expression.contains("?")) {
      return null;
    }

    // upper case the expression, to make things easier
    expression = expression.toUpperCase();

    List<String> colList = new ArrayList<>();

    if (expression.startsWith("INSERT")) {
      // expression is in the format of:  insert into table(col1, col2...) values(val1, val2...)
      // so we'll use the parens () to parse

      int colParenStart = expression.indexOf("(");
      if (colParenStart == -1)
        throw new ParseException("Unable to locate starting parenthesis for the column names.", -1);

      int colParenEnd = expression.indexOf(")", colParenStart);
      if (colParenEnd == -1)
        throw new ParseException("Unable to locate ending parenthesis for the column names.", -1);

      int valParenStart = expression.indexOf("(", colParenEnd);
      if (valParenStart == -1)
        throw new ParseException(
            "Unable to locate starting parenthesis for the column values.", -1);

      // use the last close paren, this will make weird inner select stuff work,
      // but it won't be able to guess the inner select values
      int valParenEnd = expression.lastIndexOf(")");
      if (valParenEnd == -1)
        throw new ParseException("Unable to locate ending parenthesis for the column values.", -1);

      String[] cols = expression.substring(colParenStart + 1, colParenEnd).split(",");
      String[] vals = expression.substring(valParenStart + 1, valParenEnd).split(",");

      // if cols or vals is null, it means we couldn't find any
      if (cols == null || vals == null) return null;

      if (logger.isDebugEnabled()) {
        logger.debug("Found cols: " + cols.length);
        logger.debug("Found vals: " + vals.length);
      }

      if (cols.length != vals.length)
        throw new ParseException(
            "You seem to have "
                + cols.length
                + " columns, and "
                + vals.length
                + " values.\n\nThose numbers should be equal.",
            -1);

      // filter out columns that we're not providing values for
      for (int i = 0; i < vals.length; i++) {
        String val = vals[i];
        if (val.trim().equals("?")) {
          // just a ?, use the col
          colList.add(cols[i].trim());
        } else if (val.contains("?")) {
          // more than just a ?, parse the val
          ExpressionParser qp = new BoundExpressionParser();
          qp.setExpression(val);
          colList.addAll(qp.parse());
        }
      }
    } else {
      // expression is in the format of:  ...col1 = ? , col2 = ?...
      // so we'll have to move left to right from the ? looking for the field name

      int startIdx = 0;
      Collection<Integer> indexes = getBindMarkerIndexes(expression);
      for (int qIdx : indexes) {
        String chunk = expression.substring(startIdx, qIdx);

        if (logger.isDebugEnabled()) logger.debug("Chunk [" + chunk + "]");

        int idx = chunk.length() - 1;
        int fieldStartIdx = -1;
        int fieldEndIdx = -1;

        boolean found = false;
        boolean inFunction = false;
        while (!found && (idx >= 0)) {
          char c = chunk.charAt(idx);

          if (fieldEndIdx == -1) {
            // if the character is a ( this might be a function like UPPER(), try to parse that out
            if (!inFunction && c == '(') {
              inFunction = true;
            } else if (inFunction && COMPARE_CHARS.indexOf(c) != -1) {
              inFunction = false;
            }

            // till we find the first char of the end of the field name, ignore compare chars
            if (!inFunction && COMPARE_CHARS.indexOf(c) == -1) {
              // found a char, must be the end of the field name
              fieldEndIdx = idx;
            }
          } else {
            // if we find a separator, we've reached the beginning of the field name
            if (SEPARATOR_CHARS.indexOf(c) >= 0) {
              fieldStartIdx = idx + 1;
              found = true;
            }
          }
          idx--;
        }
        if (found) {
          String col = chunk.substring(fieldStartIdx, fieldEndIdx + 1);
          colList.add(col);
        }

        // move the starting index to where the ? was
        startIdx = qIdx + 1;
      }
    }

    return colList;
  }
}

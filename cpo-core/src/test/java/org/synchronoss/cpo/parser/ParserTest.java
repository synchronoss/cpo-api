package org.synchronoss.cpo.parser;

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

import static org.testng.Assert.*;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import org.synchronoss.cpo.core.parser.BoundExpressionParser;
import org.synchronoss.cpo.core.parser.ExpressionParser;
import org.testng.annotations.Test;

public class ParserTest {

  @Test
  public void testNullExpression() {
    ExpressionParser parser = new BoundExpressionParser();
    assertEquals(null, parser.getExpression(), "Expression should be null before setting");
    assertEquals(0, parser.countArguments(), "There should be no arguments for a null expression");
    boolean raisedException = false;
    try {
      assertEquals(0, parser.parse().size(), "There should be no columns for a null expression");
    } catch (ParseException ex) {
      raisedException = true;
    }
    assertTrue(raisedException, "Expected an exception to be raised");
  }

  @Test
  public void testEmptyExpression() {
    ExpressionParser parser = new BoundExpressionParser();
    parser.setExpression("");
    assertEquals("", parser.getExpression(), "Expression should be empty");
    assertEquals(
        0, parser.countArguments(), "There should be no arguments for an empty expression");
    try {
      assertEquals(null, parser.parse(), "There should be no columns for an empty expression");
    } catch (ParseException ex) {
      fail("ExpressionParser.parse threw an exception:" + ex.getLocalizedMessage());
    }
  }

  @Test
  public void testExpressionParse() {
    validateParseExpression(
        "insert into user (firstname,lastname) values (?,?);",
        2,
        Arrays.asList("FIRSTNAME", "LASTNAME"));
    validateParseExpression(
        "update user set firstname=?,lastname=? where ssn=?;",
        3,
        Arrays.asList("FIRSTNAME", "LASTNAME", "SSN"));
    validateParseExpression(
        "select firstname,lastname from user where ssn=?;", 1, Arrays.asList("SSN"));
  }

  public void validateParseExpression(String expression, int arguments, List<String> columns) {
    ExpressionParser parser = new BoundExpressionParser();
    parser.setExpression(expression);
    assertEquals(
        expression, parser.getExpression(), "getExpression did not return the set Expression");
    assertEquals(arguments, parser.countArguments(), "the number of aguments is not correct");
    try {
      List<String> parsedColumns = parser.parse();
      assertEquals(columns.size(), parsedColumns.size(), "Number of columns did not match");
      for (int i = 0; i < columns.size(); i++) {
        assertEquals(columns.get(i), parsedColumns.get(i), "Columns do not match");
      }
    } catch (ParseException ex) {
      fail("ExpressionParser.parse threw an exception:" + ex.getLocalizedMessage());
    }
  }
}

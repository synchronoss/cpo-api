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

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class ParserTest extends TestCase {
  public void testNullExpression() {
    ExpressionParser parser = new BoundExpressionParser();
    assertEquals("Expression should be null before setting", null, parser.getExpression());
    assertEquals("There should be no arguments for a null expression", 0, parser.countArguments());
    boolean raisedException = false;
    try {
      assertEquals("There should be no columns for a null expression", 0, parser.parse().size());
    } catch (ParseException ex) {
      raisedException=true;
    }
    assertTrue("Expected an exception to be raised", raisedException);
  }

  public void testEmptyExpression() {
    ExpressionParser parser = new BoundExpressionParser();
    parser.setExpression("");
    assertEquals("Expression should be empty", "", parser.getExpression());
    assertEquals("There should be no arguments for an empty expression", 0, parser.countArguments());
    try {
      assertEquals("There should be no columns for an empty expression", null, parser.parse());
    } catch (ParseException ex) {
      fail("ExpressionParser.parse threw an exception:"+ex.getLocalizedMessage());
    }
  }

  public void testExpressionParse(){
    validateParseExpression("insert into user (firstname,lastname) values (?,?);", 2, Arrays.asList("FIRSTNAME", "LASTNAME"));
    validateParseExpression("update user set firstname=?,lastname=? where ssn=?;", 3, Arrays.asList("FIRSTNAME", "LASTNAME", "SSN"));
    validateParseExpression("select firstname,lastname from user where ssn=?;", 1, Arrays.asList("SSN"));
  }

  public void validateParseExpression(String expression, int arguments, List<String> columns) {
    ExpressionParser parser = new BoundExpressionParser();
    parser.setExpression(expression);
    assertEquals("getExpression did not return the set Expression", expression, parser.getExpression());
    assertEquals("the number of aguments is not correct", arguments, parser.countArguments());
    try {
      List<String> parsedColumns = parser.parse();
      assertEquals("Number of columns did not match", columns.size(), parsedColumns.size());
      for (int i = 0; i < columns.size(); i++) {
        assertEquals("Columns do not match", columns.get(i), parsedColumns.get(i));
      }
    } catch (ParseException ex) {
      fail("ExpressionParser.parse threw an exception:"+ex.getLocalizedMessage());
    }
  }

}

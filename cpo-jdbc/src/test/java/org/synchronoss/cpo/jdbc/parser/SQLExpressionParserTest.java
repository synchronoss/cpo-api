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
package org.synchronoss.cpo.jdbc.parser;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.List;

// TODO - add more junits for single quotes, double quotes, inner selects, etc
public class SQLExpressionParserTest extends TestCase {

  public void testSelect() {
    try {
      String query = "select * from table where a = ? and b = ? and c = ? and d = '0'";
      SQLExpressionParser parser = new SQLExpressionParser();
      parser.setExpression(query);
      List<String> colList = parser.parse();

      assertTrue(colList.size() == 3);
      assertTrue(colList.get(0).equals("A"));
      assertTrue(colList.get(1).equals("B"));
      assertTrue(colList.get(2).equals("C"));

    } catch (ParseException ex) {
      fail(ex.getMessage());
    }
  }

  public void testSelectWithFunction() {
    try {
      String query = "select * from table where a = ? and UPPER(b) = ? and c = ? and d = '0'";
      SQLExpressionParser parser = new SQLExpressionParser();
      parser.setExpression(query);
      List<String> colList = parser.parse();

      assertTrue(colList.size() == 3);
      assertTrue(colList.get(0).equals("A"));
      assertTrue(colList.get(1).equals("B"));
      assertTrue(colList.get(2).equals("C"));

    } catch (ParseException ex) {
      fail(ex.getMessage());
    }
  }

  public void testInsert() {
    try {
      String query = "insert into table(a, b, c, d) values(?, ?, ?, SYSDATE)";
      SQLExpressionParser parser = new SQLExpressionParser();
      parser.setExpression(query);
      List<String> colList = parser.parse();

      assertTrue(colList.size() == 3);
      assertTrue(colList.get(0).equals("A"));
      assertTrue(colList.get(1).equals("B"));
      assertTrue(colList.get(2).equals("C"));

    } catch (ParseException ex) {
      fail(ex.getMessage());
    }
  }
}

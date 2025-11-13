/*
 * Copyright (C) 2003-2025 David E. Berry
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

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.text.ParseException;
import java.util.List;
import org.synchronoss.cpo.parser.BoundExpressionParser;
import org.testng.annotations.Test;

// TODO - add more units for single quotes, double quotes, inner selects, etc
public class BoundExpressionParserTest {

  @Test
  public void testSelect() {
    try {
      String query = "select * from table where a = ? and b = ? and c = ? and d = '0'";
      BoundExpressionParser parser = new BoundExpressionParser();
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

  @Test
  public void testSelectWithFunction() {
    try {
      String query = "select * from table where a = ? and UPPER(b) = ? and c = ? and d = '0'";
      BoundExpressionParser parser = new BoundExpressionParser();
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

  @Test
  public void testInsert() {
    try {
      String query = "insert into table(a, b, c, d) values(?, ?, ?, SYSDATE)";
      BoundExpressionParser parser = new BoundExpressionParser();
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

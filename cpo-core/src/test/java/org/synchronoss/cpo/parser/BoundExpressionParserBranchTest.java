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
import java.util.List;
import org.synchronoss.cpo.core.parser.BoundExpressionParser;
import org.testng.annotations.Test;

/** Branch-focused tests for BoundExpressionParser quoting, insert parsing, and field scans. */
public class BoundExpressionParserBranchTest {

  private BoundExpressionParser parser(String expression) {
    BoundExpressionParser parser = new BoundExpressionParser();
    parser.setExpression(expression);
    return parser;
  }

  @Test
  public void testDoubleQuotedMarkersIgnored() {
    assertEquals(
        BoundExpressionParser.getBindMarkerIndexes("select * from t where a = \"?\" and b = ?")
            .size(),
        1,
        "a ? inside double quotes is not a bind marker");
    assertEquals(
        BoundExpressionParser.getBindMarkerIndexes("select * from t where a = '?' and b = ?")
            .size(),
        1,
        "a ? inside single quotes is not a bind marker");
  }

  @Test
  public void testNoMarkersReturnsNull() throws Exception {
    assertNull(parser("select * from t").parse(), "no ? means nothing to parse");
  }

  @Test
  public void testInsertHappyPath() throws Exception {
    List<String> cols = parser("insert into t (a, b) values (?, ?)").parse();
    assertEquals(cols, List.of("A", "B"));
  }

  @Test
  public void testInsertWithLiteralAndFunctionValues() throws Exception {
    // the literal contributes nothing, and the function-wrapped marker parses to no column
    List<String> cols = parser("insert into t (a, b, c) values (?, 'x', trim(?))").parse();
    assertEquals(cols, List.of("A"), cols.toString());
  }

  @Test
  public void testInsertMissingParens() {
    expectThrows(ParseException.class, () -> parser("insert into t values ?").parse());
    expectThrows(ParseException.class, () -> parser("insert into t (a, b ?").parse());
    expectThrows(ParseException.class, () -> parser("insert into t (a, b) ?").parse());
  }

  @Test
  public void testInsertColumnValueCountMismatch() {
    expectThrows(ParseException.class, () -> parser("insert into t (a, b) values (?)").parse());
  }

  @Test
  public void testWhereClauseFieldScan() throws Exception {
    List<String> cols = parser("update t set a = ?, b = ? where c = ?").parse();
    assertEquals(cols, List.of("A", "B", "C"));
  }

  @Test
  public void testWhereClauseWithFunction() throws Exception {
    List<String> cols = parser("select * from t where upper(name) = ?").parse();
    assertEquals(cols.size(), 1, cols.toString());
  }

  @Test
  public void testBareMarkerFindsNoField() throws Exception {
    assertTrue(parser("?").parse().isEmpty(), "a bare ? has no field name to find");
  }

  @Test
  public void testCountArguments() {
    assertEquals(parser("insert into t (a, b) values (?, ?)").countArguments(), 2);
    assertEquals(parser("select 1").countArguments(), 0);
  }
}

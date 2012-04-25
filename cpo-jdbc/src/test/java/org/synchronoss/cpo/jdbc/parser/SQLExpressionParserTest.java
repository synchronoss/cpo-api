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

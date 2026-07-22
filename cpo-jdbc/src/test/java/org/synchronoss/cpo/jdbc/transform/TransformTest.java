package org.synchronoss.cpo.jdbc.transform;

/*-
 * [[
 * jdbc
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

import static org.testng.Assert.*;

import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Calendar;
import javax.sql.rowset.serial.SerialClob;
import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/** Direct unit tests for the built-in JdbcCpoTransform implementations. */
public class TransformTest {

  // ---------- TransformClob ----------

  @Test
  public void testClobRoundTrip() throws Exception {
    TransformClob transform = new TransformClob();
    char[] chars = "Some Clob Content".toCharArray();

    Clob clob =
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, chars);
    assertNotNull(clob, "transformOut should create a Clob");
    assertEquals(transform.transformIn(clob), chars, "round trip should preserve content");
  }

  @Test
  public void testClobNulls() throws Exception {
    TransformClob transform = new TransformClob();
    assertNull(transform.transformIn(null), "null Clob should transform to null");
    assertNull(
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, null),
        "null char[] should transform to null");
  }

  @Test
  public void testClobErrorPath() {
    TransformClob transform = new TransformClob();
    // a Clob whose character stream cannot be read must surface a CpoException
    Clob badClob =
        (Clob)
            java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {Clob.class},
                (proxy, method, args) -> {
                  throw new java.sql.SQLException("bad clob");
                });
    expectThrows(CpoException.class, () -> transform.transformIn(badClob));
  }

  @Test
  public void testClobUnsupportedOverloads() {
    TransformClob transform = new TransformClob();
    char[] chars = "x".toCharArray();
    expectThrows(
        UnsupportedOperationException.class,
        () ->
            transform.transformOut(
                (org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory) null, chars));
    expectThrows(UnsupportedOperationException.class, () -> transform.transformOut(chars));
  }

  // ---------- TransformCharArray ----------

  @Test
  public void testCharArrayRoundTrip() throws Exception {
    TransformCharArray transform = new TransformCharArray();
    char[] chars = "char array content".toCharArray();

    String out =
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, chars);
    assertEquals(out, "char array content");
    assertEquals(transform.transformIn(out), chars, "round trip should preserve content");
  }

  @Test
  public void testCharArrayNulls() throws Exception {
    TransformCharArray transform = new TransformCharArray();
    assertNull(transform.transformIn(null), "null String should transform to null");
    assertNull(
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, null),
        "null char[] should transform to null");
  }

  @Test
  public void testCharArrayUnsupportedOverloads() {
    TransformCharArray transform = new TransformCharArray();
    char[] chars = "x".toCharArray();
    expectThrows(
        UnsupportedOperationException.class,
        () ->
            transform.transformOut(
                (org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory) null, chars));
    expectThrows(UnsupportedOperationException.class, () -> transform.transformOut(chars));
  }

  // ---------- TransformTimestampToCalendar ----------

  @Test
  public void testTimestampToCalendarRoundTrip() throws Exception {
    TransformTimestampToCalendar transform = new TransformTimestampToCalendar();
    Timestamp ts = new Timestamp(1234567890123L);

    Calendar cal = transform.transformIn(ts);
    assertNotNull(cal, "Timestamp should transform to a Calendar");
    assertEquals(cal.getTimeInMillis(), 1234567890123L);

    Timestamp preparedTs =
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, cal);
    assertEquals(preparedTs.getTime(), 1234567890123L);

    Timestamp callableTs =
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory) null, cal);
    assertEquals(callableTs.getTime(), 1234567890123L);
  }

  @Test
  public void testTimestampToCalendarNulls() throws Exception {
    TransformTimestampToCalendar transform = new TransformTimestampToCalendar();
    assertNull(transform.transformIn(null), "null Timestamp should transform to null");
    assertNull(
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, null));
    assertNull(
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory) null, null));
  }

  @Test
  public void testTimestampToCalendarUnsupportedOverload() {
    TransformTimestampToCalendar transform = new TransformTimestampToCalendar();
    expectThrows(
        UnsupportedOperationException.class, () -> transform.transformOut(Calendar.getInstance()));
  }

  // ---------- TransformGZipBytes ----------

  @Test
  public void testGZipBytesRoundTrip() throws Exception {
    TransformGZipBytes transform = new TransformGZipBytes();
    byte[] payload = "payload worth gzipping, payload worth gzipping".getBytes();

    byte[] zipped =
        transform.transformOut(
            (org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, payload);
    assertNotNull(zipped, "gzip output should not be null");
    assertEquals(transform.transformIn(zipped), payload, "round trip should preserve content");
  }

  @Test
  public void testGZipBytesEmptyAndNull() throws Exception {
    TransformGZipBytes transform = new TransformGZipBytes();
    assertNull(transform.transformIn(null), "null bytes in should transform to null");
    assertEquals(transform.transformIn(new byte[0]), new byte[0]);
    assertNull(
        transform.transformOut((org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, null));
    assertEquals(
        transform.transformOut(
            (org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory) null, new byte[0]),
        new byte[0]);
  }

  @Test
  public void testGZipBytesErrorPath() {
    TransformGZipBytes transform = new TransformGZipBytes();
    // bytes that are not a gzip stream must surface a CpoException
    expectThrows(CpoException.class, () -> transform.transformIn("not gzipped".getBytes()));
  }

  @Test
  public void testGZipBytesUnsupportedOverloads() {
    TransformGZipBytes transform = new TransformGZipBytes();
    byte[] bytes = "x".getBytes();
    expectThrows(
        UnsupportedOperationException.class,
        () ->
            transform.transformOut(
                (org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory) null, bytes));
    expectThrows(UnsupportedOperationException.class, () -> transform.transformOut(bytes));
  }

  // ---------- sanity: SerialClob interop ----------

  @Test
  public void testClobFromSerialClob() throws Exception {
    TransformClob transform = new TransformClob();
    char[] chars = "serial clob".toCharArray();
    assertEquals(transform.transformIn(new SerialClob(chars)), chars);
  }
}

package org.synchronoss.cpo.jta;

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

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;

/** Created by dberry on 8/9/15. */
public class StringBuilderXaResource extends CpoBaseXaResource<StringBuilder> {
  private static final Logger logger = LoggerFactory.getLogger(StringBuilderXaResource.class);

  public StringBuilderXaResource() {
    super(new StringBuilder());
  }

  @Override
  protected void prepareResource(StringBuilder xaResource) throws XAException {}

  @Override
  protected void commitResource(StringBuilder xaResource) throws XAException {}

  @Override
  protected void rollbackResource(StringBuilder xaResource) throws XAException {}

  @Override
  protected StringBuilder createNewResource() throws XAException {
    return new StringBuilder();
  }

  @Override
  protected void closeResource(StringBuilder xaResource) throws XAException {}

  /**
   * This method is called to determine if the resource manager instance represented by the target
   * object is the same as the resouce manager instance represented by the parameter xares.
   *
   * @param xaResource - An XAResource object whose resource manager instance is to be compared with
   *     the resource manager instance of the target object.
   * @return - true if it's the same RM instance; otherwise false.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR and
   *     XAER_RMFAIL.
   */
  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    if (xaResource == null) throw new XAException(XAException.XAER_INVAL);

    return xaResource instanceof StringBuilderXaResource && this.equals(xaResource);
  }

  // StringBuilder Proxy methods

  public StringBuilderXaResource append(Object obj) {
    try {
      accept((sb) -> sb.append(obj));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(String str) {
    try {
      accept((sb) -> sb.append(str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * Appends the specified {@code StringBuffer} to this sequence.
   *
   * <p>The characters of the {@code StringBuffer} argument are appended, in order, to this
   * sequence, increasing the length of this sequence by the length of the argument. If {@code sb}
   * is {@code null}, then the four characters {@code "null"} are appended to this sequence.
   *
   * <p>Let <i>n</i> be the length of this character sequence just prior to execution of the {@code
   * append} method. Then the character at index <i>k</i> in the new character sequence is equal to
   * the character at index <i>k</i> in the old character sequence, if <i>k</i> is less than
   * <i>n</i>; otherwise, it is equal to the character at index <i>k-n</i> in the argument {@code
   * sb}.
   *
   * @param stringBuffer the {@code StringBuffer} to append.
   * @return a reference to this object.
   */
  public StringBuilderXaResource append(StringBuffer stringBuffer) {
    try {
      accept((sb) -> sb.append(stringBuffer));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(CharSequence s) {
    try {
      accept((sb) -> sb.append(s));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource append(CharSequence s, int start, int end) {
    try {
      accept((sb) -> sb.append(s, start, end));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(char[] str) {
    try {
      accept((sb) -> sb.append(str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource append(char[] str, int offset, int len) {
    try {
      accept((sb) -> sb.append(str, offset, len));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(boolean b) {
    try {
      accept((sb) -> sb.append(b));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(char c) {
    try {
      accept((sb) -> sb.append(c));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(int i) {
    try {
      accept((sb) -> sb.append(i));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(long lng) {
    try {
      accept((sb) -> sb.append(lng));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(float f) {
    try {
      accept((sb) -> sb.append(f));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public StringBuilderXaResource append(double d) {
    try {
      accept((sb) -> sb.append(d));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @since 1.5
   */
  public StringBuilderXaResource appendCodePoint(int codePoint) {
    try {
      accept((sb) -> sb.appendCodePoint(codePoint));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource delete(int start, int end) {
    try {
      accept((sb) -> sb.delete(start, end));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource deleteCharAt(int index) {
    try {
      accept((sb) -> sb.deleteCharAt(index));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource replace(int start, int end, String str) {
    try {
      accept((sb) -> sb.replace(start, end, str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int index, char[] str, int offset, int len) {
    try {
      accept((sb) -> sb.insert(index, str, offset, len));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, Object obj) {
    try {
      accept((sb) -> sb.insert(offset, obj));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, String str) {
    try {
      accept((sb) -> sb.insert(offset, str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, char[] str) {
    try {
      accept((sb) -> sb.insert(offset, str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int dstOffset, CharSequence s) {
    try {
      accept((sb) -> sb.insert(dstOffset, s));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int dstOffset, CharSequence s, int start, int end) {
    try {
      accept((sb) -> sb.insert(dstOffset, s, start, end));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, boolean b) {
    try {
      accept((sb) -> sb.insert(offset, b));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, char c) {
    try {
      accept((sb) -> sb.insert(offset, c));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, int i) {
    try {
      accept((sb) -> sb.insert(offset, i));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, long l) {
    try {
      accept((sb) -> sb.insert(offset, l));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, float f) {
    try {
      accept((sb) -> sb.insert(offset, f));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilderXaResource insert(int offset, double d) {
    try {
      accept((sb) -> sb.insert(offset, d));
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  public int indexOf(String str) {
    try {
      return apply((sb) -> sb.indexOf(str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return -1;
    }
  }

  public int indexOf(String str, int fromIndex) {
    try {
      return apply((sb) -> sb.indexOf(str, fromIndex));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return -1;
    }
  }

  public int lastIndexOf(String str) {
    try {
      return apply((sb) -> sb.lastIndexOf(str));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return -1;
    }
  }

  public int lastIndexOf(String str, int fromIndex) {
    try {
      return apply((sb) -> sb.lastIndexOf(str, fromIndex));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return -1;
    }
  }

  public StringBuilderXaResource reverse() {
    try {
      accept(StringBuilder::reverse);
    } catch (CpoException e) {
      logger.error("Error appending", e);
    }
    return this;
  }

  @Override
  public String toString() {
    // Create a copy, don't share the array
    try {
      return apply(StringBuilder::toString);
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return "";
    }
  }

  /**
   * Returns the length of this character sequence. The length is the number of 16-bit <code>char
   * </code>s in the sequence.
   *
   * @return the number of <code>char</code>s in this sequence
   */
  int length() {
    try {
      return apply(StringBuilder::length);
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return 0;
    }
  }

  /**
   * Returns the <code>char</code> value at the specified index. An index ranges from zero to <code>
   * length() - 1</code>. The first <code>char</code> value of the sequence is at index zero, the
   * next at index one, and so on, as for array indexing.
   *
   * <p>If the <code>char</code> value specified by the index is a <a
   * href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the surrogate value is
   * returned.
   *
   * @param index the index of the <code>char</code> value to be returned
   * @return the specified <code>char</code> value
   * @throws IndexOutOfBoundsException if the <code>index</code> argument is negative or not less
   *     than <code>length()</code>
   */
  char charAt(int index) {
    try {
      return apply((sb) -> sb.charAt(index));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return Character.UNASSIGNED;
    }
  }

  /**
   * Returns a <code>CharSequence</code> that is a subsequence of this sequence. The subsequence
   * starts with the <code>char</code> value at the specified index and ends with the <code>char
   * </code> value at index <code>end - 1</code>. The length (in <code>char</code>s) of the returned
   * sequence is <code>end - start</code>, so if <code>start == end</code> then an empty sequence is
   * returned.
   *
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   * @return the specified subsequence
   * @throws IndexOutOfBoundsException if <code>start</code> or <code>end</code> are negative, if
   *     <code>end</code> is greater than <code>length()</code>, or if <code>start</code> is greater
   *     than <code>end</code>
   */
  CharSequence subSequence(int start, int end) {
    try {
      return apply((sb) -> sb.subSequence(start, end));
    } catch (CpoException e) {
      logger.error("Error appending", e);
      return null;
    }
  }
}

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

/** Created by dberry on 8/9/15. */
public class StringBuilderXaResource extends CpoBaseXaResource<StringBuilder> {
  private boolean busy = false;

  public StringBuilderXaResource() {
    super(new StringBuilder());
  }

  public void setBusy(boolean busy) {
    this.busy = busy;
  }

  public boolean isBusy() {
    return busy;
  }

  @Override
  protected boolean isLocalResourceBusy() throws XAException {
    return isBusy();
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

    return xaResource instanceof StringBuilderXaResource;
  }

  // StringBuilder Proxy methods

  public StringBuilder append(Object obj) {
    return getCurrentResource().append(String.valueOf(obj));
  }

  public StringBuilder append(String str) {
    return getCurrentResource().append(str);
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
   * @param sb the {@code StringBuffer} to append.
   * @return a reference to this object.
   */
  public StringBuilder append(StringBuffer sb) {
    return getCurrentResource().append(sb);
  }

  public StringBuilder append(CharSequence s) {
    return getCurrentResource().append(s);
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder append(CharSequence s, int start, int end) {
    return getCurrentResource().append(s, start, end);
  }

  public StringBuilder append(char[] str) {
    return getCurrentResource().append(str);
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder append(char[] str, int offset, int len) {
    return getCurrentResource().append(str, offset, len);
  }

  public StringBuilder append(boolean b) {
    return getCurrentResource().append(b);
  }

  public StringBuilder append(char c) {
    return getCurrentResource().append(c);
  }

  public StringBuilder append(int i) {
    return getCurrentResource().append(i);
  }

  public StringBuilder append(long lng) {
    return getCurrentResource().append(lng);
  }

  public StringBuilder append(float f) {
    return getCurrentResource().append(f);
  }

  public StringBuilder append(double d) {
    return getCurrentResource().append(d);
  }

  /**
   * @since 1.5
   */
  public StringBuilder appendCodePoint(int codePoint) {
    return getCurrentResource().appendCodePoint(codePoint);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder delete(int start, int end) {
    return getCurrentResource().delete(start, end);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder deleteCharAt(int index) {
    return getCurrentResource().deleteCharAt(index);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder replace(int start, int end, String str) {
    return getCurrentResource().replace(start, end, str);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int index, char[] str, int offset, int len) {
    return getCurrentResource().insert(index, str, offset, len);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, Object obj) {
    return getCurrentResource().insert(offset, obj);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, String str) {
    return getCurrentResource().insert(offset, str);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, char[] str) {
    return getCurrentResource().insert(offset, str);
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int dstOffset, CharSequence s) {
    return getCurrentResource().insert(dstOffset, s);
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
    return getCurrentResource().insert(dstOffset, s, start, end);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, boolean b) {
    return getCurrentResource().insert(offset, b);
  }

  /**
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, char c) {
    return getCurrentResource().insert(offset, c);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, int i) {
    return getCurrentResource().insert(offset, i);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, long l) {
    return getCurrentResource().insert(offset, l);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, float f) {
    return getCurrentResource().insert(offset, f);
  }

  /**
   * @throws StringIndexOutOfBoundsException {@inheritDoc}
   */
  public StringBuilder insert(int offset, double d) {
    return getCurrentResource().insert(offset, d);
  }

  public int indexOf(String str) {
    return getCurrentResource().indexOf(str);
  }

  public int indexOf(String str, int fromIndex) {
    return getCurrentResource().indexOf(str, fromIndex);
  }

  public int lastIndexOf(String str) {
    return getCurrentResource().lastIndexOf(str);
  }

  public int lastIndexOf(String str, int fromIndex) {
    return getCurrentResource().lastIndexOf(str, fromIndex);
  }

  public StringBuilder reverse() {
    return getCurrentResource().reverse();
  }

  @Override
  public String toString() {
    // Create a copy, don't share the array
    return getCurrentResource().toString();
  }

  /**
   * Returns the length of this character sequence. The length is the number of 16-bit <code>char
   * </code>s in the sequence.
   *
   * @return the number of <code>char</code>s in this sequence
   */
  int length() {
    return getCurrentResource().length();
  }

  /**
   * Returns the <code>char</code> value at the specified index. An index ranges from zero to
   * <tt>length() - 1</tt>. The first <code>char</code> value of the sequence is at index zero, the
   * next at index one, and so on, as for array indexing.
   *
   * <p>If the <code>char</code> value specified by the index is a <a
   * href="{@docRoot}/java/lang/Character.html#unicode">surrogate</a>, the surrogate value is
   * returned.
   *
   * @param index the index of the <code>char</code> value to be returned
   * @return the specified <code>char</code> value
   * @throws IndexOutOfBoundsException if the <tt>index</tt> argument is negative or not less than
   *     <tt>length()</tt>
   */
  char charAt(int index) {
    return getCurrentResource().charAt(index);
  }

  /**
   * Returns a <code>CharSequence</code> that is a subsequence of this sequence. The subsequence
   * starts with the <code>char</code> value at the specified index and ends with the <code>char
   * </code> value at index <tt>end - 1</tt>. The length (in <code>char</code>s) of the returned
   * sequence is <tt>end - start</tt>, so if <tt>start == end</tt> then an empty sequence is
   * returned.
   *
   * @param start the start index, inclusive
   * @param end the end index, exclusive
   * @return the specified subsequence
   * @throws IndexOutOfBoundsException if <tt>start</tt> or <tt>end</tt> are negative, if
   *     <tt>end</tt> is greater than <tt>length()</tt>, or if <tt>start</tt> is greater than
   *     <tt>end</tt>
   */
  CharSequence subSequence(int start, int end) {
    return getCurrentResource().subSequence(start, end);
  }
}

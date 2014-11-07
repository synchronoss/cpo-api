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
package org.synchronoss.cpo.cassandra;

import java.math.*;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * ValueObject - a cpo bean used to test the cpo apis
 *
 * @author david berry
 */
public class ValueObjectBean implements ValueObject, java.io.Serializable, Cloneable {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;

  private int id;
  private String attrAscii;
  private long attrBigInt;
  private ByteBuffer attrBlob;
  private ByteBuffer attrBlob2;
  private boolean attrBool;
  private long attrCounter;
  private BigDecimal attrDecimal;
  private double attrDouble;
  private float attrFloat;
  private InetAddress attrInet;
  private int attrInt;
  private List<String> attrList;
  private Map<String, String> attrMap;
  private Set<String> attrSet;
  private String attrText;
  private Date attrTimestamp;
  private UUID attrTimeUUID;
  private UUID attrUUID;
  private String attrVarChar;
  private BigInteger attrVarInt;

  public ValueObjectBean() {
    // public default constructor as required by cpo
  }

  public ValueObjectBean(int id) {
    this.id = id;
  }

  public ValueObjectBean(int id, ByteBuffer attrBlob) {
    this.id = id;
    this.attrBlob = attrBlob;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAttrAscii() {
    return attrAscii;
  }

  public void setAttrAscii(String attrAscii) {
    this.attrAscii = attrAscii;
  }

  public long getAttrBigInt() {
    return attrBigInt;
  }

  public void setAttrBigInt(long attrBigInt) {
    this.attrBigInt = attrBigInt;
  }

  public ByteBuffer getAttrBlob() {
    return attrBlob;
  }

  public void setAttrBlob(ByteBuffer attrBlob) {
    this.attrBlob = attrBlob;
  }

  public ByteBuffer getAttrBlob2() {
    return attrBlob2;
  }

  public void setAttrBlob2(ByteBuffer attrBlob2) {
    this.attrBlob2 = attrBlob2;
  }

  public boolean getAttrBool() {
    return attrBool;
  }

  public void setAttrBool(boolean attrBool) {
    this.attrBool = attrBool;
  }

  public long getAttrCounter() {
    return attrCounter;
  }

  public void setAttrCounter(long attrCounter) {
    this.attrCounter = attrCounter;
  }

  public BigDecimal getAttrDecimal() {
    return attrDecimal;
  }

  public void setAttrDecimal(BigDecimal attrDecimal) {
    this.attrDecimal = attrDecimal;
  }

  public double getAttrDouble() {
    return attrDouble;
  }

  public void setAttrDouble(double attrDouble) {
    this.attrDouble = attrDouble;
  }

  public float getAttrFloat() {
    return attrFloat;
  }

  public void setAttrFloat(float attrFloat) {
    this.attrFloat = attrFloat;
  }

  public InetAddress getAttrInet() {
    return attrInet;
  }

  public void setAttrInet(InetAddress attrInet) {
    this.attrInet = attrInet;
  }

  public int getAttrInt() {
    return attrInt;
  }

  public void setAttrInt(int attrInt) {
    this.attrInt = attrInt;
  }

  public List<String> getAttrList() {
    return attrList;
  }

  public void setAttrList(List<String> attrList) {
    this.attrList = attrList;
  }

  public Map<String, String> getAttrMap() {
    return attrMap;
  }

  public void setAttrMap(Map<String, String> attrMap) {
    this.attrMap = attrMap;
  }

  public Set<String> getAttrSet() {
    return attrSet;
  }

  public void setAttrSet(Set<String> attrSet) {
    this.attrSet = attrSet;
  }

  public String getAttrText() {
    return attrText;
  }

  public void setAttrText(String attrText) {
    this.attrText = attrText;
  }

  public Date getAttrTimestamp() {
    return attrTimestamp;
  }

  public void setAttrTimestamp(Date attrTimestamp) {
    this.attrTimestamp = attrTimestamp;
  }

  public UUID getAttrTimeUUID() {
    return attrTimeUUID;
  }

  public void setAttrTimeUUID(UUID attrTimeUUID) {
    this.attrTimeUUID = attrTimeUUID;
  }

  public UUID getAttrUUID() {
    return attrUUID;
  }

  public void setAttrUUID(UUID attrUUID) {
    this.attrUUID = attrUUID;
  }

  public String getAttrVarChar() {
    return attrVarChar;
  }

  public void setAttrVarChar(String attrVarChar) {
    this.attrVarChar = attrVarChar;
  }

  public BigInteger getAttrVarInt() {
    return attrVarInt;
  }

  public void setAttrVarInt(BigInteger attrVarInt) {
    this.attrVarInt = attrVarInt;
  }
}
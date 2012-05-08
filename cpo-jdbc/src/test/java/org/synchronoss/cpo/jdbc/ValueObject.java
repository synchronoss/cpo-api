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
package org.synchronoss.cpo.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * ValueObject - a cpo bean used to test the cpo apis
 *
 * @author david berry
 */
public class ValueObject extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private int id_ = 0; // The id for the value object in the database
  private int attrInteger_ = 0;
  private int attrInt_ = 0;
  private double attrDouble_ = 0.0;
  private float attrFloat_ = 0;
  private String attrVarChar_ = null;
  private String attrVarCharIgnoreCase_ = null;
  private String attrChar_ = null;
  private String attrCharacter_ = null;
  private String attrLongVarChar_ = null;
  private Date attrDate_ = null;
  private Time attrTime_ = null;
  private Timestamp attrTimestamp_ = null;
  private Timestamp attrDateTime_ = null;
  private BigDecimal attrDecimal_ = null;
  private BigDecimal attrNumeric_ = null;
  private boolean attrBit_ = false;
  private BigDecimal attrTinyInt_ = null;
  private int attrSmallInt_ = 0;
  private BigDecimal attrBigInt_ = null;
  private BigDecimal attrReal_ = null;
  private byte[] attrBinary_ = null;
  private byte[] attrVarBinary_ = null;
  private byte[] attrLongVarBinary_ = null;
  private Object attrOther_ = null;
  private Object attrObject_ = null;

  public ValueObject() {
    // public default constructor as required by cpo
  }

  public ValueObject(int id) {
    id_ = id;
  }

  public void setId(int id) {
    id_ = id;
  }

  public int getId() {
    return id_;
  }

  public int getAttrInteger() {
    return attrInteger_;
  }

  public int getAttrInt() {
    return attrInt_;
  }

  public double getAttrDouble() {
    return attrDouble_;
  }

  public double getAttrFloat() {
    return attrFloat_;
  }

  public String getAttrVarChar() {
    return attrVarChar_;
  }

  public String getAttrVarCharIgnoreCase() {
    return attrVarCharIgnoreCase_;
  }

  public String getAttrChar() {
    return attrChar_;
  }

  public String getAttrCharacter() {
    return attrCharacter_;
  }

  public String getAttrLongVarChar() {
    return attrLongVarChar_;
  }

  public Date getAttrDate() {
    return attrDate_;
  }

  public Time getAttrTime() {
    return attrTime_;
  }

  public Timestamp getAttrTimestamp() {
    return attrTimestamp_;
  }

  public Timestamp getAttrDatetime() {
    return attrDateTime_;
  }

  public BigDecimal getAttrDecimal() {
    return attrDecimal_;
  }

  public BigDecimal getAttrNumeric() {
    return attrNumeric_;
  }

  public boolean getAttrBit() {
    return attrBit_;
  }

  public BigDecimal getAttrTinyInt() {
    return attrTinyInt_;
  }

  public int getAttrSmallInt() {
    return attrSmallInt_;
  }

  public BigDecimal getAttrBigInt() {
    return attrBigInt_;
  }

  public BigDecimal getAttrReal() {
    return attrReal_;
  }

  public byte[] getAttrBinary() {
    return attrBinary_;
  }

  public byte[] getAttrVarBinary() {
    return attrVarBinary_;
  }

  public byte[] getAttrLongVarBinary() {
    return attrLongVarBinary_;
  }

  public Object getAttrOther() {
    return attrOther_;
  }

  public Object getAttrObject() {
    return attrObject_;
  }

  public void setAttrInteger(int attrInteger) {
    attrInteger_ = attrInteger;
  }

  public void setAttrInt(int attrInt) {
    attrInt_ = attrInt;
  }

  public void setAttrDouble(double attrDouble) {
    attrDouble_ = attrDouble;
  }

  public void setAttrFloat(float attrFloat) {
    attrFloat_ = attrFloat;
  }

  public void setAttrVarChar(String attrVarChar) {
    attrVarChar_ = attrVarChar;
  }

  public void setAttrVarCharIgnoreCase(String attrVarCharIgnoreCase) {
    attrVarCharIgnoreCase_ = attrVarCharIgnoreCase;
  }

  public void setAttrChar(String attrChar) {
    attrChar_ = attrChar;
  }

  public void setAttrCharacter(String attrCharacter) {
    attrCharacter_ = attrCharacter;
  }

  public void setAttrLongVarChar(String attrLongVarChar) {
    attrLongVarChar_ = attrLongVarChar;
  }

  public void setAttrDate(Date attrDate) {
    attrDate_ = attrDate;
  }

  public void setAttrTime(Time attrTime) {
    attrTime_ = attrTime;
  }

  public void setAttrTimestamp(Timestamp attrTimestamp) {
    attrTimestamp_ = attrTimestamp;
  }

  public void setAttrDatetime(Timestamp attrDateTime) {
    attrDateTime_ = attrDateTime;
  }

  public void setAttrDecimal(BigDecimal attrDecimal) {
    attrDecimal_ = attrDecimal;
  }

  public void setAttrNumeric(BigDecimal attrNumeric) {
    attrNumeric_ = attrNumeric;
  }

  public void setAttrBit(boolean attrBit) {
    attrBit_ = attrBit;
  }

  public void setAttrTinyInt(BigDecimal attrTinyInt) {
    attrTinyInt_ = attrTinyInt;
  }

  public void setAttrSmallInt(int attrSmallInt) {
    attrSmallInt_ = attrSmallInt;
  }

  public void setAttrBigInt(BigDecimal attrBigInt) {
    attrBigInt_ = attrBigInt;
  }

  public void setAttrReal(BigDecimal attrReal) {
    attrReal_ = attrReal;
  }

  public void setAttrBinary(byte[] attrBinary) {
    attrBinary_ = attrBinary;
  }

  public void setAttrVarBinary(byte[] attrVarBinary) {
    attrVarBinary_ = attrVarBinary;
  }

  public void setAttrLongVarBinary(byte[] attrLongVarBinary) {
    attrLongVarBinary_ = attrLongVarBinary;
  }

  public void setAttrOther(Object attrOther) {
    attrOther_ = attrOther;
  }

  public void setAttrObject(Object attrObject) {
    attrObject_ = attrObject;
  }
}
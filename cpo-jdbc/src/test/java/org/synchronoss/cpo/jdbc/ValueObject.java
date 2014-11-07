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
public interface ValueObject {

  public void setId(int id);

  public int getId();

  public int getAttrInteger();

  public int getAttrInt();

  public double getAttrDouble();

  public double getAttrFloat();

  public String getAttrVarChar();

  public String getAttrVarCharIgnoreCase();

  public String getAttrChar();

  public String getAttrCharacter();

  public String getAttrLongVarChar();

  public Date getAttrDate();

  public Time getAttrTime();

  public Timestamp getAttrTimestamp();

  public Timestamp getAttrDatetime();

  public BigDecimal getAttrDecimal();

  public BigDecimal getAttrNumeric();

  public boolean getAttrBit();

  public BigDecimal getAttrTinyInt();

  public int getAttrSmallInt();

  public BigDecimal getAttrBigInt();

  public BigDecimal getAttrReal();

  public byte[] getAttrBinary();

  public byte[] getAttrVarBinary();

  public byte[] getAttrLongVarBinary();

  public Object getAttrOther();

  public Object getAttrObject();

  public void setAttrInteger(int attrInteger);

  public void setAttrInt(int attrInt);

  public void setAttrDouble(double attrDouble);

  public void setAttrFloat(float attrFloat);

  public void setAttrVarChar(String attrVarChar);

  public void setAttrVarCharIgnoreCase(String attrVarCharIgnoreCase);

  public void setAttrChar(String attrChar);

  public void setAttrCharacter(String attrCharacter);

  public void setAttrLongVarChar(String attrLongVarChar);

  public void setAttrDate(Date attrDate);

  public void setAttrTime(Time attrTime);

  public void setAttrTimestamp(Timestamp attrTimestamp);

  public void setAttrDatetime(Timestamp attrDateTime);

  public void setAttrDecimal(BigDecimal attrDecimal);

  public void setAttrNumeric(BigDecimal attrNumeric);

  public void setAttrBit(boolean attrBit);

  public void setAttrTinyInt(BigDecimal attrTinyInt);

  public void setAttrSmallInt(int attrSmallInt);

  public void setAttrBigInt(BigDecimal attrBigInt);

  public void setAttrReal(BigDecimal attrReal);

  public void setAttrBinary(byte[] attrBinary);

  public void setAttrVarBinary(byte[] attrVarBinary);

  public void setAttrLongVarBinary(byte[] attrLongVarBinary);

  public void setAttrOther(Object attrOther);

  public void setAttrObject(Object attrObject);
}
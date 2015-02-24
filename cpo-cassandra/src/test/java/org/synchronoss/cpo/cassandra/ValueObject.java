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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * ValueObject - a cpo bean used to test the cpo apis
 *
 * @author david berry
 */
public interface ValueObject {

  public int getId();

  public void setId(int id);

  public String getAttrAscii();

  public void setAttrAscii(String attrAscii);

  public long getAttrBigInt();

  public void setAttrBigInt(long attrBigInt);

  public ByteBuffer getAttrBlob();

  public void setAttrBlob(ByteBuffer attrBlob);

  public ByteBuffer getAttrBlob2();

  public void setAttrBlob2(ByteBuffer attrBlob2);

  public boolean getAttrBool();

  public void setAttrBool(boolean attrBool);

  public long getAttrCounter();

  public void setAttrCounter(long attrCounter);

  public BigDecimal getAttrDecimal();

  public void setAttrDecimal(BigDecimal attrDecimal);

  public double getAttrDouble();

  public void setAttrDouble(double attrDouble);

  public float getAttrFloat();

  public void setAttrFloat(float attrFloat);

  public InetAddress getAttrInet();

  public void setAttrInet(InetAddress attrInet);

  public int getAttrInt();

  public void setAttrInt(int attrInt);

  public List<String> getAttrList();

  public void setAttrList(List<String> attrList);

  public Map<String, String> getAttrMap();

  public void setAttrMap(Map<String, String> attrMap);

  public Set<String> getAttrSet();

  public void setAttrSet(Set<String> attrSet);

  public String getAttrText();

  public void setAttrText(String attrText);

  public Date getAttrTimestamp();

  public void setAttrTimestamp(Date attrTimestamp);

  public UUID getAttrTimeUUID();

  public void setAttrTimeUUID(UUID attrTimeUUID);

  public UUID getAttrUUID();

  public void setAttrUUID(UUID attrUUID);

  public String getAttrVarChar();

  public void setAttrVarChar(String attrVarChar);

  public BigInteger getAttrVarInt();

  public void setAttrVarInt(BigInteger attrVarInt);
}
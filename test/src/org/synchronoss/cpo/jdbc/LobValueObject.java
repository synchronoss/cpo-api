/**
 * ValueObject.java
 * 
 *  Copyright (C) 2006  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 * 
 * $Header: /home/cvs/cpo/test/src/org/synchronoss/cpo/jdbc/LobValueObject.java,v 1.4 2006/01/31 22:55:03 dberry Exp $
 */
package org.synchronoss.cpo.jdbc;

import java.io.Serializable;


public class LobValueObject implements Serializable {
    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
  private int lobId;
  private byte[] bLob = null;
  private byte[] bLob2 = null;
  private char[] cLob = null;
  public LobValueObject() {
  }
  
  public LobValueObject(int id, byte[] bLob, char[] cLob){
    this.lobId = id;
    this.bLob=bLob;
    this.cLob=cLob;
  }
  
  public void setLobId(int lobId) {
    this.lobId = lobId;
  }
  public int getLobId() {
    return this.lobId;
  }
  public void setBLob(byte[] bLob) {
    this.bLob = bLob;
  }
  public byte[] getBLob() {
    return this.bLob;
  }
  public void setBLob2(byte[] bLob) {
    this.bLob2 = bLob;
  }
  public byte[] getBLob2() {
    return this.bLob2;
  }
  public void setCLob(char[] cLob) {
    this.cLob = cLob;
  }
  public char[] getCLob() {
    return this.cLob;
  }
}

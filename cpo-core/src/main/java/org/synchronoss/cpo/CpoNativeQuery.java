/*
 *  Copyright (C) 2003-2012 David E. Berry
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
 */

package org.synchronoss.cpo;

public class CpoNativeQuery {

  private String marker=null;
  private String nativeText=null;
  
  public CpoNativeQuery(){}
  
  public CpoNativeQuery(String marker, String text){
    this.marker=marker;
    this.nativeText = text;
  }
  
  public void setMarker(String marker){
    this.marker=marker;
  }
  
  public String getMarker(){
    return this.marker;
  }
  
  public void setNativeText(String text){
    this.nativeText = text;
  }
  
  public String getNativeText(){
    return this.nativeText;
  }
  
}


/*
 * @(#)INodeVisitor.java      4/27/2001 10:47a
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
 */

package org.synchronoss.cpo;

/**
  * This is the interface for the visitors to the Node Hierarchy
  *
  */
public interface INodeVisitor 
{
    /**
    * This is called by composite nodes prior to visiting children
    *
    * @param val The node to be visited
    * @return a boolean (false) to end visit or (true) to continue visiting  
    */
    public boolean visitBegin(Node node) throws Exception;
    
    /**
    * This is called for composite nodes between visiting children
    *
    * @param val The node to be visited
    * @return a boolean (false) to end visit or (true) to continue visiting  
    */
    public boolean visitMiddle(Node node) throws Exception;

    /**
    * This is called by composite nodes after visiting children
    *
    * @param val The node to be visited
    * @return a boolean (false) to end visit or (true) to continue visiting  
    */
    public boolean visitEnd(Node node) throws Exception;


    /**
    * This is called for component elements which have no children
    *
    * @param val The element to be visited
    * @return a boolean (false) to end visit or (true) to continue visiting  
    */
    public boolean visit(Node node) throws Exception;
}

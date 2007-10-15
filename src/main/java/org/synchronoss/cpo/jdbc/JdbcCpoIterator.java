/**
 * JdbcCpoIterator.java
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
package org.synchronoss.cpo.jdbc;

import java.util.ArrayList;
import org.synchronoss.cpo.CpoIterator;


public class JdbcCpoIterator<T> implements CpoIterator<T> {
    private boolean closed = false;
    private boolean empty = true;
    private int bufSize = 0;
    
    private ArrayList<T> list = new ArrayList<T>();
    
    protected JdbcCpoIterator(int objectBufferSize){
        bufSize=objectBufferSize;
    }
    
    public boolean add(T obj){
        boolean added = false;
        if (obj!=null){
            synchronized(list){
                if (hasRoom()){
                    list.add(obj);
                    empty=false;
                    added=true;
                }
            }
        }
        return added;
    }
    
    public boolean addfinal(T obj){
        boolean added = false;
        if (obj!=null){
            synchronized(list){
                if (hasRoom()){
                    list.add(obj);
                    empty=false;
                    closed=true;
                    added=true;
                }
            }
        }
        return added;
    }
    
    public boolean hasNext(){
        while(!closed && empty){
            // block until the datasource catches up
        }
        if (!empty) 
            return true;
        else
            return false;
    }
    
    public T next(){
        T obj=null;
        
        synchronized(list){
            obj=list.remove(0);
            empty = (list.size()==0);
        }
        
        return obj;
    }
    
    public void remove(){
        throw new UnsupportedOperationException(); 
    }
    
    private boolean hasRoom(){
        return (list.size()<bufSize);
    }
    
}

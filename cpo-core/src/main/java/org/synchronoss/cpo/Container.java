/*
 * @(#)Container.java      4/27/2001 10:47a
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

import java.io.Serializable;

public class Container extends Object implements IContainer, Serializable, Cloneable
{
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;

    private Object myObject;
    private boolean modified;
    private boolean readOnly;

    public Container()
    {
        release();
    }
    
    protected void release()
    {
       this.myObject = null;
       this.modified = false;
    }

    public Container(Object obj)
    {
        this.myObject = obj;
    }

  @Override
    public Object getObject()
    {
        return this.myObject;
    }

  @Override
    public void setObject(Object obj)
    {
        if(!isReadOnly()) {
            setModified(true);
            this.myObject = obj;
        }
    }

  @Override
    public void setModified(boolean lclModified)
    {
        this.modified = lclModified;
    }

  @Override
    public boolean getModified()
    {
        return this.modified;
    }

    public void setReadOnly(boolean ro)
    {
        this.readOnly = ro;
    }

    public boolean isReadOnly()
    {
        return this.readOnly;
    }

  @Override
    public Object clone()
    throws CloneNotSupportedException {
        Container thisClone = (Container) super.clone();
        return thisClone;
    }
}

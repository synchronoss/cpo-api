/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo;

/**
 * CpoReleasible is a class that can be called during a CpoTransform when there are resources that need to released
 * after the current prepared statement is processed.
 *
 * examples are in the the TransformBlob where there are Oracle resources that must exist until the statement is
 * executed.
 */
public interface CpoReleasible {

  /**
   * release is called by the CPO framework. A transform can register a CpoReleasible that will be called when the
   * current prepared or callable statement has been executed.
   */
  public void release() throws CpoException;
}

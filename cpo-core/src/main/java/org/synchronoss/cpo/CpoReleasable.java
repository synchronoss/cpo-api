package org.synchronoss.cpo;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

/**
 * CpoReleasable is a class that can be called during a CpoTransform when there are resources that
 * need to released after the current prepared statement is processed.
 *
 * <p>examples are in the TransformBlob where there are Oracle resources that must exist until the
 * statement is executed.
 */
public interface CpoReleasable {

  /**
   * release is called by the CPO framework. A transform can register a CpoReleasable that will be
   * called when the current prepared or callable statement has been executed.
   *
   * @throws CpoException An exception occurred
   */
  void release() throws CpoException;
}

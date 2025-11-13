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
package org.synchronoss.cpo.meta;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import org.synchronoss.cpo.CpoException;

/**
 * @author dberry
 */
public interface CpoMetaExportable {

  /**
   * Performs an export of the metadata to the specified File
   *
   * @param file The file to export to
   * @throws CpoException An error has occurred
   */
  void export(File file) throws CpoException;

  /**
   * Performs an export of the metadata to the specified Writer
   *
   * @param writer The writer to export to
   * @throws CpoException An error has occurred
   */
  void export(Writer writer) throws CpoException;

  /**
   * Performs an export of the meta ata to the specified OutputStream
   *
   * @param outputStream The output stream to export to
   * @throws CpoException An error has occurred
   */
  void export(OutputStream outputStream) throws CpoException;
}

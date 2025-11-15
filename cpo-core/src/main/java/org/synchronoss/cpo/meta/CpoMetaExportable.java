package org.synchronoss.cpo.meta;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

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

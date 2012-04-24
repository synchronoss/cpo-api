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

package org.synchronoss.cpo.jdbc;

/**
 *
 * @author dberry
 */
public interface JdbcStatics {
    public static final String ADAPTER_CONTEXT = "jdbc";
    public static final String PROP_FILE = "jdbcCpoFactory";
    public static final String PROP_DB_MILLI_SUPPORTED="default.dbMilliSupport";
    public static final String PROP_DBDRIVER="default.dbDriver";
    public static final String PROP_DB_BLOBS_SUPPORTED="default.dbBlobsSupported";
    public static int BLOB_SIZE=64999;
    public static final String PROP_DB_CALLS_SUPPORTED="default.dbCallsSupported";
    public static final String PROP_DB_SELECT4UPDATE="default.dbSelect4Update";
  
}

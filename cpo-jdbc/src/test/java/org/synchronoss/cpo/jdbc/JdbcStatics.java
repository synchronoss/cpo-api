/*
 * Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.jdbc;

/**
 *
 * @author dberry
 */
public interface JdbcStatics {

  public static final String ADAPTER_CONTEXT_JDBC = "jdbc";
  public static final String ADAPTER_CONTEXT_CLASS = "class";
  public static final String ADAPTER_CONTEXT_DRIVER = "driver";
  public static final String ADAPTER_CONTEXT_CLASSPROP = "classProp";
  public static final String ADAPTER_CONTEXT_DRIVERPROP = "driverProp";
  public static final String ADAPTER_CONTEXT_CLASSCLASS = "classclass";
  public static final String ADAPTER_CONTEXT_DRIVERDRIVER = "driverdriver";
  public static final String ADAPTER_CONTEXT_CLASSDRIVER = "classdriver";
  public static final String ADAPTER_CONTEXT_DRIVERCLASS = "driverclass";
  public static final String ADAPTER_CONTEXT_JDBC_URLONLY = "jdbcUrlOnly";
  public static final String ADAPTER_CONTEXT_CLASS_URLONLY = "classUrlOnly";
  public static final String ADAPTER_CONTEXT_DRIVER_URLONLY = "driverUrlOnly";
  public static int BLOB_SIZE = 64999;
}

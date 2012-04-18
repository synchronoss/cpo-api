/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

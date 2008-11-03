package org.synchronoss.cpo.jdbc;

public class JdbcDsiEntry {
    private JdbcDataSourceInfo metaInfo;
    private JdbcDataSourceInfo dbInfo;
    
    JdbcDsiEntry(JdbcDataSourceInfo metaInfo, JdbcDataSourceInfo dbInfo){
      this.metaInfo = metaInfo;
      this.dbInfo = dbInfo;
    }

    public JdbcDataSourceInfo getMetaInfo() {
      return metaInfo;
    }

    public void setMetaInfo(JdbcDataSourceInfo metaInfo) {
      this.metaInfo = metaInfo;
    }

    public JdbcDataSourceInfo getDbInfo() {
      return dbInfo;
    }

    public void setDbInfo(JdbcDataSourceInfo dbInfo) {
      this.dbInfo = dbInfo;
    }
  }

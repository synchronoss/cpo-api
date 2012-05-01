/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc.meta;

import java.util.List;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.JavaSqlType;
import org.synchronoss.cpo.jdbc.exporter.JdbcMetaXmlObjectExporter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaDescriptor extends CpoMetaDescriptor {
  private boolean supportsBlobs = false;
  private boolean supportsCalls = false;
  private boolean supportsMillis = false;
  private boolean supportsSelect4Update = false;
  

  public JdbcCpoMetaDescriptor(String name) throws CpoException {
    super(name);
  }
  
  @Override
  protected Class getMetaAdapterClass() throws CpoException {
    return JdbcCpoMetaAdapter.class;
  }
  
//  public static JdbcCpoMetaDescriptor getInstance(String name) throws CpoException {
//    return new JdbcCpoMetaDescriptor(name);
//  }
//  
  
  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new JdbcMetaXmlObjectExporter(this);
  }

  public boolean isSupportsBlobs() {
    return supportsBlobs;
  }

  public void setSupportsBlobs(boolean supportsBlobs) {
    this.supportsBlobs = supportsBlobs;
  }

  public boolean isSupportsCalls() {
    return supportsCalls;
  }

  public void setSupportsCalls(boolean supportsCalls) {
    this.supportsCalls = supportsCalls;
  }

  public boolean isSupportsMillis() {
    return supportsMillis;
  }

  public void setSupportsMillis(boolean supportsMillis) {
    this.supportsMillis = supportsMillis;
  }

  public boolean isSupportsSelect4Update() {
    return supportsSelect4Update;
  }

  public void setSupportsSelect4Update(boolean supportsSelect4Update) {
    this.supportsSelect4Update = supportsSelect4Update;
  }
  
  public int getJavaSqlType(String javaSqlTypeName) throws CpoException {
    return ((JdbcCpoMetaAdapter)getCpoMetaAdapter()).getJavaSqlType(javaSqlTypeName);
  }
  
  public JavaSqlType<?> getJavaSqlType(int sqlType) throws CpoException {
    return ((JdbcCpoMetaAdapter)getCpoMetaAdapter()).getJavaSqlType(sqlType);
  }
}

/**
 *  JdbcCpoAdapter.java    
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
package org.synchronoss.cpo.jdbc.meta;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.CtArgument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.JdbcArgument;
import org.synchronoss.cpo.jdbc.JdbcAttribute;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcArgument;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcAttribute;
import org.synchronoss.cpo.jdbc.exporter.JdbcMetaXmlObjectExporter;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.CpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.*;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaAdapter extends AbstractCpoMetaAdapter {
  
  private String dataSourceIdentifier=null;
  
  public JdbcCpoMetaAdapter(){
  }

  
  /**
   * Constructor for the JdbcCpoMetaAdapter. It checks to see if the string is valid xml, then checks to see if it points to a
   * resource, then checks to see if it points to a file.
   *
   * @param metaXml The resource name, file name, or the actual xml
   * @throws CpoException Throws an exception if the xml is not valid.
   */
  private JdbcCpoMetaAdapter(String metaXml) throws CpoException {
    
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param objClass DOCUMENT ME!
   * @param name     DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> CpoMetaClass<T> loadMetaClass(Class<T> objClass, String name)
      throws CpoException {
    CpoMetaClass<T> cpoClass=null;

    cpoClass = new CpoMetaClass<T>(objClass);
//    loadAttributeMap(name, cpoClass);
//    loadQueryGroups(cpoClass);

    return cpoClass;
  }

  
  protected static void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute){
    AbstractCpoMetaAdapter.loadCpoAttribute(cpoAttribute, ctAttribute);
    
    // cast to the expected subclasses
    JdbcAttribute jdbcAttribute = (JdbcAttribute)cpoAttribute;
    CtJdbcAttribute ctJdbcAttribute = (CtJdbcAttribute)ctAttribute;
    
    jdbcAttribute.setDbTable(ctJdbcAttribute.getDbTable());
    jdbcAttribute.setDbColumn(ctJdbcAttribute.getDbColumn());
    
  }
  
  protected static void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument){
    AbstractCpoMetaAdapter.loadCpoArgument(cpoArgument, ctArgument);
    
    // cast to the expected subclasses
    JdbcArgument jdbcArgument = (JdbcArgument)cpoArgument;
    CtJdbcArgument ctJdbcArgument = (CtJdbcArgument)ctArgument;
    
    jdbcArgument.setExecuteType(ctJdbcArgument.getExecType().toString());
    
  }
  
  protected static CpoAttribute createCpoAttribute() {
    return new JdbcAttribute();
  }
  
  protected static CpoArgument createCpoArgument() {
    return new JdbcArgument();
  }

  public static JdbcCpoMetaAdapter newInstance(String metaXml) throws CpoException {
    CpoMetaAdapter metaAdapter = getCpoMetaAdapter(metaXml, new JdbcCpoMetaAdapter());
    
    if (metaAdapter != null && metaAdapter instanceof JdbcCpoMetaAdapter)
      return (JdbcCpoMetaAdapter) metaAdapter;
    
    return null;
  }

  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new JdbcMetaXmlObjectExporter(this.getClass().getName());
  }
}

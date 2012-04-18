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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtArgument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.jdbc.JdbcArgument;
import org.synchronoss.cpo.jdbc.JdbcAttribute;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcArgument;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcAttribute;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaAdapter extends AbstractCpoMetaAdapter {
  
  private String dataSourceIdentifier=null;
  
  private JdbcCpoMetaAdapter(){
  }

  
  /**
   * Constructor for the JdbcCpoMetaAdapter. It checks to see if the string is valid xml, then checks to see if it points to a
   * resource, then checks to see if it points to a file.
   *
   * @param metaXml The resource name, file name, or the actual xml
   * @throws CpoException Throws an exception if the xml is not valid.
   */
  private JdbcCpoMetaAdapter(String dataSourceIdentifier, String metaXml) throws CpoException {
    InputStream is = null;
    CpoMetaDataDocument metaDataDoc = null;
    this.dataSourceIdentifier=dataSourceIdentifier;
    
    is = this.getClass().getResourceAsStream(metaXml);
    if (is == null){
      try {
        is = new FileInputStream(metaXml);
      } catch (FileNotFoundException fnfe){
        is = null;
      }
    }
    
    try {
      if (is == null){
        metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
      } else {
        metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
      }
    } catch (IOException ioe){
      throw new CpoException("Error processing metaData from InputStream");
    } catch (XmlException xe){
      throw new CpoException("Error processing metaData from String");
    }
    
    // We should have a valid metaData xml document now.
    loadCpoMetaDataDocument(metaDataDoc);
    
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param objClass DOCUMENT ME!
   * @param name     DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> CpoClass<T> loadMetaClass(Class<T> objClass, String name)
      throws CpoException {
    CpoClass<T> cpoClass=null;

    cpoClass = new CpoClass<T>(objClass);
//    loadAttributeMap(name, cpoClass);
//    loadQueryGroups(cpoClass);

    return cpoClass;
  }

  
  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute){
    super.loadCpoAttribute(cpoAttribute, ctAttribute);
    
    // cast to the expected subclasses
    JdbcAttribute jdbcAttribute = (JdbcAttribute)cpoAttribute;
    CtJdbcAttribute ctJdbcAttribute = (CtJdbcAttribute)ctAttribute;
    
    jdbcAttribute.setDbTable(ctJdbcAttribute.getDbTable());
    jdbcAttribute.setDbColumn(ctJdbcAttribute.getDbColumn());
    
  }
  
  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument){
    super.loadCpoArgument(cpoArgument, ctArgument);
    
    // cast to the expected subclasses
    JdbcArgument jdbcArgument = (JdbcArgument)cpoArgument;
    CtJdbcArgument ctJdbcArgument = (CtJdbcArgument)ctArgument;
    
    jdbcArgument.setExecuteType(ctJdbcArgument.getExecType().toString());
    
  }
  
  protected CpoAttribute createCpoAttribute() {
    return new JdbcAttribute();
  }
  
  protected CpoArgument createCpoArgument() {
    return new JdbcArgument();
  }
  
}

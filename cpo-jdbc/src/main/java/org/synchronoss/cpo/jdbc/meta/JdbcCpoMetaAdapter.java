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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger logger = LoggerFactory.getLogger(JdbcCpoMetaAdapter.class.getName());
  
  private String dataSourceIdentifier=null;
  
  public JdbcCpoMetaAdapter(){
  }

  
  @Override
  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute){
    super.loadCpoAttribute(cpoAttribute, ctAttribute);
    
    // cast to the expected subclasses
    JdbcAttribute jdbcAttribute = (JdbcAttribute)cpoAttribute;
    CtJdbcAttribute ctJdbcAttribute = (CtJdbcAttribute)ctAttribute;
    
    jdbcAttribute.setDbTable(ctJdbcAttribute.getDbTable());
    jdbcAttribute.setDbColumn(ctJdbcAttribute.getDbColumn());
    
  }
  
  @Override
  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument){
    super.loadCpoArgument(cpoArgument, ctArgument);
    
    // cast to the expected subclasses
    JdbcArgument jdbcArgument = (JdbcArgument)cpoArgument;
    CtJdbcArgument ctJdbcArgument = (CtJdbcArgument)ctArgument;
    
    jdbcArgument.setExecuteType(ctJdbcArgument.getExecType().toString());
    
  }
  
  @Override
  protected CpoAttribute createCpoAttribute() {
    return new JdbcAttribute();
  }
  
  @Override
  protected CpoArgument createCpoArgument() {
    return new JdbcArgument();
  }

  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new JdbcMetaXmlObjectExporter(this.getClass().getName());
  }
}

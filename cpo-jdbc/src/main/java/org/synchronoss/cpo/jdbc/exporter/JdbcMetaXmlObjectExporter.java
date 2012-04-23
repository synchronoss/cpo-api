package org.synchronoss.cpo.jdbc.exporter;

import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.exporter.*;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.*;
import org.synchronoss.cpo.meta.domain.*;

/**
 * XmlObject exporter for jdbc meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public class JdbcMetaXmlObjectExporter extends CoreMetaXmlObjectExporter implements MetaXmlObjectExporter {

  public JdbcMetaXmlObjectExporter(String metaAdapter) {
    super(metaAdapter);
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {

    // shouldn't happen, but if what we got wasn't a JdbcAttribute...
    if (!(cpoAttribute instanceof JdbcAttribute)) {
      super.visit(cpoAttribute);
      return;
    }

    JdbcAttribute jdbcAttribute = (JdbcAttribute)cpoAttribute;

    if (currentCtClass != null) {
      CtJdbcAttribute ctJdbcAttribute = CtJdbcAttribute.Factory.newInstance();

      ctJdbcAttribute.setJavaName(jdbcAttribute.getJavaName());
      ctJdbcAttribute.setJavaType(jdbcAttribute.getJavaType());
      ctJdbcAttribute.setDataName(jdbcAttribute.getDataName());
      ctJdbcAttribute.setDataType(jdbcAttribute.getDataType());

      if (jdbcAttribute.getTransformClassName() != null && jdbcAttribute.getTransformClassName().length() > 0)
        ctJdbcAttribute.setTransformClass(jdbcAttribute.getTransformClassName());

      if (jdbcAttribute.getDescription() != null && jdbcAttribute.getDescription().length() > 0)
        ctJdbcAttribute.setDescription(jdbcAttribute.getDescription());

      if (jdbcAttribute.getDbTable() != null && jdbcAttribute.getDbTable().length() > 0)
        ctJdbcAttribute.setDbTable(jdbcAttribute.getDbTable());

      if (jdbcAttribute.getDbColumn() != null && jdbcAttribute.getDbColumn().length() > 0)
        ctJdbcAttribute.setDbColumn(jdbcAttribute.getDbColumn());

      // add it to the class
      CtAttribute ctAttribute = currentCtClass.addNewCpoAttribute();
      ctAttribute.set(ctJdbcAttribute);
    }
  }

  @Override
  public void visit(CpoArgument cpoArgument) {

    // shouldn't happen, but if what we got wasn't a JdbcArgument...
    if (!(cpoArgument instanceof JdbcArgument)) {
      super.visit(cpoArgument);
      return;
    }

    JdbcArgument jdbcArgument = (JdbcArgument)cpoArgument;

    if (currentCtFunction != null) {

      CtJdbcArgument ctJdbcArgument = CtJdbcArgument.Factory.newInstance();

      ctJdbcArgument.setAttributeName(jdbcArgument.getAttributeName());

      if (jdbcArgument.getDescription() != null && jdbcArgument.getDescription().length() > 0)
        ctJdbcArgument.setDescription(jdbcArgument.getDescription());

      if (jdbcArgument.isInParameter() && jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setExecType(CtJdbcArgument.ExecType.BOTH);
      } else if (jdbcArgument.isInParameter()) {
        ctJdbcArgument.setExecType(CtJdbcArgument.ExecType.IN);
      } else if (jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setExecType(CtJdbcArgument.ExecType.OUT);
      }

      CtArgument ctArgument = currentCtFunction.addNewCpoArgument();
      ctArgument.set(ctJdbcArgument);
    }
  }
}

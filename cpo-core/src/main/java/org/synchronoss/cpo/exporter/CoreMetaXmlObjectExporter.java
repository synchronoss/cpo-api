package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.MetaVisitor;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.meta.CpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.*;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public class CoreMetaXmlObjectExporter implements MetaXmlObjectExporter, MetaVisitor {

  protected CpoMetaDataDocument cpoMetaDataDocument = null;

  protected CtClass currentCtClass;
  protected CtFunctionGroup currentCtFunctionGroup;
  protected CtFunction currentCtFunction;

  public CoreMetaXmlObjectExporter(CpoMetaAdapter metaAdapter) {
    cpoMetaDataDocument = CpoMetaDataDocument.Factory.newInstance();
    CtCpoMetaData ctCpoMetaData = cpoMetaDataDocument.addNewCpoMetaData();
    ctCpoMetaData.setMetaAdapter(metaAdapter.getClass().getName());
  }

  @Override
  public CpoMetaDataDocument getCpoMetaDataDocument() {
    return cpoMetaDataDocument;
  }

  @Override
  public void visit(CpoClass cpoClass) {
    CtClass ctClass = cpoMetaDataDocument.getCpoMetaData().addNewCpoClass();
    ctClass.setName(cpoClass.getName());

    if (cpoClass.getDescription() != null && cpoClass.getDescription().length() > 0)
      ctClass.setDescription(cpoClass.getDescription());

    // save the reference
    currentCtClass = ctClass;
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {
    if (currentCtClass != null) {
      CtAttribute ctAttribute = currentCtClass.addNewCpoAttribute();

      ctAttribute.setJavaName(cpoAttribute.getJavaName());
      ctAttribute.setJavaType(cpoAttribute.getJavaType());
      ctAttribute.setDataName(cpoAttribute.getDataName());
      ctAttribute.setDataType(cpoAttribute.getDataType());

      if (cpoAttribute.getTransformClass() != null && cpoAttribute.getTransformClass().length() > 0)
        ctAttribute.setTransformClass(cpoAttribute.getTransformClass());

      if (cpoAttribute.getDescription() != null && cpoAttribute.getDescription().length() > 0)
        ctAttribute.setDescription(cpoAttribute.getDescription());
    }
  }

  @Override
  public void visit(CpoFunctionGroup cpoFunctionGroup) {
    if (currentCtClass != null) {
      CtFunctionGroup ctFunctionGroup = currentCtClass.addNewCpoFunctionGroup();

      ctFunctionGroup.setName(cpoFunctionGroup.getName());
      ctFunctionGroup.setType(cpoFunctionGroup.getType());

      if (cpoFunctionGroup.getDescription() != null && cpoFunctionGroup.getDescription().length() > 0)
        ctFunctionGroup.setDescription(cpoFunctionGroup.getDescription());

      // save the reference
      currentCtFunctionGroup = ctFunctionGroup;
    }
  }

  @Override
  public void visit(CpoFunction cpoFunction) {
    if (currentCtFunctionGroup != null) {
      CtFunction ctFunction = currentCtFunctionGroup.addNewCpoFunction();

      ctFunction.setExpression(cpoFunction.getExpression());

      if (cpoFunction.getDescription() != null && cpoFunction.getDescription().length() > 0)
        ctFunction.setDescription(cpoFunction.getDescription());

      // save the reference
      currentCtFunction = ctFunction;
    }
  }

  @Override
  public void visit(CpoArgument cpoArgument) {
    if (currentCtFunction != null) {
      CtArgument ctArgument = currentCtFunction.addNewCpoArgument();

      ctArgument.setAttributeName(cpoArgument.getAttribute().getJavaName());

      if (cpoArgument.getDescription() != null && cpoArgument.getDescription().length() > 0)
        ctArgument.setDescription(cpoArgument.getDescription());
    }
  }
}

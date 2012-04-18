package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.IMetaVisitor;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.meta.domain.*;

import java.util.Collection;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public class MetaXmlObjectExporter implements IMetaXmlObjectExporter, IMetaVisitor {

  protected CtCpoMetaData ctCpoMetaData = CtCpoMetaData.Factory.newInstance();

  protected CtClass currentCtClass;
  protected CtFunctionGroup currentCtFunctionGroup;
  protected CtFunction currentCtFunction;

  public MetaXmlObjectExporter() {
  }

  @Override
  public CtCpoMetaData export(Collection<CpoClass<?>> classes) {
    for (CpoClass<?> cpoClass : classes) {
      cpoClass.acceptMetaDFVisitor(this);
    }

    return ctCpoMetaData;
  }

  @Override
  public void visit(CpoClass cpoClass) {
    CtClass ctClass = ctCpoMetaData.addNewClassMeta();
    ctClass.setName(cpoClass.getName());

    if (cpoClass.getDescription() != null && cpoClass.getDescription().length() > 0)
      ctClass.setDescription(cpoClass.getDescription());

    // save the reference
    currentCtClass = ctClass;
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {
    if (currentCtClass != null) {
      CtAttribute ctAttribute = currentCtClass.addNewAttribute();

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
      CtFunctionGroup ctFunctionGroup = currentCtClass.addNewFunctionGroup();

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
      CtFunction ctFunction = currentCtFunctionGroup.addNewFunction();

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
      CtArgument ctArgument = currentCtFunction.addNewArgument();

      ctArgument.setAttribute(cpoArgument.getAttribute().getJavaName());

      if (cpoArgument.getDescription() != null && cpoArgument.getDescription().length() > 0)
        ctArgument.setDescription(cpoArgument.getDescription());
    }
  }
}

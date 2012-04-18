package org.synchronoss.cpo;

import org.synchronoss.cpo.meta.domain.*;

/**
 * This defines a depth first meta visitor.
 *
 * @author Michael Bellomo
 */
public interface MetaVisitor {

  /**
   *
   * @param cpoClass The class to be visited
   */
  public void visit(CpoClass cpoClass);

  /**
   *
   * @param cpoAttribute The attribute to be visited
   */
  public void visit(CpoAttribute cpoAttribute);

  /**
   *
   * @param cpoFunctionGroup The function group to be visited
   */
  public void visit(CpoFunctionGroup cpoFunctionGroup);

  /**
   *
   * @param cpoFunction The function to be visited
   */
  public void visit(CpoFunction cpoFunction);

  /**
   *
   * @param cpoArgument The argument to be visited
   */
  public void visit(CpoArgument cpoArgument);

}


package org.synchronoss.cpo;

/**
 * Defines that a implementer of this interface allows visits from a IMetaVisitor
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public interface IMetaDFVisitable {

  public void acceptMetaDFVisitor(IMetaVisitor visitor);

}

package org.synchronoss.cpo;

/**
 * Defines that a implementer of this interface allows visits from a MetaVisitor
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public interface MetaDFVisitable {

  public void acceptMetaDFVisitor(MetaVisitor visitor);

}

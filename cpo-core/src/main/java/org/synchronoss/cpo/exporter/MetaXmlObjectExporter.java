package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.MetaVisitor;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public interface MetaXmlObjectExporter extends MetaVisitor {

  public CpoMetaDataDocument getCpoMetaDataDocument();
}

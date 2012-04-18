package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.meta.domain.CpoClass;

import java.util.Collection;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public interface MetaXmlObjectExporter {

  public CpoMetaDataDocument export(Collection<CpoClass> classes);

}

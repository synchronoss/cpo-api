package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.core.cpoCoreMeta.CtCpoMetaData;
import org.synchronoss.cpo.meta.domain.CpoClass;

import java.util.Collection;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public interface IMetaXmlObjectExporter {

  public CtCpoMetaData export(Collection<CpoClass<?>> classes);

}

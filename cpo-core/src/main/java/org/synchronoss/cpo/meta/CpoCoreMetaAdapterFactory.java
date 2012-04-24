/*
 *  Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.meta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 *
 * @author dberry
 */
public class CpoCoreMetaAdapterFactory implements CpoMetaAdapterFactory {

  @Override
  public CpoMetaAdapter getCpoMetaAdapter(String metaXml) throws CpoException {

    // calculate the hash of metaXml

    // see if it exists in the cache

    // if it does, return it

    // if not, load the new one.

    InputStream is = null;
    CpoMetaDataDocument metaDataDoc = null;

    is = AbstractCpoMetaAdapter.class.getResourceAsStream(metaXml);
    if (is == null) {
      try {
        is = new FileInputStream(metaXml);
      } catch (FileNotFoundException fnfe) {
        is = null;
      }
    }

    String metaAdapterClassName = null;
    AbstractCpoMetaAdapter metaAdapter = null;
    try {
      if (is == null) {
        metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
      } else {
        metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
      }
      metaAdapterClassName = metaDataDoc.getCpoMetaData().getMetaAdapter();
      metaAdapter = (AbstractCpoMetaAdapter) Class.forName(metaAdapterClassName).newInstance();
      // We should have a valid metaData xml document now.
      metaAdapter.loadCpoMetaDataDocument(metaDataDoc);


    } catch (IOException ioe) {
      throw new CpoException("Error processing metaData from InputStream");
    } catch (XmlException xe) {
      throw new CpoException("Error processing metaData from String");
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("CpoMetaAdapter not found: " + metaAdapterClassName + ": " + ExceptionHelper.getLocalizedMessage(cnfe));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access CpoMetaAdapter: " + metaAdapterClassName + ": " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (InstantiationException ie) {
      throw new CpoException("Could not instantiate CpoMetaAdapter: " + metaAdapterClassName + ": " + ExceptionHelper.getLocalizedMessage(ie));
    }

    return metaAdapter;
  }
}

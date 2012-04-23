/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    if (is == null){
      try {
        is = new FileInputStream(metaXml);
      } catch (FileNotFoundException fnfe){
        is = null;
      }
    }
    
    String metaAdapterClassName=null;
    AbstractCpoMetaAdapter metaAdapter = null;
    try {
      if (is == null){
        metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
      } else {
        metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
      }
      metaAdapterClassName = metaDataDoc.getCpoMetaData().getMetaAdapter();
      metaAdapter = (AbstractCpoMetaAdapter) Class.forName(metaAdapterClassName).newInstance();
      // We should have a valid metaData xml document now.
      metaAdapter.loadCpoMetaDataDocument(metaDataDoc);


    } catch (IOException ioe){
      throw new CpoException("Error processing metaData from InputStream");
    } catch (XmlException xe){
      throw new CpoException("Error processing metaData from String");
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("CpoMetaAdapter not found: "+metaAdapterClassName+": "+ExceptionHelper.getLocalizedMessage(cnfe));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access CpoMetaAdapter: "+metaAdapterClassName+": "+ExceptionHelper.getLocalizedMessage(iae));
    } catch (InstantiationException ie)  {
      throw new CpoException("Could not instantiate CpoMetaAdapter: "+metaAdapterClassName+": "+ExceptionHelper.getLocalizedMessage(ie));
    }
    
    return metaAdapter;
  }
}

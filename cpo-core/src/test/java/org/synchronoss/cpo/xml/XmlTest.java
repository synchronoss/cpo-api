package org.synchronoss.cpo.xml;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cpometa.CtCpoMetaData;
import org.synchronoss.cpo.helper.XmlHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlTest {

  @Test
  public void testCpoMetaDataXml() throws CpoException {
    var errBuilder = new StringBuilder();
    var metaXml = "testCoreMeta.xml";

    CtCpoMetaData ctCpoMeta =
        XmlHelper.unmarshalXmlObject(
            XmlHelper.CPO_META_XSD, metaXml, CtCpoMetaData.class, errBuilder);
    Assert.assertTrue(
        errBuilder.isEmpty(),
        "Should not have received an error message: " + errBuilder.toString());
  }
}

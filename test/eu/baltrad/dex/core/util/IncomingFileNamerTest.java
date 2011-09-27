/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*   
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.core.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import eu.baltrad.fc.Date;
import eu.baltrad.fc.Oh5Attribute;
import eu.baltrad.fc.Oh5Group;
import eu.baltrad.fc.Oh5Metadata;
import eu.baltrad.fc.Oh5Node;
import eu.baltrad.fc.Oh5Scalar;
import eu.baltrad.fc.Oh5Source;
import eu.baltrad.fc.Time;

public class IncomingFileNamerTest extends TestCase {
  private IncomingFileNamer classUnderTest;
  private Oh5Metadata metadata;

  public void setUp() {
    classUnderTest = new IncomingFileNamer();
    metadata = new Oh5Metadata();
  }

  public void testNameMetadata_Pvol() {
    Oh5Node what = metadata.root().add(new Oh5Group("what"));
    what.add(new Oh5Attribute("object", new Oh5Scalar("PVOL")));
    what.add(new Oh5Attribute("date", new Oh5Scalar(new Date(2011, 6, 22))));
    what.add(new Oh5Attribute("time", new Oh5Scalar(new Time(13, 14, 15))));
    what.add(new Oh5Attribute("source", new Oh5Scalar("_name:seang")));
    
    assertEquals("PVOL _name:seang 2011-06-22T13:14:15",
                 classUnderTest.nameMetadata(metadata));
  }

  public void testNameMetadata_Scan() {
    Oh5Node what = metadata.root().add(new Oh5Group("what"));
    what.add(new Oh5Attribute("object", new Oh5Scalar("SCAN")));
    what.add(new Oh5Attribute("date", new Oh5Scalar(new Date(2011, 6, 22))));
    what.add(new Oh5Attribute("time", new Oh5Scalar(new Time(13, 14, 15))));
    what.add(new Oh5Attribute("source", new Oh5Scalar("_name:seang"))); 
    Oh5Node ds1 = metadata.root().add(new Oh5Group("dataset1"));
    Oh5Node ds1w = ds1.add(new Oh5Group("where"));
    ds1w.add(new Oh5Attribute("elangle", new Oh5Scalar(12.5)));

    assertEquals("SCAN _name:seang 2011-06-22T13:14:15 elangle=12.5",
                 classUnderTest.nameMetadata(metadata));
  }

  public void testNameMetadata_ScanWithoutElangle() {
    Oh5Node what = metadata.root().add(new Oh5Group("what"));
    what.add(new Oh5Attribute("object", new Oh5Scalar("SCAN")));
    what.add(new Oh5Attribute("date", new Oh5Scalar(new Date(2011, 6, 22))));
    what.add(new Oh5Attribute("time", new Oh5Scalar(new Time(13, 14, 15))));
    what.add(new Oh5Attribute("source", new Oh5Scalar("_name:seang"))); 

    assertEquals("SCAN _name:seang 2011-06-22T13:14:15 elangle=?",
                 classUnderTest.nameMetadata(metadata));
  }
  
  public void testGetSourceRepr() {
    List<String> keyPriorities = new ArrayList<String>();
    keyPriorities.add("PLC");
    keyPriorities.add("NOD");
    classUnderTest.setSourceKeyPriorities(keyPriorities);

    Oh5Source src = new Oh5Source();
    src.add("NOD", "n");
    src.add("PLC", "p");

    assertEquals("PLC:p", classUnderTest.getSourceRepr(src));
  }
  
  public void testGetSourceRepr_noSourceKey() {
    Oh5Source src = new Oh5Source();
    src.add("QWE", "asd");
    assertEquals("unknown", classUnderTest.getSourceRepr(src));
  }

}

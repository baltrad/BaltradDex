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

package eu.baltrad.dex.net.util;

import eu.baltrad.dex.net.util.IncomingFileNamer;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;

import eu.baltrad.bdb.db.FileEntry;
import eu.baltrad.bdb.oh5.Attribute;
import eu.baltrad.bdb.oh5.Group;
import eu.baltrad.bdb.oh5.Metadata;
import eu.baltrad.bdb.oh5.Source;
import eu.baltrad.bdb.util.Date;
import eu.baltrad.bdb.util.Time;

public class IncomingFileNamerTest extends TestCase {
  private IncomingFileNamer classUnderTest;
  private Metadata metadata;

  public void setUp() {
    classUnderTest = new IncomingFileNamer();
    metadata = new Metadata();
  }

  public void testNameMetadata_Pvol() {
    metadata.addNode("/", new Group("what"));
    metadata.addNode("/what", new Attribute("object", "PVOL"));
    metadata.addNode("/what", new Attribute("date", "20110622"));
    metadata.addNode("/what", new Attribute("time", "131415"));
    metadata.addNode("/what", new Attribute("source", "NOD:seang"));
    
    assertEquals("PVOL NOD:seang 2011-06-22T13:14:15",
                 classUnderTest.nameMetadata(metadata));
  }

  public void testNameMetadata_Scan() {
    metadata.addNode("/", new Group("what"));
    metadata.addNode("/what", new Attribute("object", "SCAN"));
    metadata.addNode("/what", new Attribute("date", "20110622"));
    metadata.addNode("/what", new Attribute("time", "131415"));
    metadata.addNode("/what", new Attribute("source", "NOD:seang"));

    metadata.addNode("/", new Group("dataset1"));
    metadata.addNode("/dataset1", new Group("where"));
    metadata.addNode("/dataset1/where", new Attribute("elangle", 12.5));

    assertEquals("SCAN NOD:seang 2011-06-22T13:14:15 elangle=12.5",
                 classUnderTest.nameMetadata(metadata));
  }

  public void testNameMetadata_ScanWithoutElangle() {
    metadata.addNode("/", new Group("what"));
    metadata.addNode("/what", new Attribute("object", "SCAN"));
    metadata.addNode("/what", new Attribute("date", "20110622"));
    metadata.addNode("/what", new Attribute("time", "131415"));
    metadata.addNode("/what", new Attribute("source", "NOD:seang"));

    assertEquals("SCAN NOD:seang 2011-06-22T13:14:15 elangle=?",
                 classUnderTest.nameMetadata(metadata));
  }
  
  public void testGetSourceRepr() {
    List<String> keyPriorities = new ArrayList<String>();
    keyPriorities.add("PLC");
    keyPriorities.add("NOD");
    classUnderTest.setSourceKeyPriorities(keyPriorities);

    Source src = Source.fromString("NOD:n,PLC:p");

    assertEquals("PLC:p", classUnderTest.getSourceRepr(src));
  }
  
  public void testGetSourceRepr_noSourceKey() {
    Source src = new Source();
    src.put("QWE", "asd");
    assertEquals("unknown", classUnderTest.getSourceRepr(src));
  }

  public void testName_fileEntry() {
    metadata.addNode("/", new Group("what"));
    metadata.addNode("/what", new Attribute("object", "PVOL"));
    metadata.addNode("/what", new Attribute("date", "20110622"));
    metadata.addNode("/what", new Attribute("time", "131415"));
    metadata.addNode("/what", new Attribute("source", "WMO:12345"));

    Source src = Source.fromString("NOD:seang");
    
    MockControl entryControl = MockControl.createControl(FileEntry.class);
    FileEntry entry = (FileEntry)entryControl.getMock();

    entry.getMetadata();
    entryControl.setReturnValue(metadata, MockControl.ONE_OR_MORE);
    entry.getSource();
    entryControl.setReturnValue(src, MockControl.ONE_OR_MORE);
    entryControl.replay();

    assertEquals("PVOL NOD:seang 2011-06-22T13:14:15",
                 classUnderTest.name(entry));
    entryControl.verify();
  }
}

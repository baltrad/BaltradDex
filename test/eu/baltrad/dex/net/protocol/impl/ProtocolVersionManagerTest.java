/* --------------------------------------------------------------------
Copyright (C) 2009-2014 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/

package eu.baltrad.dex.net.protocol.impl;

import static org.junit.Assert.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol20;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol21;

/**
 * @author Anders Henja
 */
public class ProtocolVersionManagerTest extends EasyMockSupport {
  private ProtocolVersionManager classUnderTest;
  
  @Before
  public void setUp() throws Exception {
    classUnderTest = new ProtocolVersionManager();
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }
  
  @Test
  public void getFactory() {
    ProtocolVersionRequestFactory factory = (ProtocolVersionRequestFactory)classUnderTest.getFactory("http://127.0.0.1");
    assertEquals(ProtocolVersionRequestFactory.DEFAULT_PROTOCOL_VERSION, factory.getProtocolVersion());
  }

  @Test
  public void getFactory_withVersion() {
    ProtocolVersionRequestFactory factory = (ProtocolVersionRequestFactory)classUnderTest.getFactory("http://127.0.0.1", "2.0");
    assertEquals("2.0", factory.getProtocolVersion());

    factory = (ProtocolVersionRequestFactory)classUnderTest.getFactory("http://127.0.0.1", "2.1");
    assertEquals("2.1", factory.getProtocolVersion());
  }

  @Test
  public void getVersion() {
    assertEquals(ProtocolVersionRequestFactory.DEFAULT_PROTOCOL_VERSION, classUnderTest.getVersion());
  }

  @Test
  public void getVersion_withVersion() {
    classUnderTest = new ProtocolVersionManager("2.0");
    assertEquals("2.0", classUnderTest.getVersion());

    classUnderTest = new ProtocolVersionManager("2.1");
    assertEquals("2.1", classUnderTest.getVersion());
  }

  @Test
  public void getJsonProtocolForVersion() {
    classUnderTest = new ProtocolVersionManager("2.0");
    assertSame(JsonProtocol20.class, classUnderTest.getJsonProtocolForVersion("2.0").getClass());

    classUnderTest = new ProtocolVersionManager("2.1");
    assertSame(JsonProtocol21.class, classUnderTest.getJsonProtocolForVersion("2.1").getClass());
  }
}

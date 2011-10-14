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

package eu.baltrad.dex.util;

import junit.framework.TestCase;

public class NodeAddressTest extends TestCase {
  NodeAddress classUnderTest;

  public void setUp() {
      classUnderTest = new NodeAddress();
  }

  public void testGetNodeAddress() {
      classUnderTest.setScheme("http");
      classUnderTest.setHostAddress("example.com");
      classUnderTest.setPort(80);
      classUnderTest.setAppCtx("BaltradDex");
      classUnderTest.setEntryAddress("inject.htm");
      
      String expected = "http://example.com:80/BaltradDex/inject.htm";
      assertEquals(expected, classUnderTest.getNodeAddress());
  }

  public void testSetNodeAddress() {
      classUnderTest.setNodeAddress("https://example.com:1234/BaltradDex/inject.htm");

      assertEquals("https", classUnderTest.getScheme());
      assertEquals("example.com", classUnderTest.getHostAddress());
      assertEquals(1234, classUnderTest.getPort());
      assertEquals("BaltradDex", classUnderTest.getAppCtx());
      assertEquals("inject.htm", classUnderTest.getEntryAddress());
  }

  public void testSetNodeAddressDefaultPort() {
      classUnderTest.setNodeAddress("https://example.com/BaltradDex/inject.htm");

      assertEquals("https", classUnderTest.getScheme());
      assertEquals("example.com", classUnderTest.getHostAddress());
      assertEquals(8084, classUnderTest.getPort());
      assertEquals("BaltradDex", classUnderTest.getAppCtx());
      assertEquals("inject.htm", classUnderTest.getEntryAddress());
  }
}

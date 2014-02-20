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

import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.protocol.impl.ProtocolVersionResponseParser;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.net.protocol.json.impl.JsonProtocol21;
import eu.baltrad.dex.user.model.User;
import static org.easymock.EasyMock.*;

/**
 * Tests the protocol version response parser
 * @author Anders Henja
 */
public class ProtocolVersionResponseParserTest extends EasyMockSupport {
  interface MethodMock {
    public String readResponse();
  };
  
  private ProtocolVersionResponseParser classUnderTest = null;
  private HttpResponse response = null;
  private JsonProtocol jsonProtocol = null;
  private MethodMock methodMock = null;
  
  @Before
  public void setUp() throws Exception {
    response = createMock(HttpResponse.class);
    jsonProtocol = createMock(JsonProtocol.class);
    methodMock = createMock(MethodMock.class);
    
    classUnderTest = new ProtocolVersionResponseParser() {
      protected String readResponse() {
        return methodMock.readResponse();
      }
    };
    classUnderTest.httpResponse = response;
    classUnderTest.jsonProtocol = jsonProtocol;
  }
  
  @After
  public void tearDown() throws Exception {
    response = null;
    jsonProtocol = null;
    methodMock = null;
    classUnderTest = null;
  }
  
  @Test
  public void parseStatusCode() {
    classUnderTest.httpResponse = null;
    StatusLine sl = createMock(StatusLine.class);
    expect(response.getStatusLine()).andReturn(sl);
    expect(sl.getStatusCode()).andReturn(10);
    
    replayAll();
    int result = classUnderTest.parseStatusCode(response);
    verifyAll();
    assertEquals(10, result);
  }
  
  @Test
  public void parseNodeName() {
    classUnderTest.httpResponse = null;
    Header hdr = createMock(Header.class);
    expect(response.getFirstHeader("Node-Name")).andReturn(hdr);
    expect(hdr.getValue()).andReturn("Nisse");
    
    replayAll();
    String result = classUnderTest.parseNodeName(response);
    verifyAll();
    assertEquals("Nisse", result);
  }
  
  @Test
  public void parseProtocolVersion() {
    classUnderTest.httpResponse = null;
    Header hdr = createMock(Header.class);
    expect(response.getFirstHeader("DEX-Protocol-Version")).andReturn(hdr);
    expect(hdr.getValue()).andReturn("2.1");
    
    replayAll();
    String result = classUnderTest.parseProtocolVersion(response);
    verifyAll();
    assertEquals("2.1", result);
  }

  @Test
  public void parseProtocolVersion_noHeader() {
    classUnderTest.httpResponse = null;
    expect(response.getFirstHeader("DEX-Protocol-Version")).andReturn(null);
    
    replayAll();
    String result = classUnderTest.parseProtocolVersion(response);
    verifyAll();
    assertEquals("2.0", result);
  }
  
  @Test
  public void init() {
    classUnderTest.httpResponse = null;
    classUnderTest.jsonProtocol = null;
    
    StatusLine sl = createMock(StatusLine.class);
    expect(response.getStatusLine()).andReturn(sl);
    expect(sl.getStatusCode()).andReturn(10);
    
    Header hdr = createMock(Header.class);
    expect(response.getFirstHeader("Node-Name")).andReturn(hdr);
    expect(hdr.getValue()).andReturn("Nisse");

    Header hdr2 = createMock(Header.class);
    expect(response.getFirstHeader("DEX-Protocol-Version")).andReturn(hdr2);
    expect(hdr2.getValue()).andReturn("2.1");
   
    replayAll();
    
    classUnderTest.init(response);
    
    verifyAll();
    
    assertEquals(10, classUnderTest.getStatusCode());
    assertEquals("Nisse", classUnderTest.getNodeName());
    assertEquals("2.1", classUnderTest.getProtocolVersion());
    assertSame(JsonProtocol21.class, classUnderTest.jsonProtocol.getClass());
  }
  
  @Test
  public void getConfiguredProtocolVersion() {
    Header hdr = createMock(Header.class);
    expect(response.getFirstHeader("DEX-Node-Protocol-Version")).andReturn(hdr);
    expect(hdr.getValue()).andReturn("2.1");
    
    replayAll();
    
    String result = classUnderTest.getConfiguredProtocolVersion();
    
    verifyAll();
    assertEquals("2.1", result);
  }

  @Test
  public void getConfiguredProtocolVersion_noHeader() {
    expect(response.getFirstHeader("DEX-Node-Protocol-Version")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.getConfiguredProtocolVersion();
    
    verifyAll();
    assertNull(result);
  }

  @Test
  public void getReasonPhrase() {
    StatusLine sl = createMock(StatusLine.class);
    expect(response.getStatusLine()).andReturn(sl);
    expect(sl.getReasonPhrase()).andReturn("help me");

    replayAll();
    
    String result = classUnderTest.getReasonPhrase();
    
    verifyAll();
    assertEquals("help me", result);
  }
  
  @Test
  public void getDataSources() {
    Set<DataSource> dsset = new HashSet<DataSource>();
    expect(methodMock.readResponse()).andReturn("a json string");
    expect(jsonProtocol.jsonToDataSources("a json string")).andReturn(dsset);
    
    replayAll();
    
    Set<DataSource> result = classUnderTest.getDataSources();
    
    verifyAll();
    assertSame(dsset, result);
  }
  
  @Test
  public void getUserAccount() {
    User usr = new User();
    expect(methodMock.readResponse()).andReturn("a json string");
    expect(jsonProtocol.jsonToUserAccount("a json string")).andReturn(usr);
    
    replayAll();
    
    User result = classUnderTest.getUserAccount();
    
    verifyAll();
    assertSame(usr, result);
  }
}

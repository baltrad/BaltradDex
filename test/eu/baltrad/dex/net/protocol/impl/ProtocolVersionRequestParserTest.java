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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMockSupport;
import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keyczar.exceptions.KeyczarException;

import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.message.mo.BltAlertMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.net.request.impl.NodeRequest;
import eu.baltrad.dex.user.model.User;

/**
 * @author Anders Henja
 */
public class ProtocolVersionRequestParserTest extends EasyMockSupport {
  interface MethodMock {
    public JsonProtocol getJsonProtocol(String version);
    public String readInputStream() throws IOException;
  };
  private ProtocolVersionRequestParser classUnderTest;
  private NodeRequest request;
  private IXmlMessageParser xmlMessageParser;
  private MethodMock methods;
  
  @Before
  public void setUp() throws Exception {
    request = createMock(NodeRequest.class);
    xmlMessageParser = createMock(IXmlMessageParser.class);
    methods = createMock(MethodMock.class);
    classUnderTest = new ProtocolVersionRequestParser(request, "2.1", xmlMessageParser) {
      @Override
      protected NodeRequest createNodeRequest(HttpServletRequest req) {
        return request;
      }
      @Override
      protected String readInputStream() throws IOException {
        return methods.readInputStream();
      }
      @Override
      protected JsonProtocol getJsonProtocol(String version) {
        return methods.getJsonProtocol(version);
      }
    };
  }
  
  @After
  public void tearDown() throws Exception {
    request = null;
    xmlMessageParser = null;
    classUnderTest = null;
  }
  
  @Test
  public void getMessage() {
    expect(request.getMessage()).andReturn("a message");
    
    replayAll();
    
    String result = classUnderTest.getMessage();
    
    verifyAll();
    assertEquals("a message", result);
  }

  @Test
  public void getSignature() {
    expect(request.getSignature()).andReturn("a signature");
    
    replayAll();
    
    String result = classUnderTest.getSignature();
    
    verifyAll();
    assertEquals("a signature", result);
  }

  @Test
  public void getUser() {
    expect(request.getUser()).andReturn("a user");
    
    replayAll();
    
    String result = classUnderTest.getUser();
    
    verifyAll();
    assertEquals("a user", result);
  }

  @Test
  public void getNodeName() {
    expect(request.getNodeName()).andReturn("a node name");
    
    replayAll();
    
    String result = classUnderTest.getNodeName();
    
    verifyAll();
    assertEquals("a node name", result);
  }

  @Test
  public void getProtocolVersion() {
    expect(request.getProtocolVersion()).andReturn("1.1");
    
    replayAll();
    
    String result = classUnderTest.getProtocolVersion();
    
    verifyAll();
    assertEquals("1.1", result);
  }
  
  @Test
  public void isAuthenticated() throws Exception {
    Authenticator authenticator = createMock(Authenticator.class);
    expect(request.getMessage()).andReturn("message");
    expect(request.getSignature()).andReturn("signature");
    expect(request.getNodeName()).andReturn("node name");
    expect(authenticator.authenticate("message", "signature", "node name")).andReturn(true);
    
    replayAll();
    
    boolean result = classUnderTest.isAuthenticated(authenticator);
    
    verifyAll();
    assertEquals(true, result);
  }
  
  @Test
  public void isAuthenticated_Unauthorized() throws Exception {
    Authenticator authenticator = createMock(Authenticator.class);
    expect(request.getMessage()).andReturn("message");
    expect(request.getSignature()).andReturn("signature");
    expect(request.getNodeName()).andReturn("node name");
    expect(authenticator.authenticate("message", "signature", "node name")).andReturn(false);
    
    replayAll();
    
    boolean result = classUnderTest.isAuthenticated(authenticator);
    
    verifyAll();
    assertEquals(false, result);
  }  
  
  @Test
  public void isAuthenticated_KeyczarException() throws Exception {
    Authenticator authenticator = createMock(Authenticator.class);
    expect(request.getMessage()).andReturn("message");
    expect(request.getSignature()).andReturn("signature");
    expect(request.getNodeName()).andReturn("node name");
    expect(authenticator.authenticate("message", "signature", "node name")).andThrow(new KeyczarException("failure"));
    
    replayAll();
    
    try {
      classUnderTest.isAuthenticated(authenticator);
      fail("Expected KeyczarException");
    } catch (KeyczarException e) {
      // pass
    }
    
    verifyAll();
  }
  
  @Test
  public void getUserAccount() throws Exception {
    User user = new User();
    JsonProtocol jsonProtocol = createMock(JsonProtocol.class);
    expect(request.getProtocolVersion()).andReturn("2.1");
    expect(methods.getJsonProtocol("2.1")).andReturn(jsonProtocol);
    expect(methods.readInputStream()).andReturn("data");
    expect(jsonProtocol.jsonToUserAccount("data")).andReturn(user);
    
    replayAll();
    
    User result = classUnderTest.getUserAccount();
    
    verifyAll();
    assertSame(user, result);
  }
  
  @Test
  public void getDataSources() throws Exception {
    Set<DataSource> dataSources = new HashSet<DataSource>();
    JsonProtocol jsonProtocol = createMock(JsonProtocol.class);
    expect(request.getProtocolVersion()).andReturn("2.1");
    expect(methods.getJsonProtocol("2.1")).andReturn(jsonProtocol);
    expect(methods.readInputStream()).andReturn("data");
    expect(jsonProtocol.jsonToDataSources("data")).andReturn(dataSources);
    
    replayAll();
    
    Set<DataSource> result = classUnderTest.getDataSources();
    
    verifyAll();
    assertSame(dataSources, result);
  }
  
  @Test
  public void getSubscriptions() throws Exception {
    List<Subscription> subscriptions = new ArrayList<Subscription>();
    JsonProtocol jsonProtocol = createMock(JsonProtocol.class);
    expect(request.getProtocolVersion()).andReturn("2.1");
    expect(methods.getJsonProtocol("2.1")).andReturn(jsonProtocol);
    expect(methods.readInputStream()).andReturn("data");
    expect(jsonProtocol.jsonToSubscriptions("data")).andReturn(subscriptions);
    
    replayAll();
    
    List<Subscription> result = classUnderTest.getSubscriptions();
    
    verifyAll();
    assertSame(subscriptions, result);
  }
  
  @Test
  public void getBltXmlMessage() throws Exception {
    IBltXmlMessage xmlMessage = new BltAlertMessage();
    expect(methods.readInputStream()).andReturn("data");
    expect(xmlMessageParser.parse("data")).andReturn(xmlMessage);
    
    replayAll();
    
    IBltXmlMessage result = classUnderTest.getBltXmlMessage();
    
    verifyAll();
    assertSame(xmlMessage, result);
  }
}

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

package eu.baltrad.dex.net.util;

import static org.junit.Assert.*;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.log4j.Logger;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.log.StickyLevel;
import eu.baltrad.dex.net.auth.Authenticator;
import eu.baltrad.dex.net.protocol.ProtocolManager;
import eu.baltrad.dex.net.protocol.RequestFactory;
import eu.baltrad.dex.net.protocol.ResponseParser;
import eu.baltrad.dex.net.util.httpclient.impl.HttpClientUtil;
import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import static org.easymock.EasyMock.*;

/**
 * @author Anders Henja
 */
public class PostFileRedirectHandlerTest extends EasyMockSupport {
  private PostFileRedirectHandler classUnderTest = null;
  private ProtocolManager protocolManager = null;
  private User peerUser = null;
  private User localUser = null;
  private Authenticator authenticator = null;
  private IUserManager userManager = null;
  private Logger log = null;
  private MessageResourceUtil messages;
  private byte[] fileContent = new byte[0];
  
  @Before
  public void setUp() throws Exception {
    protocolManager = createMock(ProtocolManager.class);
    peerUser = createMock(User.class);
    localUser = createMock(User.class);
    authenticator = createMock(Authenticator.class);
    userManager = createMock(UserManager.class);
    messages = createMock(MessageResourceUtil.class);
    log = createMock(Logger.class);
    
    classUnderTest = new PostFileRedirectHandler();
    classUnderTest.setProtocolManager(protocolManager);
    classUnderTest.setAuthenticator(authenticator);
    classUnderTest.setUserManager(userManager);
    classUnderTest.setLocalUser(localUser);
    classUnderTest.setPeerUser(peerUser);
    classUnderTest.setMessages(messages);
    classUnderTest.log = log;
    classUnderTest.setFileContent(fileContent);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }
  
  @Test
  public void canHandle_SC_MOVED_PERMANENTLY() {
    HttpResponse response = createMock(HttpResponse.class);
    StatusLine statusLine = createMock(StatusLine.class);
    expect(response.getStatusLine()).andReturn(statusLine).anyTimes();
    expect(statusLine.getStatusCode()).andReturn(HttpServletResponse.SC_MOVED_PERMANENTLY).anyTimes();
    replayAll();
    assertTrue(classUnderTest.canHandle(response));
    verifyAll();
  }

  @Test
  public void canHandle_SC_MOVED_TEMPORARILY() {
    HttpResponse response = createMock(HttpResponse.class);
    StatusLine statusLine = createMock(StatusLine.class);
    expect(response.getStatusLine()).andReturn(statusLine).anyTimes();
    expect(statusLine.getStatusCode()).andReturn(HttpServletResponse.SC_MOVED_TEMPORARILY).anyTimes();
    replayAll();
    assertTrue(classUnderTest.canHandle(response));
    verifyAll();
  }
  
  @Test
  public void handle() throws Exception {
    HttpResponse response = createMock(HttpResponse.class);
    ResponseParser parser = createMock(ResponseParser.class);
    HttpUriRequest request = createMock(HttpUriRequest.class);
    RequestFactory requestFactory = createMock(RequestFactory.class);
    HttpUriRequest httpUriRequest = createMock(HttpUriRequest.class);
    HttpClientUtil httpClient = createMock(HttpClientUtil.class);
    HttpResponse httpClientResponse = createMock(HttpResponse.class);
    
    expect(protocolManager.createParser(response)).andReturn(parser);
    expect(parser.isRedirected()).andReturn(true);
    expect(peerUser.getNodeAddress()).andReturn("http://slask.se");
    expect(request.getURI()).andReturn(URI.create("http://slask.se/BaltradDex/post_file.htm"));
    expect(parser.getRedirectURL()).andReturn("https://somewhere.se/BaltradDex/post_file.htm");
    expect(peerUser.getRedirectedAddress()).andReturn(null);
    peerUser.setRedirectedAddress("https://somewhere.se");
    userManager.update(peerUser);
    expect(peerUser.getName()).andReturn("se.somewhere");
    expect(messages.getMessage(eq("postfile.server.connection_server_update_redirected"), aryEq(new String[]{"se.somewhere","https://somewhere.se"}))).andReturn("HELLO");
    log.log(StickyLevel.STICKY, "HELLO");
    expect(protocolManager.getFactory("https://somewhere.se")).andReturn(requestFactory);
    expect(requestFactory.createPostFileRequest(localUser, fileContent)).andReturn(httpUriRequest);
    expect(localUser.getName()).andReturn("local.user");
    authenticator.addCredentials(httpUriRequest, "local.user");
    expect(httpClient.post(httpUriRequest)).andReturn(httpClientResponse);
    
    replayAll();
    
    HttpResponse result = classUnderTest.handle(httpClient, request, response);
    
    verifyAll();
    assertSame(httpClientResponse, result);
  }

  @Test
  public void handle_alreadyRedirected() throws Exception {
    HttpResponse response = createMock(HttpResponse.class);
    ResponseParser parser = createMock(ResponseParser.class);
    HttpUriRequest request = createMock(HttpUriRequest.class);
    RequestFactory requestFactory = createMock(RequestFactory.class);
    HttpUriRequest httpUriRequest = createMock(HttpUriRequest.class);
    HttpClientUtil httpClient = createMock(HttpClientUtil.class);
    HttpResponse httpClientResponse = createMock(HttpResponse.class);
    
    expect(protocolManager.createParser(response)).andReturn(parser);
    expect(parser.isRedirected()).andReturn(true);
    expect(peerUser.getNodeAddress()).andReturn("http://slask.se");
    expect(request.getURI()).andReturn(URI.create("http://slask.se/BaltradDex/post_file.htm"));
    expect(parser.getRedirectURL()).andReturn("https://somewhere.se/BaltradDex/post_file.htm");
    expect(peerUser.getRedirectedAddress()).andReturn("https://somewhere.se");
    expect(protocolManager.getFactory("https://somewhere.se")).andReturn(requestFactory);
    expect(requestFactory.createPostFileRequest(localUser, fileContent)).andReturn(httpUriRequest);
    expect(localUser.getName()).andReturn("local.user");
    authenticator.addCredentials(httpUriRequest, "local.user");
    expect(httpClient.post(httpUriRequest)).andReturn(httpClientResponse);
    
    replayAll();
    
    HttpResponse result = classUnderTest.handle(httpClient, request, response);
    
    verifyAll();
    assertSame(httpClientResponse, result);
  }
  
  @Test
  public void handle_isNotRedirected() throws Exception {
    HttpResponse response = createMock(HttpResponse.class);
    ResponseParser parser = createMock(ResponseParser.class);
    HttpUriRequest request = createMock(HttpUriRequest.class);
    HttpClientUtil httpClient = createMock(HttpClientUtil.class);
    
    expect(protocolManager.createParser(response)).andReturn(parser);
    expect(parser.isRedirected()).andReturn(false);
    
    replayAll();
    
    HttpResponse result = classUnderTest.handle(httpClient, request, response);
    
    verifyAll();
    assertNull(result);
  }
  
}

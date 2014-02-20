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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.net.model.impl.Subscription;
import eu.baltrad.dex.net.protocol.json.JsonProtocol;
import eu.baltrad.dex.user.model.User;
import static org.easymock.EasyMock.*;

/**
 * @author Anders Henja
 */
public class ProtocolVersionResponseWriterTest extends EasyMockSupport {
  interface MethodMock {
    HttpServletResponseWrapper createResponseWrapper(HttpServletResponse response);
    PrintWriter createPrintWriter(HttpServletResponseWrapper wrapper) throws IOException;
    JsonProtocol getJsonProtocol(String version);
  };
  
  private HttpServletResponseWrapper responseWrapper;
  private ProtocolVersionResponseWriter classUnderTest;
  private PrintWriter printWriter;
  private JsonProtocol jsonProtocol;
  
  @Before 
  public void setUp() throws Exception {
    responseWrapper = createMock(HttpServletResponseWrapper.class);
    printWriter = createMock(PrintWriter.class);
    jsonProtocol = createMock(JsonProtocol.class);
    
    classUnderTest = new ProtocolVersionResponseWriter(null, "2.0", "2.1") {
      @Override
      protected HttpServletResponseWrapper createResponseWrapper(HttpServletResponse response) {
        return responseWrapper;
      }
      @Override
      protected PrintWriter createPrintWriter(HttpServletResponseWrapper wrapper) throws IOException {
        return printWriter;
      }
      @Override
      protected JsonProtocol getJsonProtocol(String version) {
        return jsonProtocol;
      }
    };
  }
  
  @After
  public void tearDown() throws Exception {
    
  }
  
  @Test
  public void userAccountResponse() throws Exception {
    User user = createMock(User.class);
    expect(jsonProtocol.userAccountToJson(user)).andReturn("data");
    printWriter.print("data");
    responseWrapper.setStatus(HttpServletResponse.SC_OK);
    responseWrapper.addHeader("Node-Name", "local node");
    responseWrapper.addHeader("DEX-Protocol-Version", "2.0");
    responseWrapper.addHeader("DEX-Node-Protocol-Version", "2.1");
    printWriter.close();
    
    replayAll();
    
    classUnderTest.userAccountResponse("local node", user, HttpServletResponse.SC_OK);
    
    verifyAll();
  }
  
  @Test
  public void dataSourcesResponse() throws Exception {
    Set<DataSource> dataSources = new HashSet<DataSource>();
    
    expect(jsonProtocol.dataSourcesToJson(dataSources)).andReturn("data");
    printWriter.print("data");
    responseWrapper.setStatus(HttpServletResponse.SC_OK);
    responseWrapper.addHeader("Node-Name", "local node");
    responseWrapper.addHeader("DEX-Protocol-Version", "2.0");
    responseWrapper.addHeader("DEX-Node-Protocol-Version", "2.1");
    printWriter.close();
    
    replayAll();
    
    classUnderTest.dataSourcesResponse("local node", dataSources, HttpServletResponse.SC_OK);
    
    verifyAll();
  }
  
  @Test
  public void subscriptionResponse() throws Exception {
    List<Subscription> subscriptions = new ArrayList<Subscription>();
    
    expect(jsonProtocol.subscriptionsToJson(subscriptions)).andReturn("data");
    printWriter.print("data");
    responseWrapper.setStatus(HttpServletResponse.SC_OK);
    responseWrapper.addHeader("Node-Name", "local node");
    responseWrapper.addHeader("DEX-Protocol-Version", "2.0");
    responseWrapper.addHeader("DEX-Node-Protocol-Version", "2.1");
    printWriter.close();
    
    replayAll();
    
    classUnderTest.subscriptionResponse("local node", subscriptions, HttpServletResponse.SC_OK);
    
    verifyAll();
  }
  
  @Test
  public void messageResponse() {
    responseWrapper.setStatus(HttpServletResponse.SC_NOT_FOUND, "not found");
    
    replayAll();
    
    classUnderTest.messageResponse("not found", HttpServletResponse.SC_NOT_FOUND);
    
    verifyAll();
  }
  
  @Test
  public void statusResponse() {
    responseWrapper.setStatus(HttpServletResponse.SC_NOT_FOUND);
    
    replayAll();
    
    classUnderTest.statusResponse(HttpServletResponse.SC_NOT_FOUND);
    
    verifyAll();
  }

}

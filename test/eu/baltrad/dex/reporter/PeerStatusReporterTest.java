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

package eu.baltrad.dex.reporter;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.system.SystemStatus;
import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.user.manager.IUserManager;

/**
 * @author Anders Henja
 */
public class PeerStatusReporterTest  extends EasyMockSupport {
  private PeerStatusReporter classUnderTest = null;
  private INodeStatusManager statusManager = null;
  private IUserManager userManager = null;
  
  interface MethodMock {
    SystemStatus getPeerStatus(String s, int minutesBackInTime);
  }
  private MethodMock methodMock;
  
  @Before
  public void setUp() throws Exception {
    userManager = createMock(IUserManager.class);
    statusManager = createMock(INodeStatusManager.class);
    methodMock = createMock(MethodMock.class);
    classUnderTest = new PeerStatusReporter() {
      @Override
      public SystemStatus getPeerStatus(String s, int minutesBackInTime) {
        return methodMock.getPeerStatus(s, minutesBackInTime);
      }
    };
    classUnderTest.setUserManager(userManager);
    classUnderTest.setNodeStatusManager(statusManager);
  }
  
  @After
  public void tearDown() throws Exception {
    statusManager = null;
    userManager = null;
    classUnderTest = null;
  }
  
  @Test
  public void test_getMappedStatus_allNodes() {
    Map<String, Object> request = new HashMap<String,Object>();
    
    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n1", 5)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(2, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n1"));
    assertEquals(SystemStatus.OK, mapping.get("n2"));
  }
  
  @Test
  public void test_getMappedStatus_allNodes_10minutesBackInTime() {
    Map<String, Object> request = new HashMap<String,Object>();
    request.put("minutes", new Integer(10));
    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n1", 10)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n2", 10)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(2, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n1"));
    assertEquals(SystemStatus.OK, mapping.get("n2"));
  }
  
  @Test
  public void test_getMappedStatus_allNodes_oneFailure() {
    Map<String, Object> request = new HashMap<String,Object>();
    
    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n1", 5)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.COMMUNICATION_PROBLEM);
    expect(methodMock.getPeerStatus("n3", 5)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(3, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n1"));
    assertEquals(SystemStatus.COMMUNICATION_PROBLEM, mapping.get("n2"));
    assertEquals(SystemStatus.OK, mapping.get("n3"));
  }

  @Test
  public void test_getMappedStatus_specificNode() {
    Map<String, Object> request = new HashMap<String,Object>();
    request.put("peers", "n2");

    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);

    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(1, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n2"));
  }

  @Test
  public void test_getMappedStatus_specificNode_10MinutesBackInTime() {
    Map<String, Object> request = new HashMap<String,Object>();
    request.put("peers", "n2");
    request.put("minutes", "10");

    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);

    expect(methodMock.getPeerStatus("n2", 10)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(1, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n2"));
  }
  
  @Test
  public void test_getMappedStatus_twoSpecificNode() {
    Map<String, Object> request = new HashMap<String,Object>();
    request.put("peers", "n2,n3");

    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n3", 5)).andReturn(SystemStatus.COMMUNICATION_PROBLEM);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(2, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n2"));
    assertEquals(SystemStatus.COMMUNICATION_PROBLEM, mapping.get("n3"));
  }
  
  @Test
  public void test_getMappedStatus_twoSpecificNode_oneNonExisting() {
    Map<String, Object> request = new HashMap<String,Object>();
    Set<String> runtimeNodes = new HashSet<String>();
    request.put("peers", "n2,n3,n4");

    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n3", 5)).andReturn(SystemStatus.COMMUNICATION_PROBLEM);
    expect(statusManager.getRuntimeNodeNames()).andReturn(runtimeNodes);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(3, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n2"));
    assertEquals(SystemStatus.COMMUNICATION_PROBLEM, mapping.get("n3"));
    assertEquals(SystemStatus.UNDEFINED, mapping.get("n4"));
  }
  
  @Test
  public void test_getMappedStatus_twoSpecificNode_oneRuntimeNode() {
    Map<String, Object> request = new HashMap<String,Object>();
    Set<String> runtimeNodes = new HashSet<String>();
    request.put("peers", "n2,n3,n4");
    runtimeNodes.add("n4");

    List<String> peers = new ArrayList<String>();
    peers.add("n1"); 
    peers.add("n2");
    peers.add("n3");

    expect(userManager.loadPeerNames()).andReturn(peers);
    expect(methodMock.getPeerStatus("n2", 5)).andReturn(SystemStatus.OK);
    expect(methodMock.getPeerStatus("n3", 5)).andReturn(SystemStatus.COMMUNICATION_PROBLEM);
    expect(statusManager.getRuntimeNodeNames()).andReturn(runtimeNodes);
    expect(methodMock.getPeerStatus("n4", 5)).andReturn(SystemStatus.OK);
    
    replayAll();
    
    Map<String, Map<Object, SystemStatus>> result = classUnderTest.getMappedStatus(request);
    
    verifyAll();
    
    assertEquals(1, result.size());
    Map<Object, SystemStatus> mapping = result.get("peer.status");
    assertEquals(3, mapping.size());
    assertEquals(SystemStatus.OK, mapping.get("n2"));
    assertEquals(SystemStatus.COMMUNICATION_PROBLEM, mapping.get("n3"));
    assertEquals(SystemStatus.OK, mapping.get("n4"));
  }

}

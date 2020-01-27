/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.status.controller;

import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
//import sun.security.acl.PrincipalImpl;

/**
 * Node status controller test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 2.0.0
 * @since 2.0.0
 */
public class NodeStatusControllerTest {
    
    private NSController classUnderTest;
    private INodeStatusManager nodeStatusManagerMock;
    private List mocks;
    
    private List<Status> downloadStatus;
    private List<Status> uploadStatus;
    
    class NSController extends NodeStatusController {
        
        public NSController() {}
        
        public Map<String, Boolean> getNodesStatus() {
            return nodesStatus;
        }
        public Map<String, List<Status>> getDownloads() {
            return downloads;
        }
        public Map<String, List<Status>> getUploads() {
            return uploads;
        }
    }
    
    private Object createMock(Class clazz) {
        Object mock = EasyMock.createMock(clazz);
        mocks.add(mock);
        return mock;
    }
    
    private void replayAll() {
        for (Object mock : mocks) {
            replay(mock);
        }
    }
    
    private void verifyAll() {
        for (Object mock : mocks) {
            verify(mock);
        }
    }
    
    private void resetAll() {
        for (Object mock : mocks) {
            reset(mock);
        }
    }
    
    @Before
    public void setUp() {
        mocks = new ArrayList();
        classUnderTest = new NSController();
        nodeStatusManagerMock = (INodeStatusManager) 
                createMock(INodeStatusManager.class);
        downloadStatus = new ArrayList<Status>();
        uploadStatus = new ArrayList<Status>();
    }
    
    @After
    public void tearDown() {
        resetAll();
        mocks = null;
        classUnderTest = null;
        downloadStatus = null;
        uploadStatus = null;
    }
    
//    @Test
//    public void showStatus() {
//        Model model = new ExtendedModelMap();
//        Principal principal = new PrincipalImpl("user");
//        HttpSession session = new MockHttpSession();
//        
//        assertEquals("status", 
//                classUnderTest.showStatus(model, principal, session));
//    }
    
    @Test
    public void getNodeNames() {
        List<String> nodeNames = Arrays.asList(new String[] {"se.baltrad.eu",
                    "no.met.beast", "dk.dmi"});
        expect(nodeStatusManagerMock.loadNodeNames())
                .andReturn(nodeNames).once();
        
        replayAll();
        
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        
        assertEquals(nodeNames, classUnderTest.getNodeNames());  
        
        verifyAll();
    }
    
    @Test
    public void processSubmit_ShowHideTransfers() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        downloadStatus.add(new Status(1,2,3));
        downloadStatus.add(new Status(3,2,1));
        
        expect(nodeStatusManagerMock.load("se.baltrad.eu", "local"))
                .andReturn(downloadStatus).once();
        
        uploadStatus.add(new Status(4,5,6));
        uploadStatus.add(new Status(6,5,4));
        uploadStatus.add(new Status(7,8,9));
        
        expect(nodeStatusManagerMock.load("se.baltrad.eu", "peer"))
                .andReturn(uploadStatus).once();
        
        replayAll();
        
        classUnderTest.setNodeStatusManager(nodeStatusManagerMock);
        Model model = new ExtendedModelMap();
        // show
        classUnderTest.processSubmit(model, "se.baltrad.eu", request);
        
        assertTrue(classUnderTest.getNodesStatus()
                                            .containsKey("se.baltrad.eu"));
        assertTrue(classUnderTest.getNodesStatus().get("se.baltrad.eu"));
        assertTrue(classUnderTest.getDownloads().containsKey("se.baltrad.eu"));
        assertTrue(classUnderTest.getUploads().containsKey("se.baltrad.eu"));
        assertTrue(classUnderTest.getDownloads()
                .get("se.baltrad.eu").size() == downloadStatus.size());
        assertTrue(classUnderTest.getUploads()
                .get("se.baltrad.eu").size() == uploadStatus.size());
        
        // hide
        classUnderTest.processSubmit(model, "se.baltrad.eu", request);
        
        assertTrue(classUnderTest.getNodesStatus()
                                            .containsKey("se.baltrad.eu"));
        assertFalse(classUnderTest.getNodesStatus().get("se.baltrad.eu"));
        assertTrue(classUnderTest.getDownloads().containsKey("se.baltrad.eu"));
        assertTrue(classUnderTest.getUploads().containsKey("se.baltrad.eu"));
        assertTrue(classUnderTest.getDownloads()
                .get("se.baltrad.eu").size() == downloadStatus.size());
        assertTrue(classUnderTest.getUploads()
                .get("se.baltrad.eu").size() == uploadStatus.size());
        
        verifyAll();
    }
    
}

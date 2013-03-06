/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.net.servlet;

import eu.baltrad.dex.config.manager.IConfigurationManager;
import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import java.io.File;
import javax.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Post key servlet test.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class PostKeyServletTest {
    
    private PostKeyServlet classUnderTest;
    private IConfigurationManager confManagerMock;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MessageResourceUtil messages;
    
    private List mocks;

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
        classUnderTest = new PostKeyServlet();
        confManagerMock = (IConfigurationManager)
                createMock(IConfigurationManager.class);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        messages = new MessageResourceUtil();
        messages.setBasename("resources/messages");
        classUnderTest.setMessages(messages);
    }
    
    @After
    public void tearDown() {
        classUnderTest = null;
        File f = new File("keystore/test.baltrad.eu.pub.zip");
        if (f.exists()) {
            f.delete();
        }
        resetAll();
    }

    @Test
    public void keyExists() {
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(confManagerMock.getAppConf()).andReturn(appConf).times(2);
        
        replayAll();
        
        classUnderTest.setConfManager(confManagerMock);
        assertTrue(classUnderTest.keyExists("localhost"));
        assertFalse(classUnderTest.keyExists("test.baltrad.eu"));
        
        verifyAll();
    }
    
    @Test
    public void storeKey() {
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        
        expect(confManagerMock.getAppConf()).andReturn(appConf);
        
        replayAll();
        
        classUnderTest.setConfManager(confManagerMock);
        request.setContent("public key content".getBytes());
        
        classUnderTest.storeKey(request, "test.baltrad.eu");
        
        verifyAll();
        
        assertTrue((new File("keystore/test.baltrad.eu.pub.zip")).exists());
    }
    
    @Test
    public void handleRequest_ServerError() {
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        expect(confManagerMock.getAppConf()).andReturn(appConf).times(2);
        
        classUnderTest.setConfManager(confManagerMock);
        
        replayAll();
        
        request.setAttribute("Node-Name", "test.baltrad.eu");
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                response.getStatus());
        assertEquals(messages.getMessage("postkey.server.internal_server_error"),
                response.getErrorMessage());
    }
    
    @Test
    public void handleRequest_KeyExists() {
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        expect(confManagerMock.getAppConf()).andReturn(appConf);
        
        classUnderTest.setConfManager(confManagerMock);
        
        replayAll();
        
        request.setAttribute("Node-Name", "localhost");
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }
    
    @Test
    public void handleRequest_OK() {
        Properties props = new Properties();
        props.setProperty(AppConfiguration.KEYSTORE_DIR, "keystore");
        AppConfiguration appConf = new AppConfiguration(props);
        expect(confManagerMock.getAppConf()).andReturn(appConf).times(2);
        
        classUnderTest.setConfManager(confManagerMock);
        
        replayAll();
        
        request.setAttribute("Node-Name", "test.baltrad.eu");
        request.setContent("public key content".getBytes());
        classUnderTest.handleRequest(request, response);
        
        verifyAll();
        
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
}

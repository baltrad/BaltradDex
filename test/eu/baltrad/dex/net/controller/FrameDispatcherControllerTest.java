/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.net.controller;

import eu.baltrad.beast.manager.IBltMessageManager;
import eu.baltrad.beast.message.IBltXmlMessage;
import eu.baltrad.beast.parser.IXmlMessageParser;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.easymock.MockControl;

import java.util.HashMap;

/**
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.7
 * @since 0.7.7
 */
public class FrameDispatcherControllerTest extends TestCase {

    
// Mocking stuff
    interface MethodMock {
      boolean isAuthenticated(HashMap parms);
      String getConfigNodeName();
    };
    
    MockControl methodControl = null;
    MethodMock methods = null;
    MockControl parserControl = null;
    IXmlMessageParser parser = null;
    MockControl managerControl = null;
    IBltMessageManager manager = null;
      
//------------------------------------------------------------------------------------------ Methods    
    @Override
    public void setUp() throws Exception {
        methodControl = MockControl.createControl(MethodMock.class);
        methods = (MethodMock)methodControl.getMock();
        parserControl = MockControl.createControl(IXmlMessageParser.class);
        parser = (IXmlMessageParser)parserControl.getMock();
        managerControl = MockControl.createControl(IBltMessageManager.class);
        manager = (IBltMessageManager)managerControl.getMock();
    }
    
    @Override
    public void tearDown() throws Exception {
        methodControl = null;
        methods = null;
        parserControl = null;
        parser = null;
        managerControl = null;
        manager = null;
    }
    
    protected void replay() throws Exception {
      methodControl.replay();
      parserControl.replay();
      managerControl.replay();
    }

    protected void verify() throws Exception {
      methodControl.verify();
      parserControl.verify();
      managerControl.verify();
    }

    
    
    public void testHandleMessageDeliveryRequest_success() throws Exception {
      IBltXmlMessage bltmsg = new IBltXmlMessage() {
        public Document toDocument() {return null;}
        public void fromDocument(Document arg0) {}
      };
      
      HashMap<String,String> parms = new HashMap<String,String>();
      parms.put("BF_NodeName", "se.smhi.main");
      parms.put("BF_MessageField", "<bltalert>..</bltalert>");
      
      methods.isAuthenticated(parms);
      methodControl.setReturnValue(true);
      methods.getConfigNodeName();
      methodControl.setReturnValue("se.smhi.main");
      parser.parse("<bltalert>..</bltalert>");
      parserControl.setReturnValue(bltmsg);
      manager.manage(bltmsg);
      
      FrameDispatcherController classUnderTest = new FrameDispatcherController() {
        boolean isAuthenticated(HashMap parms) {
          return methods.isAuthenticated(parms);
        }
        String getConfigNodeName() {
          return methods.getConfigNodeName();
        }
      };
      classUnderTest.setBltMessageManager(manager);
      classUnderTest.setXmlMessageParser(parser);
      
      replay();
      
      classUnderTest.handleMessageDeliveryRequest(parms, null);

      verify();
    }

    public void testHandleMessageDeliveryRequest_illegalSender() throws Exception {
      IBltXmlMessage bltmsg = new IBltXmlMessage() {
        public Document toDocument() {return null;}
        public void fromDocument(Document arg0) {}
      };
      
      HashMap<String,String> parms = new HashMap<String,String>();
      parms.put("BF_NodeName", "bad.one");
      parms.put("BF_MessageField", "<bltalert>..</bltalert>");
      
      methods.isAuthenticated(parms);
      methodControl.setReturnValue(false);
      
      FrameDispatcherController classUnderTest = new FrameDispatcherController() {
        boolean isAuthenticated(HashMap parms) {
          return methods.isAuthenticated(parms);
        }
        String getConfigNodeName() {
          return methods.getConfigNodeName();
        }
      };
      classUnderTest.setBltMessageManager(manager);
      classUnderTest.setXmlMessageParser(parser);
      
      replay();
      
      classUnderTest.handleMessageDeliveryRequest(parms, null);

      verify();
    }

    public void testHandleMessageDeliveryRequest_notBeastMessage() throws Exception {
      IBltXmlMessage bltmsg = new IBltXmlMessage() {
        public Document toDocument() {return null;}
        public void fromDocument(Document arg0) {}
      };
      
      HashMap<String,String> parms = new HashMap<String,String>();
      parms.put("BF_NodeName", "se.smhi.main");
      parms.put("BF_MessageField", "my message");
      
      methods.isAuthenticated(parms);
      methodControl.setReturnValue(true);
      methods.getConfigNodeName();
      methodControl.setReturnValue("se.smhi.main");
      parser.parse("my message");
      parserControl.setThrowable(new RuntimeException());
      
      FrameDispatcherController classUnderTest = new FrameDispatcherController() {
        boolean isAuthenticated(HashMap parms) {
          return methods.isAuthenticated(parms);
        }
        String getConfigNodeName() {
          return methods.getConfigNodeName();
        }
      };
      classUnderTest.setBltMessageManager(manager);
      classUnderTest.setXmlMessageParser(parser);
      
      replay();
      
      classUnderTest.handleMessageDeliveryRequest(parms, null);

      verify();
    }
    
    
}
//--------------------------------------------------------------------------------------------------

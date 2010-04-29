/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the beast-ui package.

The beast-ui package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The beast-ui package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the beast-ui package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/
package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.easymock.MockControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.adaptor.xmlrpc.XmlRpcAdaptor;

import junit.framework.TestCase;

/**
 * @author Anders Henja
 */
public class AdaptorsControllerTest extends TestCase {
  private AdaptorsController classUnderTest = null;
  private MockControl managerControl = null;
  private IBltAdaptorManager manager = null;
  private MockControl modelControl = null;
  private Model model = null;
  
  public void setUp() throws Exception {
    managerControl = MockControl.createControl(IBltAdaptorManager.class);
    manager = (IBltAdaptorManager)managerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model)modelControl.getMock();
    classUnderTest = new AdaptorsController();
    classUnderTest.setManager(manager);
  }
  
  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    modelControl = null;
    model = null;
  }
  
  private void replay() {
    managerControl.replay();
    modelControl.replay();
  }
  
  private void verify() {
    managerControl.verify();
    modelControl.verify();
  }
  
  public void testShowAdaptors() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    manager.getRegisteredAdaptors();
    managerControl.setReturnValue(adaptors);
    model.addAttribute("emessage", null);
    modelControl.setReturnValue(null);
    model.addAttribute("adaptors", adaptors);
    modelControl.setReturnValue(null);
    replay();
    
    String result = classUnderTest.showAdaptors(model,null);
    
    verify();
    assertEquals(result, "adaptors");
  }

  public void testShowAdaptors_withErrorMessage() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    manager.getRegisteredAdaptors();
    managerControl.setReturnValue(adaptors);
    model.addAttribute("emessage", "a bad error");
    modelControl.setReturnValue(null);
    model.addAttribute("adaptors", adaptors);
    modelControl.setReturnValue(null);
    replay();
    
    String result = classUnderTest.showAdaptors(model, "a bad error");
    
    verify();
    assertEquals(result, "adaptors");
  }
  
  public void testShowAdaptor() throws Exception {
    XmlRpcAdaptor adaptor = new XmlRpcAdaptor();
    adaptor.setName("A1");
    adaptor.setTimeout(6000);
    adaptor.setUrl("http://somewhere/somehow");
    List<String> types = new ArrayList<String>();
    
    manager.getAdaptor("A1");
    managerControl.setReturnValue(adaptor);
    manager.getAvailableTypes();
    managerControl.setReturnValue(types);
    model.addAttribute("types", types);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "A1");
    modelControl.setReturnValue(null);
    model.addAttribute("type", "XMLRPC");
    modelControl.setReturnValue(null);
    model.addAttribute("uri", "http://somewhere/somehow");
    modelControl.setReturnValue(null);
    model.addAttribute("timeout", new Long(6000));
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.showAdaptor(model, "A1");
    
    verify();
    assertEquals(result, "showadaptor");
  }
}

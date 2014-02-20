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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IAdaptor;
import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.adaptor.xmlrpc.XmlRpcAdaptor;

/**
 * @author Anders Henja
 */
public class AdaptorsControllerTest extends EasyMockSupport {
  private AdaptorsController classUnderTest = null;
  private IBltAdaptorManager manager = null;
  private Model model = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IBltAdaptorManager.class);
    model = createMock(Model.class);
    classUnderTest = new AdaptorsController();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    model = null;
  }
  
  @Test
  public void testShowAdaptors() throws Exception {
    List<IAdaptor> adaptors = new ArrayList<IAdaptor>();
    expect(manager.getRegisteredAdaptors()).andReturn(adaptors);
    expect(model.addAttribute("emessage", null)).andReturn(null);
    expect(model.addAttribute("adaptors", adaptors)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAdaptors(model,null);
    
    verifyAll();
    assertEquals(result, "adaptors");
  }

  @Test
  public void testShowAdaptors_withErrorMessage() throws Exception {
    List<IAdaptor> adaptors = new ArrayList<IAdaptor>();
    expect(manager.getRegisteredAdaptors()).andReturn(adaptors);
    expect(model.addAttribute("emessage", "a bad error")).andReturn(null);
    expect(model.addAttribute("adaptors", adaptors)).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showAdaptors(model, "a bad error");
    
    verifyAll();
    assertEquals(result, "adaptors");
  }
  
  @Test
  public void testShowAdaptor() throws Exception {
    XmlRpcAdaptor adaptor = new XmlRpcAdaptor();
    adaptor.setName("A1");
    adaptor.setTimeout(6000);
    adaptor.setUrl("http://somewhere/somehow");
    List<String> types = new ArrayList<String>();
    
    expect(manager.getAdaptor("A1")).andReturn(adaptor);
    expect(manager.getAvailableTypes()).andReturn(types);
    expect(model.addAttribute("types", types)).andReturn(null);
    expect(model.addAttribute("name", "A1")).andReturn(null);
    expect(model.addAttribute("type", "XMLRPC")).andReturn(null);
    expect(model.addAttribute("uri", "http://somewhere/somehow")).andReturn(null);
    expect(model.addAttribute("timeout", new Long(6000))).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.showAdaptor(model, "A1");
    
    verifyAll();
    assertEquals(result, "adaptor_show");
  }
}

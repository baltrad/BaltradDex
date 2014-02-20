/* --------------------------------------------------------------------
Copyright (C) 2009-2010 Swedish Meteorological and Hydrological Institute, SMHI,

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
package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;

public class ShowRoutesControllerTest extends EasyMockSupport {
  private ShowRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private Model model = null;

  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    model = createMock(Model.class);
    classUnderTest = new ShowRoutesController();
    classUnderTest.setManager(manager);
  }  

  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    model = null;
  }

  @Test
  public void testShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);

    replayAll();

    String result = classUnderTest.showRoutes(model);

    verifyAll();
    assertEquals("routes", result);
  }
  
  @Test
  public void testCreateRoute_Script() {
    replayAll();
    
    String result = classUnderTest.createRoute(model, "Script");
    
    verifyAll();
    assertEquals("redirect:route_create_groovy.htm", result);
  }

  @Test
  public void testCreateRoute_Composite() {
    replayAll();
    
    String result = classUnderTest.createRoute(model, "Composite");
    
    verifyAll();
    assertEquals("redirect:route_create_composite.htm", result);
  }

  @Test
  public void testCreateRoute_BdbTrimAge() {
    replayAll();
    
    String result = classUnderTest.createRoute(model, "BdbTrimAge");
    
    verifyAll();
    assertEquals("redirect:route_create_bdb_trim_age.htm", result);
  }

  @Test
  public void testCreateRoute_BdbTrimCount() {
    replayAll();
    
    String result = classUnderTest.createRoute(model, "BdbTrimCount");
    
    verifyAll();
    assertEquals("redirect:route_create_bdb_trim_count.htm", result);
  }
  
  @Test
  public void testCreateRoute_GoogleMap() {
    replayAll();
    
    String result = classUnderTest.createRoute(model, "GoogleMap");
    
    verifyAll();
    assertEquals("redirect:route_create_google_map.htm", result);
  }
  
  @Test
  public void testCreateRoute_Unknown() {
    expect(model.addAttribute("emessage", "Unknown operation: 'Unknown'")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "Unknown");
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }
  
  @Test
  public void testShowRoute_Script() {
    RouteDefinition def = createMock(RouteDefinition.class);
    
    expect(manager.getDefinition("Nisse")).andReturn(def);
    expect(def.getRuleType()).andReturn("groovy");
    
    expect(model.addAttribute("name", "Nisse")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verifyAll();
    
    assertEquals("redirect:route_show_groovy.htm", result);
  }

  @Test
  public void testShowRoute_Composite() {
    RouteDefinition def = createMock(RouteDefinition.class);
    
    expect(manager.getDefinition("Nisse")).andReturn(def);
    expect(def.getRuleType()).andReturn("blt_composite");
    expect(model.addAttribute("name", "Nisse")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verifyAll();
    
    assertEquals("redirect:route_show_composite.htm", result);
  }

  @Test
  public void testShowRoute_BdbTrimAge() {
    RouteDefinition def = createMock(RouteDefinition.class);
    
    expect(manager.getDefinition("Nisse")).andReturn(def);
    expect(def.getRuleType()).andReturn("bdb_trim_age");
    expect(model.addAttribute("name", "Nisse")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verifyAll();
    
    assertEquals("redirect:route_show_bdb_trim_age.htm", result);
  }

  @Test
  public void testShowRoute_BdbTrimCount() {
    RouteDefinition def = createMock(RouteDefinition.class);
    
    expect(manager.getDefinition("Nisse")).andReturn(def);
    expect(def.getRuleType()).andReturn("bdb_trim_count");
    expect(model.addAttribute("name", "Nisse")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verifyAll();
    
    assertEquals("redirect:route_show_bdb_trim_count.htm", result);
  }

  @Test
  public void testShowRoute_GoogleMap() {
    RouteDefinition def = createMock(RouteDefinition.class);
    
    expect(manager.getDefinition("Nisse")).andReturn(def);
    expect(def.getRuleType()).andReturn("blt_gmap");
    expect(model.addAttribute("name", "Nisse")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verifyAll();
    
    assertEquals("redirect:route_show_google_map.htm", result);
  }
}

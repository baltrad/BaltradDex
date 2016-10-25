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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.router.RouteDefinition.RouteComparator;

public class ShowRoutesControllerTest extends EasyMockSupport {
  private ShowRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private Model model = null;
  private HttpSession httpSession = null;

  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    model = createMock(Model.class);
    httpSession = createMock(HttpSession.class);
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
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, null);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertTrue(routeComparators.get(0) instanceof RouteDefinition.NameComparator);
    assertTrue(routeComparators.get(1) instanceof RouteDefinition.TypeComparator);
    assertTrue(routeComparators.get(2) instanceof RouteDefinition.ActiveComparator);
    assertTrue(routeComparators.get(0).isAscendingSort());
    assertTrue(routeComparators.get(1).isAscendingSort());
    assertTrue(routeComparators.get(2).isAscendingSort());
  }
  
  @Test
  public void testShowRoutes_storedSorting() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    ArrayList<RouteComparator> storedSorting = new ArrayList<RouteComparator>();
    storedSorting.add(new RouteDefinition.ActiveComparator());
    storedSorting.add(new RouteDefinition.DescriptionComparator());
    storedSorting.add(new RouteDefinition.NameComparator());
    
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(storedSorting);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, null);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertEquals(storedSorting, routeComparators);
  }
  
  @Test
  public void testShowRoutes_sortByName() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, ShowRoutesController.SORT_BY_NAME_TAG);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertTrue(routeComparators.get(0) instanceof RouteDefinition.TypeComparator);
    assertTrue(routeComparators.get(1) instanceof RouteDefinition.ActiveComparator);
    assertTrue(routeComparators.get(2) instanceof RouteDefinition.NameComparator);
    assertTrue(routeComparators.get(0).isAscendingSort());
    assertTrue(routeComparators.get(1).isAscendingSort());
    assertTrue(!routeComparators.get(2).isAscendingSort());
  }
  
  @Test
  public void testShowRoutes_sortByType() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, ShowRoutesController.SORT_BY_TYPE_TAG);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertTrue(routeComparators.get(0) instanceof RouteDefinition.NameComparator);
    assertTrue(routeComparators.get(1) instanceof RouteDefinition.ActiveComparator);
    assertTrue(routeComparators.get(2) instanceof RouteDefinition.TypeComparator);
    assertTrue(routeComparators.get(0).isAscendingSort());
    assertTrue(routeComparators.get(1).isAscendingSort());
    assertTrue(!routeComparators.get(2).isAscendingSort());
  }
  
  @Test
  public void testShowRoutes_sortByActive() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, ShowRoutesController.SORT_BY_ACTIVE_TAG);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertTrue(routeComparators.get(0) instanceof RouteDefinition.NameComparator);
    assertTrue(routeComparators.get(1) instanceof RouteDefinition.TypeComparator);
    assertTrue(routeComparators.get(2) instanceof RouteDefinition.ActiveComparator);
    assertTrue(routeComparators.get(0).isAscendingSort());
    assertTrue(routeComparators.get(1).isAscendingSort());
    assertTrue(!routeComparators.get(2).isAscendingSort());
  }
  
  @Test
  public void testShowRoutes_sortByDescription() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, ShowRoutesController.SORT_BY_DESCRIPTION_TAG);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    ArrayList<RouteComparator> routeComparators = capturedComparators.getValue();
    assertTrue(routeComparators.get(0) instanceof RouteDefinition.NameComparator);
    assertTrue(routeComparators.get(1) instanceof RouteDefinition.TypeComparator);
    assertTrue(routeComparators.get(2) instanceof RouteDefinition.ActiveComparator);
    assertTrue(routeComparators.get(3) instanceof RouteDefinition.DescriptionComparator);
    assertTrue(routeComparators.get(0).isAscendingSort());
    assertTrue(routeComparators.get(1).isAscendingSort());
    assertTrue(routeComparators.get(2).isAscendingSort());
    assertTrue(routeComparators.get(3).isAscendingSort());
  }
  
  @Test
  public void testShowRoutes_correctNameSorting() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    RouteDefinition routeA = new RouteDefinition();
    routeA.setName("A");
    RouteDefinition routeB = new RouteDefinition();
    routeB.setName("B");
    RouteDefinition routeC = new RouteDefinition();
    routeC.setName("C");
    RouteDefinition routeD = new RouteDefinition();
    routeD.setName("D");
    
    // add definitions un-ordered
    definitions.add(routeC);
    definitions.add(routeA);
    definitions.add(routeD);
    definitions.add(routeB);
    
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<List<RouteDefinition>> capturedDefinitions = 
        new Capture<List<RouteDefinition>>();
    Capture<String> capturedAddString = 
        new Capture<String>();
    model.addAttribute(EasyMock.capture(capturedAddString), 
                       EasyMock.capture(capturedDefinitions));
    expectLastCall().andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedSetString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedSetString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, null);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedAddString.getValue(), "routes");
    assertEquals(capturedSetString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    List<RouteDefinition> routeDefinitions = capturedDefinitions.getValue();
    assertTrue(routeDefinitions.get(0).getName().equals("A"));
    assertTrue(routeDefinitions.get(1).getName().equals("B"));
    assertTrue(routeDefinitions.get(2).getName().equals("C"));
    assertTrue(routeDefinitions.get(3).getName().equals("D"));
  }
  
  @Test
  public void testShowRoutes_correctActiveSorting() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    RouteDefinition routeA = new RouteDefinition();
    routeA.setActive(false);
    RouteDefinition routeB = new RouteDefinition();
    routeB.setActive(true);
    RouteDefinition routeC = new RouteDefinition();
    routeC.setActive(false);
    RouteDefinition routeD = new RouteDefinition();
    routeD.setActive(true);

    definitions.add(routeA);
    definitions.add(routeB);
    definitions.add(routeC);
    definitions.add(routeD);
    
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(httpSession.getAttribute(ShowRoutesController.ROUTES_COMPARATORS_ATTR)).andReturn(null);
    
    Capture<List<RouteDefinition>> capturedDefinitions = 
        new Capture<List<RouteDefinition>>();
    Capture<String> capturedAddString = 
        new Capture<String>();
    model.addAttribute(EasyMock.capture(capturedAddString), 
                       EasyMock.capture(capturedDefinitions));
    expectLastCall().andReturn(null);
    
    Capture<ArrayList<RouteComparator>> capturedComparators = 
        new Capture<ArrayList<RouteComparator>>();
    Capture<String> capturedSetString = 
        new Capture<String>();
    httpSession.setAttribute(EasyMock.capture(capturedSetString), 
                             EasyMock.capture(capturedComparators));

    replayAll();

    String result = classUnderTest.showRoutes(model, null, httpSession, null);

    verifyAll();
    
    assertEquals("routes", result);
    assertEquals(capturedAddString.getValue(), "routes");
    assertEquals(capturedSetString.getValue(), ShowRoutesController.ROUTES_COMPARATORS_ATTR);
    
    List<RouteDefinition> routeDefinitions = capturedDefinitions.getValue();
    assertTrue(routeDefinitions.get(0).isActive());
    assertTrue(routeDefinitions.get(1).isActive());
    assertTrue(!routeDefinitions.get(2).isActive());
    assertTrue(!routeDefinitions.get(3).isActive());
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

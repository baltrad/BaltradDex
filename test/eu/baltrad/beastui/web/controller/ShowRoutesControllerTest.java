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

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;

public class ShowRoutesControllerTest extends TestCase {
  private ShowRoutesController classUnderTest = null;
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl modelControl = null;
  private Model model = null;

  protected void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    classUnderTest = new ShowRoutesController();
    classUnderTest.setManager(manager);
  }  
  
  protected void tearDown() throws Exception {
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
  
  public void testShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    manager.getDefinitions();
    managerControl.setReturnValue(definitions);
    model.addAttribute("routes", definitions);
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.showRoutes(model);

    verify();
    assertEquals("showroutes", result);
  }
  
  public void testCreateRoute_Script() {
    replay();
    
    String result = classUnderTest.createRoute(model, "Script");
    
    verify();
    assertEquals("redirect:groovyroute_create.htm", result);
  }

  public void testCreateRoute_Composite() {
    replay();
    
    String result = classUnderTest.createRoute(model, "Composite");
    
    verify();
    assertEquals("redirect:compositeroute_create.htm", result);
  }

  public void testCreateRoute_BdbTrimAge() {
    replay();
    
    String result = classUnderTest.createRoute(model, "BdbTrimAge");
    
    verify();
    assertEquals("redirect:bdbtrimageroute_create.htm", result);
  }
  
  public void testCreateRoute_Unknown() {
    model.addAttribute("emessage", "Unknown operation: 'Unknown'");
    modelControl.setReturnValue(null);
    
    replay();
    
    String result = classUnderTest.createRoute(model, "Unknown");
    
    verify();
    assertEquals("redirect:showroutes.htm", result);
  }
  
  public void testShowRoute_Script() {
    MockControl defControl = MockClassControl.createControl(RouteDefinition.class);
    RouteDefinition def = (RouteDefinition)defControl.getMock();
    
    manager.getDefinition("Nisse");
    managerControl.setReturnValue(def);
    def.getRuleType();
    defControl.setReturnValue("groovy");
    
    model.addAttribute("name", "Nisse");
    modelControl.setReturnValue(null);

    replay();
    defControl.replay();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verify();
    defControl.verify();
    
    assertEquals("redirect:groovyroute_show.htm", result);
  }

  public void testShowRoute_Composite() {
    MockControl defControl = MockClassControl.createControl(RouteDefinition.class);
    RouteDefinition def = (RouteDefinition)defControl.getMock();
    
    manager.getDefinition("Nisse");
    managerControl.setReturnValue(def);
    def.getRuleType();
    defControl.setReturnValue("blt_composite");
    
    model.addAttribute("name", "Nisse");
    modelControl.setReturnValue(null);

    replay();
    defControl.replay();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verify();
    defControl.verify();
    
    assertEquals("redirect:compositeroute_show.htm", result);
  }

  public void testShowRoute_BdbTrimAge() {
    MockControl defControl = MockClassControl.createControl(RouteDefinition.class);
    RouteDefinition def = (RouteDefinition)defControl.getMock();
    
    manager.getDefinition("Nisse");
    managerControl.setReturnValue(def);
    def.getRuleType();
    defControl.setReturnValue("bdb_trim_age");
    
    model.addAttribute("name", "Nisse");
    modelControl.setReturnValue(null);

    replay();
    defControl.replay();
    
    String result = classUnderTest.showRoute(model, "Nisse");
    
    verify();
    defControl.verify();
    
    assertEquals("redirect:bdbtrimageroute_show.htm", result);
  }

  
}

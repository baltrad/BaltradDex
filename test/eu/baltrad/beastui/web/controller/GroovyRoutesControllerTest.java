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

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.groovy.GroovyRule;

/**
 * @author Anders Henja
 */
public class GroovyRoutesControllerTest extends EasyMockSupport {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        List<String> recipients,
        String definition,
        String emessage);
    
    public String viewShowRoutes(Model model,
        String emessage);
    
    public String viewShowRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        List<String> recipients,
        String definition,
        String emessage); 
    
    public GroovyRule createRule(String script);
    
    public String modifyRoute(Model model, String name, String author,
        Boolean active, String description, List<String> recipients, String script);
  };
  private GroovyRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private IBltAdaptorManager adaptorManager = null;
  private Model model = null;
  private MethodMocker methodMock = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    adaptorManager = createMock(IBltAdaptorManager.class);
    model = createMock(Model.class);
    methodMock = createMock(MethodMocker.class);
    
    // All creator methods and view methods are mocked to simplify testing
    //
    classUnderTest = new GroovyRoutesController() {
      protected String viewCreateRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          List<String> recipients,
          String definition,
          String emessage) {
        return methodMock.viewCreateRoute(model, name, author, active, description, recipients, definition, emessage);
      }
      
      protected String viewShowRoutes(Model model,
          String emessage) {
        return methodMock.viewShowRoutes(model, emessage);
      }
      
      protected String viewShowRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          List<String> recipients,
          String definition,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, recipients, definition, emessage);
      }
      
      protected GroovyRule createRule(String script) {
        return methodMock.createRule(script);
      }
      
      protected String modifyRoute(Model model, String name, String author,
          Boolean active, String description, List<String> recipients, String script) {
        return methodMock.modifyRoute(model, name, author, active, description, recipients, script);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
  }

  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    adaptorManager = null;
    model = null;
    methodMock = null;
  }

  @Test
  public void testCreateRoute_initial() {
    List<String> types = new ArrayList<String>();
    types.add("groovy");
    expect(methodMock.viewCreateRoute(model, null, null, null, null, null, null,null)).andReturn("route_create_groovy");
    
    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null, null);

    verifyAll();
    assertEquals("route_create_groovy", result);
  }

  @Test
  public void testCreateRoute_noAdaptors() {
    expect(methodMock.viewCreateRoute(model, null, null, null, null, null, null,null)).andReturn("route_create_groovy");

    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null, null);

    verifyAll();
    assertEquals("route_create_groovy", result);
  }

  @Test
  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";
    // GroovyRule constructor is protected so mock it
    GroovyRule rule = createMock(GroovyRule.class);
    
    List<String> recipients = new ArrayList<String>();
    expect(methodMock.createRule(def)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    expect(methodMock.viewCreateRoute(model, name, "nisse", true, "some description", recipients, "some code", "Name must be specified.")).andReturn("route_create_groovy");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verifyAll();
    assertEquals("route_create_groovy", result);
  }
  
  @Test
  public void testCreateRoute_noDefinition() {
    String name = "abc";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    expect(methodMock.viewCreateRoute(model, name, author, active, description, recipients, def, "Can not create a groovy rule when script is empty.")).andReturn("createroute");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verifyAll();
    assertEquals("createroute", result);
  }

  @Test
  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";
    // GroovyRule constructor is protected so mock it
    GroovyRule rule = createMock(GroovyRule.class);
    
    List<String> recipients = new ArrayList<String>();
    expect(methodMock.createRule(def)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);
    expectLastCall().andThrow(new RuleException("Duplicate name"));
    expect(methodMock.viewCreateRoute(model, name, author, active, description, recipients, def, "Failed to create definition: 'Duplicate name'")).andReturn("route_create_groovy");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verifyAll();
    assertEquals("route_create_groovy", result);
  }

  @Test
  public void testShowRoute_byName() {
    String name = "somename";
    List<String> recipients = new ArrayList<String>();
    
    RouteDefinition routeDefinition = new RouteDefinition();
    routeDefinition.setActive(true);
    routeDefinition.setAuthor("nisse");
    routeDefinition.setDescription("descr");
    routeDefinition.setName(name);
    routeDefinition.setRecipients(recipients);
    routeDefinition.setRule(new GroovyRule() {
      public String getScript() {
        return "def";
      }
      public int getState() {
        return GroovyRule.OK;
      }
    });
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    expect(methodMock.viewShowRoute(model, name, "nisse", true, "descr", recipients, "def", null)).andReturn("route_show_groovy");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null);
    
    verifyAll();
    assertEquals("route_show_groovy", result);
  }

  @Test
  public void testShowRoute_byName_nonExisting() {
    String name = "somename";
    expect(manager.getDefinition(name)).andReturn(null);
    expect(methodMock.viewShowRoutes(model, "No route named \"somename\"")).andReturn("routes");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null,null, null);
    
    verifyAll();
    assertEquals("routes", result);
  }

  @Test
  public void testShowRoute_modify() {
    String name = "somename";
    List<String> newrecipients = new ArrayList<String>();
    
    RouteDefinition routeDefinition = new RouteDefinition();
    
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    expect(methodMock.modifyRoute(model, name, "hugga", false, "new descr", newrecipients, "def")).andReturn("somedirect");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", newrecipients, "def", "Save");
    
    verifyAll();
    assertEquals("somedirect", result);
  }

  @Test
  public void testShowRoute_delete() {
    String name = "somename";
    RouteDefinition routeDefinition = new RouteDefinition();
    
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    manager.deleteDefinition("somename");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, "Delete");
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testShowRoute_delete_failed() {
    String name = "somename";
    RouteDefinition routeDefinition = new RouteDefinition();
    
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    manager.deleteDefinition("somename");
    expectLastCall().andThrow(new RuleException());
    expect(methodMock.viewShowRoutes(model, "Failed to delete \"somename\", have you verified that there are no reffering scheduled jobs")).andReturn("routes");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, "Delete");
    
    verifyAll();
    assertEquals("routes", result);
  }

  @Test
  public void testViewCreateRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    RouteDefinition routeDefinition = new RouteDefinition();

    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(model.addAttribute("adaptors", adaptors)).andReturn(null);
    expect(manager.create("somename", "someauthor", true, "descr", recipients, null)).andReturn(routeDefinition);

    expect(model.addAttribute("route", routeDefinition)).andReturn(null);
    expect(model.addAttribute("emessage", null)).andReturn(null);
    expect(model.addAttribute("typdef", "def")).andReturn(null);

    classUnderTest = new GroovyRoutesController();
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replayAll();

    String result = classUnderTest.viewCreateRoute(model, "somename", "someauthor", true, "descr", recipients, "def", null);
    
    verifyAll();
    assertEquals("route_create_groovy", result);
  }

  @Test
  public void testViewShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setManager(manager);
    
    replayAll();

    String result = classUnderTest.viewShowRoutes(model, null);
    
    verifyAll();
    assertEquals("routes", result);
  }
  
  @Test
  public void testViewShowRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    RouteDefinition routeDefinition = new RouteDefinition();

    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(model.addAttribute("adaptors", adaptors)).andReturn(null);
    expect(manager.create("somename", "someauthor", true, "descr", recipients, null)).andReturn(routeDefinition);
    expect(model.addAttribute("route", routeDefinition)).andReturn(null);
    expect(model.addAttribute("definition", "def")).andReturn(null);
    expect(model.addAttribute("emessage", null)).andReturn(null);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replayAll();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", recipients, "def", null);
    
    verifyAll();
    assertEquals("route_show_groovy", result);
  }

  @Test
  public void testModifyRoute() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    String script = "a script";
    // GroovyRule constructor is protected so mock it
    GroovyRule rule = createMock(GroovyRule.class);
    RouteDefinition definition = new RouteDefinition();
    
    classUnderTest = new GroovyRoutesController() {
      protected GroovyRule createRule(String script) {
        return methodMock.createRule(script);
      }
    };
    classUnderTest.setManager(manager);
    
    expect(methodMock.createRule(script)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(definition);
    manager.updateDefinition(definition);
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testModifyRoute_noScript() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    String script = null;
    
    classUnderTest = new GroovyRoutesController() {
      protected GroovyRule createRule(String script) {
        return methodMock.createRule(script);
      }
      protected String viewShowRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          List<String> recipients,
          String definition,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, recipients, definition, emessage);
      }
      
    };
    classUnderTest.setManager(manager);
    
    expect(methodMock.viewShowRoute(model, name, author, active, description, recipients, script, "Definition missing.")).andReturn("someredirect");
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verifyAll();
    assertEquals("someredirect", result);
  }

  @Test
  public void testModifyRoute_failedUpdate() throws Exception {
    // GroovyRule constructor is protected so mock it
    GroovyRule rule = createMock(GroovyRule.class);

    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    String script = "some def";
    RouteDefinition definition = new RouteDefinition();
     
    classUnderTest = new GroovyRoutesController() {
      protected GroovyRule createRule(String script) {
        return methodMock.createRule(script);
      }
      protected String viewShowRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          List<String> recipients,
          String definition,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, recipients, definition, emessage);
      }
      
    };
    classUnderTest.setManager(manager);
    expect(methodMock.createRule(script)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(definition);
    manager.updateDefinition(definition);
    expectLastCall().andThrow(new RuntimeException("Bad..."));
    expect(methodMock.viewShowRoute(model, name, author, active, description, recipients, script, "Failed to update definition: 'Bad...'")).andReturn("someredirect");
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verifyAll();
    assertEquals("someredirect", result);
  }
  
  @Test
  public void testCreateRule() throws Exception {
    GroovyRule groovyRule = createMock(GroovyRule.class);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setManager(manager);
    
    expect(manager.createRule(GroovyRule.TYPE)).andReturn(groovyRule);
    groovyRule.setScript("abc");

    replayAll();
    
    GroovyRule result = classUnderTest.createRule("abc");
    
    verifyAll();
    assertSame(groovyRule, result);
  }
}

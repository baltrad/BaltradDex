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

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.bdb.BdbTrimAgeRule;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


public class BdbTrimAgeRoutesControllerTest extends EasyMockSupport {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        Integer ageLimit,
        String emessage);
    
    public String viewShowRoutes(Model model,
        String emessage);
    
    public String viewShowRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        Integer ageLimit,
        String emessage); 
    
    public BdbTrimAgeRule createRule(Integer ageLimit);
    
    public String modifyRoute(Model model, String name, String author,
        Boolean active, String description, Integer ageLimit);
  };
  private BdbTrimAgeRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private Model model = null;
  private MethodMocker methodMock = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    model = createMock(Model.class);
    methodMock = createMock(MethodMocker.class);
    
    // All creator methods and view methods are mocked to simplify testing
    //
    classUnderTest = new BdbTrimAgeRoutesController() {
      protected String viewCreateRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Integer ageLimit,
          String emessage) {
        return methodMock.viewCreateRoute(model, name, author, active,
            description, ageLimit, emessage);
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
          Integer ageLimit,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ageLimit, emessage);
      }
      
      protected BdbTrimAgeRule createRule(Integer ageLimit) {
        return methodMock.createRule(ageLimit);
      }
      
      protected String modifyRoute(Model model, String name, String author,
          Boolean active, String description, Integer ageLimit) {
        return methodMock.modifyRoute(model, name, author, active, description, ageLimit);
      }
    };
    classUnderTest.setManager(manager);
  }

  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    model = null;
    methodMock = null;
  }

  @Test
  public void testCreateRoute_initial() {
    expect(methodMock.viewCreateRoute(model, null, null, null, null, null, null)).andReturn("route_create_bdb_trim_age");
    
    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null);

    verifyAll();
    assertEquals("route_create_bdb_trim_age", result);
  }

  @Test
  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 1;
    // BdbTrimAgeRule constructor is protected so mock it
    BdbTrimAgeRule rule = createMock(BdbTrimAgeRule.class);
    
    List<String> recipients = new ArrayList<String>();

    expect(methodMock.createRule(ageLimit)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 1;

    expect(methodMock.viewCreateRoute(model, name, "nisse", true, "some description", 1, "Name must be specified.")).andReturn("route_create_bdb_trim_age");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verifyAll();
    assertEquals("route_create_bdb_trim_age", result);
  }
  
  @Test
  public void testCreateRoute_noAgeLimit() {
    String name = "abc";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 0;

    expect(methodMock.viewCreateRoute(model, name, author, active, description, ageLimit, "age limit must be specified and greater than 0.")).andReturn("createroute");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

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
    Integer ageLimit = 1;
    // BdbTrimAgeRule constructor is protected so mock it
    BdbTrimAgeRule rule = createMock(BdbTrimAgeRule.class);
    
    List<String> recipients = new ArrayList<String>();
    expect(methodMock.createRule(ageLimit)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);
    expectLastCall().andThrow(new RuleException("Duplicate name"));
    expect(methodMock.viewCreateRoute(model, name, author, active, description, ageLimit, "Failed to create definition: 'Duplicate name'")).andReturn("route_create_bdb_trim_age");
    
    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verifyAll();
    assertEquals("route_create_bdb_trim_age", result);
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
    routeDefinition.setRule(new BdbTrimAgeRule() {
      public int getFileAgeLimit() {
        return 1;
      }
    });
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    
    expect(methodMock.viewShowRoute(model, name, "nisse", true, "descr", 1, null)).andReturn("route_show_bdb_trim_age");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null);
    
    verifyAll();
    assertEquals("route_show_bdb_trim_age", result);
  }

  @Test
  public void testShowRoute_byName_nonExisting() {
    String name = "somename";
    expect(manager.getDefinition(name)).andReturn(null);
    expect(methodMock.viewShowRoutes(model, "No route named \"somename\"")).andReturn("routes");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, null, null, null, null,null);
    
    verifyAll();
    assertEquals("routes", result);
  }

  @Test
  public void testShowRoute_modify() {
    String name = "somename";
    
    RouteDefinition routeDefinition = new RouteDefinition();
    
    expect(manager.getDefinition(name)).andReturn(routeDefinition);
    expect(methodMock.modifyRoute(model, name, "hugga", false, "new descr", 1)).andReturn("somedirect");
    
    replayAll();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", 1, "Save");
    
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

    String result = classUnderTest.showRoute(model, name, null, null, null, null, "Delete");
    
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

    String result = classUnderTest.showRoute(model, name, null, null, null, null, "Delete");
    
    verifyAll();
    assertEquals("routes", result);
  }

  @Test
  public void testViewCreateRoute() {
    expect(model.addAttribute("name", "somename")).andReturn(null);
    expect(model.addAttribute("author", "someauthor")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "descr")).andReturn(null);
    expect(model.addAttribute("ageLimit", 1)).andReturn(null);

    classUnderTest = new BdbTrimAgeRoutesController();
    
    replayAll();

    String result = classUnderTest.viewCreateRoute(model, "somename", "someauthor", true, "descr", 1, null);
    
    verifyAll();
    assertEquals("route_create_bdb_trim_age", result);
    
  }
  
  @Test
  public void testViewShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();
    classUnderTest.setManager(manager);
    
    replayAll();

    String result = classUnderTest.viewShowRoutes(model, null);
    
    verifyAll();
    assertEquals("routes", result);
  }
  
  @Test
  public void testViewShowRoute() {
    expect(model.addAttribute("name", "somename")).andReturn(null);
    expect(model.addAttribute("author", "someauthor")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "descr")).andReturn(null);
    expect(model.addAttribute("ageLimit", 1)).andReturn(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();
    
    replayAll();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", 1, null);
    
    verifyAll();
    assertEquals("route_show_bdb_trim_age", result);
  }

  @Test
  public void testViewShowRoute_wEmessage() {
    expect(model.addAttribute("name", "somename")).andReturn(null);
    expect(model.addAttribute("author", "someauthor")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "descr")).andReturn(null);
    expect(model.addAttribute("ageLimit", 1)).andReturn(null);
    expect(model.addAttribute("emessage", "xyz")).andReturn(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();

    replayAll();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", 1, "xyz");
    
    verifyAll();
    assertEquals("route_show_bdb_trim_age", result);
  }
  
  @Test
  public void testModifyRoute() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Integer ageLimit = 1;
    // BdbTrimAgeRule constructor is protected so mock it
    BdbTrimAgeRule rule = createMock(BdbTrimAgeRule.class);
    RouteDefinition definition = new RouteDefinition();
    
    classUnderTest = new BdbTrimAgeRoutesController() {
      protected BdbTrimAgeRule createRule(Integer ageLimit) {
        return methodMock.createRule(ageLimit);
      }
    };
    classUnderTest.setManager(manager);
    
    expect(methodMock.createRule(ageLimit)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(definition);
    manager.updateDefinition(definition);
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testModifyRoute_noAgeLimit() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    Integer ageLimit = null;
    
    classUnderTest = new BdbTrimAgeRoutesController() {
      protected BdbTrimAgeRule createRule(Integer ageLimit) {
        return methodMock.createRule(ageLimit);
      }
      protected String viewShowRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Integer ageLimit,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ageLimit, emessage);
      }
      
    };
    classUnderTest.setManager(manager);
    
    expect(methodMock.viewShowRoute(model, name, author, active, description, ageLimit, "age limit missing.")).andReturn("someredirect");
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verifyAll();
    assertEquals("someredirect", result);
  }

  @Test
  public void testModifyRoute_failedUpdate() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Integer ageLimit = 1;
    // BdbTrimAgeRule constructor is protected so mock it
    BdbTrimAgeRule rule = createMock(BdbTrimAgeRule.class);
    RouteDefinition definition = new RouteDefinition();
     
    classUnderTest = new BdbTrimAgeRoutesController() {
      protected BdbTrimAgeRule createRule(Integer ageLimit) {
        return methodMock.createRule(ageLimit);
      }
      protected String viewShowRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Integer ageLimit,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ageLimit, emessage);
      }
      
    };
    classUnderTest.setManager(manager);
    expect(methodMock.createRule(ageLimit)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(definition);
    manager.updateDefinition(definition);
    expectLastCall().andThrow(new RuntimeException("Bad..."));
    expect(methodMock.viewShowRoute(model, name, author, active, description, ageLimit, "Failed to update definition: 'Bad...'")).andReturn("someredirect");
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verifyAll();
    assertEquals("someredirect", result);
  }
}

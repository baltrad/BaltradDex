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

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.bdb.BdbTrimAgeRule;

public class BdbTrimAgeRoutesControllerTest extends TestCase {
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
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodMockControl = null;
  private MethodMocker methodMock = null;
  
  public void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    methodMockControl = MockControl.createControl(MethodMocker.class);
    methodMock = (MethodMocker)methodMockControl.getMock();
    
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

  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    modelControl = null;
    model = null;
    methodMockControl = null;
    methodMock = null;
  }

  private void replay() {
    managerControl.replay();
    modelControl.replay();
    methodMockControl.replay();
  }

  private void verify() {
    managerControl.verify();
    modelControl.verify();
    methodMockControl.verify();
  }

  public void testCreateRoute_initial() {
    methodMock.viewCreateRoute(model, null, null, null, null, null, null);
    methodMockControl.setReturnValue("bdbtrimageroute_create");
    
    replay();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null);

    verify();
    assertEquals("bdbtrimageroute_create", result);
  }

  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 1;
    BdbTrimAgeRule rule = new BdbTrimAgeRule();
    
    List<String> recipients = new ArrayList<String>();

    methodMock.createRule(ageLimit);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 1;

    List<String> recipients = new ArrayList<String>();

    methodMock.viewCreateRoute(model, name, "nisse", true, "some description", 1, "Name must be specified.");
    methodMockControl.setReturnValue("bdbtrimageroute_create");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verify();
    assertEquals("bdbtrimageroute_create", result);
  }
  
  public void testCreateRoute_noAgeLimit() {
    String name = "abc";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 0;

    List<String> recipients = new ArrayList<String>();

    methodMock.viewCreateRoute(model, name, author, active, description, ageLimit, "age limit must be specified and greater than 0.");
    methodMockControl.setReturnValue("createroute");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verify();
    assertEquals("createroute", result);
  }

  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Integer ageLimit = 1;
    BdbTrimAgeRule rule = new BdbTrimAgeRule();
    
    List<String> recipients = new ArrayList<String>();
    methodMock.createRule(ageLimit);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);
    managerControl.setThrowable(new RuleException("Duplicate name"));
    methodMock.viewCreateRoute(model, name, author, active, description, ageLimit, "Failed to create definition: 'Duplicate name'");
    methodMockControl.setReturnValue("bdbtrimageroute_create");
    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, ageLimit);

    verify();
    assertEquals("bdbtrimageroute_create", result);
  }

  
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
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    
    methodMock.viewShowRoute(model, name, "nisse", true, "descr", 1, null);
    methodMockControl.setReturnValue("bdbtrimageroute_show");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null);
    
    verify();
    assertEquals("bdbtrimageroute_show", result);
  }

  public void testShowRoute_byName_nonExisting() {
    String name = "somename";
    manager.getDefinition(name);
    managerControl.setReturnValue(null);
    methodMock.viewShowRoutes(model, "No route named \"somename\"");
    methodMockControl.setReturnValue("showroutes");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null,null);
    
    verify();
    assertEquals("showroutes", result);
  }

  public void testShowRoute_modify() {
    String name = "somename";
    
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    methodMock.modifyRoute(model, name, "hugga", false, "new descr", 1);
    methodMockControl.setReturnValue("somedirect");
    
    replay();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", 1, "Modify");
    
    verify();
    assertEquals("somedirect", result);
  }

  public void testShowRoute_delete() {
    String name = "somename";
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    manager.deleteDefinition("somename");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, "Delete");
    
    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

  public void testShowRoute_delete_failed() {
    String name = "somename";
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    manager.deleteDefinition("somename");
    managerControl.setThrowable(new RuleException());
    methodMock.viewShowRoutes(model, "Failed to delete \"somename\"");
    methodMockControl.setReturnValue("routes");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, "Delete");
    
    verify();
    assertEquals("routes", result);
  }

  public void testViewCreateRoute() {
    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("ageLimit", 1);
    modelControl.setReturnValue(null);

    classUnderTest = new BdbTrimAgeRoutesController();
    
    replay();

    String result = classUnderTest.viewCreateRoute(model, "somename", "someauthor", true, "descr", 1, null);
    
    verify();
    assertEquals("bdbtrimageroute_create", result);
    
  }
  
  public void testViewShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    manager.getDefinitions();
    managerControl.setReturnValue(definitions);
    
    model.addAttribute("routes", definitions);
    modelControl.setReturnValue(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();
    classUnderTest.setManager(manager);
    
    replay();

    String result = classUnderTest.viewShowRoutes(model, null);
    
    verify();
    assertEquals("showroutes", result);
  }
  
  public void testViewShowRoute() {
    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("ageLimit", 1);
    modelControl.setReturnValue(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();
    
    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", 1, null);
    
    verify();
    assertEquals("bdbtrimageroute_show", result);
  }

  public void testViewShowRoute_wEmessage() {
    List<String> recipients = new ArrayList<String>();

    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("ageLimit", 1);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "xyz");
    modelControl.setReturnValue(null);
    
    classUnderTest = new BdbTrimAgeRoutesController();

    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", 1, "xyz");
    
    verify();
    assertEquals("bdbtrimageroute_show", result);
  }
  
  public void testModifyRoute() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Integer ageLimit = 1;
    BdbTrimAgeRule rule = new BdbTrimAgeRule();
    RouteDefinition definition = new RouteDefinition();
    
    classUnderTest = new BdbTrimAgeRoutesController() {
      protected BdbTrimAgeRule createRule(Integer ageLimit) {
        return methodMock.createRule(ageLimit);
      }
    };
    classUnderTest.setManager(manager);
    
    methodMock.createRule(ageLimit);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(definition);
    manager.updateDefinition(definition);
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

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
    
    methodMock.viewShowRoute(model, name, author, active, description, ageLimit, "age limit missing.");
    methodMockControl.setReturnValue("someredirect");
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verify();
    assertEquals("someredirect", result);
  }

  public void testModifyRoute_failedUpdate() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Integer ageLimit = 1;
    BdbTrimAgeRule rule = new BdbTrimAgeRule();
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
    methodMock.createRule(ageLimit);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(definition);
    manager.updateDefinition(definition);
    managerControl.setThrowable(new RuntimeException("Bad..."));
    methodMock.viewShowRoute(model, name, author, active, description, ageLimit, "Failed to update definition: 'Bad...'");
    methodMockControl.setReturnValue("someredirect");
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ageLimit);
    
    verify();
    assertEquals("someredirect", result);
  }
  
}

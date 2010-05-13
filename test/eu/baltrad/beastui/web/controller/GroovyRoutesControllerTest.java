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

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.groovy.GroovyRule;

/**
 * @author Anders Henja
 */
public class GroovyRoutesControllerTest extends TestCase {
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
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl adaptorControl = null;
  private IBltAdaptorManager adaptorManager = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodMockControl = null;
  private MethodMocker methodMock = null;
  
  public void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    adaptorControl = MockControl.createControl(IBltAdaptorManager.class);
    adaptorManager = (IBltAdaptorManager) adaptorControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    methodMockControl = MockControl.createControl(MethodMocker.class);
    methodMock = (MethodMocker)methodMockControl.getMock();
    
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

  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    adaptorControl = null;
    adaptorManager = null;
    modelControl = null;
    model = null;
    methodMockControl = null;
    methodMock = null;
  }

  private void replay() {
    managerControl.replay();
    adaptorControl.replay();
    modelControl.replay();
    methodMockControl.replay();
  }

  private void verify() {
    managerControl.verify();
    adaptorControl.verify();
    modelControl.verify();
    methodMockControl.verify();
  }

  public void testCreateRoute_initial() {
    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, null, null, null, null, null, null,null);
    methodMockControl.setReturnValue("groovyroute_create");
    
    replay();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null, null);

    verify();
    assertEquals("groovyroute_create", result);
  }

  public void testCreateRoute_noAdaptors() {
    List<String> adaptors = new ArrayList<String>();
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null,
        null, null, null);

    verify();
    assertEquals("redirect:adaptors.htm", result);
  }

  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";
    GroovyRule rule = new GroovyRule();
    
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.createRule(def);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, name, "nisse", true, "some description", recipients, "some code", "Name must be specified.");
    methodMockControl.setReturnValue("groovyroute_create");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verify();
    assertEquals("groovyroute_create", result);
  }
  
  public void testCreateRoute_noDefinition() {
    String name = "abc";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, name, author, active, description, recipients, def, "Can not create a groovy rule when script is empty.");
    methodMockControl.setReturnValue("createroute");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verify();
    assertEquals("createroute", result);
  }

  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String def = "some code";
    GroovyRule rule = new GroovyRule();
    
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.createRule(def);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);
    managerControl.setThrowable(new RuleException("Duplicate name"));
    methodMock.viewCreateRoute(model, name, author, active, description, recipients, def, "Failed to create definition: 'Duplicate name'");
    methodMockControl.setReturnValue("groovyroute_create");
    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, def);

    verify();
    assertEquals("groovyroute_create", result);
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
    routeDefinition.setRule(new GroovyRule() {
      public String getScript() {
        return "def";
      }
    });
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    
    methodMock.viewShowRoute(model, name, "nisse", true, "descr", recipients, "def", null);
    methodMockControl.setReturnValue("groovyroute_show");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null);
    
    verify();
    assertEquals("groovyroute_show", result);
  }

  public void testShowRoute_byName_nonExisting() {
    String name = "somename";
    manager.getDefinition(name);
    managerControl.setReturnValue(null);
    methodMock.viewShowRoutes(model, "No route named \"somename\"");
    methodMockControl.setReturnValue("showroutes");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null,null, null);
    
    verify();
    assertEquals("showroutes", result);
  }

  public void testShowRoute_modify() {
    String name = "somename";
    List<String> newrecipients = new ArrayList<String>();
    
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    methodMock.modifyRoute(model, name, "hugga", false, "new descr", newrecipients, "def");
    methodMockControl.setReturnValue("somedirect");
    
    replay();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", newrecipients, "def", "Modify");
    
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

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, "Delete");
    
    verify();
    assertEquals("redirect:routes.htm", result);
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

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, "Delete");
    
    verify();
    assertEquals("routes", result);
  }

  public void testViewCreateRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("adaptors", adaptors);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("typdef", "def");
    modelControl.setReturnValue(null);

    classUnderTest = new GroovyRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replay();

    String result = classUnderTest.viewCreateRoute(model, "somename", "someauthor", true, "descr", recipients, "def", null);
    
    verify();
    assertEquals("groovyroute_create", result);
    
  }
  
  public void testViewShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    manager.getDefinitions();
    managerControl.setReturnValue(definitions);
    
    model.addAttribute("routes", definitions);
    modelControl.setReturnValue(null);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setManager(manager);
    
    replay();

    String result = classUnderTest.viewShowRoutes(model, null);
    
    verify();
    assertEquals("showroutes", result);
  }
  
  public void testViewShowRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("adaptors", adaptors);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("definition", "def");
    modelControl.setReturnValue(null);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", recipients, "def", null);
    
    verify();
    assertEquals("groovyroute_show", result);
  }

  public void testViewShowRoute_wEmessage() {
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("adaptors", adaptors);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "somename");
    modelControl.setReturnValue(null);
    model.addAttribute("author", "someauthor");
    modelControl.setReturnValue(null);
    model.addAttribute("active", true);
    modelControl.setReturnValue(null);
    model.addAttribute("description", "descr");
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("definition", "def");
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "xyz");
    modelControl.setReturnValue(null);
    
    classUnderTest = new GroovyRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);

    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", recipients, "def", "xyz");
    
    verify();
    assertEquals("groovyroute_show", result);
  }
  
  public void testModifyRoute() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    String script = "a script";
    GroovyRule rule = new GroovyRule();
    RouteDefinition definition = new RouteDefinition();
    
    classUnderTest = new GroovyRoutesController() {
      protected GroovyRule createRule(String script) {
        return methodMock.createRule(script);
      }
    };
    classUnderTest.setManager(manager);
    
    methodMock.createRule(script);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(definition);
    manager.updateDefinition(definition);
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

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
    
    methodMock.viewShowRoute(model, name, author, active, description, recipients, script, "Definition missing.");
    methodMockControl.setReturnValue("someredirect");
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verify();
    assertEquals("someredirect", result);
  }

  public void testModifyRoute_failedUpdate() throws Exception {
    String name = "A";
    String author = "B";
    boolean active = false;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    String script = "some def";
    GroovyRule rule = new GroovyRule();
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
    methodMock.createRule(script);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(definition);
    manager.updateDefinition(definition);
    managerControl.setThrowable(new RuntimeException("Bad..."));
    methodMock.viewShowRoute(model, name, author, active, description, recipients, script, "Failed to update definition: 'Bad...'");
    methodMockControl.setReturnValue("someredirect");
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, recipients, script);
    
    verify();
    assertEquals("someredirect", result);
  }
  
}

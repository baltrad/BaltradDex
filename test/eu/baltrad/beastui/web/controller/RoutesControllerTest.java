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
import eu.baltrad.beast.message.IBltMessage;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.IRule;
import eu.baltrad.beast.rules.IRuleFactory;
import eu.baltrad.beast.rules.RuleException;

/**
 * @author Anders Henja
 */
public class RoutesControllerTest extends TestCase {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        List<String> recipients,
        String type,
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
        String type,
        String definition,
        String emessage);    
  };
  private RoutesController classUnderTest = null;
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl factoryControl = null;
  private IRuleFactory factory = null;
  private MockControl adaptorControl = null;
  private IBltAdaptorManager adaptorManager = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodMockControl = null;
  private MethodMocker methodMock = null;
  
  public void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    factoryControl = MockControl.createControl(IRuleFactory.class);
    factory = (IRuleFactory) factoryControl.getMock();
    adaptorControl = MockControl.createControl(IBltAdaptorManager.class);
    adaptorManager = (IBltAdaptorManager) adaptorControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    methodMockControl = MockControl.createControl(MethodMocker.class);
    methodMock = (MethodMocker)methodMockControl.getMock();
    
    classUnderTest = new RoutesController() {
      protected String viewCreateRoute(Model model,
          String name,
          String author,
          Boolean active,
          String description,
          List<String> recipients,
          String type,
          String definition,
          String emessage) {
        return methodMock.viewCreateRoute(model, name, author, active, description, recipients, type, definition, emessage);
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
          String type,
          String definition,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, recipients, type, definition, emessage);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setRuleFactory(factory);
    classUnderTest.setAdaptorManager(adaptorManager);
  }

  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    factoryControl = null;
    factory = null;
    adaptorControl = null;
    adaptorManager = null;
    modelControl = null;
    model = null;
    methodMockControl = null;
    methodMock = null;
  }

  private void replay() {
    managerControl.replay();
    factoryControl.replay();
    adaptorControl.replay();
    modelControl.replay();
    methodMockControl.replay();
  }

  private void verify() {
    managerControl.verify();
    factoryControl.verify();
    adaptorControl.verify();
    modelControl.verify();
    methodMockControl.verify();
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
    assertEquals("routes", result);
  }

  public void testCreateRoute_initial() {
    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, null, null, null, null, null, null, null, null);
    methodMockControl.setReturnValue("createroute");
    
    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null,
        null, null, null);

    verify();
    assertEquals("createroute", result);
  }

  public void testCreateRoute_noAdaptors() {
    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null,
        null, null, null);

    verify();
    assertEquals("redirect:adaptors.htm", result);
  }

  public void testCreateRoute_noTypes() {
    List<String> types = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model
        .addAttribute("emessage",
            "No types defined, configuration error, please contact your administrator.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null,
        null, null, null);

    verify();
    assertEquals("routes", result);
  }

  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String type = "groovy";
    String def = "some code";

    List<String> recipients = new ArrayList<String>();

    IRule rule = new IRule() {
      public IBltMessage handle(IBltMessage message) {return null;}
      public String getType() {return null;}
      public String getDefinition() {return null;}
    };

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    factory.create(type, def);
    factoryControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, type, def);

    verify();
    assertEquals("redirect:routes.htm", result);
  }

  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String type = "groovy";
    String def = "some code";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, name, "nisse", true, "some description", recipients, "groovy", "some code", "Name must be specified.");
    methodMockControl.setReturnValue("createroute");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, type, def);

    verify();
    assertEquals("createroute", result);
  }
  
  public void testCreateRoute_noDefinition() {
    String name = "abc";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String type = "groovy";
    String def = "";

    List<String> recipients = new ArrayList<String>();

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, name, author, active, description, recipients, type, def, "Definition must be specified.");
    methodMockControl.setReturnValue("createroute");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, type, def);

    verify();
    assertEquals("createroute", result);
  }

  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    String type = "groovy";
    String def = "some code";

    List<String> recipients = new ArrayList<String>();

    IRule rule = new IRule() {
      public IBltMessage handle(IBltMessage message) {return null;}
      public String getType() {return null;}
      public String getDefinition() {return null;}
    };

    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    factory.create(type, def);
    factoryControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);
    managerControl.setThrowable(new RuleException("Duplicate name"));
    methodMock.viewCreateRoute(model, name, author, active, description, recipients, type, def, "Failed to create definition: 'Duplicate name'");
    methodMockControl.setReturnValue("createroute");
    replay();

    String result = classUnderTest.createRoute(model, name, author, active,
        description, recipients, type, def);

    verify();
    assertEquals("createroute", result);
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
    routeDefinition.setRule(new IRule() {
      @Override
      public IBltMessage handle(IBltMessage message) {return null;}
      @Override
      public String getType() {return "tt";}
      @Override
      public String getDefinition() {return "def";}
    });
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    
    methodMock.viewShowRoute(model, name, "nisse", true, "descr", recipients, "tt", "def", null);
    methodMockControl.setReturnValue("showroute");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null, null);
    
    verify();
    assertEquals("showroute", result);
  }

  public void testShowRoute_byName_nonExisting() {
    String name = "somename";
    manager.getDefinition(name);
    managerControl.setReturnValue(null);
    methodMock.viewShowRoutes(model, "No route named \"somename\"");
    methodMockControl.setReturnValue("routes");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null, null);
    
    verify();
    assertEquals("routes", result);
  }

  public void testShowRoute_modify() {
    String name = "somename";
    List<String> newrecipients = new ArrayList<String>();
    
    IRule rule = new IRule() {
      public IBltMessage handle(IBltMessage message) {return null;}
      public String getType() {return "groovy";}
      public String getDefinition() {return "def";}
    };

    RouteDefinition routeDefinition = new RouteDefinition();
    RouteDefinition newRouteDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    factory.create("groovy", "def");
    factoryControl.setReturnValue(rule);
    manager.create("somename", "hugga", false, "new descr", newrecipients, rule);
    managerControl.setReturnValue(newRouteDefinition);
    manager.updateDefinition(newRouteDefinition);
    
    replay();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", newrecipients, "groovy", "def", "Modify");
    
    verify();
    assertEquals("redirect:routes.htm", result);
  }

  public void testShowRoute_modify_badTypDef() {
    String name = "somename";
    List<String> newrecipients = new ArrayList<String>();
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    methodMock.viewShowRoute(model, name, "hugga", false, "new descr", newrecipients, "groovy", null, "Definition missing.");
    methodMockControl.setReturnValue("showroute");
    
    replay();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", newrecipients, "groovy", null, "Modify");
    
    verify();
    assertEquals("showroute", result);
  }
  
  public void testShowRoute_modify_failed() {
    String name = "somename";
    List<String> newrecipients = new ArrayList<String>();
    
    IRule rule = new IRule() {
      public IBltMessage handle(IBltMessage message) {return null;}
      public String getType() {return "groovy";}
      public String getDefinition() {return "def";}
    };

    RouteDefinition routeDefinition = new RouteDefinition();
    RouteDefinition newRouteDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    factory.create("groovy", "def");
    factoryControl.setReturnValue(rule);
    manager.create("somename", "hugga", false, "new descr", newrecipients, rule);
    managerControl.setReturnValue(newRouteDefinition);
    manager.updateDefinition(newRouteDefinition);
    managerControl.setThrowable(new RuleException("null value"));
    methodMock.viewShowRoute(model, name, "hugga", false, "new descr", newrecipients, "groovy", "def", "Failed to update definition: 'null value'");
    methodMockControl.setReturnValue("showroute");
    replay();

    String result = classUnderTest.showRoute(model, name, "hugga", false, "new descr", newrecipients, "groovy", "def", "Modify");
    
    verify();
    assertEquals("showroute", result);
  }
  
  
  public void testShowRoute_delete() {
    String name = "somename";
    RouteDefinition routeDefinition = new RouteDefinition();
    
    manager.getDefinition(name);
    managerControl.setReturnValue(routeDefinition);
    manager.deleteDefinition("somename");
    
    replay();

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null, "Delete");
    
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

    String result = classUnderTest.showRoute(model, name, null, null, null, null, null, null, "Delete");
    
    verify();
    assertEquals("routes", result);
  }

  public void testViewCreateRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> types = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("types", types);
    modelControl.setReturnValue(null);
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
    model.addAttribute("type", "t");
    modelControl.setReturnValue(null);
    model.addAttribute("typdef", "def");
    modelControl.setReturnValue(null);

    classUnderTest = new RoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setRuleFactory(factory);
    
    replay();

    String result = classUnderTest.viewCreateRoute(model, "somename", "someauthor", true, "descr", recipients, "t", "def", null);
    
    verify();
    assertEquals("createroute", result);
    
  }
  
  public void testViewShowRoutes() {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    
    manager.getDefinitions();
    managerControl.setReturnValue(definitions);
    
    model.addAttribute("routes", definitions);
    modelControl.setReturnValue(null);
    
    classUnderTest = new RoutesController();
    classUnderTest.setManager(manager);
    
    replay();

    String result = classUnderTest.viewShowRoutes(model, null);
    
    verify();
    assertEquals("routes", result);
  }
  
  public void testViewShowRoute() {
    List<String> recipients = new ArrayList<String>();
    List<String> types = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("types", types);
    modelControl.setReturnValue(null);
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
    model.addAttribute("type", "t");
    modelControl.setReturnValue(null);
    model.addAttribute("definition", "def");
    modelControl.setReturnValue(null);
    
    classUnderTest = new RoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setRuleFactory(factory);
    
    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", recipients, "t", "def", null);
    
    verify();
    assertEquals("showroute", result);
  }

  public void testViewShowRoute_wEmessage() {
    List<String> recipients = new ArrayList<String>();
    List<String> types = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();

    factory.getTypes();
    factoryControl.setReturnValue(types);
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    model.addAttribute("types", types);
    modelControl.setReturnValue(null);
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
    model.addAttribute("type", "t");
    modelControl.setReturnValue(null);
    model.addAttribute("definition", "def");
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", "xyz");
    modelControl.setReturnValue(null);
    
    classUnderTest = new RoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setRuleFactory(factory);

    replay();

    String result = classUnderTest.viewShowRoute(model, "somename", "someauthor", true, "descr", recipients, "t", "def", "xyz");
    
    verify();
    assertEquals("showroute", result);
  }
  
}

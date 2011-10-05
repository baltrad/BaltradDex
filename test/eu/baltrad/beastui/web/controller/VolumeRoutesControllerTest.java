package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.volume.VolumeRule;

public class VolumeRoutesControllerTest extends TestCase {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name, 
        String author,
        Boolean active, 
        String description, 
        Boolean ascending, 
        Double mine, 
        Double maxe,
        List<String> recipients, 
        Integer interval, 
        Integer timeout, 
        List<String> sources,
        String emessage);
    
    public String viewShowRoutes(Model model, String emessage);
    
    public String viewShowRoute(
        Model model,
        String name,
        String author,
        Boolean active,
        String description,
        Boolean ascending, 
        Double mine, 
        Double maxe,
        List<String> recipients,
        Integer interval,
        Integer timeout,
        List<String> sources,
        String emessage); 
    
    public VolumeRule createRule(
        boolean ascending, 
        double mine, 
        double maxe, 
        int interval, 
        List<String> sources, 
        int timeout);
    
    public String modifyRoute(
        Model model, 
        String name, 
        String author,
        Boolean active, 
        String description,
        Boolean ascending,
        Double mine,
        Double maxe,
        List<String> recipients,
        Integer interval,
        Integer timeout,
        List<String> sources);
  };
  
  private VolumeRoutesController classUnderTest = null;
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
    classUnderTest = new VolumeRoutesController() {
      protected String viewCreateRoute(Model model,
          String name, 
          String author,
          Boolean active, 
          String description, 
          Boolean ascending, 
          Double mine, 
          Double maxe,
          List<String> recipients, 
          Integer interval, 
          Integer timeout, 
          List<String> sources,
          String emessage) {
        return methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
      }
      
      protected String viewShowRoutes(Model model,
          String emessage) {
        return methodMock.viewShowRoutes(model, emessage);
      }
      
      protected String viewShowRoute(
          Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Boolean ascending, 
          Double mine, 
          Double maxe,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          List<String> sources,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
      }
      
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          int interval, 
          List<String> sources, 
          int timeout) {
        return methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
      }
      
      protected String modifyRoute(
          Model model, 
          String name, 
          String author,
          Boolean active, 
          String description,
          Boolean ascending,
          Double mine,
          Double maxe,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          List<String> sources) {
        return methodMock.modifyRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);
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
    methodMock.viewCreateRoute(model, null, null, null, null, null, null, null, null, null, null, null, null);
    methodMockControl.setReturnValue("volumeroute_create");
    
    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null);

    verify();
    assertEquals("volumeroute_create", result);
  }

  public void testCreateRoute_noAdaptors() {
    List<String> adaptors = new ArrayList<String>();
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null);

    verify();
    assertEquals("redirect:adaptors.htm", result);
  }

  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    // Constructor for VolumeRule is protected so just mock it
    MockControl ruleControl = MockClassControl.createControl(VolumeRule.class);
    VolumeRule rule = (VolumeRule)ruleControl.getMock();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);

    replay();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);

    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

  public void testCreateRoute_noadaptor_selected() {
    RouteDefinition routedef = new RouteDefinition();
    // Constructor for VolumeRule is protected so just mock it
    MockControl ruleControl = MockClassControl.createControl(VolumeRule.class);
    VolumeRule rule = (VolumeRule)ruleControl.getMock();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    String emessage = "You must specify at least one recipient";
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
    methodMockControl.setReturnValue("somestring");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);

    verify();
    assertEquals("somestring", result);
  }

  
  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    String emessage = "Name must be specified.";

    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
    methodMockControl.setReturnValue("somestring");

    replay();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);

    verify();
    assertEquals("somestring", result);
  }
  
  
  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    MockControl ruleControl = MockClassControl.createControl(VolumeRule.class);
    VolumeRule rule = (VolumeRule)ruleControl.getMock();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);
    managerControl.setThrowable(new RuleException("Duplicate name"));
    methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, "Failed to create definition: 'Duplicate name'");
    methodMockControl.setReturnValue("volumeroute_create");
    replay();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);

    verify();
    assertEquals("volumeroute_create", result);
  }
  
  public void testModifyRoute_success() throws Exception {
    MockControl ruleControl = MockClassControl.createControl(VolumeRule.class);
    VolumeRule rule = (VolumeRule)ruleControl.getMock();
    RouteDefinition routedef = new RouteDefinition();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
    methodMockControl.setReturnValue(rule);
    manager.create(name, author, active, description, recipients, rule);
    managerControl.setReturnValue(routedef);
    manager.updateDefinition(routedef);
    
    classUnderTest = new VolumeRoutesController() {
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          int interval, 
          List<String> sources, 
          int timeout) {
        return methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
      }      
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);
    
    verify();
    assertEquals("redirect:showroutes.htm", result);
  }

  public void testModifyRoute_noadaptorselected() throws Exception {
    MockControl ruleControl = MockClassControl.createControl(VolumeRule.class);
    VolumeRule rule = (VolumeRule)ruleControl.getMock();
    RouteDefinition routedef = new RouteDefinition();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    String emessage = "You must specify at least one recipient.";
    
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    
    methodMock.viewShowRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
    methodMockControl.setReturnValue("somestring");
    
    classUnderTest = new VolumeRoutesController() {
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          int interval, 
          List<String> sources, 
          int timeout) {
        return methodMock.createRule(ascending, mine, maxe, interval, sources, timeout);
      }
      protected String viewShowRoute(
          Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Boolean ascending, 
          Double mine, 
          Double maxe,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          List<String> sources,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ascending, mine, maxe, recipients, interval, timeout, sources, emessage);
      }      
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replay();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ascending, mine, maxe, recipients, interval, timeout, sources);
    
    verify();
    assertEquals("somestring", result);
  }

}

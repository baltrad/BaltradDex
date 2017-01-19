package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.RuleException;
import eu.baltrad.beast.rules.volume.VolumeRule;

public class VolumeRoutesControllerTest extends EasyMockSupport {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name, 
        String author,
        Boolean active, 
        String description, 
        Boolean ascending, 
        Double mine, 
        Double maxe,
        String elangles,
        List<String> recipients, 
        Integer interval, 
        Integer timeout, 
        Boolean nominal_timeout,
        List<String> sources,
        List<String> detectors,
        Integer quality_control_mode,
        String jsonFilter,
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
        String elangles,
        List<String> recipients,
        Integer interval,
        Integer timeout,
        Boolean nominal_timeout,
        List<String> sources,
        List<String> detectors,
        Integer quality_control_mode,
        String jsonFilter,
        String emessage); 
    
    public VolumeRule createRule(
        boolean ascending, 
        double mine, 
        double maxe, 
        String elangles,
        int interval, 
        List<String> sources, 
        List<String> detectors,
        int quality_control_mode,
        int timeout,
        boolean nominal_timeout,
        String jsonFilter);
    
    public String modifyRoute(
        Model model, 
        String name, 
        String author,
        Boolean active, 
        String description,
        Boolean ascending,
        Double mine,
        Double maxe,
        String elangles,
        List<String> recipients,
        Integer interval,
        Integer timeout,
        Boolean nominal_timeout,
        List<String> sources,
        List<String> detectors,
        Integer quality_control_mode,
        String jsonFilter);
  };
  
  private VolumeRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private IBltAdaptorManager adaptorManager = null;
  private IAnomalyDetectorManager anomalyManager = null;  
  private Model model = null;
  private MethodMocker methodMock = null;

  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    adaptorManager = createMock(IBltAdaptorManager.class);
    anomalyManager = createMock(IAnomalyDetectorManager.class);    
    model = createMock(Model.class);
    methodMock = createMock(MethodMocker.class);
    
    // All creator methods and view methods are mocked to simplify testing
    //
    classUnderTest = new VolumeRoutesController() {
      @Override
      protected String viewCreateRoute(Model model,
          String name, 
          String author,
          Boolean active, 
          String description, 
          Boolean ascending, 
          Double mine, 
          Double maxe,
          String elangles,
          List<String> recipients, 
          Integer interval, 
          Integer timeout, 
          Boolean nominal_timeout,
          List<String> sources,
          List<String> detectors,
          Integer quality_control_mode,
          String jsonFilter,
          String emessage) {
        return methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage);
      }
      
      @Override
      protected String viewShowRoutes(Model model,
          String emessage) {
        return methodMock.viewShowRoutes(model, emessage);
      }
      
      @Override
      protected String viewShowRoute(
          Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Boolean ascending, 
          Double mine, 
          Double maxe,
          String elangles,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          Boolean nominal_timeout,
          List<String> sources,
          List<String> detectors, 
          Integer quality_control_mode,
          String jsonFilter,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage);
      }
      
      @Override
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          String elangles,
          int interval, 
          List<String> sources, 
          List<String> detectors, 
          int quality_control_mode,
          int timeout,
          boolean nominal_timeout,
          String jsonFilter) {
        return methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter);
      }
      
      @Override
      protected String modifyRoute(
          Model model, 
          String name, 
          String author,
          Boolean active, 
          String description,
          Boolean ascending,
          Double mine,
          Double maxe,
          String elangles,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          Boolean nominal_timeout,
          List<String> sources,
          List<String> detectors,
          Integer quality_control_mode,
          String jsonFilter) {
        return methodMock.modifyRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);    
  }

  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    adaptorManager = null;
    anomalyManager = null;
    model = null;
    methodMock = null;
  }

  @Test
  public void testCreateRoute_initial() {
    List<String> types = new ArrayList<String>();
    types.add("groovy");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(methodMock.viewCreateRoute(model, null, null, null, null, true, null, null, null, null, null, null, null, null, null, null, null, null)).andReturn("route_create_volume");
    
    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    verifyAll();
    assertEquals("route_create_volume", result);
  }

  @Test
  public void testCreateRoute_noAdaptors() {
    List<String> adaptors = new ArrayList<String>();
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);

    expect(model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.")).andReturn(null);

    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    verifyAll();
    assertEquals("redirect:adaptors.htm", result);
  }

  @Test
  public void testCreateRoute_success() {
    RouteDefinition routedef = new RouteDefinition();
    // Constructor for VolumeRule is protected so just mock it
    VolumeRule rule = createMock(VolumeRule.class);
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5,2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    List<String> detectors = new ArrayList<String>();
    sources.add("nisse");
    Integer quality_control_mode = 1;
    String jsonFilter = "";
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);

    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testCreateRoute_noadaptor_selected() {
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5, 2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    String jsonFilter = ""; 
    String emessage = "You must specify at least one recipient";
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    List<String> detectors = new ArrayList<String>();
    sources.add("nisse");
    Integer quality_control_mode = 1;

    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage)).andReturn("somestring");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);

    verifyAll();
    assertEquals("somestring", result);
  }

  @Test
  public void testCreateRoute_noName() {
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5,2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    String jsonFilter = "";
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    String emessage = "Name must be specified.";

    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    
    expect(methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage)).andReturn("somestring");

    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);

    verifyAll();
    assertEquals("somestring", result);
  }
  
  @Test
  public void testCreateRoute_failedCreate() {
    RouteDefinition routedef = new RouteDefinition();
    VolumeRule rule = createMock(VolumeRule.class);
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5, 2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    String jsonFilter = "";
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);
    expectLastCall().andThrow(new RuleException("Duplicate name"));
    expect(methodMock.viewCreateRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, "Failed to create definition: 'Duplicate name'")).andReturn("route_create_volume");
    
    replayAll();

    String result = classUnderTest.createRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);

    verifyAll();
    assertEquals("route_create_volume", result);
  }
  
  @Test
  public void testModifyRoute_success() throws Exception {
    VolumeRule rule = createMock(VolumeRule.class);
    RouteDefinition routedef = new RouteDefinition();
    
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5,2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    String jsonFilter = "";
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    
    List<String> recipients = new ArrayList<String>();
    recipients.add("A1");
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    expect(methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter)).andReturn(rule);
    expect(manager.create(name, author, active, description, recipients, rule)).andReturn(routedef);
    manager.updateDefinition(routedef);
    
    classUnderTest = new VolumeRoutesController() {
      @Override
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          String elangles,
          int interval, 
          List<String> sources, 
          List<String> detectors,
          int quality_control_mode,
          int timeout,
          boolean nominal_timeout,
          String jsonFilter) {
        return methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter);
      }      
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testModifyRoute_noadaptorselected() throws Exception {
    String name = "MyRoute";
    String author = "nisse";
    Boolean active = true;
    String description = "some description";
    Boolean ascending = true;
    Double mine = 0.5;
    Double maxe = 0.5;
    String elangles = "1.5,2.5";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    String jsonFilter = "";
    List<String> sources = new ArrayList<String>();
    sources.add("seang");
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    String emessage = "You must specify at least one recipient.";
    
    List<String> recipients = new ArrayList<String>();
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A1");
    
    expect(methodMock.viewShowRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter, emessage)).andReturn("somestring");
    
    classUnderTest = new VolumeRoutesController() {
      @Override
      protected VolumeRule createRule(
          boolean ascending, 
          double mine, 
          double maxe, 
          String elangles,
          int interval, 
          List<String> sources, 
          List<String> detectors,
          int quality_control_mode,
          int timeout,
          boolean nominal_timeout,
          String jsonFilter) {
        return methodMock.createRule(ascending, mine, maxe, elangles, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, jsonFilter);
      }
      @Override
      protected String viewShowRoute(
          Model model,
          String name,
          String author,
          Boolean active,
          String description,
          Boolean ascending, 
          Double mine, 
          Double maxe,
          String elangles,
          List<String> recipients,
          Integer interval,
          Integer timeout,
          Boolean nominal_timeout,
          List<String> sources,
          List<String> detectors,
          Integer quality_control_mode,
          String jsonFilter,
          String emessage) {
        return methodMock.viewShowRoute(model, name, author, active,
            description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter,  emessage);
      }      
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    
    replayAll();
    
    String result = classUnderTest.modifyRoute(model, name, author, active, description, ascending, mine, maxe, elangles, recipients, interval, timeout, nominal_timeout, sources, detectors, quality_control_mode, jsonFilter);
    
    verifyAll();
    assertEquals("somestring", result);
  }
}

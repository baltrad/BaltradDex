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
import eu.baltrad.beast.pgf.IPgfClientHelper;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.gmap.GoogleMapRule;

public class GoogleMapRoutesControllerTest extends EasyMockSupport {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model,
        String name,
        String author,
        Boolean active,
        String description,
        List<String> recipients,
        String area,
        String path,
        String emessage);
    public GoogleMapRule createRule(String area, String path);
  };
  
  private MethodMocker method = null;
  private GoogleMapRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private IBltAdaptorManager adaptorManager = null;
  private IPgfClientHelper pgfClientHelper = null;
  private Model model = null;

  @Before
  public void setUp() throws Exception {
    method = createMock(MethodMocker.class);
    manager = createMock(IRouterManager.class);
    adaptorManager = createMock(IBltAdaptorManager.class);
    pgfClientHelper = createMock(IPgfClientHelper.class);
    
    model = createMock(Model.class);
    classUnderTest = new GoogleMapRoutesController() {
      protected String viewCreateRoute(Model model, String name, String author,
          Boolean active, String description, List<String> recipients,
          String area, String path, String emessage) {
        return method.viewCreateRoute(model, name, author, active, description, recipients, area, path, emessage);
      }
      protected GoogleMapRule createRule(String area, String path) {
        return method.createRule(area, path);
      }
    };
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
  }

  @After
  public void tearDown() throws Exception {
    method = null;
    manager = null;
    adaptorManager = null;
    model = null;
    classUnderTest = null;
  }
  
  @Test
  public void testCreateRoute_initial() throws Exception {
    expect(method.viewCreateRoute(model, null, null, null, null, null, null, null, null)).andReturn("route_create_google_map");
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null);
    
    verifyAll();
    assertEquals("route_create_google_map", result);
  }

  @Test
  public void testCreateRoute() throws Exception {
    List<String> recipients = new ArrayList<String>();
    recipients.add("NISSE");
    GoogleMapRule rule = new GoogleMapRule();
    RouteDefinition routedef = new RouteDefinition();
    
    expect(method.createRule("sswe", "/tmp")).andReturn(rule);
    expect(manager.create("name", "author", true, "test", recipients, rule)).andReturn(routedef);
    manager.storeDefinition(routedef);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "name", "author", true, "test", recipients, "sswe", "/tmp");
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testViewShowRoutes() throws Exception {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    expect(manager.getDefinitions()).andReturn(definitions);
    expect(model.addAttribute("routes", definitions)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.viewShowRoutes(model, null);
    
    verifyAll();
    assertEquals("routes", result);
  }
  
  @Test
  public void testViewCreateRoute_allNull() throws Exception {
    List<String> names = new ArrayList<String>();
    List<String> areas = new ArrayList<String>();
    
    expect(adaptorManager.getAdaptorNames()).andReturn(names);
    expect(model.addAttribute("adaptors", names)).andReturn(null);
    expect(model.addAttribute("name", "")).andReturn(null);
    expect(model.addAttribute("author", "")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "")).andReturn(null);
    expect(model.addAttribute("recipients", new ArrayList<String>())).andReturn(null);
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(areas);
    expect(model.addAttribute("arealist", areas)).andReturn(null);
    expect(model.addAttribute("area", "")).andReturn(null);
    expect(model.addAttribute("path", "")).andReturn(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, null, null, null, null, null, null, null, null);
    
    verifyAll();
    assertEquals("route_create_google_map", result);
  }

  @Test
  public void testViewCreateRoute() throws Exception {
    List<String> names = new ArrayList<String>();
    List<String> areas = new ArrayList<String>();
    List<String> recipients = new ArrayList<String>();
    
    expect(adaptorManager.getAdaptorNames()).andReturn(names);
    expect(model.addAttribute("adaptors", names)).andReturn(null);
    expect(model.addAttribute("name", "name")).andReturn(null);
    expect(model.addAttribute("author", "author")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "a test")).andReturn(null);
    expect(model.addAttribute("recipients", recipients)).andReturn(null);;
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(areas);
    expect(model.addAttribute("arealist", areas)).andReturn(null);
    expect(model.addAttribute("area", "sswe")).andReturn(null);
    expect(model.addAttribute("path", "/tmp")).andReturn(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, "name", "author", true, "a test", recipients, "sswe", "/tmp", null);
    
    verifyAll();
    assertEquals("route_create_google_map", result);
  }

  @Test
  public void testViewShowRoute() throws Exception {
    List<String> names = new ArrayList<String>();
    List<String> areas = new ArrayList<String>();
    List<String> recipients = new ArrayList<String>();
    
    expect(adaptorManager.getAdaptorNames()).andReturn(names);
    expect(model.addAttribute("adaptors", names)).andReturn(null);
    expect(model.addAttribute("name", "name")).andReturn(null);
    expect(model.addAttribute("author", "author")).andReturn(null);
    expect(model.addAttribute("active", true)).andReturn(null);
    expect(model.addAttribute("description", "a test")).andReturn(null);
    expect(model.addAttribute("recipients", recipients)).andReturn(null);
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(areas);
    expect(model.addAttribute("arealist", areas)).andReturn(null);
    expect(model.addAttribute("area", "sswe")).andReturn(null);
    expect(model.addAttribute("path", "/tmp")).andReturn(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
    
    replayAll();
    
    String result = classUnderTest.viewShowRoute(model, "name", "author", true, "a test", recipients, "sswe", "/tmp", null);
    
    verifyAll();
    assertEquals("route_show_google_map", result);
  }
    
}

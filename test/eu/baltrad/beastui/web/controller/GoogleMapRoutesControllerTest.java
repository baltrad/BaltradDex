package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.gmap.GoogleMapRule;

public class GoogleMapRoutesControllerTest extends TestCase {
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
  
  private MockControl methodControl = null;
  private MethodMocker method = null;
  private GoogleMapRoutesController classUnderTest = null;
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl adaptorManagerControl = null;
  private IBltAdaptorManager adaptorManager = null;
  private MockControl modelControl = null;
  private Model model = null;
  
  public void setUp() throws Exception {
    methodControl = MockControl.createControl(MethodMocker.class);
    method = (MethodMocker)methodControl.getMock();
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager)managerControl.getMock();
    adaptorManagerControl = MockControl.createControl(IBltAdaptorManager.class);
    adaptorManager = (IBltAdaptorManager)adaptorManagerControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model)modelControl.getMock();
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
  }
  
  public void tearDown() throws Exception {
    methodControl = null;
    method = null;
    managerControl = null;
    manager = null;
    adaptorManagerControl = null;
    adaptorManager = null;
    modelControl = null;
    model = null;
    classUnderTest = null;
  }
  
  protected void replayAll() {
    methodControl.replay();
    managerControl.replay();
    adaptorManagerControl.replay();
    modelControl.replay();
  }

  protected void verifyAll() {
    methodControl.verify();
    managerControl.verify();
    adaptorManagerControl.verify();
    modelControl.verify();
  }

  public void testCreateRoute_initial() throws Exception {
    method.viewCreateRoute(model, null, null, null, null, null, null, null, null);
    methodControl.setReturnValue("googlemaproute_create");
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null);
    
    verifyAll();
    assertEquals("googlemaproute_create", result);
  }

  public void testCreateRoute() throws Exception {
    List<String> recipients = new ArrayList<String>();
    recipients.add("NISSE");
    GoogleMapRule rule = new GoogleMapRule();
    RouteDefinition routedef = new RouteDefinition();
    
    method.createRule("sswe", "/tmp"); methodControl.setReturnValue(rule);
    manager.create("name", "author", true, "test", recipients, rule); managerControl.setReturnValue(routedef);
    manager.storeDefinition(routedef);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "name", "author", true, "test", recipients, "sswe", "/tmp");
    
    verifyAll();
    assertEquals("redirect:showroutes.htm", result);
  }
  
  public void testViewShowRoutes() throws Exception {
    List<RouteDefinition> definitions = new ArrayList<RouteDefinition>();
    manager.getDefinitions();
    managerControl.setReturnValue(definitions);
    model.addAttribute("routes", definitions);
    modelControl.setReturnValue(null);
    
    replayAll();
    
    String result = classUnderTest.viewShowRoutes(model, null);
    
    verifyAll();
    assertEquals("showroutes", result);
  }
  
  public void testViewCreateRoute_allNull() throws Exception {
    List<String> names = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorManagerControl.setReturnValue(names);
    model.addAttribute("adaptors", names);
    modelControl.setReturnValue(null);
    model.addAttribute("name", ""); modelControl.setReturnValue(null);
    model.addAttribute("author", ""); modelControl.setReturnValue(null);
    model.addAttribute("active", true); modelControl.setReturnValue(null);
    model.addAttribute("description", ""); modelControl.setReturnValue(null);
    model.addAttribute("recipients", new ArrayList<String>()); modelControl.setReturnValue(null);
    model.addAttribute("area", ""); modelControl.setReturnValue(null);
    model.addAttribute("path", ""); modelControl.setReturnValue(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, null, null, null, null, null, null, null, null);
    
    verifyAll();
    assertEquals("googlemaproute_create", result);
  }

  public void testViewCreateRoute() throws Exception {
    List<String> names = new ArrayList<String>();
    List<String> recipients = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorManagerControl.setReturnValue(names);
    model.addAttribute("adaptors", names);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "name"); modelControl.setReturnValue(null);
    model.addAttribute("author", "author"); modelControl.setReturnValue(null);
    model.addAttribute("active", true); modelControl.setReturnValue(null);
    model.addAttribute("description", "a test"); modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients); modelControl.setReturnValue(null);
    model.addAttribute("area", "sswe"); modelControl.setReturnValue(null);
    model.addAttribute("path", "/tmp"); modelControl.setReturnValue(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, "name", "author", true, "a test", recipients, "sswe", "/tmp", null);
    
    verifyAll();
    assertEquals("googlemaproute_create", result);
  }

  public void testViewShowRoute() throws Exception {
    List<String> names = new ArrayList<String>();
    List<String> recipients = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorManagerControl.setReturnValue(names);
    model.addAttribute("adaptors", names);
    modelControl.setReturnValue(null);
    model.addAttribute("name", "name"); modelControl.setReturnValue(null);
    model.addAttribute("author", "author"); modelControl.setReturnValue(null);
    model.addAttribute("active", true); modelControl.setReturnValue(null);
    model.addAttribute("description", "a test"); modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients); modelControl.setReturnValue(null);
    model.addAttribute("area", "sswe"); modelControl.setReturnValue(null);
    model.addAttribute("path", "/tmp"); modelControl.setReturnValue(null);

    classUnderTest = new GoogleMapRoutesController();
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    
    replayAll();
    
    String result = classUnderTest.viewShowRoute(model, "name", "author", true, "a test", recipients, "sswe", "/tmp", null);
    
    verifyAll();
    assertEquals("googlemaproute_show", result);
  }
    
}

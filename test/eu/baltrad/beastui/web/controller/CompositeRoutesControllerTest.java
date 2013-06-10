package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;
import org.easymock.classextension.MockClassControl;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.composite.CompositingRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

public class CompositeRoutesControllerTest extends TestCase {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model, String name, String author,
        Boolean active, String description, List<String> recipients, Boolean byscan, String method, String prodpar, Integer selection_method,
        String areaid, Integer interval, Integer timeout, List<String> sources, List<String> detectors, String emessage);
    
    public List<String> getSources();
    
    public List<Integer> getIntervals();
    
    public CompositingRule createRule(String areaid, int interval,
        List<String> sources, List<String> detectors, int timeout, boolean byscan, String method, String prodpar, int selection_method);
  }
  
  private CompositeRoutesController classUnderTest = null;
  private MockControl managerControl = null;
  private IRouterManager manager = null;
  private MockControl adaptorControl = null;
  private IBltAdaptorManager adaptorManager = null;
  private MockControl anomalyControl = null;
  private IAnomalyDetectorManager anomalyManager = null;
  private MockControl utilitiesControl = null;
  private IRuleUtilities utilities = null;
  private MockControl modelControl = null;
  private Model model = null;
  private MockControl methodControl = null;
  private MethodMocker method = null;

  protected void setUp() throws Exception {
    managerControl = MockControl.createControl(IRouterManager.class);
    manager = (IRouterManager) managerControl.getMock();
    adaptorControl = MockControl.createControl(IBltAdaptorManager.class);
    adaptorManager = (IBltAdaptorManager) adaptorControl.getMock();
    anomalyControl = MockControl.createControl(IAnomalyDetectorManager.class);
    anomalyManager = (IAnomalyDetectorManager)anomalyControl.getMock();
    utilitiesControl = MockControl.createControl(IRuleUtilities.class);
    utilities = (IRuleUtilities)utilitiesControl.getMock();
    modelControl = MockControl.createControl(Model.class);
    model = (Model) modelControl.getMock();
    methodControl = MockControl.createControl(MethodMocker.class);
    method = (MethodMocker)methodControl.getMock();
    
    classUnderTest = new CompositeRoutesController() {
      protected String viewCreateRoute(Model model, String name, String author,
          Boolean active, String description, List<String> recipients, Boolean byscan, 
          String pmethod, String prodpar, Integer selection_method,
          String areaid, Integer interval, Integer timeout, List<String> sources, List<String> detectors, String emessage) {
        return method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid,
            interval, timeout, sources, detectors, emessage);
      }
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
  }
  
  public void tearDown() throws Exception {
    classUnderTest = null;
    managerControl = null;
    manager = null;
    adaptorControl = null;
    adaptorManager = null;
    anomalyControl = null;
    anomalyManager = null;
    modelControl = null;
    model = null;
  }
  
  private void replay() {
    managerControl.replay();
    adaptorControl.replay();
    anomalyControl.replay();
    utilitiesControl.replay();
    modelControl.replay();
    methodControl.replay();
  }

  private void verify() {
    managerControl.verify();
    adaptorControl.verify();
    anomalyControl.verify();
    utilitiesControl.verify();
    modelControl.verify();
    methodControl.verify();
  }
  
  public void testCreateRoute_initial() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    methodControl.setReturnValue("somestring");
    replay();
    
    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null);

    verify();
    assertEquals("somestring", result);
  }
  
  public void testCreateRoute() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    method.createRule("areaid", 30, sources, detectors, 40, true, CompositingRule.CAPPI, "500", 0);
    methodControl.setReturnValue(rule);
    manager.create("aname", "author", true, "a description", recipients, rule);
    managerControl.setReturnValue(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      protected CompositingRule createRule(String areaid, int interval,
          List<String> sources, List<String> detectors, int timeout, boolean byscan, String pmethod, String prodpar, int selection_method) {
        return method.createRule(areaid, interval, sources, detectors, timeout, byscan, pmethod, prodpar, selection_method);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replay();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.CAPPI, "500", 0, "areaid", 30, 40, sources, detectors);
    
    verify();
    assertEquals("redirect:routes.htm", result);
  }
  
  public void testCreateRoute_noAdaptors() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.");
    modelControl.setReturnValue(null);

    replay();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null);

    verify();
    assertEquals("redirect:adaptors.htm", result);
  }
  
  public void testCreateRoute_noName() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = null;
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String pmethod = CompositingRule.CAPPI;
    String prodpar = "500";
    Integer selection_method = new Integer(0);
    String areaid = "xyz";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    
    String emessage = "Name must be specified.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors);
    
    verify();
    assertEquals("somestring", result);
    
  }

  public void testCreateRoute_noAreaid() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = "somename";
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String pmethod = CompositingRule.PCAPPI;
    String prodpar = "600.0";
    Integer selection_method = new Integer(0);
    String areaid = null;
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    String emessage = "Areaid must be specified.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
   
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors);
    
    verify();
    assertEquals("somestring", result);
    
  }

  public void testCreateRoute_noSources() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    String name = "somename";
    String author = "nisse";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String pmethod = CompositingRule.PPI;
    String prodpar = "0.5";
    Integer selection_method = new Integer(0);
    String areaid = "area";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    String emessage = "Must specify at least one source.";
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors);
    
    verify();
    assertEquals("somestring", result);
  }
  
  public void testCreateRoute_pmax() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    method.createRule("areaid", 30, sources, detectors, 40, true, CompositingRule.PMAX, "500, 70000.0", 0);
    methodControl.setReturnValue(rule);
    manager.create("aname", "author", true, "a description", recipients, rule);
    managerControl.setReturnValue(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      protected CompositingRule createRule(String areaid, int interval,
          List<String> sources, List<String> detectors, int timeout, boolean byscan, String pmethod, String prodpar, int selection_method) {
        return method.createRule(areaid, interval, sources, detectors, timeout, byscan, pmethod, prodpar, selection_method);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replay();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.PMAX, "500, 70000.0", 0, "areaid", 30, 40, sources, detectors);
    
    verify();
    assertEquals("redirect:routes.htm", result);
    
  }

  public void testCreateRoute_pmax_2() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);

    method.createRule("areaid", 30, sources, detectors, 40, true, CompositingRule.PMAX, "0,1", 0);
    methodControl.setReturnValue(rule);
    manager.create("aname", "author", true, "a description", recipients, rule);
    managerControl.setReturnValue(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      protected CompositingRule createRule(String areaid, int interval,
          List<String> sources, List<String> detectors, int timeout, boolean byscan, String pmethod, String prodpar, int selection_method) {
        return method.createRule(areaid, interval, sources, detectors, timeout, byscan, pmethod, prodpar, selection_method);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replay();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.PMAX, "0,1", 0, "areaid", 30, 40, sources, detectors);
    
    verify();
    assertEquals("redirect:routes.htm", result);
    
  }

  public void testCreateRoute_pmax_badprodpar() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    String emessage = "Product parameter must be <height>,<range> value for pmax.";
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptors);
    
    method.viewCreateRoute(model, "name", "author", true, "description", recipients, false, "pmax", ",500000", 0, "areaid", 30, 40, sources, detectors, emessage);
    methodControl.setReturnValue("somestring");
    
    replay();
    String result = classUnderTest.createRoute(model, "name", "author", true, "description", recipients, false, CompositingRule.PMAX, ",500000", 0, "areaid", 30, 40, sources, detectors);
    
    verify();
    assertEquals("somestring", result);
  }

  public void testViewCreateRoute() throws Exception {
    List<String> adaptornames = new ArrayList<String>();
    List<String> sourceids = new ArrayList<String>();
    List<Integer> intervals = new ArrayList<Integer>();
    List<AnomalyDetector> anomalydetectors = new ArrayList<AnomalyDetector>();

    String name = "A";
    String author = "B";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String pmethod = CompositingRule.CAPPI;
    String prodpar = "500.0";
    Integer selection_method = new Integer(0);
    String areaid = "blt_lambert";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    
    String emessage = null;

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptornames);
    utilities.getRadarSources();
    utilitiesControl.setReturnValue(sourceids);
    method.getIntervals();
    methodControl.setReturnValue(intervals);
    anomalyManager.list();
    anomalyControl.setReturnValue(anomalydetectors);

    model.addAttribute("adaptors", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("sourceids", sourceids);
    modelControl.setReturnValue(null);
    model.addAttribute("intervals", intervals);
    modelControl.setReturnValue(null);
    model.addAttribute("anomaly_detectors", anomalydetectors);
    modelControl.setReturnValue(null);
    model.addAttribute("name", name);
    modelControl.setReturnValue(null);
    model.addAttribute("author", author);
    modelControl.setReturnValue(null);
    model.addAttribute("active", active);
    modelControl.setReturnValue(null);
    model.addAttribute("description", description);
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("byscan", byscan);
    modelControl.setReturnValue(null);
    model.addAttribute("method", pmethod);
    modelControl.setReturnValue(null);
    model.addAttribute("prodpar", "500.0");
    modelControl.setReturnValue(null);
    model.addAttribute("selection_method", selection_method);
    modelControl.setReturnValue(null);
    model.addAttribute("areaid", areaid);
    modelControl.setReturnValue(null);
    model.addAttribute("interval", interval);
    modelControl.setReturnValue(null);
    model.addAttribute("timeout", timeout);
    modelControl.setReturnValue(null);
    model.addAttribute("sources", sources);
    modelControl.setReturnValue(null);
    model.addAttribute("detectors", detectors);
    modelControl.setReturnValue(null);
    
    classUnderTest = new CompositeRoutesController() {
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }      
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setRuleUtilities(utilities);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    replay();
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors, emessage);
    verify();
    assertEquals("route_create_composite", result);
  }

  public void testViewCreateRoute_emessage() throws Exception {
    List<String> adaptornames = new ArrayList<String>();
    List<String> sourceids = new ArrayList<String>();
    List<Integer> intervals = new ArrayList<Integer>();
    List<AnomalyDetector> anomalydetectors = new ArrayList<AnomalyDetector>();
    
    String name = "A";
    String author = "B";
    Boolean active = true;
    String description = "descr";
    List<String> recipients = new ArrayList<String>();
    Boolean byscan = new Boolean(false);
    String pmethod = CompositingRule.PCAPPI;
    String prodpar = "600.0";
    Integer selection_method = new Integer(0);
    String areaid = "blt_lambert";
    Integer interval = 10;
    Integer timeout = 10000;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    
    String emessage = "Some message";

    adaptorManager.getAdaptorNames();
    adaptorControl.setReturnValue(adaptornames);
    utilities.getRadarSources();
    utilitiesControl.setReturnValue(sourceids);
    method.getIntervals();
    methodControl.setReturnValue(intervals);
    anomalyManager.list();
    anomalyControl.setReturnValue(anomalydetectors);
    
    model.addAttribute("adaptors", adaptornames);
    modelControl.setReturnValue(null);
    model.addAttribute("sourceids", sourceids);
    modelControl.setReturnValue(null);
    model.addAttribute("intervals", intervals);
    modelControl.setReturnValue(null);
    model.addAttribute("anomaly_detectors", anomalydetectors);
    modelControl.setReturnValue(null);
    model.addAttribute("name", name);
    modelControl.setReturnValue(null);
    model.addAttribute("author", author);
    modelControl.setReturnValue(null);
    model.addAttribute("active", active);
    modelControl.setReturnValue(null);
    model.addAttribute("description", description);
    modelControl.setReturnValue(null);
    model.addAttribute("recipients", recipients);
    modelControl.setReturnValue(null);
    model.addAttribute("byscan", byscan);
    modelControl.setReturnValue(null);
    model.addAttribute("method", pmethod);
    modelControl.setReturnValue(null);
    model.addAttribute("prodpar", prodpar);
    modelControl.setReturnValue(null);
    model.addAttribute("selection_method", selection_method);
    modelControl.setReturnValue(null);
    model.addAttribute("areaid", areaid);
    modelControl.setReturnValue(null);
    model.addAttribute("interval", interval);
    modelControl.setReturnValue(null);
    model.addAttribute("timeout", timeout);
    modelControl.setReturnValue(null);
    model.addAttribute("sources", sources);
    modelControl.setReturnValue(null);
    model.addAttribute("detectors", detectors);
    modelControl.setReturnValue(null);
    model.addAttribute("emessage", emessage);
    modelControl.setReturnValue(null);
    
    classUnderTest = new CompositeRoutesController() {
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }      
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setRuleUtilities(utilities);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);

    replay();
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, interval, timeout, sources, detectors, emessage);
    verify();
    assertEquals("route_create_composite", result);
  }
  
  public void testCreateRule() throws Exception {
    MockControl cruleControl = MockClassControl.createControl(CompositingRule.class);
    CompositingRule crule = (CompositingRule)cruleControl.getMock();
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    
    manager.createRule(CompositingRule.TYPE);
    managerControl.setReturnValue(crule);
    crule.setArea("abc");
    crule.setInterval(1);
    crule.setSources(sources);
    crule.setDetectors(detectors);
    crule.setTimeout(2);
    crule.setScanBased(false);
    crule.setMethod(CompositingRule.CAPPI);
    crule.setProdpar("500.0");
    crule.setSelectionMethod(0);
    
    replay();
    cruleControl.replay();
    
    CompositingRule result = classUnderTest.createRule("abc", 1, sources, detectors, 2, false, CompositingRule.CAPPI, "500.0", 0);
    
    verify();
    cruleControl.verify();
    assertSame(crule, result);
  }
}

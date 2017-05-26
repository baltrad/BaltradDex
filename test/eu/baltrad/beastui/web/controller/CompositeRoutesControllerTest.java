package eu.baltrad.beastui.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.adaptor.IBltAdaptorManager;
import eu.baltrad.beast.pgf.IPgfClientHelper;
import eu.baltrad.beast.qc.AnomalyDetector;
import eu.baltrad.beast.qc.IAnomalyDetectorManager;
import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.router.RouteDefinition;
import eu.baltrad.beast.rules.composite.CompositingRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class CompositeRoutesControllerTest extends EasyMockSupport {
  private static interface MethodMocker {
    public String viewCreateRoute(Model model, String name, String author,
        Boolean active, String description, List<String> recipients, Boolean byscan, String method, String prodpar, Integer selection_method,
        String areaid, String quantity, Integer interval, Integer timeout, Boolean nominal_timeout, Boolean applygra, Double ZR_A, Double ZR_b, Boolean ignore_malfunc, Boolean ctfilter, 
        String qitotal_field, List<String> sources, List<String> detectors, Integer quality_control_mode, Boolean reprocess_quality, String jsonFilter, String emessage);
    
    public List<String> getSources();
    
    public List<Integer> getIntervals();

    public CompositingRule createRule(String areaid, String quantity, int interval,
        List<String> sources, List<String> detectors, int quality_control_mode, int timeout, boolean nominal_timeout, boolean byscan, String method, String prodpar, int selection_method,
        boolean applygra, double ZR_A, double ZR_b, boolean ignore_malfunc, boolean ctfilter, boolean reprocess_quality, String qitotalField, String jsonFilter);
  }
  
  private CompositeRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private IBltAdaptorManager adaptorManager = null;
  private IAnomalyDetectorManager anomalyManager = null;
  private IRuleUtilities utilities = null;
  private IPgfClientHelper pgfClientHelper = null;
  private Model model = null;
  private MethodMocker method = null;

  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    adaptorManager = createMock(IBltAdaptorManager.class);
    anomalyManager = createMock(IAnomalyDetectorManager.class);
    utilities = createMock(IRuleUtilities.class);
    pgfClientHelper = createMock(IPgfClientHelper.class);
    model = createMock(Model.class);
    method = createMock(MethodMocker.class);
    
    classUnderTest = new CompositeRoutesController() {
      @Override
      protected String viewCreateRoute(Model model, String name, String author,
          Boolean active, String description, List<String> recipients, Boolean byscan, 
          String pmethod, String prodpar, Integer selection_method,
          String areaid, String quantity, Integer interval, Integer timeout, Boolean nominal_timeout, Boolean applygra, Double ZR_A, Double ZR_b, Boolean ignore_malfunc, Boolean ctfilter, 
          String qitotalField, List<String> sources, List<String> detectors, Integer quality_control_mode, Boolean reprocess_quality, String jsonFilter, String emessage) {
        return method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, 
            interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, jsonFilter, emessage);
      }
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    manager = null;
    adaptorManager = null;
    anomalyManager = null;
    model = null;
  }

  @Test
  public void testCreateRoute_initial() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    
    expect(method.viewCreateRoute(model, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)).andReturn("somestring");

    replayAll();
    
    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null);

    verifyAll();
    assertEquals("somestring", result);
  }
  
  @Test
  public void testCreateRoute() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    recipients.add("A");
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    String filter = "";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);

    expect(method.createRule("areaid", "VRAD", 30, sources, detectors, 1, 40, true, true, CompositingRule.CAPPI, "500", 0, true, 10.0, 20.0, true, true, false, "se.somewhere", filter)).andReturn(rule);
    expect(manager.create("aname", "author", true, "a description", recipients, rule)).andReturn(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      @Override
      protected CompositingRule createRule(String areaid, String quantity, int interval,
          List<String> sources, List<String> detectors, int quality_control_mode, int timeout, boolean nominal_timeout, boolean byscan, String pmethod, String prodpar, int selection_method,
          boolean applygra, double ZR_A, double ZR_b, boolean ignore_malfunc, boolean ctfilter, boolean reprocess_quality, String qitotalField, String jsonFilter) {
        return method.createRule(areaid, quantity, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, byscan, pmethod, prodpar, selection_method, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, reprocess_quality, qitotalField, jsonFilter);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.CAPPI, "500", 0, "areaid", "VRAD", 30, 40, true, true, 10.0, 20.0, true, true, "se.somewhere", sources, detectors, 1, false, filter);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }
  
  @Test
  public void testCreateRoute_noAdaptors() throws Exception {
    List<String> adaptors = new ArrayList<String>();
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);

    expect(model.addAttribute("emessage",
        "No adaptors defined, please add one before creating a route.")).andReturn(null);

    replayAll();

    String result = classUnderTest.createRoute(model, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    verifyAll();
    assertEquals("redirect:adaptors.htm", result);
  }
  
  @Test
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
    String quantity = "noop";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = false;
    Boolean applygra = false;
    Double ZR_A = 210.0;
    Double ZR_b = 1.9;
    Boolean ignore_malfunc = false;
    Boolean ctfilter = false;
    String qitotalField = "se.somewhere";
    String filter = "";
    Boolean reprocess_quality = false;
    
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    
    String emessage = "Name must be specified.";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter, emessage)).andReturn("somestring");
    
    replayAll();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, 
        qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter);
    
    verifyAll();
    assertEquals("somestring", result);
    
  }

  @Test
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
    String quantity = "noop";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = false;
    Boolean applygra = false;
    Double ZR_A = 100.0;
    Double ZR_b = 1.9;
    Boolean ignore_malfunc = true;
    Boolean ctfilter = true;
    String qitotalField = "se.somewhere";
    String filter = "";
    Boolean reprocess_quality = false;
    
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    String emessage = "Areaid must be specified.";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
   
    expect(method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter, emessage)).andReturn("somestring");
    
    replayAll();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter);
    
    verifyAll();
    assertEquals("somestring", result);
  }

  @Test
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
    String quantity = "noop";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = false;
    Boolean applygra = false;
    Double ZR_A = 100.0;
    Double ZR_b = 1.9;
    Boolean ignore_malfunc = false;
    Boolean ctfilter = false;
    String qitotalField = "se.somewhere";
    String filter = "";
    Boolean reprocess_quality = false;
    
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    String emessage = "Must specify at least one source.";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(method.viewCreateRoute(model, name, author, active, description, recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter, emessage)).andReturn("somestring");
    
    replayAll();
    String result = classUnderTest.createRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotalField, sources, detectors, quality_control_mode, reprocess_quality, filter);
    
    verifyAll();
    assertEquals("somestring", result);
  }
  
  @Test
  public void testCreateRoute_pmax() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    recipients.add("A");
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    String filter = "";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);

    expect(method.createRule("areaid", "quantity", 30, sources, detectors, 1, 40, true, true, CompositingRule.PMAX, "500, 70000.0", 0, true, 10.0, 20.0, true, true, false, "se.somewhere", filter)).andReturn(rule);
    expect(manager.create("aname", "author", true, "a description", recipients, rule)).andReturn(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      @Override
      protected CompositingRule createRule(String areaid, String quantity, int interval,
          List<String> sources, List<String> detectors, int quality_control_mode, int timeout, boolean nominal_timeout, boolean byscan, String pmethod, String prodpar, int selection_method, boolean applygra, double ZR_A, double ZR_b, boolean ignore_malfunc, boolean ctfilter, boolean reprocess_quality, String qitotalField, String jsonFilter) {
        return method.createRule(areaid, quantity, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, byscan, pmethod, prodpar, selection_method, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, reprocess_quality, qitotalField, jsonFilter);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.PMAX, "500, 70000.0", 0, "areaid", "quantity", 30, 40, true, true, 10.0, 20.0, true, true, "se.somewhere", sources, detectors, 1, false, filter);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testCreateRoute_pmax_2() throws Exception {
    CompositingRule rule = new CompositingRule(){};
    RouteDefinition def = new RouteDefinition();
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    recipients.add("A");
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    String filter = "";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    expect(method.createRule("areaid", "quantity", 30, sources, detectors, 1, 40, true, true, CompositingRule.PMAX, "0,1", 0, true, 10.0, 20.0, true, true, false, "se.somewhere", filter)).andReturn(rule);
    expect(manager.create("aname", "author", true, "a description", recipients, rule)).andReturn(def);
    manager.storeDefinition(def);
    
    classUnderTest = new CompositeRoutesController() {
      protected CompositingRule createRule(String areaid, String quantity, int interval,
          List<String> sources, List<String> detectors, int quality_control_mode, int timeout, boolean nominal_timeout, boolean byscan, String pmethod, String prodpar, int selection_method, 
          boolean applygra, double ZR_A, double ZR_b, boolean ignore_malfunc, boolean ctfilter, boolean reprocess_quality, String qitotal_field, String jsonFilter) {
        return method.createRule(areaid, quantity, interval, sources, detectors, quality_control_mode, timeout, nominal_timeout, byscan, pmethod, prodpar, selection_method, applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, reprocess_quality, qitotal_field, jsonFilter);
      }
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    
    replayAll();
    
    String result = classUnderTest.createRoute(model, "aname", "author", true, "a description", recipients, true, CompositingRule.PMAX, "0,1", 0, "areaid", "quantity", 30, 40, true, true, 10.0, 20.0, true, true, "se.somewhere", sources, detectors, 1, false, filter);
    
    verifyAll();
    assertEquals("redirect:routes.htm", result);
  }

  @Test
  public void testCreateRoute_pmax_badprodpar() throws Exception {
    String emessage = "Product parameter must be <height>,<range> value for pmax.";
    
    List<String> adaptors = new ArrayList<String>();
    adaptors.add("A");
    List<String> recipients = new ArrayList<String>();
    List<String> sources = new ArrayList<String>();
    sources.add("A");
    List<String> detectors = new ArrayList<String>();
    
    String filter = "";
    
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptors);
    
    expect(method.viewCreateRoute(model, "name", "author", true, "description", recipients, false, "pmax", ",500000", 0, "areaid", "quantity", 30, 40, true, true, 10.0, 20.0, false, false, "se.somewhere", sources, detectors, 1, false, filter, emessage)).andReturn("somestring");
    
    replayAll();
    String result = classUnderTest.createRoute(model, "name", "author", true, "description", recipients, false, CompositingRule.PMAX, ",500000", 0, "areaid", "quantity", 30, 40, true, true, 10.0, 20.0, false, false, "se.somewhere", sources, detectors, 1, false, filter);
    
    verifyAll();
    assertEquals("somestring", result);
  }

  @Test
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
    String quantity = "quantity";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    Boolean applygra = true;
    Double ZR_A = 10.0;
    Double ZR_b = 1.1;
    Boolean ignore_malfunc = true;
    Boolean ctfilter = true;
    String qitotal_field = "se.somewhere";
    Boolean reprocess_quality = true;
    
    String filter = "filter_test";
    
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    List<String> areas = new ArrayList<String>();
    String emessage = null;

    expect(adaptorManager.getAdaptorNames()).andReturn(adaptornames);
    expect(utilities.getRadarSources()).andReturn(sourceids);
    expect(method.getIntervals()).andReturn(intervals);
    expect(anomalyManager.list()).andReturn(anomalydetectors);

    expect(model.addAttribute("adaptors", adaptornames)).andReturn(null);
    expect(model.addAttribute("sourceids", sourceids)).andReturn(null);
    expect(model.addAttribute("intervals", intervals)).andReturn(null);
    expect(model.addAttribute("anomaly_detectors", anomalydetectors)).andReturn(null);
    expect(model.addAttribute("name", name)).andReturn(null);
    expect(model.addAttribute("author", author)).andReturn(null);
    expect(model.addAttribute("active", active)).andReturn(null);
    expect(model.addAttribute("description", description)).andReturn(null);
    expect(model.addAttribute("recipients", recipients)).andReturn(null);
    expect(model.addAttribute("byscan", byscan)).andReturn(null);
    expect(model.addAttribute("method", pmethod)).andReturn(null);
    expect(model.addAttribute("prodpar", "500.0")).andReturn(null);
    expect(model.addAttribute("selection_method", selection_method)).andReturn(null);
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(areas);
    expect(model.addAttribute("arealist", areas)).andReturn(null);
    expect(model.addAttribute("areaid", areaid)).andReturn(null);
    expect(model.addAttribute("quantity", quantity)).andReturn(null);
    expect(model.addAttribute("interval", interval)).andReturn(null);
    expect(model.addAttribute("timeout", timeout)).andReturn(null);
    expect(model.addAttribute("nominal_timeout", nominal_timeout)).andReturn(null);
    
    expect(model.addAttribute("applygra", applygra)).andReturn(null);
    expect(model.addAttribute("ZR_A", ZR_A)).andReturn(null);
    expect(model.addAttribute("ZR_b", ZR_b)).andReturn(null);
    expect(model.addAttribute("ignore_malfunc", ignore_malfunc)).andReturn(null);
    expect(model.addAttribute("ctfilter", ctfilter)).andReturn(null);
    expect(model.addAttribute("qitotal_field", qitotal_field)).andReturn(null);
    expect(model.addAttribute("sources", sources)).andReturn(null);
    expect(model.addAttribute("detectors", detectors)).andReturn(null);
    expect(model.addAttribute("quality_control_mode", quality_control_mode)).andReturn(null);
    expect(model.addAttribute("reprocess_quality", reprocess_quality)).andReturn(null);
    expect(model.addAttribute("filterJson", filter)).andReturn(null);
    
    classUnderTest = new CompositeRoutesController() {
      @Override
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }
    };
    
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setRuleUtilities(utilities);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout,
        applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotal_field, sources, detectors, quality_control_mode, reprocess_quality, filter, emessage);
    
    verifyAll();
    
    assertEquals("route_create_composite", result);
  }

  @Test
  public void testViewCreateRoute_emessage() throws Exception {
    List<String> adaptornames = new ArrayList<String>();
    List<String> sourceids = new ArrayList<String>();
    List<Integer> intervals = new ArrayList<Integer>();
    List<AnomalyDetector> anomalydetectors = new ArrayList<AnomalyDetector>();
    List<String> areas = new ArrayList<String>();
    
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
    String quantity = "quantity";
    Integer interval = 10;
    Integer timeout = 10000;
    Boolean nominal_timeout = true;
    Boolean applygra = true;
    Double ZR_A = 10.0;
    Double ZR_b = 1.1;    
    Boolean ignore_malfunc = true;
    Boolean ctfilter = true;
    String qitotal_field = "se.somewhere";
    String filter = "";
    Boolean reprocess_quality = false;
    
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    Integer quality_control_mode = 1;
    
    String emessage = "Some message";

    expect(adaptorManager.getAdaptorNames()).andReturn(adaptornames);
    expect(utilities.getRadarSources()).andReturn(sourceids);
    expect(method.getIntervals()).andReturn(intervals);
    expect(anomalyManager.list()).andReturn(anomalydetectors);
    
    expect(model.addAttribute("adaptors", adaptornames)).andReturn(null);
    expect(model.addAttribute("sourceids", sourceids)).andReturn(null);
    expect(model.addAttribute("intervals", intervals)).andReturn(null);
    expect(model.addAttribute("anomaly_detectors", anomalydetectors)).andReturn(null);
    expect(model.addAttribute("name", name)).andReturn(null);
    expect(model.addAttribute("author", author)).andReturn(null);
    expect(model.addAttribute("active", active)).andReturn(null);
    expect(model.addAttribute("description", description)).andReturn(null);
    expect(model.addAttribute("recipients", recipients)).andReturn(null);
    expect(model.addAttribute("byscan", byscan)).andReturn(null);
    expect(model.addAttribute("method", pmethod)).andReturn(null);
    expect(model.addAttribute("prodpar", prodpar)).andReturn(null);
    expect(model.addAttribute("selection_method", selection_method)).andReturn(null);
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(areas);
    expect(model.addAttribute("arealist", areas)).andReturn(null);
    expect(model.addAttribute("areaid", areaid)).andReturn(null);
    expect(model.addAttribute("quantity", quantity)).andReturn(null);
    expect(model.addAttribute("interval", interval)).andReturn(null);
    expect(model.addAttribute("timeout", timeout)).andReturn(null);
    expect(model.addAttribute("nominal_timeout", nominal_timeout)).andReturn(null);
    expect(model.addAttribute("applygra", applygra)).andReturn(null);
    expect(model.addAttribute("ZR_A", ZR_A)).andReturn(null);
    expect(model.addAttribute("ZR_b", ZR_b)).andReturn(null);
    expect(model.addAttribute("ignore_malfunc", ignore_malfunc)).andReturn(null);
    expect(model.addAttribute("ctfilter", ctfilter)).andReturn(null);
    expect(model.addAttribute("qitotal_field", qitotal_field)).andReturn(null);
    expect(model.addAttribute("sources", sources)).andReturn(null);
    expect(model.addAttribute("detectors", detectors)).andReturn(null);
    expect(model.addAttribute("quality_control_mode", quality_control_mode)).andReturn(null);
    expect(model.addAttribute("reprocess_quality", reprocess_quality)).andReturn(null);
    expect(model.addAttribute("filterJson", filter)).andReturn(null);
    expect(model.addAttribute("emessage", emessage)).andReturn(null);
    
    classUnderTest = new CompositeRoutesController() {
      @Override
      protected List<Integer> getIntervals() {
        return method.getIntervals();
      }      
    };
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setManager(manager);
    classUnderTest.setRuleUtilities(utilities);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
    
    replayAll();
    
    String result = classUnderTest.viewCreateRoute(model, name, author, active, description,
        recipients, byscan, pmethod, prodpar, selection_method, areaid, quantity, interval, timeout, nominal_timeout,
        applygra, ZR_A, ZR_b, ignore_malfunc, ctfilter, qitotal_field, sources, detectors, quality_control_mode, reprocess_quality, filter, emessage);
    
    verifyAll();
    assertEquals("route_create_composite", result);
  }
  
  @Test
  public void testCreateRule() throws Exception {
    CompositingRule crule = createMock(CompositingRule.class);
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    String filter = "";
    
    expect(manager.createRule(CompositingRule.TYPE)).andReturn(crule);

    crule.setArea("abc");
    crule.setQuantity("noop");
    crule.setInterval(1);
    crule.setSources(sources);
    crule.setDetectors(detectors);
    crule.setQualityControlMode(1);
    crule.setTimeout(2);
    crule.setNominalTimeout(true);
    crule.setScanBased(false);
    crule.setMethod(CompositingRule.CAPPI);
    crule.setProdpar("500.0");
    crule.setSelectionMethod(0);
    crule.setApplyGRA(true);
    crule.setZR_A(10.0);
    crule.setZR_b(20.0);
    crule.setIgnoreMalfunc(true);
    crule.setCtFilter(true);
    crule.setQitotalField("se.somewhere");
    crule.setReprocessQuality(true);
    
    replayAll();
    
    CompositingRule result = classUnderTest.createRule("abc", "noop", 1, sources, detectors, 1, 2, true, false, CompositingRule.CAPPI, "500.0", 0, true, 10.0, 20.0, true, true, true, "se.somewhere", filter);
    
    verifyAll();
    assertSame(crule, result);
  }
}

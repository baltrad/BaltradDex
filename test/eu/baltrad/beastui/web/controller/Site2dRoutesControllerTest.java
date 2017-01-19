package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import eu.baltrad.beast.rules.site2d.Site2DRule;
import eu.baltrad.beast.rules.util.IRuleUtilities;

public class Site2dRoutesControllerTest extends EasyMockSupport {
  private Site2dRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  private IBltAdaptorManager adaptorManager = null;
  private IRuleUtilities utils = null;
  private IAnomalyDetectorManager anomalyManager = null;
  private IPgfClientHelper pgfClientHelper = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    adaptorManager = createMock(IBltAdaptorManager.class);
    utils = createMock(IRuleUtilities.class);
    anomalyManager = createMock(IAnomalyDetectorManager.class);
    pgfClientHelper = createMock(IPgfClientHelper.class);
    
    classUnderTest = new Site2dRoutesController();
    classUnderTest.setManager(manager);
    classUnderTest.setAdaptorManager(adaptorManager);
    classUnderTest.setRuleUtilities(utils);
    classUnderTest.setAnomalyDetectorManager(anomalyManager);
    classUnderTest.setPgfClientHelper(pgfClientHelper);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }

  @Test
  public void test_createRule() {
    String areaid = "test_area";
    int interval = 10;
    List<String> sources = null;
    List<String> detectors = null;
    boolean byscan = false;
    String method = "test_method";
    String prodpar = "test_prodpar";
    boolean applygra = false;
    double ZR_A = 0.5;
    double ZR_b = 5.0;
    boolean ignoreMalfunc = false;
    boolean ctFilter = true;
    String pcsid = "test_pcsid";
    double xscale = 3.0;
    double yscale = 5.0;
    String jsonFilter = "";
    int quality_control_mode = 1;
    
    Site2DRule rule = createMock(Site2DRule.class);
    rule.setArea("test_area");
    rule.setInterval(interval);
    rule.setSources(sources);
    rule.setDetectors(detectors);
    rule.setScanBased(byscan);
    rule.setMethod(method);
    rule.setProdpar(prodpar);
    rule.setApplyGRA(applygra);
    rule.setZR_A(ZR_A);
    rule.setZR_b(ZR_b);
    rule.setIgnoreMalfunc(ignoreMalfunc);
    rule.setCtFilter(ctFilter);
    rule.setPcsid(pcsid);
    rule.setXscale(xscale);
    rule.setYscale(yscale);
    rule.setQualityControlMode(quality_control_mode);
    
    expect(manager.createRule("blt_site2d")).andReturn(rule);
    
    replayAll();
    
    Site2DRule result = classUnderTest.createRule(areaid, interval, sources, detectors, quality_control_mode, 
        byscan, method, prodpar, applygra, ZR_A, ZR_b, ignoreMalfunc, ctFilter, pcsid, 
        xscale, yscale, jsonFilter);
    
    verifyAll();
    assertSame(rule, result);
  }
  
  @Test
  public void test_showRoute_duplicate() {
    String name = "test_name";
    String author = "test_author";
    boolean active = true;
    String description = "test_desc";
    List<String> recipients = new ArrayList<String>();
    String areaid = "test_area";
    int interval = 10;
    List<String> sources = new ArrayList<String>();
    List<String> detectors = new ArrayList<String>();
    int quality_control_mode = 1;
    boolean byscan = false;
    String method = "test_method";
    String prodpar = "test_prodpar";
    boolean applygra = false;
    double ZR_A = 0.5;
    double ZR_b = 5.0;
    boolean ignoreMalfunc = false;
    boolean ctFilter = true;
    String pcsid = "test_pcsid";
    double xscale = 3.0;
    double yscale = 5.0;
    String jsonFilter = "";
    List<String> adaptorNames = new ArrayList<String>();
    List<String> radarSources = new ArrayList<String>();
    List<AnomalyDetector> anomalyDetectors = new ArrayList<AnomalyDetector>();
    List<String> uniqueAreaIds = new ArrayList<String>();
    List<String> uniquePcsIds = new ArrayList<String>();
    
    String operation = "Duplicate";
    
    Model model = createMock(Model.class);
    RouteDefinition routeDef = createMock(RouteDefinition.class);
    
    expect(model.addAttribute(isA(String.class), isA(Object.class))).andReturn(null).anyTimes();
    
    expect(manager.getDefinition(name)).andReturn(routeDef);
    expect(adaptorManager.getAdaptorNames()).andReturn(adaptorNames);
    expect(utils.getRadarSources()).andReturn(radarSources);
    expect(anomalyManager.list()).andReturn(anomalyDetectors);
    expect(pgfClientHelper.getUniqueAreaIds()).andReturn(uniqueAreaIds);
    expect(pgfClientHelper.getUniquePcsIds()).andReturn(uniquePcsIds);
    
    replayAll();
    
    String result = classUnderTest.showRoute(model, name, author, active, description, recipients, byscan, 
        method, prodpar, areaid, interval, applygra, ZR_A, ZR_b, ignoreMalfunc, ctFilter, 
        pcsid, xscale, yscale, sources, detectors, quality_control_mode, jsonFilter, operation);
    
    verifyAll();
    assertTrue(result.equals("route_create_site2d"));
  }
}

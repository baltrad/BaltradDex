package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.Model;

import eu.baltrad.beast.system.ISystemSupervisor;
import eu.baltrad.beast.system.SystemStatus;
import eu.baltrad.beast.system.XmlSystemStatusGenerator;
import eu.baltrad.beast.system.host.IHostFilterManager;

public class SupervisorControllerTest extends EasyMockSupport {
  private interface MethodMock {
    public XmlSystemStatusGenerator getXmlGenerator();
    public boolean isAuthorized(HttpServletRequest request);
    public Map<String, Object> createMap(String sources, String areas, String objects, String minutes);
    public String createValueString(String reporter, String sources, String areas, String objects, String minutes);
  }
  private SupervisorController classUnderTest = null;
  private ISystemSupervisor supervisor = null;
  private IHostFilterManager hostManager = null;
  private HttpServletResponse response = null;
  private HttpServletRequest request = null;
  private MethodMock methods = null;
  private XmlSystemStatusGenerator generator = null;
  private Model model = null;
  
  @Before
  public void setUp() throws Exception {
    classUnderTest = new SupervisorController() {
      protected XmlSystemStatusGenerator getXmlGenerator() {
        return methods.getXmlGenerator();
      }
      protected boolean isAuthorized(HttpServletRequest request) {
        return methods.isAuthorized(request);
      }
      protected Map<String, Object> createMap(String sources, String areas, String objects, String minutes) {
        return methods.createMap(sources, areas, objects, minutes);
      }
      protected String createValueString(String reporter, String sources, String areas, String objects, String minutes) {
        return methods.createValueString(reporter, sources, areas, objects, minutes);
      }
    };
    supervisor = createMock(ISystemSupervisor.class);
    hostManager = createMock(IHostFilterManager.class);
    generator = createMock(XmlSystemStatusGenerator.class);
    response = createMock(HttpServletResponse.class);
    request = createMock(HttpServletRequest.class);
    methods = createMock(MethodMock.class);
    model = createMock(Model.class);
    classUnderTest.setSupervisor(supervisor);
    classUnderTest.setHostManager(hostManager);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
    supervisor = null;
    hostManager = null;
    generator = null;
    response = null;
    request = null;
    methods = null;
    model = null;
  }

  @Test
  public void testSupervisorSettings() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(request.getRemoteAddr()).andReturn("123.123.123.123");
    expect(model.addAttribute("currentip", "123.123.123.123")).andReturn(null);
    replayAll();
    
    String result = classUnderTest.supervisorSettings(model, request);
    
    verifyAll();
    assertEquals("supervisor_settings", result);
  }
  
  @Test
  public void testAddSupervisorSetting() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.isRegistered("192.168.1.1")).andReturn(false);
    hostManager.add("192.168.1.1");
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filter", "")).andReturn(null);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(request.getRemoteAddr()).andReturn("123.123.123.123");
    expect(model.addAttribute("currentip", "123.123.123.123")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.addSupervisorSetting(model, "192.168.1.1", request);
    
    verifyAll();
    assertEquals("supervisor_settings", result);
  }
  
  @Test
  public void testAddSupervisorSetting_alreadyRegistered() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.isRegistered("192.168.1.1")).andReturn(true);
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filter", "192.168.1.1")).andReturn(null);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(request.getRemoteAddr()).andReturn("123.123.123.123");
    expect(model.addAttribute("currentip", "123.123.123.123")).andReturn(null);
    expect(model.addAttribute("emessage", "Filter already registered")).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.addSupervisorSetting(model, "192.168.1.1", request);
    
    verifyAll();
    assertEquals("supervisor_settings", result);
  }

  @Test
  public void testRemoveSupervisorSetting() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    hostManager.remove("192.168.1.1");
    expectLastCall().andThrow(new RuntimeException("bad"));
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(request.getRemoteAddr()).andReturn("123.123.123.123");
    expect(model.addAttribute("currentip", "123.123.123.123")).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to remove filter: bad")).andReturn(null);
    replayAll();
    
    String result = classUnderTest.removeSupervisorSetting(model, "192.168.1.1",request);
    
    verifyAll();
    assertEquals("supervisor_settings", result);
  }

  @Test
  public void testRemoveSupervisorSetting_failed() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    hostManager.remove("192.168.1.1");
    
    
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(request.getRemoteAddr()).andReturn("123.123.123.123");
    expect(model.addAttribute("currentip", "123.123.123.123")).andReturn(null);

    replayAll();
    
    String result = classUnderTest.removeSupervisorSetting(model, "192.168.1.1",request);
    
    verifyAll();
    assertEquals("supervisor_settings", result);
  }
  
  @Test
  public void testSupervisorStatus() throws Exception {
    Set<SystemStatus> status = EnumSet.of(SystemStatus.OK);
    ServletOutputStream sos = createMock(ServletOutputStream.class);
    Map<String,Object> valuemap = new HashMap<String, Object>();
    
    expect(methods.getXmlGenerator()).andReturn(generator);
    expect(methods.isAuthorized(request)).andReturn(true);
    expect(methods.createMap("s1,s2", "a1,a2", "o1,o2", "5")).andReturn(valuemap);
    expect(methods.createValueString("r1","s1,s2", "a1,a2", "o1,o2", "5")).andReturn("cvs1");
    expect(supervisor.supportsMappableStatus("r1")).andReturn(false);
    expect(supervisor.getStatus("r1", valuemap)).andReturn(status);
    generator.add("r1", "cvs1", status);
    expect(methods.createValueString("r2","s1,s2", "a1,a2", "o1,o2", "5")).andReturn("cvs2");
    expect(supervisor.supportsMappableStatus("r2")).andReturn(false);
    expect(supervisor.getStatus("r2", valuemap)).andReturn(status);
    generator.add("r2", "cvs2", status);
    expect(generator.getXmlString()).andReturn("xmlstring");
    expect(response.getOutputStream()).andReturn(sos);
    sos.write(aryEq("xmlstring".getBytes("UTF-8")));
    response.setStatus(HttpServletResponse.SC_OK);
    
    replayAll();
    
    classUnderTest.supervisorStatus(null, "r1,r2", "s1,s2", "a1,a2", "o1,o2", "5", request, response);
    
    verifyAll();
  }
  
  @Test
  public void testSupervisorStatus_default_bdb_db() throws Exception {
    Set<SystemStatus> status = EnumSet.of(SystemStatus.OK);
    ServletOutputStream sos = createMock(ServletOutputStream.class);
    Map<String,Object> valuemap = new HashMap<String, Object>();
    
    expect(methods.getXmlGenerator()).andReturn(generator);
    expect(methods.isAuthorized(request)).andReturn(true);
    expect(methods.createMap(null, null, null, null)).andReturn(valuemap);
    expect(methods.createValueString("db.status",null, null, null, null)).andReturn("cvs1");
    expect(supervisor.supportsMappableStatus("db.status")).andReturn(false);
    expect(supervisor.getStatus("db.status", valuemap)).andReturn(status);
    generator.add("db.status", "cvs1", status);
    expect(methods.createValueString("bdb.status",null, null, null, null)).andReturn("cvs2");
    expect(supervisor.supportsMappableStatus("bdb.status")).andReturn(false);
    expect(supervisor.getStatus("bdb.status", valuemap)).andReturn(status);
    generator.add("bdb.status", "cvs2", status);
    
    expect(generator.getXmlString()).andReturn("xmlstring");
    expect(response.getOutputStream()).andReturn(sos);
    sos.write(aryEq("xmlstring".getBytes("UTF-8")));
    response.setStatus(HttpServletResponse.SC_OK);
    
    replayAll();
    
    classUnderTest.supervisorStatus(null, null, null, null, null, null, request, response);
    
    verifyAll();
  }

  @Test
  public void testCreateMap() throws Exception  {
    classUnderTest = new SupervisorController();
    
    Map<String,Object> result = classUnderTest.createMap("a,b", "c,d", "e,f", "5");
    assertEquals("a,b", (String)result.get("sources"));
    assertEquals("c,d", (String)result.get("areas"));
    assertEquals("e,f", (String)result.get("objects"));
    assertEquals("5", (String)result.get("minutes"));
  }
  
  @Test
  public void testCreateValueString() throws Exception {
    classUnderTest = new SupervisorController();
    classUnderTest.setSupervisor(supervisor);
    
    Set<String> supset = new HashSet<String>();
    supset.add("sources");
    supset.add("minutes");
    
    expect(supervisor.getSupportedAttributes("a.status")).andReturn(supset);
    
    replayAll();
    
    String result = classUnderTest.createValueString("a.status", "s1", "a1", "o1", "10");
    
    verifyAll();
    assertEquals("sources=s1&minutes=10", result);
  }
  
  @Test
  public void testCreateValueString_2() throws Exception {
    classUnderTest = new SupervisorController();
    classUnderTest.setSupervisor(supervisor);
    
    Set<String> supset = new HashSet<String>();
    supset.add("sources");
    supset.add("areas");
    supset.add("minutes");
    
    expect(supervisor.getSupportedAttributes("a.status")).andReturn(supset);
    
    replayAll();
    
    String result = classUnderTest.createValueString("a.status", "s1", "a1", "o1", "10");
    
    verifyAll();
    assertEquals("sources=s1&areas=a1&minutes=10", result);
  }
}

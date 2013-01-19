package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.springframework.ui.Model;

import junit.framework.TestCase;
import eu.baltrad.beast.system.ISystemSupervisor;
import eu.baltrad.beast.system.SystemStatus;
import eu.baltrad.beast.system.XmlSystemStatusGenerator;
import eu.baltrad.beast.system.host.IHostFilterManager;

public class SupervisorControllerTest extends TestCase {
  private interface MethodMock {
    public XmlSystemStatusGenerator getXmlGenerator();
    public boolean isAuthorized(HttpServletRequest request);
  }
  private SupervisorController classUnderTest = null;
  private ISystemSupervisor supervisor = null;
  private IHostFilterManager hostManager = null;
  private HttpServletResponse response = null;
  private HttpServletRequest request = null;
  private MethodMock methods = null;
  private XmlSystemStatusGenerator generator = null;
  private Model model = null;
  
  protected void setUp() throws Exception {
    classUnderTest = new SupervisorController() {
      protected XmlSystemStatusGenerator getXmlGenerator() {
        return methods.getXmlGenerator();
      }
      protected boolean isAuthorized(HttpServletRequest request) {
        return methods.isAuthorized(request);
      }
    };
    supervisor = createMock(ISystemSupervisor.class);
    hostManager = createMock(IHostFilterManager.class);
    generator = org.easymock.classextension.EasyMock.createMock(XmlSystemStatusGenerator.class);
    response = createMock(HttpServletResponse.class);
    request = createMock(HttpServletRequest.class);
    methods = createMock(MethodMock.class);
    model = createMock(Model.class);
    classUnderTest.setSupervisor(supervisor);
    classUnderTest.setHostManager(hostManager);
  }
  
  protected void tearDown() throws Exception {
    classUnderTest = null;
    supervisor = null;
    hostManager = null;
    generator = null;
    response = null;
    request = null;
    methods = null;
    model = null;
  }
  
  protected void replayAll() {
    replay(supervisor);
    replay(hostManager);
    replay(generator);
    replay(response);
    replay(request);
    replay(methods);
    replay(model);
  }
  
  protected void verifyAll() {
    verify(supervisor);
    verify(hostManager);
    verify(generator);
    verify(response);
    verify(request);
    verify(methods);
    verify(model);
  }

  public void testSupervisorSettings() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.supervisorSettings(model);
    
    verifyAll();
    Assert.assertEquals("supervisorsettings", result);
  }
  
  public void testAddSupervisorSetting() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.isRegistered("192.168.1.1")).andReturn(false);
    hostManager.add("192.168.1.1");
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filter", "")).andReturn(null);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    
    replayAll();
    
    String result = classUnderTest.addSupervisorSetting(model, "192.168.1.1");
    
    verifyAll();
    Assert.assertEquals("supervisorsettings", result);
  }
  
  public void testAddSupervisorSetting_alreadyRegistered() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    expect(hostManager.isRegistered("192.168.1.1")).andReturn(true);
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filter", "192.168.1.1")).andReturn(null);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(model.addAttribute("emessage", "Filter already registered")).andReturn(null);
    replayAll();
    
    String result = classUnderTest.addSupervisorSetting(model, "192.168.1.1");
    
    verifyAll();
    Assert.assertEquals("supervisorsettings", result);
  }

  public void testRemoveSupervisorSetting() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    hostManager.remove("192.168.1.1");
    expectLastCall().andThrow(new RuntimeException("bad"));
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);
    expect(model.addAttribute("emessage", "Failed to remove filter: bad")).andReturn(null);
    replayAll();
    
    String result = classUnderTest.removeSupervisorSetting(model, "192.168.1.1");
    
    verifyAll();
    Assert.assertEquals("supervisorsettings", result);
  }

  public void testRemoveSupervisorSetting_failed() throws Exception {
    List<String> filters = new ArrayList<String>();
    
    hostManager.remove("192.168.1.1");
    
    
    expect(hostManager.getPatterns()).andReturn(filters);
    expect(model.addAttribute("filters", filters)).andReturn(null);

    replayAll();
    
    String result = classUnderTest.removeSupervisorSetting(model, "192.168.1.1");
    
    verifyAll();
    Assert.assertEquals("supervisorsettings", result);
  }
  
  public void testSupervisorStatus() throws Exception {
    Set<SystemStatus> status = EnumSet.of(SystemStatus.OK);
    ServletOutputStream sos = org.easymock.classextension.EasyMock.createMock(ServletOutputStream.class);
    
    expect(methods.getXmlGenerator()).andReturn(generator);
    expect(methods.isAuthorized(request)).andReturn(true);
    expect(supervisor.getStatus("system", "bdb,db")).andReturn(status);
    generator.add("system", "bdb,db", status);
    
    expect(generator.getXmlString()).andReturn("xmlstring");
    expect(response.getOutputStream()).andReturn(sos);
    sos.write(aryEq("xmlstring".getBytes("UTF-8")));
    response.setStatus(HttpServletResponse.SC_OK);
    
    replayAll();
    replay(sos);
    
    classUnderTest.supervisorStatus(null, null, null, request, response);
    
    verifyAll();
    verify(sos);
  }
  
  public void testSupervisorStatus_system() throws Exception {
    Set<SystemStatus> status = EnumSet.of(SystemStatus.OK);
    ServletOutputStream sos = org.easymock.classextension.EasyMock.createMock(ServletOutputStream.class);
    
    expect(methods.getXmlGenerator()).andReturn(generator);
    expect(methods.isAuthorized(request)).andReturn(true);
    expect(supervisor.getStatus("system", "bdb,db")).andReturn(status);
    generator.add("system", "bdb,db", status);
    
    expect(generator.getXmlString()).andReturn("xmlstring");
    expect(response.getOutputStream()).andReturn(sos);
    sos.write(aryEq("xmlstring".getBytes("UTF-8")));
    response.setStatus(HttpServletResponse.SC_OK);
    
    replayAll();
    replay(sos);
    
    classUnderTest.supervisorStatus(null, "bdb,db", null, request, response);
    
    verifyAll();
    verify(sos);
  }
  
  public void testSupervisorStatus_radars() throws Exception {
    Set<SystemStatus> status = EnumSet.of(SystemStatus.OK);
    ServletOutputStream sos = org.easymock.classextension.EasyMock.createMock(ServletOutputStream.class);
    
    expect(methods.getXmlGenerator()).andReturn(generator);
    expect(methods.isAuthorized(request)).andReturn(true);
    expect(supervisor.getStatus("radar", "abc,def,ghi")).andReturn(status);
    generator.add("radar", "abc,def,ghi", status);
    
    expect(generator.getXmlString()).andReturn("xmlstring");
    expect(response.getOutputStream()).andReturn(sos);
    sos.write(aryEq("xmlstring".getBytes("UTF-8")));
    response.setStatus(HttpServletResponse.SC_OK);
    
    replayAll();
    replay(sos);
    
    classUnderTest.supervisorStatus(null, null, "abc,def,ghi", request, response);
    
    verifyAll();
    verify(sos);
  }

}

package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.wrwp.WrwpRule;

public class WrwpRoutesControllerTest extends EasyMockSupport {
  private WrwpRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    classUnderTest = new WrwpRoutesController();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    manager = null;
    classUnderTest = null;
  }
  
  @Test
  public void test_createRule() {
    List<String> sources = new ArrayList<String>();
    List<String> fields = new ArrayList<String>();
    WrwpRule rule = createMock(WrwpRule.class);

    expect(manager.createRule("blt_wrwp")).andReturn(rule);
    
    rule.setInterval(5);
    rule.setMaxheight(10);
    rule.setMindistance(55);
    rule.setMaxdistance(333);
    rule.setMinelevationangle(1.5);
    rule.setMinvelocitythreshold(2.5);
    expect(rule.setFields((List<String>)fields)).andReturn(true);
    rule.setSources(sources);
    
    replayAll();

    classUnderTest.createRule(5, 10, 55, 333, 1.5, 2.5, fields, sources, null);
    
    verifyAll();
  }
}

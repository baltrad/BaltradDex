package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.wrwp.WrwpRule;
import junit.framework.TestCase;

public class WrwpRoutesControllerTest extends TestCase {
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
  
  protected void replayAll() {
    replay(manager);
  }
  
  protected void verifyAll() {
    verify(manager);
  }

  @Test
  public void test_createRule() {
    List<String> sources = new ArrayList<String>();
    WrwpRule rule = org.easymock.classextension.EasyMock.createMock(WrwpRule.class);

    expect(manager.createRule("blt_wrwp")).andReturn(rule);
    rule.setInterval(5);
    rule.setMaxheight(10);
    rule.setMindistance(55);
    rule.setMaxdistance(333);
    rule.setMinelevationangle(1.5);
    rule.setMinvelocitythreshold(2.5);
    rule.setSources(sources);
    
    replayAll();
    org.easymock.classextension.EasyMock.replay(rule);

    WrwpRule result = classUnderTest.createRule(5, 10, 55, 333, 1.5, 2.5, sources);
    
    verifyAll();
    org.easymock.classextension.EasyMock.verify(rule);
  }
}

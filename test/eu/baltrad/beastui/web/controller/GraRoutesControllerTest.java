package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.acrr.AcrrRule;
import eu.baltrad.beast.rules.gra.GraRule;
import junit.framework.TestCase;

public class GraRoutesControllerTest extends TestCase {
  private GraRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    classUnderTest = new GraRoutesController();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
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
    GraRule rule = org.easymock.classextension.EasyMock.createMock(GraRule.class);
    rule.setArea("nrd_swe");
    rule.setObjectType("IMAGE");
    rule.setQuantity("DBZH");
    rule.setHours(12);
    rule.setFilesPerHour(4);
    rule.setAcceptableLoss(1);
    rule.setDistancefield("eu.x.y");
    rule.setZrA(100.0);
    rule.setZrB(0.5);
    rule.setFirstTermUTC(6);
    rule.setInterval(12);
    
    expect(manager.createRule("blt_gra")).andReturn(rule);
    
    replayAll();
    org.easymock.classextension.EasyMock.replay(rule);
    
    GraRule result = classUnderTest.createRule("nrd_swe", "IMAGE", "DBZH", 12, 4, 1, "eu.x.y", 100.0, 0.5, 6, 12);
    
    verifyAll();
    org.easymock.classextension.EasyMock.verify(rule);
    assertNotNull(result);
  }
}

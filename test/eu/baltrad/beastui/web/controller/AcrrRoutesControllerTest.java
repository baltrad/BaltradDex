package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.acrr.AcrrRule;

public class AcrrRoutesControllerTest extends EasyMockSupport {
  private AcrrRoutesController classUnderTest = null;
  private IRouterManager manager = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IRouterManager.class);
    classUnderTest = new AcrrRoutesController();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    classUnderTest = null;
  }

  @Test
  public void test_createRule() {
    AcrrRule rule = createMock(AcrrRule.class);
    rule.setArea("nrd_swe");
    rule.setObjectType("IMAGE");
    rule.setQuantity("DBZH");
    rule.setHours(12);
    rule.setFilesPerHour(4);
    rule.setAcceptableLoss(1);
    rule.setDistancefield("eu.x.y");
    rule.setZrA(100.0);
    rule.setZrB(0.5);
    rule.setApplyGRA(true);
    rule.setProductId("pn151");
    
    expect(manager.createRule("blt_acrr")).andReturn(rule);
    
    replayAll();
    
    AcrrRule result = classUnderTest.createRule("nrd_swe", "IMAGE", "DBZH", 12, 4, 1, "eu.x.y", 100.0, 0.5, true, "pn151", null);
    
    verifyAll();
    assertSame(rule, result);
  }
}

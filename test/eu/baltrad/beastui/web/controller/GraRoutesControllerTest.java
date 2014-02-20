package eu.baltrad.beastui.web.controller;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.gra.GraRule;

public class GraRoutesControllerTest extends EasyMockSupport {
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

  @Test
  public void test_createRule() {
    GraRule rule = createMock(GraRule.class);
    rule.setArea("nrd_swe");
    rule.setObjectType("IMAGE");
    rule.setQuantity("DBZH");
    rule.setFilesPerHour(4);
    rule.setAcceptableLoss(1);
    rule.setDistancefield("eu.x.y");
    rule.setZrA(100.0);
    rule.setZrB(0.5);
    rule.setFirstTermUTC(6);
    rule.setInterval(12);
    
    expect(manager.createRule("blt_gra")).andReturn(rule);
    
    replayAll();
    
    GraRule result = classUnderTest.createRule("nrd_swe", "IMAGE", "DBZH", 4, 1, "eu.x.y", 100.0, 0.5, 6, 12);
    
    verifyAll();
    assertNotNull(result);
  }
}

package eu.baltrad.beastui.web.controller;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.baltrad.beast.router.IRouterManager;
import eu.baltrad.beast.rules.acrr.AcrrRule;
import eu.baltrad.beast.rules.acrr.AcrrRuleManager;

public class AcrrRoutesControllerTest extends TestCase {
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

  protected void replayAll() {
    replay(manager);
  }
  
  protected void verifyAll() {
    verify(manager);
  }
  
  @Test
  public void test_createRule() {
    AcrrRule rule = org.easymock.classextension.EasyMock.createMock(AcrrRule.class);
    rule.setArea("nrd_swe");
    rule.setObjectType("IMAGE");
    rule.setQuantity("DBZH");
    rule.setHours(12);
    rule.setFilesPerHour(4);
    rule.setAcceptableLoss(1);
    rule.setDistancefield("eu.x.y");
    rule.setZrA(100.0);
    rule.setZrB(0.5);
    
    expect(manager.createRule("blt_acrr")).andReturn(rule);
    
    replayAll();
    org.easymock.classextension.EasyMock.replay(rule);
    
    AcrrRule result = classUnderTest.createRule("nrd_swe", "IMAGE", "DBZH", 12, 4, 1, "eu.x.y", 100.0, 0.5);
    
    verifyAll();
    org.easymock.classextension.EasyMock.verify(rule);
  }

}

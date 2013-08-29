package eu.baltrad.dex.auth.ldap;

import static org.junit.Assert.*;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

import static org.easymock.EasyMock.*;

public class BaltradUserDetailsContextMapperTest {
  private BaltradUserDetailsContextMapper classUnderTest = null;
  private BaltradUserDetailsService service = null;
  
  @Before
  public void setUp() throws Exception {
    service = EasyMock.createMock(BaltradUserDetailsService.class);
    classUnderTest = new BaltradUserDetailsContextMapper();
    classUnderTest.setService(service);
  }
  
  @After
  public void tearDown() throws Exception {
    service = null;
    classUnderTest = null;
  }
  
  protected void replay() {
    EasyMock.replay(service);
  }
  
  protected void verify() {
    EasyMock.verify(service);
  }
  
  @Test
  public void test_mapUserFromContext() {
    UserDetails details = createMock(UserDetails.class);
    expect(service.loadUserByUsername("baltrad/nisse")).andReturn(details);
    
    replay();
    UserDetails result = classUnderTest.mapUserFromContext(null, "baltrad/nisse", null);
    verify();
    assertSame(details, result);
  }

  @Test
  public void test_afterPropertiesSet() throws Exception {
    classUnderTest = new BaltradUserDetailsContextMapper();
    try {
      classUnderTest.afterPropertiesSet();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // pass
    }

    classUnderTest = new BaltradUserDetailsContextMapper();
    classUnderTest.setService(service);
    classUnderTest.afterPropertiesSet();
  }
}

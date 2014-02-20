package eu.baltrad.dex.auth.ldap;

import static org.junit.Assert.*;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

import static org.easymock.EasyMock.*;

public class BaltradUserDetailsContextMapperTest extends EasyMockSupport {
  private BaltradUserDetailsContextMapper classUnderTest = null;
  private BaltradUserDetailsService service = null;
  
  @Before
  public void setUp() throws Exception {
    service = createMock(BaltradUserDetailsService.class);
    classUnderTest = new BaltradUserDetailsContextMapper();
    classUnderTest.setService(service);
  }
  
  @After
  public void tearDown() throws Exception {
    service = null;
    classUnderTest = null;
  }
  
  @Test
  public void test_mapUserFromContext() {
    UserDetails details = createMock(UserDetails.class);
    expect(service.loadUserByUsername("baltrad/nisse")).andReturn(details);
    
    replayAll();
    
    UserDetails result = classUnderTest.mapUserFromContext(null, "baltrad/nisse", null);
    
    verifyAll();
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

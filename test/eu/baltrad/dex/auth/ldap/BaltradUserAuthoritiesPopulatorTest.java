package eu.baltrad.dex.auth.ldap;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public class BaltradUserAuthoritiesPopulatorTest extends EasyMockSupport {
  private BaltradUserAuthoritiesPopulator classUnderTest = null;
  private BaltradUserDetailsService service = null;
  
  @Before
  public void setUp() throws Exception {
    service = createMock(BaltradUserDetailsService.class);
    classUnderTest = new BaltradUserAuthoritiesPopulator();
    classUnderTest.setService(service);
  }
  
  @After
  public void tearDown() throws Exception {
    service = null;
    classUnderTest = null;
  }

  @Test
  public void test_getGrantedAuthorities() {
    UserDetails details = createMock(UserDetails.class);
    GrantedAuthority[] auths = new GrantedAuthority[0];
    
    expect(service.loadUserByUsername("baltrad/admin")).andReturn(details);
    expect(details.getAuthorities()).andReturn(auths);
    
    replayAll();
    
    GrantedAuthority[] result = classUnderTest.getGrantedAuthorities(null, "baltrad/admin");
    
    verifyAll();
    
    assertSame(auths, result);
  }
  
  @Test
  public void test_afterPropertiesSet() throws Exception {
    classUnderTest = new BaltradUserAuthoritiesPopulator();
    try {
      classUnderTest.afterPropertiesSet();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // pass
    }

    classUnderTest = new BaltradUserAuthoritiesPopulator();
    classUnderTest.setService(service);
    classUnderTest.afterPropertiesSet();
  }
}

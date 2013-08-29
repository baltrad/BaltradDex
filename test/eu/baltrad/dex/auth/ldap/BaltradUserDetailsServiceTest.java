package eu.baltrad.dex.auth.ldap;

import static org.junit.Assert.*;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;
import static org.easymock.EasyMock.*;

public class BaltradUserDetailsServiceTest {
  private BaltradUserDetailsService classUnderTest = null;
  private IUserManager manager = null;
  
  @Before
  public void setUp() throws Exception {
    manager = EasyMock.createMock(IUserManager.class);
    classUnderTest = new BaltradUserDetailsService();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    manager = null;
    classUnderTest = null;
  }
  
  protected void replay() {
    EasyMock.replay(manager);
  }
  
  protected void verify() {
    EasyMock.verify(manager);
  }
  
  @Test
  public void test_loadUserByUsername_noPrefix() {
    User user = new User();
    user.setRole(Role.ADMIN);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replay();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verify();
    GrantedAuthority[] auth = details.getAuthorities();
    assertEquals(3, auth.length);
    assertEquals("ROLE_ADMIN", auth[0].getAuthority());
    assertEquals("ROLE_OPERATOR", auth[1].getAuthority());
    assertEquals("ROLE_USER", auth[2].getAuthority());
  }
  
  @Test
  public void test_loadUserByUsername_prefix() {
    User user = new User();
    user.setRole(Role.ADMIN);
    user.setName("admin");
    
    expect(manager.load("admin")).andReturn(user);
    
    replay();
    
    classUnderTest.setUserPrefix("baltrad/");
    
    UserDetails details = classUnderTest.loadUserByUsername("admin");
    
    verify();
    GrantedAuthority[] auth = details.getAuthorities();
    assertEquals(3, auth.length);
    assertEquals("ROLE_ADMIN", auth[0].getAuthority());
    assertEquals("ROLE_OPERATOR", auth[1].getAuthority());
    assertEquals("ROLE_USER", auth[2].getAuthority());
  }
  
  @Test
  public void test_loadUserByUsername_operator() {
    User user = new User();
    user.setRole(Role.OPERATOR);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replay();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verify();
    GrantedAuthority[] auth = details.getAuthorities();
    assertEquals(2, auth.length);
    assertEquals("ROLE_OPERATOR", auth[0].getAuthority());
    assertEquals("ROLE_USER", auth[1].getAuthority());
  }
  
  @Test
  public void test_loadUserByUsername_user() {
    User user = new User();
    user.setRole(Role.USER);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replay();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verify();
    GrantedAuthority[] auth = details.getAuthorities();
    assertEquals(1, auth.length);
    assertEquals("ROLE_USER", auth[0].getAuthority());
  }
  
  @Test
  public void test_afterPropertiesSet() throws Exception {
    classUnderTest = new BaltradUserDetailsService();
    try {
      classUnderTest.afterPropertiesSet();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException iae) {
      // pass
    }
    classUnderTest = new BaltradUserDetailsService();
    classUnderTest.setManager(manager);
    classUnderTest.afterPropertiesSet();
  }
}

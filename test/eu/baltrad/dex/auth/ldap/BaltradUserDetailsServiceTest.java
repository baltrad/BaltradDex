package eu.baltrad.dex.auth.ldap;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.easymock.EasyMockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

public class BaltradUserDetailsServiceTest extends EasyMockSupport {
  private BaltradUserDetailsService classUnderTest = null;
  private IUserManager manager = null;
  
  @Before
  public void setUp() throws Exception {
    manager = createMock(IUserManager.class);
    classUnderTest = new BaltradUserDetailsService();
    classUnderTest.setManager(manager);
  }
  
  @After
  public void tearDown() throws Exception {
    manager = null;
    classUnderTest = null;
  }
  
  @Test
  public void test_loadUserByUsername_noPrefix() {
    User user = new User();
    user.setRole(Role.ADMIN);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replayAll();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verifyAll();
    Set<GrantedAuthority> auth = (Set)details.getAuthorities();
    assertEquals(3, auth.size());
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_ADMIN")));
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_OPERATOR")));
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_USER")));
  }
  
  @Test
  public void test_loadUserByUsername_prefix() {
    User user = new User();
    user.setRole(Role.ADMIN);
    user.setName("admin");
    
    expect(manager.load("admin")).andReturn(user);
    
    replayAll();
    
    classUnderTest.setUserPrefix("baltrad/");
    
    UserDetails details = classUnderTest.loadUserByUsername("admin");
    
    verifyAll();
    Set<GrantedAuthority> auth = (Set)details.getAuthorities();
    assertEquals(3, auth.size());
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_ADMIN")));
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_OPERATOR")));
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_USER")));
  }
  
  @Test
  public void test_loadUserByUsername_operator() {
    User user = new User();
    user.setRole(Role.OPERATOR);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replayAll();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verifyAll();
    Set<GrantedAuthority> auth = (Set)details.getAuthorities();
    assertEquals(2, auth.size());
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_OPERATOR")));
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_USER")));
  }
  
  @Test
  public void test_loadUserByUsername_user() {
    User user = new User();
    user.setRole(Role.USER);
    user.setName("baltrad/admin");
    
    expect(manager.load("baltrad/admin")).andReturn(user);
    
    replayAll();
    
    UserDetails details = classUnderTest.loadUserByUsername("baltrad/admin");
    
    verifyAll();
    Set<GrantedAuthority> auth = (Set)details.getAuthorities();
    assertEquals(1, auth.size());
    assertTrue(auth.contains(new GrantedAuthorityImpl("ROLE_USER")));
  }
  
  @Test
  public void test_loadUserByUsername_notExisting() {
    expect(manager.load("baltrad/admin")).andReturn(null);
    
    replayAll();

    try {
      classUnderTest.loadUserByUsername("baltrad/admin");
      fail("Expected UsernameNotFoundException");
    } catch (UsernameNotFoundException e) {
      // pass
    }
    
    verifyAll();
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

/* --------------------------------------------------------------------
Copyright (C) 2009-2013 Swedish Meteorological and Hydrological Institute, SMHI,

This file is part of the BaltradDex package.

The BaltradDex package is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The BaltradDex package is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the BaltradDex package library.  If not, see <http://www.gnu.org/licenses/>.
------------------------------------------------------------------------*/
package eu.baltrad.dex.auth.ldap;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import eu.baltrad.dex.user.manager.IUserManager;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.model.User;

/**
 * A user service for fetching the user details from the database when
 * using a ldap as the authentication mechanism.
 * The baltrad user details service will read the user information from the database
 * but the actual password is not used since that has been handled in earlier steps
 * in the LdapAuthenticationProvider.
 *
 * You can define a prefix that should be removed from the username before loading from the
 * database. If, for example, you have the the user 'admin' in the database, but it is known
 * as 'baltrad/admin' in the ldap, you can define the userPrefix='baltrad/' and hence the
 * user read from the database will be 'admin' even though the authorization was performed on 'baltrad/admin' 
 * 
 * @author Anders Henja
 */
public class BaltradUserDetailsService implements UserDetailsService, InitializingBean {
  /**
   * Administrator
   */
  private GrantedAuthority ADMIN_AUTH = new GrantedAuthorityImpl("ROLE_ADMIN");
  
  /**
   * Operator
   */
  private GrantedAuthority OPERATOR_AUTH = new GrantedAuthorityImpl("ROLE_OPERATOR");
  
  /**
   * User
   */
  private GrantedAuthority USER_AUTH = new GrantedAuthorityImpl("ROLE_USER");
  
  /**
   * The user prefix
   */
  private String userPrefix = null;

  /**
   * The user manager for getting authority information
   */
  private IUserManager manager = null;
  
  /**
   * The logger
   */
  private final static Logger logger = LogManager.getLogger(BaltradUserDetailsService.class);
  
  /**
   * @see UserDetailsService#loadUserByUsername(String)
   */
  @Override
  public UserDetails loadUserByUsername(String userName)
      throws UsernameNotFoundException, DataAccessException {
    String loaduser = userName;
    logger.debug("loadUserByUsername("+userName+")");
    
    if (userPrefix != null) {
      loaduser = loaduser.replaceFirst(userPrefix, "");
    }
    User dbuser = manager.load(loaduser);
    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
    if (dbuser.getRole().equals(Role.ADMIN)) {
      logger.debug("ADMIN,OPERATOR,USER");
      authorities.add(ADMIN_AUTH);
      authorities.add(OPERATOR_AUTH);
      authorities.add(USER_AUTH);
    }
    if (dbuser.getRole().equals(Role.OPERATOR)) {
      logger.debug("OPERATOR,USER");
      authorities.add(OPERATOR_AUTH);
      authorities.add(USER_AUTH);
    }
    if (dbuser.getRole().equals(Role.USER)) {
      logger.debug("USER");
      authorities.add(USER_AUTH);
    }

    return new org.springframework.security.core.userdetails.User(dbuser.getName(), " ", true, true, true, true, authorities);
  }

  /**
   * @return the user prefix
   */
  public String getUserPrefix() {
    return userPrefix;
  }

  /**
   * @param userPrefix the user prefix to set
   */
  public void setUserPrefix(String userPrefix) {
    this.userPrefix = userPrefix;
  }

  /**
   * Requires that a user manager has been set
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    if (manager == null) {
      throw new IllegalArgumentException("User manager is missing");
    }
  }

  /**
   * @return the user manager
   */
  public IUserManager getManager() {
    return manager;
  }

  /**
   * @param manager the user manager to set
   */
  public void setManager(IUserManager manager) {
    this.manager = manager;
  }
  

}

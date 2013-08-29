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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;

/**
 * Context mapper that makes it possible to map a ldap user to a user details. Just forwards
 * the mapUserToContext to the BaltradUserDetailsService
 * 
 * @author Anders Henja
 */
public class BaltradUserDetailsContextMapper implements UserDetailsContextMapper, InitializingBean {
  /**
   * The user service
   */
  private BaltradUserDetailsService service = null;
  
  /**
   * The logger
   */
  private final static Logger logger = LogManager.getLogger(BaltradUserDetailsContextMapper.class);
  
  /**
   * @see UserDetailsContextMapper#mapUserFromContext(DirContextOperations, String, GrantedAuthority[])
   */
  @Override
  public UserDetails mapUserFromContext(DirContextOperations arg0, String username,
      GrantedAuthority[] arg2) {
    logger.debug("mapUserFromContext("+username+")");
    return service.loadUserByUsername(username);
  }

  /**
   * @see UserDetailsContextMapper#mapUserToContext(UserDetails, DirContextAdapter)
   */
  @Override
  public void mapUserToContext(UserDetails arg0, DirContextAdapter arg1) {
    throw new IllegalStateException("Only retrieving data from ldap possible");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (service == null) {
      throw new IllegalArgumentException("user details service missing");
    }
  }

  /**
   * @return the user details service
   */
  public BaltradUserDetailsService getService() {
    return service;
  }

  /**
   * @param service the user details service to set
   */
  public void setService(BaltradUserDetailsService service) {
    this.service = service;
  }
  
}

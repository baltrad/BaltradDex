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
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * The authorities populator required by the LdapAuthenticationProvider.
 *  
 * @author Anders Henja
 */
public class BaltradUserAuthoritiesPopulator implements LdapAuthoritiesPopulator, InitializingBean {
  /**
   * The user details service
   */
  private BaltradUserDetailsService service = null;
  
  /**
   * The logger
   */
  private final static Logger logger = LogManager.getLogger(BaltradUserAuthoritiesPopulator.class);
  
  @Override
  public Collection<? extends GrantedAuthority> getGrantedAuthorities(DirContextOperations arg0, String user) {
    logger.debug("getGrantedAuthorities(" + user + ")");
    try {
      return service.loadUserByUsername(user).getAuthorities();
    } catch (Exception e) {
      return new ArrayList<GrantedAuthority>();
    }
  }
  
  /**
   * @return the user details service
   */
  public BaltradUserDetailsService getService() {
    return service;
  }

  /**
   * @param service the user details service
   */
  public void setService(BaltradUserDetailsService service) {
    this.service = service;
  }
  
  @Override
  public void afterPropertiesSet() throws Exception {
    if (service == null) {
      throw new IllegalArgumentException("user details service missing");
    }
  }
}

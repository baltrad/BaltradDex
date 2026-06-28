/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

package eu.baltrad.dex.auth.util;

import java.util.ArrayList;
import java.util.List;

import eu.baltrad.dex.user.manager.impl.UserManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.Role;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implements role-based user details service for authentication.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class SimpleUserDetailsService implements UserDetailsService  {

    private static final Logger log = Logger.getLogger(SimpleUserDetailsService.class);

    /** Authorities */
    private GrantedAuthority authAdmin;
    private GrantedAuthority authOperator;
    private GrantedAuthority authUser;

    private UserManager userManager;

    /**
     * Constructor.
     */
    public SimpleUserDetailsService() {
        authAdmin = new SimpleGrantedAuthority("ROLE_ADMIN");
        authOperator = new SimpleGrantedAuthority("ROLE_OPERATOR");
        authUser = new SimpleGrantedAuthority("ROLE_USER");
    }

    /**
     * Loads user by name and sets authority respectively.
     * @param name User name
     * @return User details
     */
    public UserDetails loadUserByUsername(String name) {
        log.info("loadUserByUsername: looking up '" + name + "'");
        User user = userManager.load(name);
        if (user == null) {
            log.warn("loadUserByUsername: user '" + name + "' not found in database");
            throw new UsernameNotFoundException("User not found: " + name);
        } else {
            log.info("loadUserByUsername: found user '" + name + "' with role '" + user.getRole() + "'");
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            if (user.getRole().equals(Role.ADMIN)) {
              authorities.add(authAdmin);
              authorities.add(authOperator);
              authorities.add(authUser);
            }
            if (user.getRole().equals(Role.OPERATOR)) {
              authorities.add(authOperator);
              authorities.add(authUser);
            }
            if (user.getRole().equals(Role.USER)) {
              authorities.add(authUser);
            }
            log.info("loadUserByUsername: assigned " + authorities.size() + " authorities to '" + name + "': " + authorities);
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getName(), user.getPassword(), authorities);
            return userDetails;
        }
    }

    /**
     * @param userManager the userManager to set
     */
    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
   
}

/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.model.Role;
import eu.baltrad.dex.user.manager.impl.AccountManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.User;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UserDetails;

/**
 * Implements role-based user details service for authentication.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public class SimpleUserDetailsService implements UserDetailsService  {
    
    /** Authorities */
    private GrantedAuthority authAdmin;
    private GrantedAuthority authOperator;
    private GrantedAuthority authUser;
    
    private AccountManager accountManager;
    
    /**
     * Constructor.
     */
    public SimpleUserDetailsService() {
        authAdmin = new GrantedAuthorityImpl("ROLE_ADMIN");
        authOperator = new GrantedAuthorityImpl("ROLE_OPERATOR");
        authUser = new GrantedAuthorityImpl("ROLE_USER");
    }
    
    /**
     * Loads user by name and sets authority respectively. 
     * @param name User name
     * @return User details 
     */
    public UserDetails loadUserByUsername(String name) {
        Account account = accountManager.load(name);
        if (account == null) {
            return null;
        } else {
            GrantedAuthority[] authorities = null;
            if (account.getRoleName().equals(Role.ADMIN)) {
                authorities = new GrantedAuthority[] {authAdmin, authOperator, 
                    authUser};
            }
            if (account.getRoleName().equals(Role.OPERATOR)) {
                authorities = new GrantedAuthority[] {authOperator, authUser};
            }
            if (account.getRoleName().equals(Role.USER)) {
                authorities = new GrantedAuthority[] {authUser};
            }
            UserDetails user = new User(account.getName(), account.getPassword(), 
                    true, authorities);
            return user;
        }
    }

    /**
     * @param accountManager the userManager to set
     */
    @Autowired
    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
   
}

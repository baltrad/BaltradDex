/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.auth.util;

import eu.baltrad.dex.util.ApplicationSecurityManager;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

/**
 * Class implementing user authentication functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class LoginValidator implements Validator {
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager = new UserManager();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return User.class.equals( aClass );
    }
    /**
     * Method validating user object.
     *
     * @param command Command object
     * @param errors Errors
     * @return True if validation is successful, false otherwise
     */
    public void validate( Object command, Errors errors ) {
        User formUser = ( User )command;
        if( formUser == null ) return;
        // Look for user in the database
        User dbUser = userManager.getUserByName( formUser.getName() );
        String userName = formUser.getName().trim();
        String password = formUser.getPassword().trim();
        if( userName == null || password == null || userName.isEmpty() || password.isEmpty() ||
                ApplicationSecurityManager.authenticateFormUser( formUser, dbUser ) == false ) {
            errors.rejectValue( "name", "error.login.invalid" );
        }
    }
}
//--------------------------------------------------------------------------------------------------

/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import eu.baltrad.dex.user.model.User;

/**
 * Class implementing user authentication functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SignInValidator implements Validator {
//--------------------------------------------------------------------------------------------------
    /**
     * Method implemented by the Validator interface
     *
     * @param clazz User class object
     * @return True if classes are equal
     */
    public boolean supports( Class clazz ) {
        return clazz.equals( User.class );
    }
    /**
     * Method validating user object.
     *
     * @param command Command object
     * @param errors Errors
     * @return True if validation is successful, false otherwise
     */
    public void validate( Object command, Errors errors ) {
        User user = ( User )command;
        if( user == null ) return;
        String userName = user.getName().trim();
        String password = user.getPassword().trim();
        if( userName == null || password == null || userName.isEmpty() || password.isEmpty() ) {
            errors.reject( "error.login.invalid" );
        }
    }
}
//--------------------------------------------------------------------------------------------------
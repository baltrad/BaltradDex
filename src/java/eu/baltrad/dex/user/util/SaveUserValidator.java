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

package eu.baltrad.dex.user.util;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.UserManager;
import eu.baltrad.dex.util.WebUtil;

/**
 * Validator class used to validate input from add user form.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SaveUserValidator implements Validator {
//---------------------------------------------------------------------------------------- Constants
    private static final int MIN_FIELD_LENGTH = 6;
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
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
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        User user = ( User )command;
        if( user == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "retPassword", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "factory", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "country", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "city", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "cityCode", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "street", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "number", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "phone", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", "error.field.required" );
        // validate password
        if( !errors.hasFieldErrors( "password" ) && !errors.hasFieldErrors( "retPassword" ) ) {
            if( !user.getPassword().trim().equals( user.getRetPassword().trim() ) ) {
                errors.rejectValue( "retPassword", "error.field.passwd.mismatch" );
            }      
        }
        if( user.getPassword().trim().length() > 0 && user.getPassword().trim().length() 
                < MIN_FIELD_LENGTH ) {
            errors.rejectValue( "password", "error.field.passwd.tooshort" );
        }
        // validate node address
        boolean isValidPortNumber = false;
        try {
            int port = Integer.parseInt( user.getPortNumber() );
            isValidPortNumber = true;
        } catch( NumberFormatException e ) {
            isValidPortNumber = false;
        } catch( Exception e ) {
            isValidPortNumber = false;
        }
        if( user.getShortAddress().isEmpty() || user.getPortNumber().isEmpty() ||
                !isValidPortNumber ) {
            ValidationUtils.rejectIfEmptyOrWhitespace( errors, "fullAddress",
                "error.address.invalid" );
        }
        // validate email address
        if( user.getEmail().trim().length() > 0 && !WebUtil.validateEmailAddress( user.getEmail() ) ) {
            errors.rejectValue( "email", "error.address.invalid" );
        }
    }
    /**
     * Method gets reference to user manager object.
     *
     * @return Reference to user manager object
     */
    public UserManager getUserManager() { return userManager; }
    /**
     * Method sets reference to user manager object.
     *
     * @param userManager Reference to user manager object
     */
    public void setUserManager( UserManager userManager ) { this.userManager = userManager; }
}
//--------------------------------------------------------------------------------------------------
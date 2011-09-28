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
 * @author Maciej Szewczykowski | maciej@baltrad.eu
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
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", "error.missing.username" );
        // Password is only validated for new accounts
        if( user.getId() == 0 ) {
            ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", "error.missing.password" );
            ValidationUtils.rejectIfEmptyOrWhitespace( errors, "confirmPassword",
                "error.missing.confirmpassword" );
        }
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "factory", "error.missing.organization" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "country", "error.missing.country" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "city", "error.missing.city" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "cityCode", "error.missing.zipcode" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "street", "error.missing.street" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "number", "error.missing.addressnumber" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "phone", "error.missing.phone" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", "error.missing.email" );
        // Password is only validated for new accounts
        if( user.getId() == 0 ) {
            if( !errors.hasFieldErrors( "password" ) && !errors.hasFieldErrors( "confirmPassword" ) ) {
                if( !user.getPassword().trim().equals( user.getConfirmPassword().trim() ) ) {
                    errors.rejectValue( "confirmPassword", "error.field.passwd.mismatch" );
                }
            }
            if( user.getPassword().trim().length() > 0 && user.getPassword().trim().length()
                    < MIN_FIELD_LENGTH ) {
                errors.rejectValue( "password", "error.field.passwd.tooshort" );
            }
        }
        // Validate host address
        if( user.getHostAddress().isEmpty() ) {
            ValidationUtils.rejectIfEmptyOrWhitespace( errors, "hostAddress",
                "error.address.invalid" );
        }
        // Validate port number
        if( errors.hasFieldErrors( "port") ) {
            errors.rejectValue( "hostAddress", "error.portnumber.invalid" );
        }
        if( user.getPort() <= 0 ) {
            errors.rejectValue( "hostAddress", "error.portnumber.invalid" );
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
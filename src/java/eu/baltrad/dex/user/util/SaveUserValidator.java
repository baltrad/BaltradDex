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
import eu.baltrad.dex.util.WebValidator;

/**
 * Validator class used to validate input from add user form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class SaveUserValidator implements Validator {
//---------------------------------------------------------------------------------------- Constants
    private static final int MIN_PASSWD_LENGTH = 6;
    private static final int COUNTRY_CODE_LENGTH = 2;
//---------------------------------------------------------------------------------------- Variables
    private UserManager userManager;
    private WebValidator webValidator;
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
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "organizationName", 
                "error.missing.orgname");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "organizationUnit", 
                "error.missing.orgunit");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localityName", 
                "error.missing.locality");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stateName", "error.missing.state");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", 
                "error.missing.code");
        // validate node address
        if (!webValidator.validateUrl(user.getNodeAddress())) {
            errors.rejectValue("nodeAddress","error.address.invalid");
        }
        // Password is only validated for new accounts
        if( user.getId() == 0 ) {
            if( !errors.hasFieldErrors("password") && !errors.hasFieldErrors("confirmPassword")) {
                if(!user.getPassword().trim().equals(user.getConfirmPassword().trim())) {
                    errors.rejectValue("confirmPassword", "error.field.passwd.mismatch");
                }
            }
            if(user.getPassword().trim().length() > 0 && user.getPassword().trim().length()
                    < MIN_PASSWD_LENGTH) {
                errors.rejectValue("password", "error.field.passwd.tooshort");
            }
        }
        if (user.getCountryCode().trim().length() > 0 && 
                user.getCountryCode().trim().length() != COUNTRY_CODE_LENGTH) {
            errors.rejectValue("countryCode", "error.missing.code");
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
    /**
     * @return the webValidator
     */
    public WebValidator getWebValidator() {
        return webValidator;
    }

    /**
     * @param webValidator the webValidator to set
     */
    public void setWebValidator(WebValidator webValidator) {
        this.webValidator = webValidator;
    }
}
//--------------------------------------------------------------------------------------------------
/*******************************************************************************
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
*******************************************************************************/

package eu.baltrad.dex.user.util;

import eu.baltrad.dex.user.model.Password;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator class used to validate password modification form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.1
 * @since 0.7.1
 */
public class ChangePasswordValidator implements Validator {

    /* Minimum password length */
    private static final int MIN_PASSWD_LENGTH = 6;

    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports(Class clazz) {
        return Password.class.equals(clazz);
    }
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate(Object command, Errors errors) {
        Password passwd = (Password) command;
        // Password object is null
        if (passwd == null) return;
        // User submits empty fields
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswd", 
                "error.missing.newpasswd");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmNewPasswd",
                "error.missing.confirmnewpasswd");
        // New password is too short
        if (!errors.hasFieldErrors("newPasswd")) {
            if (passwd.getNewPasswd().trim().length() < MIN_PASSWD_LENGTH) {
                errors.rejectValue("newPasswd", "error.field.passwd.tooshort");
            }
        }
        // New password and confirmed password don't match
        if (!errors.hasFieldErrors("newPasswd") &&
                !errors.hasFieldErrors("confirmNewPasswd")) {
            if (!passwd.getNewPasswd().equals( passwd.getConfirmNewPasswd())) {
                errors.rejectValue("confirmNewPasswd", 
                        "error.field.passwd.mismatch" );
            }
        }
    }
}


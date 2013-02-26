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

package eu.baltrad.dex.user.validator;

import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Validator class used to validate password modification form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.1
 * @since 0.7.1
 */
public class PasswordValidator {

    /* Minimum password length */
    private static final int MIN_PASSWD_LENGTH = 8;

    private MessageResourceUtil messages;
    
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports(Class clazz) {
        return User.class.equals(clazz);
    }
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate(HttpServletRequest request, Object command, 
            Errors errors) {
        User user = (User) command;
        // Account object is null
        if (user == null) return; 
        // User submits empty fields
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", 
                "changepassword.missing.password",
                messages.getMessage("changepassword.missing.password"));
        // New password is too short
        if (!errors.hasFieldErrors("password")) {
            if (user.getPassword().trim().length() < MIN_PASSWD_LENGTH) 
            {
                errors.rejectValue("password", 
                        "changepassword.invalid.password",
                        messages.getMessage("changepassword.invalid.password"));
            }
        }
        // New password and confirmed password don't match
        String repeatPassword = request.getParameter("repeat_password");
        if (user.getPassword().length() > MIN_PASSWD_LENGTH 
                    && repeatPassword.isEmpty()) {
                errors.rejectValue("password", 
                    "saveaccount.missing.repeat_password",
                    messages.getMessage("saveaccount.missing.repeat_password"));
        }
        if (repeatPassword.length() > 0 && 
            !user.getPassword().equals(repeatPassword)) {
                errors.rejectValue("password",
                    "changepassword.mismatch.password",
                    messages.getMessage("changepassword.mismatch.password"));
        }
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
}


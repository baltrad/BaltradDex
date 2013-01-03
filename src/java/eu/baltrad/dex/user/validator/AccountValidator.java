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

package eu.baltrad.dex.user.validator;

import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.util.MessageResourceUtil;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Validates user account settings.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0
 */
public class AccountValidator implements Validator {

    private static final int MIN_PASSWD_LENGTH = 8;
    private static final int COUNTRY_CODE_LENGTH = 2;

    private MessageResourceUtil messages;

    /**
     * Declares class supported by this validator.
     * @param clazz Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports(Class clazz) {
        return Account.class.equals(clazz);
    }
    
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate(Object command, Errors errors) {
        Account account = (Account) command;
        if (account == null) {
            return;
        }    
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", 
                "saveaccount.missing.name",
                messages.getMessage("saveaccount.missing.name"));
        // Password is only validated for new accounts
        if (account.getId() == 0) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
                    "saveaccount.missing.password",
                    messages.getMessage("saveaccount.missing.password"));
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "repeatPassword",
                    "saveaccount.missing.repeat_password",
                    messages.getMessage("saveaccount.missing.repeat_password"));
            if (account.getPassword().length() > 0 && 
                    account.getPassword().length() < MIN_PASSWD_LENGTH) {
                        errors.rejectValue("password", 
                        "saveaccount.invalid.password",
                        messages.getMessage("saveaccount.invalid.password"));
            }
            if (account.getRepeatPassword().length() > 0 && 
                !account.getPassword().equals(account.getRepeatPassword())) {
                    errors.rejectValue("repeatPassword", 
                        "saveaccount.mismatch.password",
                        messages.getMessage("saveaccount.mismatch.password"));
            }
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgName",
                "saveaccount.missing.organization_name",
                messages.getMessage("saveaccount.missing.organization_name"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgUnit",
                "saveaccount.missing.organization_unit",
                messages.getMessage("saveaccount.missing.organization_unit"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "locality",
                "saveaccount.missing.locality",
                messages.getMessage("saveaccount.missing.locality"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state",
                "saveaccount.missing.state",
                messages.getMessage("saveaccount.missing.state"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode",
                "saveaccount.missing.country_code",
                messages.getMessage("saveaccount.missing.country_code"));
        if (account.getCountryCode().length() > 0 &&
                account.getCountryCode().length() != COUNTRY_CODE_LENGTH) {
            errors.rejectValue("countryCode", 
                    "saveaccount.invalid.country_code",
                    messages.getMessage("saveaccount.invalid.country_code"));
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

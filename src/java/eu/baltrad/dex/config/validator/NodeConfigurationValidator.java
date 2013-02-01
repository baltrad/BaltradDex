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

package eu.baltrad.dex.config.validator;

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.util.WebValidator;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validate node configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0
 */
public class NodeConfigurationValidator implements Validator {

    private static final int MIN_FIELD_LENGTH = 8;
    private static final int COUNTRY_CODE_LENGTH = 2;

    private MessageResourceUtil messages;
    private WebValidator webValidator;

    /**
     * Declares classes supported by this validator.
     * @param clazz Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports(Class clazz) {
        return AppConfiguration.class.equals(clazz);
    }
    
    /**
     * Validates form object.
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate(Object command, Errors errors) {
        AppConfiguration conf = (AppConfiguration) command;
        if (conf == null) return;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nodeName", 
                "saveconf.missing.node_name", 
                messages.getMessage("saveconf.missing.node_name"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nodeAddress", 
                "saveconf.missing.node_address",
                messages.getMessage("saveconf.missing.node_address"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgName", 
                "saveconf.missing.organization_name",
                messages.getMessage("saveconf.missing.organization_name"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "orgUnit", 
                "saveconf.missing.organization_unit",
                messages.getMessage("saveconf.missing.organization_unit"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "locality", 
                "saveconf.missing.locality",
                messages.getMessage("saveconf.missing.locality"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", 
                "saveconf.missing.organization_address",
                messages.getMessage("saveconf.missing.state"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "countryCode", 
                "saveconf.missing.organization_address",
                messages.getMessage("saveconf.missing.country_code"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "timeZone", 
                "saveconf.missing.time_zone",
                messages.getMessage("saveconf.missing.time_zone"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "workDir", 
                "saveconf.missing.work_directory",
                messages.getMessage("saveconf.missing.work_directory"));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminEmail", 
                "saveconf.missing.admin_email",
                messages.getMessage("saveconf.missing.admin_email"));
        if (conf.getNodeName().trim().length() > 0 
                && conf.getNodeName().trim().length() < MIN_FIELD_LENGTH) {
            errors.rejectValue("nodeName", "saveconf.invalid.node_name",
                    messages.getMessage("saveconf.invalid.node_name"));
        }
        if (!webValidator.validateUrl(conf.getNodeAddress())) {
            errors.rejectValue("nodeAddress", "saveconf.invalid.node_address",
                    messages.getMessage("saveconf.invalid.node_address"));
        }
        if (!webValidator.validateEmail(conf.getAdminEmail())) {
            errors.rejectValue("adminEmail", "saveconf.invalid.admin_email",
                    messages.getMessage("saveconf.invalid.admin_email"));
        }
        if (conf.getCountryCode().length() > 0 &&
                conf.getCountryCode().length() != COUNTRY_CODE_LENGTH) {
            errors.rejectValue("countryCode", 
                    "saveconf.invalid.country_code",
                    messages.getMessage("saveconf.invalid.country_code"));
        }
    }
   
    /**
     * @param webValidator the webValidator to set
     */
    @Autowired
    public void setWebValidator(WebValidator webValidator) {
        this.webValidator = webValidator;
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
}

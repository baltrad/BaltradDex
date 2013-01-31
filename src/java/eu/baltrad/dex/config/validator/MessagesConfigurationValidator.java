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

package eu.baltrad.dex.config.validator;

import eu.baltrad.dex.config.model.LogConfiguration;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

/**
 * Validates system log configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.7.3
 */
public class MessagesConfigurationValidator implements Validator {
    
    /**
     * Declares classes supported by this validator.
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports(Class clazz) {
        return LogConfiguration.class.equals(clazz);
    }
    
    /**
     * Validates form object.
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate(Object command, Errors errors) {
        LogConfiguration conf = (LogConfiguration) command;
        
        if (conf == null) return;
        
        if (!conf.getMsgRecordLimit().matches("[1-9][0-9]*")) {
            errors.rejectValue("msgRecordLimit", 
                    "savemsgconf.invalid.record_limit");
        }
        if (!conf.getMsgMaxAgeDays().matches("0|[1-9]{1}[0-9]*")) {
            errors.rejectValue("msgMaxAgeDays", 
                    "savemsgconf.invalid.days_limit");
        }
        if (!conf.getMsgMaxAgeHours().matches("[0-9]|[1-2][0-3]")) {
            errors.rejectValue("msgMaxAgeHours", 
                    "savemsgconf.invalid.hours_limit");
        }
        if (!conf.getMsgMaxAgeMinutes().matches("[0-5]|[1-5][0-9]")) {
            errors.rejectValue("msgMaxAgeMinutes", 
                    "savemsgconf.invalid.minutes_limit");
        }
        if (conf.getMsgMaxAgeDays().matches("0") &&
                    conf.getMsgMaxAgeHours().matches("0") &&
                    conf.getMsgMaxAgeMinutes().matches("0")) {
                errors.rejectValue("msgMaxAgeDays", 
                        "savemsgconf.invalid.age_limit");
        }
    }
    
}


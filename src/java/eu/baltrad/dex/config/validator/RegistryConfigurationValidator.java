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

import eu.baltrad.dex.config.model.LogConfiguration;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

/**
 * Validates registry configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.7.3
 */
public class RegistryConfigurationValidator implements Validator {
    
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
     *
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate(Object command, Errors errors) {
        LogConfiguration conf = (LogConfiguration) command;
        
        if (conf == null) return;
        
        if (conf.getTrimByNumber() && conf.getRecordLimit() <= 0) {
            errors.rejectValue("recordLimit", 
                    "saveregistryconf.invalid.record_limit");
        }
        
        if (conf.getTrimByAge()) {
            if (conf.getMaxAgeDays() < 0) {
                errors.rejectValue("maxAgeDays", 
                        "saveregistryconf.invalid.days_limit");
            }
            if (conf.getMaxAgeHours() < 0) {
                errors.rejectValue("maxAgeHours", 
                        "saveregistryconf.invalid.hours_limit");
            }
            if (conf.getMaxAgeHours() > 23) {
                errors.rejectValue("maxAgeHours", 
                        "saveregistryconf.invalid.hours_limit");
            }
            if (conf.getMaxAgeMinutes() < 0) {
                errors.rejectValue("maxAgeMinutes", 
                        "saveregistryconf.invalid.minutes_limit");
            }
            if (conf.getMaxAgeMinutes() > 59) {
                errors.rejectValue("maxAgeMinutes", 
                        "saveregistryconf.invalid.minutes_limit");
            }
            if (conf.getMaxAgeDays() == 0 && conf.getMaxAgeHours() == 0 &&
                    conf.getMaxAgeMinutes() == 0) {
                errors.rejectValue("maxAgeDays", 
                        "saveregistryconf.invalid.age_limit");
            }
        }
    }
    
}

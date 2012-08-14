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

package eu.baltrad.dex.datasource.util;

import eu.baltrad.dex.util.MessageResourceUtil;
import eu.baltrad.dex.datasource.model.DataSource;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Data source validator.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class DataSourceValidator implements Validator {
    
    private static final String MISSING_NAME_ERROR_MSG_KEY = 
            "savedatasource.missing.name";
    private static final String MISSING_DESCRIPTION_ERROR_MSG_KEY = 
            "savedatasource.missing.description";
    
    private MessageResourceUtil messages;
    
    /**
     * Defines supported class.
     * @param aClass Supported class
     * @return True is class is supported
     */
    public boolean supports(Class clazz) {
        return DataSource.class.equals(clazz);
    }
    
    /**
     * Validates data source object
     * @param command Command object
     * @param errors Errors
     */
    public void validate(Object command, Errors errors) {
        DataSource dataSource = (DataSource) command;
        if (dataSource == null) {
            return;
        }
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", 
                MISSING_NAME_ERROR_MSG_KEY, 
                messages.getMessage(MISSING_NAME_ERROR_MSG_KEY));
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", 
                MISSING_DESCRIPTION_ERROR_MSG_KEY,
                messages.getMessage(MISSING_DESCRIPTION_ERROR_MSG_KEY));
    }

    /**
     * @param messages the messages to set
     */
    @Autowired
    public void setMessages(MessageResourceUtil messages) {
        this.messages = messages;
    }
    
}

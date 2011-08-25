/***************************************************************************************************
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
***************************************************************************************************/

package eu.baltrad.dex.config.util;

import eu.baltrad.dex.config.model.LogConfiguration;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;

import java.util.Date;

/**
 * Validates registry configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.3
 * @since 0.7.3
 */
public class SaveRegistryConfigurationValidator implements Validator {
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return LogConfiguration.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        LogConfiguration logConf = ( LogConfiguration )command;
        if( logConf == null ) return;
        // Validate record limit number
        if( logConf.getRecordLimit() <= 0 && logConf.getTrimByNumber() ) {
            errors.rejectValue( "recordLimit", "error.recordlimit.zero" );
        }
        // Validate date limit
        if( logConf.getDateLimit() == null ) {
            logConf.setDateLimit( new Date() );
        }
    }
}
//--------------------------------------------------------------------------------------------------
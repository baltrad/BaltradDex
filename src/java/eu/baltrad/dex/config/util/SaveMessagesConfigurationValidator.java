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

/**
 * Validates log configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.3
 * @since 0.7.3
 */
public class SaveMessagesConfigurationValidator implements Validator {

//------------------------------------------------------------------------------------------ Methods
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
        if( logConf.getTrimByNumber() && logConf.getRecordLimit() <= 0  ) {
            errors.rejectValue( "recordLimit", "error.recordlimit.zero" );
        }
        // Validate age limit
        if( logConf.getMaxAgeDays() < 0 ) {
            errors.rejectValue( "maxAgeDays", "error.agelimit.invalid" );
        }
        if( logConf.getMaxAgeHours() < 0 ) {
            errors.rejectValue( "maxAgeHours", "error.agelimit.invalid" );
        }
        if( logConf.getMaxAgeHours() > 23 ) {
            errors.rejectValue( "maxAgeHours", "error.agelimit.hours_exceeded" );
        }
        if( logConf.getMaxAgeMinutes() < 0 ) {
            errors.rejectValue( "maxAgeMinutes", "error.agelimit.invalid" );
        }
        if( logConf.getMaxAgeMinutes() > 59 ) {
            errors.rejectValue( "maxAgeMinutes", "error.agelimit.minutes_exceeded" );
        }
        if( logConf.getMaxAgeDays() == 0 && logConf.getMaxAgeHours() == 0 &&
                logConf.getMaxAgeMinutes() == 0 ) {
            errors.rejectValue( "maxAgeDays", "error.recordlimit.zero" );
            errors.rejectValue( "maxAgeHours", "error.recordlimit.zero" );
            errors.rejectValue( "maxAgeMinutes", "error.recordlimit.zero" );
        }
    }
}
//--------------------------------------------------------------------------------------------------

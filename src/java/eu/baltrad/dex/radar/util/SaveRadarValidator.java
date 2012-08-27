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

package eu.baltrad.dex.radar.util;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import eu.baltrad.dex.radar.model.Radar;

/**
 * Validates save radar form input.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class SaveRadarValidator implements Validator {

    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return Radar.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param obj Form object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        Radar radar = ( Radar )command;
        if( radar == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "name", 
                "error.missing.radarname" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "wmoNumber", 
                "error.missing.wmonumber" );
    }
}

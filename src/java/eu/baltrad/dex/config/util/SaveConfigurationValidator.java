/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.config.model.Configuration;
import eu.baltrad.dex.util.WebUtil;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator class used to validate input from system configuration form.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class SaveConfigurationValidator implements Validator {
//---------------------------------------------------------------------------------------- Constants
    private static final int MIN_FIELD_LENGTH = 6;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return Configuration.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        Configuration conf = ( Configuration )command;
        if( conf == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nodeName", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nodeAddress", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "orgName", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "orgAddress", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "timeZone", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "tempDir", "error.field.required" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "adminEmail", "error.field.required" );
        // validate node name
        if( conf.getNodeName().trim().length() > 0 && conf.getNodeName().trim().length()
                < MIN_FIELD_LENGTH ) {
            errors.rejectValue( "nodeName", "error.field.name.tooshort" );
        }
        // validate node address - uncomment when nodes have FQDNs
        /*if( conf.getNodeAddress().trim().length() > 0
                && !WebUtil.validateWebAddress( conf.getNodeAddress() ) ) {
            errors.rejectValue( "nodeAddress", "error.field.nodeaddress.invalid" );
        }*/
        // validate email address
        if( conf.getAdminEmail().trim().length() > 0 && !WebUtil.validateEmailAddress(
                conf.getAdminEmail() ) ) {
            errors.rejectValue( "adminEmail", "error.field.email.invalid" );
        }
    }
}
//--------------------------------------------------------------------------------------------------
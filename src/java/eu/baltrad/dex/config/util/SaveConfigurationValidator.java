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

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.util.WebUtil;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator class used to validate input from system configuration form.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
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
        return AppConfiguration.class.equals( aClass );
    }
    /**
     * Validates form object.
     *
     * @param command Form - bound object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        AppConfiguration conf = ( AppConfiguration )command;
        if( conf == null ) return;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nodeName", "error.missing.nodename" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "organization", "error.missing.orgname" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "address", "error.missing.address" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "timeZone", "error.missing.timezone" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "workDir", "error.missing.workdir" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", "error.missing.email" );
        // validate node name
        if( conf.getNodeName().trim().length() > 0 && conf.getNodeName().trim().length()
                < MIN_FIELD_LENGTH ) {
            errors.rejectValue( "nodeName", "error.short.nodename" );
        }
        // validate host address
        if( conf.getHostAddress().isEmpty() ) {
            ValidationUtils.rejectIfEmptyOrWhitespace( errors, "hostAddress",
                "error.address.invalid" );
        }
        // Validate port number
        if( errors.hasFieldErrors( "port") ) {
            errors.rejectValue( "hostAddress", "error.portnumber.invalid" );
        }
        if( conf.getPort() <= 0 ) {
            errors.rejectValue( "hostAddress", "error.portnumber.invalid" );
        }
        // validate email address
        if( conf.getEmail().trim().length() > 0 && !WebUtil.validateEmailAddress(
                conf.getEmail() ) ) {
            errors.rejectValue( "email", "error.address.invalid" );
        }
    }
}
//--------------------------------------------------------------------------------------------------
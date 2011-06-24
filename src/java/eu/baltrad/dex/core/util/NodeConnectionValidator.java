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

package eu.baltrad.dex.core.util;

import eu.baltrad.dex.core.model.NodeConnection;

import org.springframework.validation.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * Validator class used to validate user input on connect to node page.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class NodeConnectionValidator implements Validator {
//------------------------------------------------------------------------------------------ Methods
    /**
     * Declares classes supported by this validator.
     *
     * @param aClass Class instance of supported type
     * @return True if class is supported, false otherwise
     */
    public boolean supports( Class aClass ) {
        return NodeConnection.class.equals( aClass );
    }
    /**
     * Validates connection parameters object.
     *
     * @param command Form object
     * @param errors Errors object
     */
    public void validate( Object command, Errors errors ) {
        NodeConnection conn = ( NodeConnection )command;
        if( conn == null ) return;
        if( conn.getConnectionName() == null ) {
            if( conn.getFullAddress() == null &&
                conn.getUserName() == null &&
                conn.getPassword() == null ) {
                ValidationUtils.rejectIfEmptyOrWhitespace( errors, "connectionName",
                    "error.select.connection" );
            } else {
                boolean isValidPortNumber = false;
                try {
                    int port = Integer.parseInt( conn.getPortNumber() );
                    isValidPortNumber = true;
                } catch( NumberFormatException e ) {
                    isValidPortNumber = false;
                } catch( Exception e ) {
                    isValidPortNumber = false;
                }
                if( conn.getShortAddress().isEmpty() || conn.getPortNumber().isEmpty() ||
                        !isValidPortNumber ) {
                    ValidationUtils.rejectIfEmptyOrWhitespace( errors, "fullAddress",
                        "error.address.invalid" );
                }
                if( conn.getUserName().isEmpty() ) {
                    ValidationUtils.rejectIfEmptyOrWhitespace( errors, "userName",
                        "error.missing.username" );
                }
                if( conn.getPassword().isEmpty() ) {
                    ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password",
                        "error.missing.password" );
                }
            }   
        }
    }
}
//--------------------------------------------------------------------------------------------------

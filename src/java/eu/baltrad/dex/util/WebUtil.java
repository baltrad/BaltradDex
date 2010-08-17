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

package eu.baltrad.dex.util;

/**
 * Utility class implementing methods allowing to validate e-mail and web addresses.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class WebUtil {
//---------------------------------------------------------------------------------------- Constants
    private static final String ET = "@";
    private static final String DOT = "\\.";
    private static final String HTTP_PREFIX = "http://";
//------------------------------------------------------------------------------------------ Methods
    /**
     * Validates email address.
     *
     * @param emailAddress Email address
     * @return True if the address is valid, false otherwise
     */
    public static boolean validateEmailAddress( String emailAddress ) {
        if( emailAddress == null ) return false;
        return hasNameAndDomain( emailAddress );
    }
    /**
     * Validates web address.
     *
     * @param webAddress Web address
     * @return True if the address is valid, false otherwise
     */
    public static boolean validateWebAddress( String webAddress ) {
        if( webAddress == null ) return false;
        return hasProtocolAndDomain( webAddress );
    }
    /**
     * Checks if an email address has valid name and domain.
     * 
     * @param emailAddress Email address
     * @return True if email address has valid parts, false otherwise
     */
    private static boolean hasNameAndDomain( String emailAddress ) {
        String[] tokens = emailAddress.split( ET );
        return tokens.length == 2 && hasTextContent( tokens[ 0 ] ) &&
            hasTextContent( tokens[ 1 ] );
    }
    /**
     * Checks if web address has valid protocol prefix and domain.
     *
     * @param webAddress Web address
     * @return True if web address has valid parts, false otherwise
     */
    private static boolean hasProtocolAndDomain( String webAddress ) {
        boolean res = true;
        if( !webAddress.startsWith( HTTP_PREFIX ) ) {
            res =  false;
        }
        String[] tokens = webAddress.split( DOT );
        if( tokens.length < 3 ) {
            res = false;
        }
        for( int i = 0; i < tokens.length; i++ ) {
            if( !hasTextContent( tokens[ i ] ) ) {
                res = false;
            }
        }
        return res;
    }
    /**
     * Checks is string has text content.
     *
     * @param s String
     * @return True if string has text content, false otherwise
     */
    private static boolean hasTextContent( String s ) {
        boolean res;
        try {
            res = ( s != null ) && ( s.trim().length() > 0 );
        } catch( NullPointerException e ) {
            res = false;
        }
        return res;
    }
}
//--------------------------------------------------------------------------------------------------

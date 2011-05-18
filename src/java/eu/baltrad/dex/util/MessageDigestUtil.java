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

package eu.baltrad.dex.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class implementing MD5 hash function used to protect passwords.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class MessageDigestUtil {
//---------------------------------------------------------------------------------------- Constants
    // Algorithm type identifier
    private static final String ALGORITHM = "MD5";
    // Hash length constant
    private static final int HASH_LEN = 16;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method returns hash for a given message.
     * 
     * @param message Message string
     * @return Hash
     */
    public static String createHash( String message ) {
        String hashString = "";
        try {
            MessageDigest md = MessageDigest.getInstance( ALGORITHM );
            byte[] messageDigest = md.digest( message.getBytes() );
                BigInteger number = new BigInteger( 1, messageDigest );
                hashString = number.toString( HASH_LEN );
        } catch( NoSuchAlgorithmException e ) {
            System.err.println( "Error while initializing hash function: " + e.getMessage() );
        }
        return hashString;
    }
}
//--------------------------------------------------------------------------------------------------

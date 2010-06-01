/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class implementing MD5 hash function used to protect passwords.
 *
 * @author szewczenko
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

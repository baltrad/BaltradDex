/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.util;

import java.math.BigInteger;
import java.security.MessageDigest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Utility class implementing MD5 hash function used to protect passwords.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class MessageDigestUtil {
    
    private static ByteArrayOutputStream bos = new ByteArrayOutputStream();
    
    /**
     * Create message hash.
     * @param algorithm Algorithm
     * @param message Input message
     * @return Message hash 
     */
    public static String createHash(String algorithm, String message) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.reset();
            md.update(message.getBytes("UTF-8"));
            byte[] messageDigest = md.digest();
            BigInteger number = new BigInteger(1, messageDigest);
            String hashString = number.toString(16);
            while (hashString.length() < 32) {
                hashString = "0" + hashString;
            }
            return hashString;
        } catch (Exception e) {
            throw new RuntimeException("Error while initializing hash function", 
                    e);
        }
        
    }
    
    /**
     * Reads file or directory into byte array.
     * @param directory File or directory to read
     * @param bytes Byte array
     * @return Byte array 
     * @throws RuntimeException 
     */
    public static byte[] getBytes(File directory) throws Exception {
        try {
            try {
                byte[] buff = new byte[1024];
                for (File file : directory.listFiles()) {
                    if (file.isFile()) {
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(file);
                            int len;
                            while((len = fis.read(buff)) > 0) {
                                bos.write(buff, 0, len);
                            }
                        } finally {
                            fis.close();
                        }
                    } else {
                        getBytes(file); 
                    }    
                }
            } finally {
                byte[] bytes = bos.toByteArray();
                bos.close();
                bos.reset();
                return bytes;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read directory to " +
                            "byte array", e);
        }
    }
    
}


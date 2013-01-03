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

package eu.baltrad.dex.util;

/**
 * Implements URL and email address validators.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
public class WebValidator {

    private static final String ET = "@";
    private static final String DOT = "\\.";
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    /**
     * Validates URL address.
     * 
     * @param url URL address to validate. 
     * @return True if given address is a valid URL address, false otherwise
     */
    public boolean validateUrl(String url) {
        boolean res = false;
        if (url != null) {
            if (!url.trim().isEmpty()) {
                res = hasProtocol(url) && hasDomain(url);
            }
        }
        return res;
    }
    /**
     * Validates email address.
     * 
     * @param email Email address to validate. 
     * @return True if given address is a valid email address, false otherwise
     */
    public boolean validateEmail(String email) {
         boolean res = false;
        if (email != null) {
            if (!email.trim().isEmpty()) {
                res = hasUserAndDomain(email);
            }
        }
        return res;
    }
    /**
     * Checks if a given URL contains protocol string.
     * 
     * @param url URL address to validate.
     * @return True if a given URL contains protocol string.
     */
    private boolean hasProtocol(String url) {
        boolean res = false;
        if (url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX)) {
            res = true;
        }
        return res;
    }
    /**
     * Checks if a given URL contains a domain name.
     * 
     * @param url URL address to validate
     * @return True if a given address contains domain name
     */
    private boolean hasDomain(String url) {
        boolean res = false;
        String[] tokens = url.split(DOT);
        if (tokens.length >= 2) {
            res = hasTextContent(tokens);
        }
        return res;
    }
    /**
     * Check if a given email address contains user name and domain.
     * 
     * @param email Email address to validate
     * @return True if a given email address contains user name and domain
     */
    private boolean hasUserAndDomain(String email) {
        boolean res = false;
        String[] tokens = email.split(ET);
        if (tokens.length == 2) {
            res = hasTextContent(tokens);
        }
        return res;
    }
    /**
     * Validates an array of strings.
     * 
     * @param tokens Array of string parameters
     * @return True if all parameters are not empty
     */
    private boolean hasTextContent(String[] tokens) {
        boolean res = true;
        int i = 0;
        while (tokens[i] != null) {
            if (!validate(tokens[i]))
                res = false;
                break;
        }
        return res;        
    }
    /**
     * Validates a string parameter.
     * 
     * @param param String parameter
     * @return True upon successful validation
     */
    public static boolean validate(String param) {
        boolean result = false;
        if (param != null) {
            if (!param.trim().isEmpty()) {
                result = true;
            }
        }
        return result;
    }
}


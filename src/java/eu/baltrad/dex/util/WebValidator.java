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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Implements URL and email address validators.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0.0
 */
public class WebValidator {

    private static final String ET = "@";

    /**
     * Validates URL address.
     * 
     * @param url URL address to validate. 
     * @return True if given address is a valid URL address, false otherwise
     */
    public static boolean validateUrl(String urlString) {
    	URL url;
    	try {
            url = new URL(urlString);
        } catch (MalformedURLException malformedURLException) {
            return false;
        }
    	
    	if (url.getHost().equals(""))
    	{
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Validates email address.
     * 
     * @param email Email address to validate. 
     * @return True if given address is a valid email address, false otherwise
     */
    public static boolean validateEmail(String email) {
    	boolean res = false;
    	if (email != null) {
    		if (!email.trim().isEmpty()) {
    			res = hasUserAndDomain(email);
    		}
    	}
    	return res;
    }
    
    public static boolean isUrlLocal(String urlString) {
    	URL url;
    	try {
    		url = new URL(urlString);
    	} catch (MalformedURLException malformedURLException) {
    		return false;
    	}

    	String host = url.getHost();
    	if (host.equals("127.0.0.1") || host.equals("localhost"))
    	{
    		return true;
    	}

    	return false;
    }

    /**
     * Check if a given email address contains user name and domain.
     * 
     * @param email Email address to validate
     * @return True if a given email address contains user name and domain
     */
    private static boolean hasUserAndDomain(String email) {
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
    private static boolean hasTextContent(String[] tokens) {
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
    
    /**
     * Validates a list parameter.
     * 
     * @param param List parameter
     * @return True upon successful validation
     */
    public static boolean validate(List list) {
        boolean result = false;
        if (list != null) {
            if (list.size() > 0) {
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Validates a date string
     * @param dateFormat the expected date format
     * @param dateString the date string that should match the format
     * @return True if successful
     */
    public static boolean validateDateString(String dateFormat, String dateString) {
      boolean result = validate(dateString);
      if (result) {
        try {
          new SimpleDateFormat(dateFormat).parse(dateString);
        } catch (ParseException pe) {
          result = false;
        }
      }
      return result;
    }
    
}


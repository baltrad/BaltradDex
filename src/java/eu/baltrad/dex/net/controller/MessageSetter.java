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

package eu.baltrad.dex.net.controller;

import org.springframework.ui.Model;

/**
 * Defines methods allowing to set diagnostic messages.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.0
 * @since 1.1.0
 */
public interface MessageSetter {
    
    /** Error message */
    static final String ERROR_MSG_KEY = "error_message";
    /** Detailed error message */
    static final String ERROR_DETAILS_KEY = "error_details";
    /** Success message */
    static final String SUCCESS_MSG_KEY = "success_message";
    /** Detailed success message */
    static final String SUCCESS_DETAILS_KEY = "success_details";
    
    public void setMessage(Model model, String messageKey, String message);
    
    public void setMessage(Model model, String messageKey, String detailsKey,
                           String message, String details);
    
}

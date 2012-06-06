/***************************************************************************************************
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
***************************************************************************************************/
package eu.baltrad.dex.auth.util;

import eu.baltrad.dex.user.model.User;

import javax.servlet.http.HttpSession;

/**
 * Implements session management functionality and user authentication control.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public class SecurityManager {
    
    /**
     * Gets session user.
     * @param session Http session
     * @return Session user
     */
    public static User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("session_user");
    }
    
    /**
     * Sets session user.
     * @param session Http session
     * @param user User to set
     */
    public static void setSessionUser(HttpSession session, User user) {
        session.setAttribute("session_user", user);
    }
    
    /**
     * Resets session user
     * @param session Http session 
     */
    public static void resetSessionUser(HttpSession session) {
        session.removeAttribute("session_user");
    }
}
//--------------------------------------------------------------------------------------------------

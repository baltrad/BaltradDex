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

package eu.baltrad.dex.user.model;

/**
 * Class implements password object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.1
 * @since 0.7.1
 */
public class Password {
//---------------------------------------------------------------------------------------- Variables
    /* User name associated with a given account */
    private String userName;
    /* Password currently set for this account*/
    private String currentPasswd;
    /* Current password confirmed */
    private String confirmCurrentPasswd;
    /* New password */
    private String newPasswd;
    /* New password confirmed */
    private String confirmNewPasswd;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     * 
     * @param userName User name
     * @param currentPasswd Current password
     */
    public Password( String userName, String currentPasswd ) {
        this.userName = userName;
        this.currentPasswd = currentPasswd;
    }
    /**
     * Gets user name.
     *
     * @return User name
     */
    public String getUserName() { return userName; }
    /**
     * Sets user name.
     *
     * @param userName User name to set
     */
    public void setUserName( String userName ) {
        this.userName = userName;
    }
    /**
     * Gets current password.
     *
     * @return Current password
     */
    public String getCurrentPasswd() { return currentPasswd; }
    /**
     * Sets current password.
     *
     * @param currentPasswd Current password to set
     */
    public void setCurrentPasswd( String currentPasswd ) { this.currentPasswd = currentPasswd; }
    /**
     * Gets confirmed current password.
     *
     * @return Confirmed current password
     */
    public String getConfirmCurrentPasswd() { return confirmCurrentPasswd; }
    /**
     * Sets confirmed current password.
     *
     * @param confirmCurrentPasswd Confirmed current password to set
     */
    public void setConfirmCurrentPasswd( String confirmCurrentPasswd ) {
        this.confirmCurrentPasswd = confirmCurrentPasswd;
    }
    /**
     * Gets new password.
     *
     * @return New password
     */
    public String getNewPasswd() { return newPasswd; }
    /**
     * Sets new password.
     *
     * @param newPasswd New password to set
     */
    public void setNewPasswd( String newPasswd ) { this.newPasswd = newPasswd; }
    /**
     * Gets confirmed new password.
     *
     * @return Confirmed new password
     */
    public String getConfirmNewPasswd() { return confirmNewPasswd; }
    /**
     * Sets confirmed new password.
     *
     * @param confirmNewPasswd Confirmed new password to set
     */
    public void setConfirmNewPasswd( String confirmNewPasswd ) {
        this.confirmNewPasswd = confirmNewPasswd;
    }
}
//--------------------------------------------------------------------------------------------------

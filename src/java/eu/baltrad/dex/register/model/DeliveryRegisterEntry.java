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

package eu.baltrad.dex.register.model;

/**
 * Class implements data delivery register entry.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class DeliveryRegisterEntry {
//---------------------------------------------------------------------------------------- Variables
    // Record ID
    private int id;
    // User ID
    private int userId;
    // File's hash code
    private int hashCode;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Default constructor
     */
    public DeliveryRegisterEntry() {}
    /**
     * Constructor sets field values.
     *
     * @param userId User id
     * @param hashCode File's hash code
     */
    public DeliveryRegisterEntry( int userId, int hashCode ) {
        this.userId = userId;
        this.hashCode = hashCode;
    }
    /**
     * Method returns register entry id.
     *
     * @return Register entry id
     */
    public int getId() { return id; }
    /**
     * Method sets register entry id.
     *
     * @param registerEntryID Register entry id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method returns user id.
     *
     * @return User id
     */
    public int getUserId() { return userId; }
    /**
     * Method sets user id.
     *
     * @param userId User id
     */
    public void setUserId( int userId ) { this.userId = userId; }
    /**
     * Method returns file's hash code.
     *
     * @return File's hash code
     */
    public int getHashCode() { return hashCode; }
    /**
     * Method sets file's hash code.
     *
     * @param hashCode File's hash code
     */
    public void setHashCode( int hashCode ) { this.hashCode = hashCode; }
}
//--------------------------------------------------------------------------------------------------

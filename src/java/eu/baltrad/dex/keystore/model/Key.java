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

package eu.baltrad.dex.keystore.model;

/**
 * Implements key object.
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.7.0
 */
public class Key {
    
    private int id;
    private String name;
    private String checksum;
    private boolean authorized;

    /**
     * Default constructor.
     */
    public Key() {}
    
    /**
     * Constructor.
     * @param id Record id
     * @param name Key name
     * @param checksum File checksum
     * @param authorized Authorization toggle 
     */
    public Key(int id, String name, String checksum, boolean authorized) {
        this.id = id;
        this.name = name;
        this.checksum = checksum;
        this.authorized = authorized;
    }
    
    /**
     * Constructor.
     * @param name Key name
     * @param checksum File checksum
     * @param authorized Authorization toggle 
     */
    public Key(String name, String checksum, boolean authorized) {
        this.name = name;
        this.checksum = checksum;
        this.authorized = authorized;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the checksum
     */
    public String getChecksum() {
        return checksum;
    }

    /**
     * @param checksum the checksum to set
     */
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * @return the authorized
     */
    public boolean isAuthorized() {
        return authorized;
    }

    /**
     * @param authorized the authorized to set
     */
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
    
}

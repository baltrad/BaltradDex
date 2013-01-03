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

package eu.baltrad.dex.user.model;

/**
 * User role used for setting system access level.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.1.0
 */
public class Role {
    
    public final static String ADMIN = "admin";
    public final static String OPERATOR = "operator";
    public final static String PEER = "peer";
    public final static String USER = "user";
    
    private int id;
    private String name;
    
    /**
     * Constructor.
     *
     * @param id Role ID
     * @param name Role name
     */
    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * Gets role ID.
     * @return Role ID
     */
    public int getId() { return id; }
    
    /**
     * Method sets role ID.
     * @param id Role ID
     */
    public void setId(int id) { this.id = id; }
    
    /**
     * Gets role name.
     * @return Role name
     */
    public String getName() { return name; }
    
    /**
     * Sets role name.
     * @param name Role name
     */
    public void setName(String name) { this.name = name; }
}

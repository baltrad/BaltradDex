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

package eu.baltrad.dex.user.model;

/**
 * Class implements role object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class Role {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String role;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Role() {}
    /**
     * Creates new role object with given field values.
     *
     * @param id Role ID
     * @param role Role name
     */
    public Role( int id, String role ) {
        this.id = id;
        this.role = role;
    }
    /**
     * Gets role ID.
     *
     * @return Role ID
     */
    public int getId() { return id; }
    /**
     * Method sets role ID.
     *
     * @param id Role ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets role name.
     *
     * @return Role name
     */
    public String getRole() { return role; }
    /**
     * Sets role name.
     *
     * @param role Role name
     */
    public void setRole( String role ) { this.role = role; }
}
//--------------------------------------------------------------------------------------------------
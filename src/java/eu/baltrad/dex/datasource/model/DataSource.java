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

package eu.baltrad.dex.datasource.model;

import java.io.Serializable;

/**
 * Class implements data source object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class DataSource implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** Data source ID */
    private int id;
    /** Data source name */
    private String name;
    /** Description */
    private String description;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public DataSource() {}
    /**
     * Constructor.
     *
     * @param name Data source name
     * @param description Description
     */
    public DataSource( String name, String description ) {
        this.name = name;
        this.description = description;
    }
    /**
     * Constructor.
     *
     * @param id Data source ID
     * @param name Data source name
     * @param description Description
     */
    public DataSource( int id, String name, String description ) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    /**
     * Compares data source with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getName().equals( ( ( DataSource )o ).getName() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates data source name hash code.
     *
     * @return Data source name code or 0 if product identifier is null
     */
    @Override
    public int hashCode() {
        return( name != null ? name.hashCode() : 0 );
    }
    /**
     * Gets data source ID.
     *
     * @return Data source ID
     */
    public int getId() { return id; }
    /**
     * Sets data source ID
     *
     * @param id Data source ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets data source name.
     *
     * @return Data source name
     */
    public String getName() { return name; }
    /**
     * Sets data source name.
     *
     * @param name Data source name to set
     */
    public void setName( String name ) { this.name = name; }
    /**
     * Gets data source description.
     *
     * @return Data source description
     */
    public String getDescription() { return description; }
    /**
     * Sets data source description.
     *
     * @param description Data source description to set
     */
    public void setDescription( String description ) { this.description = description; }
}
//--------------------------------------------------------------------------------------------------

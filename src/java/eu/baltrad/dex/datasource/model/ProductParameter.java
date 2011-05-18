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
 * Class implements product parameter according to ODIM radar data information model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class ProductParameter implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** Product parameter ID */
    private int id;
    /** Product parameter */
    private String parameter;
    /** Description */
    private String description;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public ProductParameter() {}
    /**
     * Constructor.
     *
     * @param id Product parameter ID
     * @param quantity Product parameter
     * @param description Description
     */
    public ProductParameter( int id, String parameter, String description ) {
        this.id = id;
        this.parameter = parameter;
        this.description = description;
    }
    /**
     * Compares product parameter with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getParameter().equals( ( ( ProductParameter )o ).getParameter() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates product parameter identifier hash code.
     *
     * @return Product parameter identifier hash code or 0 if product parameter identifier is null
     */
    @Override
    public int hashCode() {
        return( parameter != null ? parameter.hashCode() : 0 );
    }
    /**
     * Gets product parameter ID.
     *
     * @return Product parameter ID
     */
    public int getId() { return id; }
    /**
     * Sets product parameter ID
     *
     * @param id Product parameter ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets product parameter identifier.
     *
     * @return Product parameter identifier
     */
    public String getParameter() { return parameter; }
    /**
     * Sets product parameter identifier.
     *
     * @param parameter Product parameter identifier to set
     */
    public void setParameter( String parameter ) { this.parameter = parameter; }
    /**
     * Gets product parameter description.
     *
     * @return Product parameter description
     */
    public String getDescription() { return description; }
    /**
     * Sets product parameter description.
     *
     * @param description Product parameter description to set
     */
    public void setDescription( String description ) { this.description = description; }
}
//--------------------------------------------------------------------------------------------------

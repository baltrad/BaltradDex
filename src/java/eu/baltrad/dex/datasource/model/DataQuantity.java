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
 * Class implements data quantity according to ODIM radar data information model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class DataQuantity implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** Quantity ID */
    private int id;
    /** Quantity identifier */
    private String quantity;
    /** Unit */
    private String unit;
    /** Description */
    private String description;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public DataQuantity() {}
    /**
     * Constructor.
     *
     * @param id Quantity ID
     * @param quantity Quantity identifier
     * @param unit Unit
     * @param description Description
     */
    public DataQuantity( int id, String quantity, String unit, String description ) {
        this.id = id;
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
    }
    /**
     * Compares quantity with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getQuantity().equals( ( ( DataQuantity )o ).getQuantity() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates quantity identifier hash code.
     *
     * @return Quantity identifier hash code or 0 if identifier is null
     */
    @Override
    public int hashCode() {
        return( quantity != null ? quantity.hashCode() : 0 );
    }
    /**
     * Gets quantity ID.
     *
     * @return Quantity ID
     */
    public int getId() { return id; }
    /**
     * Sets quantity ID
     *
     * @param id Quantity ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets quantity identifier.
     *
     * @return Quantity identifier
     */
    public String getQuantity() { return quantity; }
    /**
     * Sets quantity identifier.
     *
     * @param quantity Quantity identifier to set
     */
    public void setQuantity( String quantity ) { this.quantity = quantity; }
    /**
     * Gets quantity unit.
     *
     * @return Quantity unit
     */
    public String getUnit() { return unit; }
    /**
     * Sets quantity unit.
     *
     * @param unit Quantity unit to set
     */
    public void setUnit( String unit ) { this.unit = unit; }
    /**
     * Gets quantity description.
     *
     * @return Quantity description
     */
    public String getDescription() { return description; }
    /**
     * Sets quantity description.
     *
     * @param description Quantity description to set
     */
    public void setDescription( String description ) { this.description = description; }
}
//--------------------------------------------------------------------------------------------------

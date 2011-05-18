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
 * Class implements product according to ODIM radar data information model.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class Product implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** Product ID */
    private int id;
    /** Product identifier */
    private String product;
    /** Description */
    private String description;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Product() {}
    /**
     * Constructor.
     *
     * @param id Product ID
     * @param quantity Product identifier
     * @param description Description
     */
    public Product( int id, String product, String description ) {
        this.id = id;
        this.product = product;
        this.description = description;
    }
    /**
     * Compares product with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getProduct().equals( ( ( Product )o ).getProduct() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates product identifier hash code.
     *
     * @return Product identifier hash code or 0 if product identifier is null
     */
    @Override
    public int hashCode() {
        return( product != null ? product.hashCode() : 0 );
    }
    /**
     * Gets product ID.
     *
     * @return Product ID
     */
    public int getId() { return id; }
    /**
     * Sets product ID
     *
     * @param id Product ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets product identifier.
     *
     * @return Product identifier
     */
    public String getProduct() { return product; }
    /**
     * Sets product identifier.
     *
     * @param product Product identifier to set
     */
    public void setProduct( String product ) { this.product = product; }
    /**
     * Gets product description.
     *
     * @return Product description
     */
    public String getDescription() { return description; }
    /**
     * Sets product description.
     *
     * @param description Product description to set
     */
    public void setDescription( String description ) { this.description = description; }
}
//--------------------------------------------------------------------------------------------------

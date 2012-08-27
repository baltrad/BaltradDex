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

package eu.baltrad.dex.radar.model;

import java.io.Serializable;

/**
 * Class implements radar object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class Radar implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String name;
    private String wmoNumber;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public Radar() {}
    /**
     * Constructor setting field values.
     *
     * @param id Radar ID
     * @param name Radar name
     * @param wmoNumber Radar WMO number
     */
    public Radar( int id, String name, String wmoNumber ) {
        this.id = id;
        this.name = name;
        this.wmoNumber = wmoNumber;
    }
    /**
     * Constructor setting field values.
     *
     * @param name Radar name
     * @param wmoNumber Radar WMO number
     */
    public Radar( String name, String wmoNumber ) {
        this.name = name;
        this.wmoNumber = wmoNumber;
    }
    /**
     * Compares radar object with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getName().equals( ( ( Radar )o ).getName() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates radar name hash code.
     *
     * @return Radar name hash code or 0 if radar name is null
     */
    @Override
    public int hashCode() {
        return( name != null ? name.hashCode() : 0 );
    }
    /**
     * Method gets radar id.
     *
     * @return Radar id
     */
    public int getId() { return id; }
    /**
     * Method sets radar id.
     *
     * @param id Radar id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets radar name.
     *
     * @return Radar name
     */
    public String getName() { return name; }
    /**
     * Method sets radar name.
     *
     * @param name Radar name
     */
    public void setName( String name ) { this.name = name; }
    /**
     * Method gets WMO number.
     *
     * @return WMO number
     */
    public String getWmoNumber() { return wmoNumber; }
    /**
     * Method sets WMO number.
     *
     * @param wmoNumber WMO number
     */
    public void setWmoNumber( String wmoNumber ) { this.wmoNumber = wmoNumber; }
}
//--------------------------------------------------------------------------------------------------

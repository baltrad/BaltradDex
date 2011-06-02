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
 * Class implements data channel object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class Radar implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String channelName;
    private String wmoNumber;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor
     */
    public Radar() {}
    /**
     * Constructor setting field values.
     *
     * @param id Channel ID
     * @param channelName Channel name
     * @param wmoNumber Channel WMO number
     */
    public Radar( int id, String channelName, String wmoNumber ) {
        this.id = id;
        this.channelName = channelName;
        this.wmoNumber = wmoNumber;
    }
    /**
     * Constructor setting field values.
     *
     * @param channelName Channel name
     * @param wmoNumber Channel WMO number
     */
    public Radar( String channelName, String wmoNumber ) {
        this.channelName = channelName;
        this.wmoNumber = wmoNumber;
    }
    /**
     * Compares channel object with another object.
     *
     * @param o Object to compare with
     * @return True if objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object o ) {
        boolean res = false;
        if( getClass() == o.getClass() ) {
            if( this.getChannelName().equals( ( ( Radar )o ).getChannelName() ) ) {
                res = true;
            }
        }
        return res;
    }
    /**
     * Creates channel name hash code.
     *
     * @return Channel name hash code or 0 if channel name is null
     */
    @Override
    public int hashCode() {
        return( channelName != null ? channelName.hashCode() : 0 );
    }
    /**
     * Method gets data channel id.
     *
     * @return Data channel id
     */
    public int getId() { return id; }
    /**
     * Method sets data channel id.
     *
     * @param id Data channel id
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Method gets data channel name.
     *
     * @return Data channel name
     */
    public String getChannelName() { return channelName; }
    /**
     * Method sets data channel name.
     *
     * @param name Data channel name
     */
    public void setChannelName( String channelName ) { this.channelName = channelName; }
    /**
     * Method gets data channel's WMO number.
     *
     * @return Data channel's WMO number
     */
    public String getWmoNumber() { return wmoNumber; }
    /**
     * Method sets data channel's WMO number.
     *
     * @param wmoNumber Data channel's WMO number
     */
    public void setWmoNumber( String wmoNumber ) { this.wmoNumber = wmoNumber; }
}
//--------------------------------------------------------------------------------------------------
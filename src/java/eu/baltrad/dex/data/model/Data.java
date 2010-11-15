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

package eu.baltrad.dex.data.model;

/**
 * Class implements radar data object.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class Data {
//---------------------------------------------------------------------------------------- Variables
    private String uuid;
    private String path;
    private String timeStamp;
    private String radarName;
    private String date;
    private String time;
    private String type;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Data(){}
    /**
     * Constructor.
     *
     * @param uuid Data file's identity string
     * @param path Data file's absolute path
     * @param timeStamp Data file's time stamp
     * @param radarName Radar station name
     * @param date Date string
     * @param time Time string
     * @param type Radar data type
     */
    public Data( String uuid, String path, String timeStamp, String radarName, String date,
            String time, String type ) {
        this.uuid = uuid;
        this.path = path;
        this.timeStamp = timeStamp;
        this.radarName = radarName;
        this.date = date;
        this.time = time;
        this.type = type;
    }
    /**
     * Method gets file's identity string.
     *
     * @return File's identity string
     */
    public String getUuid() { return uuid; }
    /**
     * Method sets file's identity string.
     *
     * @param uuid File's identity string
     */
    public void setUuid( String uuid ) { this.uuid = uuid; }
    /**
     * Gets radar station name.
     *
     * @return Radar station name
     */
    public String getRadarName() { return radarName; }
    /**
     * Sets radar station name.
     *
     * @param radarName Radar station name
     */
    public void setRadarName( String radarName ) { this.radarName = radarName; }
    /**
     * Method gets file's path.
     *
     * @return Path to the file
     */
    public String getPath() { return path; }
    /**
     * Method sets file's path.
     *
     * @param path Path to the file
     */
    public void setPath( String path ) { this.path = path; }
    /**
     * Method gets date.
     *
     * @return Date
     */
    public String getDate() { return date; }
    /**
     * Method sets date.
     *
     * @param date Date
     */
    public void setDate( String date ) { this.date = date; }
    /**
     * Method gets time.
     *
     * @return Time
     */
    public String getTime() { return time; }
    /**
     * Method sets time.
     *
     * @param time Time
     */
    public void setTime( String time ) { this.time = time; }
    /**
     * Gets data file's time stamp.
     *
     * @return Data file's time stamp
     */
    public String getTimeStamp() { return timeStamp; }
    /**
     * Sets data file's time stamp.
     *
     * @param timeStamp Data file's time stamp
     */
    public void setTimeStamp( String timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Gets data type.
     *
     * @return Data type
     */
    public String getType() { return type; }
    /**
     * Sets data type.
     *
     * @param type Data type
     */
    public void setType( String type ) { this.type = type; }
}
//--------------------------------------------------------------------------------------------------

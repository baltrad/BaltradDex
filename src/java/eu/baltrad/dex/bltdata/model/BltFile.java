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

package eu.baltrad.dex.bltdata.model;

import java.io.Serializable;

import java.util.Date;

/**
 * Class implements Baltrad file object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class BltFile implements Serializable, Comparable<BltFile> {
//---------------------------------------------------------------------------------------- Variables
    private String uuid;
    private String path;
    private Date timeStamp;
    private Date storageTime;
    private String source;
    private String type;
    private String thumbPath;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public BltFile(){}
    /**
     * Constructor.
     *
     * @param uuid Data file's identity string
     * @param path Data file's absolute path
     * @param timeStamp Data file's time stamp
     * @param storageTime Time when the data was stored
     * @param source File's origin
     * @param type Radar data type
     * @param thumbPath Image thumb's absolute path
     */
    public BltFile( String uuid, String path, Date timeStamp, Date storageTime, String source,
            String type, String thumbPath ) {
        this.uuid = uuid;
        this.path = path;
        this.timeStamp = timeStamp;
        this.storageTime = storageTime;
        this.source = source;
        this.type = type;
        this.thumbPath = thumbPath;
    }
    /**
     * Method implementing comparable interface. Allows to sort files based on date and time.
     *
     * @param File to compare with current file
     * @return 0 if objects are equal, 1 if current file is later than compared entry, -1 otherwise
     */
    public int compareTo( BltFile bltFile ) {
        return -this.getTimeStamp().compareTo( bltFile.getTimeStamp() );
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
     * Gets source name.
     *
     * @return Source name
     */
    public String getSource() { return source; }
    /**
     * Sets source name.
     *
     * @param source Source name
     */
    public void setSource( String source ) { this.source = source; }
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
     * Gets data file's time stamp.
     *
     * @return Data file's time stamp
     */
    public Date getTimeStamp() { return timeStamp; }
    /**
     * Sets data file's time stamp.
     *
     * @param timeStamp Data file's time stamp
     */
    public void setTimeStamp( Date timeStamp ) { this.timeStamp = timeStamp; }
    /**
     * Gets data file's storage time.
     *
     * @return Data file's storage time
     */
    public Date getStorageTime() { return storageTime; }
    /**
     * Sets data file's storage time.
     *
     * @param storageTime Data file's storage time
     */
    public void setStorageTime( Date storageTime ) { this.storageTime = storageTime; }
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
    /**
     * Gets image thumb's absolute path.
     *
     * @return Image thumb's absolute path
     */
    public String getThumbPath() { return thumbPath; }
    /**
     * Sets image thumb's absolute path.
     *
     * @param thumbPath Image thumb's absolute path to set
     */
    public void setThumbPath( String thumbPath ) { this.thumbPath = thumbPath; }
}
//--------------------------------------------------------------------------------------------------

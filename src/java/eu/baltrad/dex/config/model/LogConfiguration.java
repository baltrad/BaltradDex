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

package eu.baltrad.dex.config.model;

import java.util.Date;

/**
 * Class implements log table configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.3
 * @since 0.7.3
 */
public class LogConfiguration {
//---------------------------------------------------------------------------------------- Variables
    /** Trim by number toggle */
    private boolean trimByNumber;
    /** Trim by date toggle */
    private boolean trimByDate;
    /** Record limit */
    private int recordLimit;
    /** Date limit */
    private Date dateLimit;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public LogConfiguration() {}
    /**
     * Constructor.
     *
     * @param trimByNumber Trim by number toggle
     * @param trimByDate Trim by date toggle
     * @param recordLimit Maximum number of records limit
     * @param dateLimit Maximum date limit
     */
    public LogConfiguration( boolean trimByNumber, boolean trimByDate, int recordLimit,
            Date dateLimit ) {
        this.trimByNumber = trimByNumber;
        this.trimByDate = trimByDate;
        this.recordLimit = recordLimit;
        this.dateLimit = dateLimit;
    }
    /**
     * Gets trim by number toggle.
     *
     * @return trimByNumber Trim by number toggle
     */
    public boolean getTrimByNumber() { return trimByNumber; }
    /**
     * Sets trim by number toggle.
     *
     * @param trimByNumber Trim by number toggle to set
     */
    public void setTrimByNumber( boolean trimByNumber ) { this.trimByNumber = trimByNumber; }
    /**
     * Gets trim by date toggle.
     *
     * @return trimByDate Trim by date toggle
     */
    public boolean getTrimByDate() { return trimByDate; }
    /**
     * Sets trim by date toggle.
     *
     * @param trimByDate Trim by date toggle to set
     */
    public void setTrimByDate( boolean trimByDate ) { this.trimByDate = trimByDate; }
    /**
     * Gets number of records limit.
     *
     * @return recordLimit Number of records limit
     */
    public int getRecordLimit() { return recordLimit; }
    /**
     * Sets number of records limit.
     *
     * @param recordLimit Number of records limit to set
     */
    public void setRecordLimit( int recordLimit ) { this.recordLimit = recordLimit; }
    /**
     * Gets date limit.
     *
     * @return dateLimit Date limit
     */
    public Date getDateLimit() { return dateLimit; }
    /**
     * Sets date limit.
     *
     * @param dateLimit Date limit to set
     */
    public void setDateLimit( Date dateLimit ) { this.dateLimit = dateLimit; }
}
//--------------------------------------------------------------------------------------------------

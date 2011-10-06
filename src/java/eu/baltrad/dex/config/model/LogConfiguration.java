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
    /** Trim by age toggle */
    private boolean trimByAge;
    /** Record limit */
    private int recordLimit;
    /** Age limit - number of days */
    private int maxAgeDays;
    /** Age limit - number of hours */
    private int maxAgeHours;
    /** Age limit - number of minutes */
    private int maxAgeMinutes;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public LogConfiguration() {}
    /**
     * Constructor.
     *
     * @param trimByNumber Trim by number toggle
     * @param trimByAge Trim by age toggle
     * @param recordLimit Maximum number of records limit
     * @param maxAgeDays Age limit - number of days
     * @param maxAgeHours Age limit - number of hours
     * @param maxAgeMinutes Age limit - number of minutes
     */
    public LogConfiguration( boolean trimByNumber, boolean trimByDate, int recordLimit,
            int maxAgeDays, int maxAgeHours, int maxAgeMinutes ) {
        this.trimByNumber = trimByNumber;
        this.trimByAge = trimByDate;
        this.recordLimit = recordLimit;
        this.maxAgeDays = maxAgeDays;
        this.maxAgeHours = maxAgeHours;
        this.maxAgeMinutes = maxAgeMinutes;
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
     * Gets trim by age toggle.
     *
     * @return trimByAge Trim by age toggle
     */
    public boolean getTrimByAge() { return trimByAge; }
    /**
     * Sets trim by age toggle.
     *
     * @param trimByAge Trim by age toggle to set
     */
    public void setTrimByAge( boolean trimByAge ) { this.trimByAge = trimByAge; }
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
     * Get number of days in age limit.
     *
     * @return maxAgeDays Number of days
     */
    public int getMaxAgeDays() { return maxAgeDays; }
    /**
     * Set number of days in age limit.
     *
     * @param maxAgeDays Number of days to set
     */
    public void setMaxAgeDays( int maxAgeDays ) { this.maxAgeDays = maxAgeDays; }
    /**
     * Get number of hours in age limit.
     *
     * @return maxAgeHours Number of hours
     */
    public int getMaxAgeHours() { return maxAgeHours; }
    /**
     * Set number of hours in age limit.
     *
     * @param maxAgeHours Number of hours to set
     */
    public void setMaxAgeHours( int maxAgeHours ) { this.maxAgeHours = maxAgeHours; }
    /**
     * Get number of minutes in age limit.
     *
     * @return maxAgeMinutes Number of minutes
     */
    public int getMaxAgeMinutes() { return maxAgeMinutes; }
    /**
     * Set number of minutes in age limit.
     *
     * @param maxAgeMinutes Number of minutes to set
     */
    public void setMaxAgeMinutes( int maxAgeMinutes ) { this.maxAgeMinutes = maxAgeMinutes; }
}
//--------------------------------------------------------------------------------------------------

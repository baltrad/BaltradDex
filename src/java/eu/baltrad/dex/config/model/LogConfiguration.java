/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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
*******************************************************************************/

package eu.baltrad.dex.config.model;

import java.util.Properties;

/**
 * System log configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.1
 * @since 0.7.3
 */
public class LogConfiguration {

    public static final String TRIM_BY_NUMBER = "messages.trim_by_number";
    public static final String TRIM_BY_AGE = "messages.trim_by_age";
    public static final String REC_LIMIT = "messages.rec_limit";
    public static final String MAX_DAYS = "messages.max_age_days";
    public static final String MAX_HOURS = "messages.max_age_hours";
    public static final String MAX_MINUTES = "messages.max_age_minutes";
    
    /** Trim by number toggle */
    private String msgTrimByNumber;
    /** Trim by age toggle */
    private String msgTrimByAge;
    /** Record limit */
    private String msgRecordLimit;
    /** Age limit - number of days */
    private String msgMaxAgeDays;
    /** Age limit - number of hours */
    private String msgMaxAgeHours;
    /** Age limit - number of minutes */
    private String msgMaxAgeMinutes;

    /**
     * Constructor.
     */
    public LogConfiguration() {}
    
    /**
     * Constructor.
     * @param props Properties to read
     */
    public LogConfiguration(Properties props) {
        this.msgTrimByNumber = props.getProperty(TRIM_BY_NUMBER);
        this.msgTrimByAge = props.getProperty(TRIM_BY_AGE);
        this.msgRecordLimit = props.getProperty(REC_LIMIT);
        this.msgMaxAgeDays = props.getProperty(MAX_DAYS);
        this.msgMaxAgeHours = props.getProperty(MAX_HOURS);
        this.msgMaxAgeMinutes = props.getProperty(MAX_MINUTES);
    }
    
    /**
     * Compares current object with another.
     * @param obj Object to compare with
     * @return True if tested parameters are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        LogConfiguration conf = (LogConfiguration) obj;
        return this.getMsgTrimByNumber() != null && 
                this.getMsgTrimByNumber().equals(conf.getMsgTrimByNumber()) &&
                this.getMsgTrimByAge() != null &&
                this.getMsgTrimByAge().equals(conf.getMsgTrimByAge()) &&
                this.getMsgRecordLimit() != null &&
                this.getMsgRecordLimit().equals(conf.getMsgRecordLimit()) &&
                this.getMsgMaxAgeDays() != null &&
                this.getMsgMaxAgeDays().equals(conf.getMsgMaxAgeDays()) &&
                this.getMsgMaxAgeHours() != null && 
                this.getMsgMaxAgeHours().equals(conf.getMsgMaxAgeHours()) &&
                this.getMsgMaxAgeMinutes() != null &&
                this.getMsgMaxAgeMinutes().equals(conf.getMsgMaxAgeMinutes());
    }
    
    /**
     * Generate hash code.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int prime = 7;
        int result = 1;
        result = prime * result + (this.getMsgTrimByNumber() == null ? 
                0 : this.getMsgTrimByNumber().hashCode());
        result = prime * result + (this.getMsgTrimByAge() == null ? 
                0 : this.getMsgTrimByAge().hashCode());
        result = prime * result + (this.getMsgRecordLimit() == null ? 
                0 : this.getMsgRecordLimit().hashCode());
        result = prime * result + (this.getMsgMaxAgeDays() == null ? 
                0 : this.getMsgMaxAgeDays().hashCode());
        result = prime * result + (this.getMsgMaxAgeHours() == null ? 
                0 : this.getMsgMaxAgeHours().hashCode());
        result = prime * result + (this.getMsgMaxAgeMinutes() == null ? 
                0 : this.getMsgMaxAgeMinutes().hashCode());
        return result;
    }

    /**
     * @return the msgTrimByNumber
     */
    public String getMsgTrimByNumber() {
        return msgTrimByNumber;
    }

    /**
     * @param msgTrimByNumber the msgTrimByNumber to set
     */
    public void setMsgTrimByNumber(String msgTrimByNumber) {
        this.msgTrimByNumber = msgTrimByNumber;
    }

    /**
     * @return the msgTrimByAge
     */
    public String getMsgTrimByAge() {
        return msgTrimByAge;
    }

    /**
     * @param msgTrimByAge the msgTrimByAge to set
     */
    public void setMsgTrimByAge(String msgTrimByAge) {
        this.msgTrimByAge = msgTrimByAge;
    }

    /**
     * @return the msgRecordLimit
     */
    public String getMsgRecordLimit() {
        return msgRecordLimit;
    }

    /**
     * @param msgRecordLimit the msgRecordLimit to set
     */
    public void setMsgRecordLimit(String msgRecordLimit) {
        this.msgRecordLimit = msgRecordLimit;
    }

    /**
     * @return the msgMaxAgeDays
     */
    public String getMsgMaxAgeDays() {
        return msgMaxAgeDays;
    }

    /**
     * @param msgMaxAgeDays the msgMaxAgeDays to set
     */
    public void setMsgMaxAgeDays(String msgMaxAgeDays) {
        this.msgMaxAgeDays = msgMaxAgeDays;
    }

    /**
     * @return the msgMaxAgeHours
     */
    public String getMsgMaxAgeHours() {
        return msgMaxAgeHours;
    }

    /**
     * @param msgMaxAgeHours the msgMaxAgeHours to set
     */
    public void setMsgMaxAgeHours(String msgMaxAgeHours) {
        this.msgMaxAgeHours = msgMaxAgeHours;
    }

    /**
     * @return the msgMaxAgeMinutes
     */
    public String getMsgMaxAgeMinutes() {
        return msgMaxAgeMinutes;
    }

    /**
     * @param msgMaxMinutes the msgMaxMinutes to set
     */
    public void setMsgMaxAgeMinutes(String msgMaxAgeMinutes) {
        this.msgMaxAgeMinutes = msgMaxAgeMinutes;
    }
    
}

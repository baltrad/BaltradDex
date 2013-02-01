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
 * Delivery registry configuration object.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.1
 * @since 0.7.3
 */
public class RegistryConfiguration {

    public static final String TRIM_BY_NUMBER = "registry.trim_by_number";
    public static final String TRIM_BY_AGE = "registry.trim_by_age";
    public static final String REC_LIMIT = "registry.rec_limit";
    public static final String MAX_DAYS = "registry.max_age_days";
    public static final String MAX_HOURS = "registry.max_age_hours";
    public static final String MAX_MINUTES = "registry.max_age_minutes";
    
    /** Trim by number toggle */
    private String regTrimByNumber;
    /** Trim by age toggle */
    private String regTrimByAge;
    /** Record limit */
    private String regRecordLimit;
    /** Age limit - number of days */
    private String regMaxAgeDays;
    /** Age limit - number of hours */
    private String regMaxAgeHours;
    /** Age limit - number of minutes */
    private String regMaxAgeMinutes;

    /**
     * Constructor.
     */
    public RegistryConfiguration() {}
    
    /**
     * Constructor.
     * @param props Properties to read
     */
    public RegistryConfiguration(Properties props) {
        this.regTrimByNumber = props.getProperty(TRIM_BY_NUMBER);
        this.regTrimByAge = props.getProperty(TRIM_BY_AGE);
        this.regRecordLimit = props.getProperty(REC_LIMIT);
        this.regMaxAgeDays = props.getProperty(MAX_DAYS);
        this.regMaxAgeHours = props.getProperty(MAX_HOURS);
        this.regMaxAgeMinutes = props.getProperty(MAX_MINUTES);
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
        RegistryConfiguration conf = (RegistryConfiguration) obj;
        return this.getRegTrimByNumber() != null && 
                this.getRegTrimByNumber().equals(conf.getRegTrimByNumber()) &&
                this.getRegTrimByAge() != null &&
                this.getRegTrimByAge().equals(conf.getRegTrimByAge()) &&
                this.getRegRecordLimit() != null &&
                this.getRegRecordLimit().equals(conf.getRegRecordLimit()) &&
                this.getRegMaxAgeDays() != null &&
                this.getRegMaxAgeDays().equals(conf.getRegMaxAgeDays()) &&
                this.getRegMaxAgeHours() != null && 
                this.getRegMaxAgeHours().equals(conf.getRegMaxAgeHours()) &&
                this.getRegMaxAgeMinutes() != null &&
                this.getRegMaxAgeMinutes().equals(conf.getRegMaxAgeMinutes());
    }
    
    /**
     * Generate hash code.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int prime = 7;
        int result = 1;
        result = prime * result + (this.getRegTrimByNumber() == null ? 
                0 : this.getRegTrimByNumber().hashCode());
        result = prime * result + (this.getRegTrimByAge() == null ? 
                0 : this.getRegTrimByAge().hashCode());
        result = prime * result + (this.getRegRecordLimit() == null ? 
                0 : this.getRegRecordLimit().hashCode());
        result = prime * result + (this.getRegMaxAgeDays() == null ? 
                0 : this.getRegMaxAgeDays().hashCode());
        result = prime * result + (this.getRegMaxAgeHours() == null ? 
                0 : this.getRegMaxAgeHours().hashCode());
        result = prime * result + (this.getRegMaxAgeMinutes() == null ? 
                0 : this.getRegMaxAgeMinutes().hashCode());
        return result;
    }
    
    /**
     * @return the regTrimByNumber
     */
    public String getRegTrimByNumber() {
        return regTrimByNumber;
    }

    /**
     * @param regTrimByNumber the regTrimByNumber to set
     */
    public void setRegTrimByNumber(String regTrimByNumber) {
        this.regTrimByNumber = regTrimByNumber;
    }

    /**
     * @return the regTrimByAge
     */
    public String getRegTrimByAge() {
        return regTrimByAge;
    }

    /**
     * @param regTrimByAge the regTrimByAge to set
     */
    public void setRegTrimByAge(String regTrimByAge) {
        this.regTrimByAge = regTrimByAge;
    }

    /**
     * @return the regRecordLimit
     */
    public String getRegRecordLimit() {
        return regRecordLimit;
    }

    /**
     * @param regRecordLimit the regRecordLimit to set
     */
    public void setRegRecordLimit(String regRecordLimit) {
        this.regRecordLimit = regRecordLimit;
    }

    /**
     * @return the regMaxAgeDays
     */
    public String getRegMaxAgeDays() {
        return regMaxAgeDays;
    }

    /**
     * @param regMaxAgeDays the regMaxAgeDays to set
     */
    public void setRegMaxAgeDays(String regMaxAgeDays) {
        this.regMaxAgeDays = regMaxAgeDays;
    }

    /**
     * @return the regMaxAgeHours
     */
    public String getRegMaxAgeHours() {
        return regMaxAgeHours;
    }

    /**
     * @param regMaxAgeHours the regMaxAgeHours to set
     */
    public void setRegMaxAgeHours(String regMaxAgeHours) {
        this.regMaxAgeHours = regMaxAgeHours;
    }

    /**
     * @return the regMaxAgeMinutes
     */
    public String getRegMaxAgeMinutes() {
        return regMaxAgeMinutes;
    }

    /**
     * @param regMaxAgeMinutes the regMaxAgeMinutes to set
     */
    public void setRegMaxAgeMinutes(String regMaxAgeMinutes) {
        this.regMaxAgeMinutes = regMaxAgeMinutes;
    }
    
}

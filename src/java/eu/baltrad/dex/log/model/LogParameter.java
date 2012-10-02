/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.log.model;

/**
 * Log entry parameters.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class LogParameter {
    
    private String logger;
    private String flag;
    private String startDate;
    private String startHour;
    private String startMinutes;
    private String startSeconds;
    private String endDate;
    private String endHour;
    private String endMinutes;
    private String endSeconds;
    private String phrase;

    public LogParameter() {}
    
    /**
     * @return the logger
     */
    public String getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return the phrase
     */
    public String getPhrase() {
        return phrase;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the startHour
     */
    public String getStartHour() {
        return startHour;
    }

    /**
     * @param startHour the startHour to set
     */
    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    /**
     * @return the startMinutes
     */
    public String getStartMinutes() {
        return startMinutes;
    }

    /**
     * @param startMinutes the startMinutes to set
     */
    public void setStartMinutes(String startMinutes) {
        this.startMinutes = startMinutes;
    }

    /**
     * @return the startSeconds
     */
    public String getStartSeconds() {
        return startSeconds;
    }

    /**
     * @param startSeconds the startSeconds to set
     */
    public void setStartSeconds(String startSeconds) {
        this.startSeconds = startSeconds;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the endHour
     */
    public String getEndHour() {
        return endHour;
    }

    /**
     * @param endHour the endHour to set
     */
    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    /**
     * @return the endMinutes
     */
    public String getEndMinutes() {
        return endMinutes;
    }

    /**
     * @param endMinutes the endMinutes to set
     */
    public void setEndMinutes(String endMinutes) {
        this.endMinutes = endMinutes;
    }

    /**
     * @return the endSeconds
     */
    public String getEndSeconds() {
        return endSeconds;
    }

    /**
     * @param endSeconds the endSeconds to set
     */
    public void setEndSeconds(String endSeconds) {
        this.endSeconds = endSeconds;
    }
    
    /**
     * @param phrase the phrase to set
     */
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }
    
}

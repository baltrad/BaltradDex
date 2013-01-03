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
    private String level;
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
     * @return Message level
     */
    public String getLevel() {
        return level;
    }

    /**
     * @param level Level to set
     */
    public void setLevel(String level) {
        this.level = level;
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
        if (startHour.trim().length() == 1) { 
            this.startHour = "0" + startHour;
        } else {
            this.startHour = startHour;
        }    
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
        if (startMinutes.trim().length() == 1) { 
            this.startMinutes = "0" + startMinutes;
        } else {
            this.startMinutes = startMinutes;
        }
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
        if (startSeconds.trim().length() == 1) { 
            this.startSeconds = "0" + startSeconds;
        } else {
            this.startSeconds = startSeconds;
        }
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
        if (endHour.trim().length() == 1) { 
            this.endHour = "0" + endHour;
        } else {
            this.endHour = endHour;
        }
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
        if (endMinutes.trim().length() == 1) { 
            this.endMinutes = "0" + endMinutes;
        } else {
            this.endMinutes = endMinutes;
        }
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
        if (endSeconds.trim().length() == 1) { 
            this.endSeconds = "0" + endSeconds;
        } else {
            this.endSeconds = endSeconds;
        }
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

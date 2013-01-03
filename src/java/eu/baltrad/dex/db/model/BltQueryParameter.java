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

package eu.baltrad.dex.db.model;

/**
 * File query parameter.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class BltQueryParameter {
    
    /** Sort in descending order */
    public static final String SORT_DESC = "DESC";
    /** Sort in ascending order */
    public static final String SORT_ASC = "ASC";
    /** No sorting */
    public static final String SORT_NONE = "";
    
    private String radar;
    private String fileObject;
    private String startDate;
    private String startHour;
    private String startMinute;
    private String startSecond;
    private String endDate;
    private String endHour;
    private String endMinute;
    private String endSecond;
    private String offset;
    private String limit;
    private String sortByDate;
    private String sortByTime;
    private String sortBySource;
    private String sortByObject;            

    /**
     * Default constructor.
     */
    public BltQueryParameter() {}
    
    /**
     * @return the radar
     */
    public String getRadar() {
        return radar;
    }

    /**
     * @param radar the radar to set
     */
    public void setRadar(String radar) {
        this.radar = radar;
    }

    /**
     * @return the fileObject
     */
    public String getFileObject() {
        return fileObject;
    }

    /**
     * @param fileObject the fileObject to set
     */
    public void setFileObject(String fileObject) {
        this.fileObject = fileObject;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
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
     * @return the startMinute
     */
    public String getStartMinute() {
        return startMinute;
    }

    /**
     * @param startMinute the startMinute to set
     */
    public void setStartMinute(String startMinute) {
        this.startMinute = startMinute;
    }

    /**
     * @return the startSecond
     */
    public String getStartSecond() {
        return startSecond;
    }

    /**
     * @param startSecond the startSecond to set
     */
    public void setStartSecond(String startSecond) {
        this.startSecond = startSecond;
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
     * @return the endMinute
     */
    public String getEndMinute() {
        return endMinute;
    }

    /**
     * @param endMinute the endMinute to set
     */
    public void setEndMinute(String endMinute) {
        this.endMinute = endMinute;
    }

    /**
     * @return the endSecond
     */
    public String getEndSecond() {
        return endSecond;
    }

    /**
     * @param endSecond the endSecond to set
     */
    public void setEndSecond(String endSecond) {
        this.endSecond = endSecond;
    }

    /**
     * @return the offset
     */
    public String getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(String offset) {
        this.offset = offset;
    }

    /**
     * @return the limit
     */
    public String getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(String limit) {
        this.limit = limit;
    }

    /**
     * @return the sortByDate
     */
    public String getSortByDate() {
        return sortByDate;
    }

    /**
     * @param sortByDate the sortByDate to set
     */
    public void setSortByDate(String sortByDate) {
        this.sortByDate = sortByDate;
    }

    /**
     * @return the sortByTime
     */
    public String getSortByTime() {
        return sortByTime;
    }

    /**
     * @param sortByTime the sortByTime to set
     */
    public void setSortByTime(String sortByTime) {
        this.sortByTime = sortByTime;
    }

    /**
     * @return the sortBySource
     */
    public String getSortBySource() {
        return sortBySource;
    }

    /**
     * @param sortBySource the sortBySource to set
     */
    public void setSortBySource(String sortBySource) {
        this.sortBySource = sortBySource;
    }

    /**
     * @return the sortByObject
     */
    public String getSortByObject() {
        return sortByObject;
    }

    /**
     * @param sortByObject the sortByObject to set
     */
    public void setSortByObject(String sortByObject) {
        this.sortByObject = sortByObject;
    }
    
}

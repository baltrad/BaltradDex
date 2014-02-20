/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.status.model;

import java.util.Date;

/**
 * Implements status object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class Status {
    
    private int id;
    private long downloads;
    private long uploads;
    private long uploadFailures;
    private long subscriptionStart;
    private boolean subscriptionActive;
    private String nodeName;
    private String dataSource;
    private String subscriptionType;
    private Date subscriptionStartDate;
    
    /**
     * Constructor.
     * @param id Record id
     * @param nodeName Node name 
     * @param dataSource Data source name
     * @param subscriptionType Subscription type
     * @param subscriptionStart Subscription start time
     * @param subscriptionActive Subscription active toggle
     * @param downloads Number of downloaded files
     * @param uploads Number of uploaded files
     * @param uploadFailures Number of upload failures 
     */
    public Status(int id, String nodeName, String dataSource, 
            String subscriptionType, long subscriptionStart, 
            boolean subscriptionActive, long downloads, long uploads, 
            long uploadFailures) {
        this.id = id;
        this.nodeName = nodeName;
        this.dataSource = dataSource;
        this.subscriptionType = subscriptionType;
        this.subscriptionStart = subscriptionStart;
        this.subscriptionStartDate = new Date(subscriptionStart);
        this.subscriptionActive = subscriptionActive;
        this.downloads = downloads;
        this.uploads = uploads;
        this.uploadFailures = uploadFailures;
    }
    
    /**
     * Constructor.
     * @param downloads Number of downloaded files
     * @param uploads Number of uploaded files
     * @param uploadFailures Number of upload failures 
     */
    public Status(long downloads, long uploads, long uploadFailures) {
        this.downloads = downloads;
        this.uploads = uploads;
        this.uploadFailures = uploadFailures;
    }
    
    /**
     * Increment downloads counter.
     */
    public void incrementDownloads() {
        this.downloads++;
    }
    
    /**
     * Increment uploads counter.
     */
    public void incrementUploads() {
        this.uploads++;
    }
    
    /**
     * Increment upload failures counter.
     */
    public void incrementUploadFailures() {
        this.uploadFailures++;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @return the downloads
     */
    public long getDownloads() {
        return downloads;
    }

    /**
     * @param downloads the downloads to set
     */
    public void setDownloads(long downloads) {
        this.downloads = downloads;
    }

    /**
     * @return the uploads
     */
    public long getUploads() {
        return uploads;
    }

    /**
     * @param uploads the uploads to set
     */
    public void setUploads(long uploads) {
        this.uploads = uploads;
    }

    /**
     * @return the uploadFailures
     */
    public long getUploadFailures() {
        return uploadFailures;
    }

    /**
     * @param uploadFailures the uploadFailures to set
     */
    public void setUploadFailures(long uploadFailures) {
        this.uploadFailures = uploadFailures;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName the nodeName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the dataSource
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    
    /**
     * @return the subscriptionType
     */
    public String getSubscriptionType() {
        return subscriptionType;
    }

    /**
     * @param subscriptionType the subscriptionType to set
     */
    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    /**
     * @return the subscriptionStart
     */
    public long getSubscriptionStart() {
        return subscriptionStart;
    }

    /**
     * @param subscriptionStart the subscriptionStart to set
     */
    public void setSubscriptionStart(long subscriptionStart) {
        this.subscriptionStart = subscriptionStart;
    }

    /**
     * @return the subscriptionActive
     */
    public boolean getSubscriptionActive() {
        return subscriptionActive;
    }

    /**
     * @param subscriptionActive the subscriptionActive to set
     */
    public void setSubscriptionActive(boolean subscriptionActive) {
        this.subscriptionActive = subscriptionActive;
    }

    /**
     * @return the subscriptionStartDate
     */
    public Date getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    /**
     * @param subscriptionStartDate the subscriptionStartDate to set
     */
    public void setSubscriptionStartDate(Date subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }
    
}

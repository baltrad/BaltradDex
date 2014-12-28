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
 * System configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.0
 * @since 1.0.1
 */
public class AppConfiguration {

    public static final String NODE_NAME = "node.name";
    public static final String NODE_ADDRESS = "node.address";
    public static final String NODE_TYPE = "node.type"; 
    public static final String ORG_NAME = "organization.name";
    public static final String ORG_UNIT = "organization.unit";
    public static final String LOCALITY = "organization.locality";
    public static final String STATE = "organization.state";
    public static final String COUNTRY_CODE = "organization.country_code";
    public static final String TIME_ZONE = "time.zone";
    public static final String ADMIN_EMAIL = "admin.email";
    public static final String WORK_DIR = "work.directory";
    public static final String IMAGES_DIR = "images.directory";
    public static final String THUMBS_DIR = "thumbnails.directory";
    public static final String KEYSTORE_DIR = "keystore.directory";
    public static final String VERSION = "software.version";
    public static final String SO_TIMEOUT = "socket.timeout";
    public static final String CONN_TIMEOUT = "connection.timeout";
    
    private String nodeName;
    private String nodeAddress;
    private String nodeType;
    private String orgName;
    private String orgUnit;
    private String locality;
    private String state;
    private String countryCode;
    private String timeZone;
    private String adminEmail;
    private String workDir;
    private String imagesDir;
    private String thumbsDir;
    private String keystoreDir;
    private String version;
    private String soTimeout;
    private String connTimeout;
    
    /**
     * Default constructor.
     */
    public AppConfiguration() {}
    
    /**
     * Constructor.
     * @param props Properties to read 
     */
    public AppConfiguration(Properties props) {
        this.nodeName = props.getProperty(NODE_NAME);
        this.nodeAddress = props.getProperty(NODE_ADDRESS);
        this.nodeType = props.getProperty(NODE_TYPE);
        this.orgName = props.getProperty(ORG_NAME);
        this.orgUnit = props.getProperty(ORG_UNIT);
        this.locality = props.getProperty(LOCALITY);
        this.state = props.getProperty(STATE);
        this.countryCode = props.getProperty(COUNTRY_CODE);
        this.timeZone = props.getProperty(TIME_ZONE);
        this.adminEmail = props.getProperty(ADMIN_EMAIL);
        this.workDir = props.getProperty(WORK_DIR);
        this.imagesDir = props.getProperty(IMAGES_DIR);
        this.thumbsDir = props.getProperty(THUMBS_DIR);
        this.keystoreDir = props.getProperty(KEYSTORE_DIR);
        this.version = props.getProperty(VERSION);
        this.soTimeout = props.getProperty(SO_TIMEOUT);
        this.connTimeout = props.getProperty(CONN_TIMEOUT);
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
        AppConfiguration conf = (AppConfiguration) obj;
        return this.getNodeName() != null && 
                this.getNodeName().equals(conf.getNodeName()) &&
                this.getNodeAddress() != null &&
                this.getNodeAddress().equals(conf.getNodeAddress()) &&
                this.getNodeType() != null &&
                this.getNodeType().equals(conf.getNodeType()) &&
                this.getOrgName() != null &&
                this.getOrgName().equals(conf.getOrgName()) &&
                this.getOrgUnit() != null &&
                this.getOrgUnit().equals(conf.getOrgUnit()) &&
                this.getLocality() != null &&
                this.getLocality().equals(conf.getLocality()) &&
                this.getState() != null &&
                this.getState().equals(conf.getState()) &&
                this.getCountryCode() != null &&
                this.getCountryCode().equals(conf.getCountryCode()) &&
                this.getTimeZone() != null &&
                this.getTimeZone().equals(conf.getTimeZone()) &&
                this.getAdminEmail() != null &&
                this.getAdminEmail().equals(conf.getAdminEmail()) &&
                this.getWorkDir() != null &&
                this.getWorkDir().equals(conf.getWorkDir());
    }
    
    /**
     * Generate hash code.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int prime = 7;
        int result = 1;
        result = prime * result + ((this.getNodeName() == null) ? 
                0 : this.getNodeName().hashCode());
        result = prime * result + ((this.getNodeAddress() == null) ? 
                0 : this.getNodeAddress().hashCode());
        result = prime * result + ((this.getNodeType() == null) ? 
                0 : this.getNodeType().hashCode());
        result = prime * result + ((this.getOrgName() == null) ? 
                0 : this.getOrgName().hashCode());
        result = prime * result + ((this.getOrgUnit() == null) ? 
                0 : this.getOrgUnit().hashCode());
        result = prime * result + ((this.getLocality() == null) ? 
                0 : this.getLocality().hashCode());
        result = prime * result + ((this.getState() == null) ? 
                0 : this.getState().hashCode());
        result = prime * result + ((this.getCountryCode() == null) ? 
                0 : this.getCountryCode().hashCode());
        result = prime * result + ((this.getTimeZone() == null) ? 
                0 : this.getTimeZone().hashCode());
        result = prime * result + ((this.getAdminEmail() == null) ? 
                0 : this.getAdminEmail().hashCode());
        result = prime * result + ((this.getWorkDir() == null) ? 
                0 : this.getWorkDir().hashCode());
        result = prime * result + ((this.getImagesDir() == null) ? 
                0 : this.getImagesDir().hashCode());
        result = prime * result + ((this.getThumbsDir() == null) ? 
                0 : this.getThumbsDir().hashCode());
        result = prime * result + ((this.getKeystoreDir() == null) ? 
                0 : this.getKeystoreDir().hashCode());
        result = prime * result + ((this.getVersion() == null) ? 
                0 : this.getVersion().hashCode());
        result = prime * result + ((this.getSoTimeout() == null) ? 
                0 : this.getSoTimeout().hashCode());
        result = prime * result + ((this.getConnTimeout() == null) ? 
                0 : this.getConnTimeout().hashCode());
        return result;
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
     * @return the nodeAddress
     */
    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * @param nodeAddress the nodeAddress to set
     */
    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType the nodeType to set
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName the orgName to set
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return the orgUnit
     */
    public String getOrgUnit() {
        return orgUnit;
    }

    /**
     * @param orgUnit the orgUnit to set
     */
    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    /**
     * @return the locality
     */
    public String getLocality() {
        return locality;
    }

    /**
     * @param locality the locality to set
     */
    public void setLocality(String locality) {
        this.locality = locality;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return the timeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone the timeZone to set
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * @return the adminEmail
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     * @param adminEmail the adminEmail to set
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * @return the workDir
     */
    public String getWorkDir() {
        return workDir;
    }

    /**
     * @param workDir the workDir to set
     */
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    /**
     * @return the imagesDir
     */
    public String getImagesDir() {
        return imagesDir;
    }

    /**
     * @param imagesDir the imagesDir to set
     */
    public void setImagesDir(String imagesDir) {
        this.imagesDir = imagesDir;
    }

    /**
     * @return the thumbsDir
     */
    public String getThumbsDir() {
        return thumbsDir;
    }

    /**
     * @param thumbsDir the thumbsDir to set
     */
    public void setThumbsDir(String thumbsDir) {
        this.thumbsDir = thumbsDir;
    }

    /**
     * @return the keystoreDir
     */
    public String getKeystoreDir() {
        return keystoreDir;
    }

    /**
     * @param keystoreDir the keystoreDir to set
     */
    public void setKeystoreDir(String keystoreDir) {
        this.keystoreDir = keystoreDir;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the soTimeout
     */
    public String getSoTimeout() {
        return soTimeout;
    }

    /**
     * @param soTimeout the soTimeout to set
     */
    public void setSoTimeout(String soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * @return the connTimeout
     */
    public String getConnTimeout() {
        return connTimeout;
    }

    /**
     * @param connTimeout the connTimeout to set
     */
    public void setConnTimeout(String connTimeout) {
        this.connTimeout = connTimeout;
    }
}


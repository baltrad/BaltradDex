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

package eu.baltrad.dex.config.model;

/**
 * Class implements system configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.0
 */
public class AppConfiguration {

    /** Node name, e.g. baltrad.imgw.pl */
    private String nodeName;
    /** Fully qualified node address */
    private String nodeAddress;
    /** Node type - primary / backup */
    private String nodeType;
    /** Software version */
    private String version;
    /** Socket timeout */
    private int soTimeout;
    /** Connection timeout */
    private int connTimeout;
    /** Organization's name */
    private String orgName;
    /** Organization's unit */
    private String orgUnit;
    /** Locality / city */
    private String locality;
    /** State */
    private String state;
    /** Country code*/
    private String countryCode;
    /** Host time zone */
    private String timeZone;
    /** Work directory */
    private String workDir;
    /** Image storage directory */
    private String imagesDir;
    /** Thumbnails storage directory */
    private String thumbsDir;
    /** Node administrator's email */
    private String email;
    /** Keystore directory */
    private String keystoreDir;

    /**
     * Constructor
     *
     * @param nodeName Node name
     * @param nodeAddress Fully qualified node address
     * @param nodeType Node type
     * @param version Software version
     * @param soTimeout Socket timeout
     * @param connTimeout Connection timeout
     * @param workDir Work directory
     * @param imagesDir Images storage directory
     * @param thumbsDir Thumbnails storage directory
     * @param orgName Organization's name
     * @param orgUnit Organization's unit
     * @param locality Locality / city
     * @param state State / country
     * @param countryCode Country code
     * @param timeZone Time zone
     * @param email Node admin's email
     * @param keystorePass Keystore password  
     * @param keystoreDir Keystore directory
     */
    public AppConfiguration( String nodeName, String nodeAddress, 
            String nodeType, String version, int soTimeout, int connTimeout, 
            String workDir, String imagesDir, String thumbsDir, 
            String orgName, String orgUnit, String locality, String state,
            String countryCode, String timeZone, String email, 
            String keystoreDir) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
        this.nodeType = nodeType;
        this.version = version;
        this.soTimeout = soTimeout;
        this.connTimeout = connTimeout;
        this.workDir = workDir;
        this.imagesDir = imagesDir;
        this.thumbsDir = thumbsDir;
        this.orgName = orgName;
        this.orgUnit = orgUnit;
        this.locality = locality;
        this.state = state;
        this.countryCode = countryCode;
        this.timeZone = timeZone;
        this.email = email;
        this.keystoreDir = keystoreDir;
    }
    /**
     * Used to compare application configuration objects.
     * 
     * @param obj Application configuration object
     * @return True if all fields are equal, false otherwise 
     */
    @Override
    public boolean equals( Object obj ) {
        if( this == obj ) return true;
        if( obj instanceof AppConfiguration ) {
            AppConfiguration appConf = ( AppConfiguration )obj;
            return appConf.getNodeName().equals( this.getNodeName() ) &&
                appConf.getNodeAddress().equals( this.getNodeAddress() ) &&    
                appConf.getNodeType().equals( this.getNodeType() ) &&
                appConf.getVersion().equals( this.getVersion() ) &&
                appConf.getSoTimeout() == this.getSoTimeout() &&
                appConf.getConnTimeout() == this.getConnTimeout() &&
                appConf.getWorkDir().equals( this.getWorkDir() ) &&
                appConf.getImagesDir().equals( this.getImagesDir() ) &&
                appConf.getThumbsDir().equals( this.getThumbsDir() ) &&
                appConf.getOrgName().equals( this.getOrgName() ) &&
                appConf.getOrgUnit().equals( this.getOrgUnit() ) &&    
                appConf.getLocality().equals( this.getLocality() ) &&
                appConf.getState().equals( this.getState() ) &&
                appConf.getCountryCode().equals( this.getCountryCode() ) &&
                appConf.getTimeZone().equals( this.getTimeZone() ) &&
                appConf.getEmail().equals( this.getEmail() ) &&
                appConf.getKeystoreDir().equals(this.getKeystoreDir());
        } else {
            return false;
        }
    }
    /**
     * Gets node name.
     *
     * @return node name
     */
    public String getNodeName() { return nodeName; }
    /**
     * Sets short node name.
     *
     * @param nodeName Short node name
     */
    public void setNodeName( String nodeName ) { this.nodeName = nodeName; }
    /**
     * Gets node address.
     * 
     * @return nodeAddress Fully qualified node address
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets node address.
     * 
     * @param nodeAddress Fully qualified node address
     */
    public void setNodeAddress(String nodeAddress) { 
        this.nodeAddress = nodeAddress; 
    }
    /**
     * Gets node type.
     *
     * @return Type of a node
     */
    public String getNodeType() { return nodeType; }
    /**
     * Sets node type.
     *
     * @param nodeType Type of a node
     */
    public void setNodeType( String nodeType ) { this.nodeType = nodeType; }
    /**
     * Gets software version
     *
     * @return version Software version
     */
    public String getVersion() { return version; }
    /**
     * Sets software version
     *
     * @param version Software version to set
     */
    public void setVersion( String version ) { this.version = version; }
    /**
     * Gets socket timeout
     *
     * @return soTimeout Socket timeout
     */
    public int getSoTimeout() { return soTimeout; }
    /**
     * Sets socket timeout
     *
     * @param soTimeout Socket timeout to set
     */
    public void setSoTimeout( int soTimeout ) { this.soTimeout = soTimeout; }
    /**
     * Gets connection timeout
     *
     * @return connTimeout Connection timeout
     */
    public int getConnTimeout() { return connTimeout; }
    /**
     * Sets connection timeout
     *
     * @param connTimeout Connection timeout to set
     */
    public void setConnTimeout( int connTimeout ) { 
        this.connTimeout = connTimeout; 
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
     * Gets local time zone.
     *
     * @return Local time zone
     */
    public String getTimeZone() { return timeZone; }
    /**
     * Sets local time zone.
     *
     * @param timeZone the timeZone to set
     */
    public void setTimeZone( String timeZone ) { this.timeZone = timeZone; }
    /**
     * Gets work directory.
     *
     * @return Work directory
     */
    public String getWorkDir() { return workDir; }
    /**
     * Sets work directory.
     *
     * @param workDir Work directory
     */
    public void setWorkDir( String workDir ) { this.workDir = workDir; }
    /**
     * Gets images storage directory
     *
     * @return imagesDir Images storage directory
     */
    public String getImagesDir() { return imagesDir; }
    /**
     * Sets images storage directory
     *
     * @param imagesDir Images storage directory to set
     */
    public void setImagesDir( String imagesDir ) { this.imagesDir = imagesDir; }
    /**
     * Gets thumbnails storage directory
     *
     * @return thumbsDir Thumbnails storage directory
     */
    public String getThumbsDir() { return thumbsDir; }
    /**
     * Sets thumbnails storage directory
     *
     * @param thumbsDir Thumbnails storage directory to set
     */
    public void setThumbsDir( String thumbsDir ) { this.thumbsDir = thumbsDir; }
    /**
     * Gets node administrator's email.
     *
     * @return Node administrator's email
     */
    public String getEmail() { return email; }
    /**
     * Sets node administrator's email.
     * 
     * @param email Node administrator's email
     */
    public void setEmail( String email ) { this.email = email; }
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
}


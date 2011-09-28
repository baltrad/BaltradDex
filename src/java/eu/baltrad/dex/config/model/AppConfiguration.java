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

import eu.baltrad.dex.util.NodeAddress;

/**
 * Class implements system configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class AppConfiguration extends NodeAddress {
//---------------------------------------------------------------------------------------- Variables
    /** Node name, e.g. baltrad.imgw.pl */
    private String nodeName;
    /** Node type - primary / backup */
    private String nodeType;
    /** Software version */
    private String version;
    /** Socket timeout */
    private int soTimeout;
    /** Connection timeout */
    private int connTimeout;
    /** Organization's name */
    private String organization;
    /** Organization's address */
    private String address;
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
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor
     *
     * @param nodeName Node name
     * @param nodeType Node type
     * @param version Software version
     * @param scheme Communication scheme
     * @param hostAddress Host address
     * @param port Port number
     * @param appCtx Application context
     * @param entryAddress Entry point address
     * @param soTimeout Socket timeout
     * @param connTimeout Connection timeout
     * @param workDir Work directory
     * @param imagesDir Images storage directory
     * @param thumbsDir Thumbnails storage directory
     * @param organization Organization's name
     * @param address Organization's address
     * @param timeZone Time zone
     * @param email Node admin's email
     */
    public AppConfiguration( String nodeName, String nodeType, String version, String scheme,
            String hostAddress, int port, String appCtx, String entryAddress, int soTimeout,
            int connTimeout, String workDir, String imagesDir, String thumbsDir, String organization,
            String address, String timeZone, String email ) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.version = version;
        this.scheme = scheme;
        this.hostAddress = hostAddress;
        this.port = port;
        this.appCtx = appCtx;
        this.entryAddress = entryAddress;
        this.soTimeout = soTimeout;
        this.connTimeout = connTimeout;
        this.workDir = workDir;
        this.imagesDir = imagesDir;
        this.thumbsDir = thumbsDir;
        this.organization = organization;
        this.address= address;
        this.timeZone = timeZone;
        this.email = email;
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
    public void setConnTimeout( int connTimeout ) { this.connTimeout = connTimeout; }
    /**
     * Gets organization name.
     *
     * @return Organization name
     */
    public String getOrganization() { return organization; }
    /**
     * Sets organization name.
     *
     * @param organization Organization name
     */
    public void setOrganization( String organization ) { this.organization = organization; }
    /**
     * Gets organization's address.
     *
     * @return Organization's address
     */
    public String getAddress() { return address; }
    /**
     * Sets organization's address.
     *
     * @param organization Organization's address
     */
    public void setAddress( String address ) { this.address = address; }
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
}
//--------------------------------------------------------------------------------------------------

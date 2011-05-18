/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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
 * Class implements system configuration object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class Configuration {
//---------------------------------------------------------------------------------------- Variables
    // Record ID
    private int id;
    // Node name, e.g. baltrad.imgw.pl
    private String nodeName;
    // Node type - primary / backup
    private String nodeType;
    // Short node address, e.g. baltrad.imgw.pl (without port name and other stuff)
    private String shortAddress;
    // Full node address, e.g. http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm
    private String fullAddress;
    // Port number
    private String portNumber;
    // Host organization name
    private String orgName;
    // Host organization address
    private String orgAddress;
    // Host time zone
    private String timeZone;
    // Temporary directory
    private String tempDir;
    // Administrator's email
    private String adminEmail;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Configuration() {}
    /**
     * Constructor sets class fields values.
     *
     * @param id Configuration ID
     * @param nodeName Short node name
     * @param nodeType Node type (primary or backup node)
     * @param shortAddress Node's short address
     * @param portNumber Port number
     * @param orgName Host organization name
     * @param orgAddress Host organization address
     * @param timeZone Local time zone
     * @param tempDir Temporary directory
     * @param adminEmail Node administrator's email
     */
    public Configuration( int id, String nodeName, String nodeType, String shortAddress, String portNumber,
            String orgName, String orgAddress, String timeZone, String tempDir,
            String adminEmail ) {
        this.id = id;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.timeZone = timeZone;
        this.tempDir = tempDir;
        this.adminEmail = adminEmail;
    }
    /**
     * Constructor sets class fields values.
     *
     * @param nodeName Short node name
     * @param nodeType Node type (primary or backup node)
     * @param shortAddress Node's short address
     * @param portNumber Port number
     * @param orgName Host organization name
     * @param orgAddress Host organization address
     * @param timeZone Local time zone
     * @param tempDir Temporary directory
     * @param adminEmail Node administrator's email
     */
    public Configuration( String nodeName, String nodeType, String shortAddress, String portNumber,
            String orgName, String orgAddress, String timeZone, String tempDir,
            String adminEmail ) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
        this.orgName = orgName;
        this.orgAddress = orgAddress;
        this.timeZone = timeZone;
        this.tempDir = tempDir;
        this.adminEmail = adminEmail;
    }
    /**
     * Gets record ID.
     *
     * @return Record's ID in the database
     */
    public int getId() { return id; }
    /**
     * Sets record's ID.
     *
     * @param id Record's ID in the database
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets short node name.
     *
     * @return Short node name
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
     * Gets node's short address.
     *
     * @return Node's short address
     */
    public String getShortAddress() { return shortAddress; }
    /**
     * Sets node's short address.
     *
     * @param shortAddress Node's short address
     */
    public void setShortAddress( String shortAddress ) { this.shortAddress = shortAddress; }
    /**
     * Gets node's full address.
     *
     * @return Node's full address
     */
    public String getFullAddress() { return fullAddress; }
    /**
     * Sets node's full address.
     *
     * @param fullAddress Node's full address
     */
    public void setFullAddress( String fullAddress ) { this.fullAddress = fullAddress; }
    /**
     * Gets port number.
     *
     * @return Port number
     */
    public String getPortNumber() { return portNumber; }
    /**
     * Sets port number.
     *
     * @param portNumber Port number to set
     */
    public void setPortNumber( String portNumber ) { this.portNumber = portNumber; }
    /**
     * Gets host organization name.
     *
     * @return Host organization name
     */
    public String getOrgName() { return orgName; }
    /**
     * Sets host organization name.
     *
     * @param orgName Host organization name
     */
    public void setOrgName( String orgName ) { this.orgName = orgName; }
    /**
     * Gets host organization address.
     *
     * @return Host organization address
     */
    public String getOrgAddress() { return orgAddress; }
    /**
     * Gets host organization address.
     *
     * @param orgAddress Host organization address
     */
    public void setOrgAddress( String orgAddress ) { this.orgAddress = orgAddress; }
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
     * Gets temporary directory.
     *
     * @return Temporary directory
     */
    public String getTempDir() { return tempDir; }
    /**
     * Sets temporary directory.
     *
     * @param tempDir Temporary directory
     */
    public void setTempDir( String tempDir ) { this.tempDir = tempDir; }
    /**
     * Gets node administrator's email.
     *
     * @return Node administrator's email
     */
    public String getAdminEmail() { return adminEmail; }
    /**
     * Sets node administrator's email.
     * 
     * @param adminEmail Node administrator's email
     */
    public void setAdminEmail( String adminEmail ) { this.adminEmail = adminEmail; }
}
//--------------------------------------------------------------------------------------------------

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
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class Configuration {
//---------------------------------------------------------------------------------------- Variables
    private int id;
    private String nodeName;
    private String nodeType;
    private String nodeAddress;
    private String orgName;
    private String orgAddress;
    private String timeZone;
    private String tempDir;
    private String adminEmail;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public Configuration() {}
    /**
     * Constructor sets class fields values.
     *
     * @param nodeName Short node name
     * @param nodeType Node type (primary or backup node)
     * @param nodeAddress Full address of the node pointing to frame dispatcher
     * @param orgName Host organization name
     * @param orgAddress Host organization address
     * @param timeZone Local time zone
     * @param tempDir Temporary directory
     * @param adminEmail Node administrator's email
     */
    public Configuration( String nodeName, String nodeType, String nodeAddress,
            String orgName, String orgAddress, String timeZone, String tempDir,
            String adminEmail ) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.nodeAddress = nodeAddress;
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
     * Gets address of a node.
     *
     * @return Address of a node
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets address of a node.
     *
     * @param nodeAddress Address of a node
     */
    public void setNodeAddress( String nodeAddress ) { this.nodeAddress = nodeAddress; }
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

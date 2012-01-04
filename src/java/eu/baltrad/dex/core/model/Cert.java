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

package eu.baltrad.dex.core.model;

/**
 * Encapsulates certificate object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.7.8
 * @since 0.7.8
 */
public class Cert {
//---------------------------------------------------------------------------------------- Variables    
    /** Certificate ID */
    private int id;
    /** Certificate alias */
    private String alias;
    /** Node address */
    private String nodeAddress;
    /** Certificate file's path */
    private String filePath;
    /** Trusted certificate toggle */
    private boolean trusted;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Default constructor.
     */
    public Cert() {}
    /**
     * Constructor.
     * 
     * @param id Record ID
     * @param alias Certificate alias
     * @param nodeAddress Issuer node's address
     * @param filePath Certificate file's path
     * @param trusted Trusted certificate toggle
     */
    public Cert(int id, String alias, String nodeAddress, String filePath, boolean trusted) {
        this.id = id;
        this.alias = alias;
        this.nodeAddress = nodeAddress;
        this.filePath = filePath;
        this.trusted = trusted;
    }
    /**
     * Constructor.
     * 
     * @param alias Certificate alias
     * @param nodeAddress Issuer node's address
     * @param filePath Certificate file's path
     * @param trusted Trusted certificate toggle
     */
    public Cert(String alias, String nodeAddress, String filePath, boolean trusted) {
        this.alias = alias;
        this.nodeAddress = nodeAddress;
        this.filePath = filePath;
        this.trusted = trusted;
    }
    /**
     * Gets certificate ID.
     *
     * @return Certificate ID
     */
    public int getId() { return id; }
    /**
     * Sets certificate ID.
     *
     * @param id Certificate ID
     */
    public void setId( int id ) { this.id = id; }
    /**
     * Gets certificate alias. 
     * 
     * @return Certificate alias
     */
    public String getAlias() { return alias; }
    /**
     * Sets certificate alias.
     * 
     * @param certAlias Sets certificate alias 
     */
    public void setAlias(String alias) { this.alias = alias; }
    /**
     * Gets issuer node's address.
     * 
     * @return Issuer node's address
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets issuer node's address.
     * 
     * @param nodeAddress Issuer node's address
     */
    public void setNodeAddress(String nodeAddress) { this.nodeAddress = nodeAddress; }
    /**
     * Gets certificate file's path.
     * 
     * @return Certificate file's path 
     */
    public String getFilePath() { return filePath; }
    /**
     * Sets certificate file's path.
     * 
     * @param certFilePath Certificate file's path
     */
    public void setFilePath(String filePath) { this.filePath = filePath; }
    /**
     * Gets trusted certificate toggle.
     * 
     * @return Trusted certificate toggle
     */
    public boolean getTrusted() { return trusted; }
    /**
     * Sets trusted certificate toggle.
     * 
     * @param trusted Trusted certificate toggle
     */
    public void setTrusted(boolean trusted) { this.trusted = trusted; }
}
//--------------------------------------------------------------------------------------------------

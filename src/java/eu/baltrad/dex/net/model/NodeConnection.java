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

package eu.baltrad.dex.net.model;

import java.io.Serializable;

/**
 * Class encapsulating node connection object. Connection object is validated and sent
 * to the remote node where it serves as a basis for authentication.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class NodeConnection implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    /** Connection id */
    private int id;
    /** Node name */
    private String nodeName;
    /** Node address */
    private String nodeAddress;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public NodeConnection() {}
    /**
     * Constructor.
     * 
     * @param nodeAddress Node address 
     */
    public NodeConnection(String nodeAddress) { this.nodeAddress = nodeAddress; }
    /**
     * Constructor.
     * 
     * @param nodeName Node name
     * @param nodeAddress Node address
     */
    public NodeConnection(String nodeName, String nodeAddress) {
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
    }
    /**
     * Constructor.
     * 
     * @param id Record ID
     * @param nodeName Node name
     * @param nodeAddress Node address
     */
    public NodeConnection(int id, String nodeName, String nodeAddress) {
        this.id = id;
        this.nodeName = nodeName;
        this.nodeAddress = nodeAddress;
    }
    /**
     * Gets connection ID.
     *
     * @return Connection ID
     */
    public int getId() { return id; }
    /**
     * Sets connection ID.
     *
     * @param id Connection ID
     */
    public void setId(int id) { this.id = id; }
    /**
     * Gets node name.
     *
     * @return Node name
     */
    public String getNodeName() { return nodeName; }
    /**
     * Sets node name.
     *
     * @param nodeName Node name
     */
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    /**
     * Gets node address.
     *
     * @return Node address
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets nodeAddress.
     *
     * @param nodeAddress Node address
     */
    public void setNodeAddress(String nodeAddress) { this.nodeAddress = nodeAddress; }
}
//--------------------------------------------------------------------------------------------------

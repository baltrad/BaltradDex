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

package eu.baltrad.dex.core.model;

import java.io.Serializable;

/**
 * Class encapsulating node connection object. Connection object is validated and sent
 * to the remote node where it serves as a basis for authentication.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.6
 * @since 0.1.6
 */
public class NodeConnection implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    // Connection id
    private int id;
    // Connection name
    private String connectionName;
    // Node address
    private String nodeAddress;
    // User's name on the remote node
    private String userName;
    // User's password on the remote node
    private String password;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public NodeConnection() {}
    /**
     * Constructor sets node address, user name and password field values.
     *
     * @param nodeAddress Node address
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( String nodeAddress, String userName, String password ) {
        this.nodeAddress = nodeAddress;
        this.userName = userName;
        this.password = password;
    }
    /**
     * Constructor sets all field values.
     *
     * @param connectionName Connection name
     * @param nodeAddress Node address
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( String connectionName, String nodeAddress, String userName,
            String password ) {
        this.connectionName = connectionName;
        this.nodeAddress = nodeAddress;
        this.userName = userName;
        this.password = password;
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
    public void setId( int id ) { this.id = id; }
    /**
     * Gets connection name.
     *
     * @return Connection name
     */
    public String getConnectionName() { return connectionName; }
    /**
     * Sets connection name.
     *
     * @param connectionName Connection name
     */
    public void setConnectionName( String connectionName ) { this.connectionName = connectionName; }
    /**
     * Gets node address.
     *
     * @return Node address
     */
    public String getNodeAddress() { return nodeAddress; }
    /**
     * Sets node address.
     *
     * @param nodeAddress Node address
     */
    public void setNodeAddress( String nodeAddress ) { this.nodeAddress = nodeAddress; }
    /**
     * Gets user name.
     *
     * @return User name
     */
    public String getUserName() { return userName; }
    /**
     * Sets user name.
     *
     * @param userName User name
     */
    public void setUserName( String userName ) { this.userName = userName; }
    /**
     * Gets user's password.
     *
     * @return User's password
     */
    public String getPassword() { return password; }
    /**
     * Sets user's password.
     *
     * @param password User's password
     */
    public void setPassword( String password ) { this.password = password; }
}
//--------------------------------------------------------------------------------------------------

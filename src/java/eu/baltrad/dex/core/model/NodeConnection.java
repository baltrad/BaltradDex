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

import eu.baltrad.dex.util.NodeAddress;
import java.io.Serializable;

/**
 * Class encapsulating node connection object. Connection object is validated and sent
 * to the remote node where it serves as a basis for authentication.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.1.6
 * @since 0.1.6
 */
public class NodeConnection extends NodeAddress implements Serializable {
//---------------------------------------------------------------------------------------- Variables
    // Connection id
    private int id;
    // Connection name
    private String connectionName;
    // Full address of node's entry point
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
     * @param scheme Communication scheme identifier
     * @param hostAddress Host address
     * @param port Port number
     * @param appCtx Application context
     * @param entryAddress Entry point address
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( String scheme, String hostAddress, int port, String appCtx,
            String entryAddress, String userName, String password ) {
        this.scheme = scheme;
        this.hostAddress = hostAddress;
        this.port = port;
        this.appCtx = appCtx;
        this.entryAddress = entryAddress;
        this.userName = userName;
        this.password = password;
    }
    /**
     * Constructor sets all field values.
     *
     * @param id Node connection ID
     * @param connectionName Connection name
     * @param scheme Communication scheme
     * @param hostAddress Host address
     * @param port Port number
     * @param appCtx Application context
     * @param entryAddress Entry point address
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( int id, String connectionName, String scheme, String hostAddress,
            int port, String appCtx, String entryAddress, String userName, String password ) {
        this.id = id;
        this.connectionName = connectionName;
        this.scheme = scheme;
        this.hostAddress = hostAddress;
        this.port = port;
        this.appCtx = appCtx;
        this.entryAddress = entryAddress;
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

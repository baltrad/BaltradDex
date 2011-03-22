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
//---------------------------------------------------------------------------------------- Constants
    // HTTP protocol prefix
    public static final String HTTP_PREFIX = "http://";
    // Application context
    public static final String APP_CONTEXT = "BaltradDex";
    // Relative entry point address
    public static final String ENTRY_ADDRESS = "dispatch.htm";
    // Address separator
    public static final String ADDRESS_SEPARATOR = "/";
    // Port separator
    public static final String PORT_SEPARATOR = ":";
//---------------------------------------------------------------------------------------- Variables
    // Connection id
    private int id;
    // Connection name
    private String connectionName;
    // Short node address, e.g. baltrad.imgw.pl (without port name and protocol)
    private String shortAddress;
    // Full node address, e.g. http://baltrad.imgw.pl:8084/BaltradDex/dispatch.htm
    private String fullAddress;
    // Port number
    private String portNumber;
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
     * @param shortAddress Node's short address
     * @param portNumber Port number
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( String shortAddress, String portNumber, String userName,
            String password ) {
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
        this.userName = userName;
        this.password = password;
    }
    /**
     * Constructor sets all field values.
     *
     * @param id Node connection ID
     * @param connectionName Connection name
     * @param shortAddress Node's short address
     * @param portNumber Port number
     * @param userName User's name
     * @param password User's password
     */
    public NodeConnection( int id, String connectionName, String shortAddress, String portNumber,
            String userName, String password ) {
        this.id = id;
        this.connectionName = connectionName;
        this.shortAddress = shortAddress;
        this.portNumber = portNumber;
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

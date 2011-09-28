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

package eu.baltrad.dex.util;

/**
 * Implements node address manager.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 0.7.3
 * @since 0.7.3
 */
public class NodeAddress {
//---------------------------------------------------------------------------------------- Constants
    /** Scheme separator */
    public static final String SCHEME_SEPARATOR = "://";
    /** Port separator */
    public static final String PORT_SEPARATOR = ":";
    /** Address separator */
    public static final String ADDR_SEPARATOR = "/";
//---------------------------------------------------------------------------------------- Variables
    /** Communication scheme */
    protected String scheme;
    /** Host address, eg. localhost, 127.0.0.1 */
    protected String hostAddress;
    /** Port number */
    protected int port;
    /** Application context */
    protected String appCtx;
    /** Entry point address */
    protected String entryAddress;
    /** Complete node addres */
    private String nodeAddress;
//------------------------------------------------------------------------------------------ Methods
    /** 
     * Gets communication scheme
     *
     * @return scheme Scheme identifier
     */
    public String getScheme() { return scheme; }
    /** 
     * Sets communication scheme
     *
     * @param scheme Scheme identifier to set
     */
    public void setScheme( String scheme ) { this.scheme = scheme; }
    /** 
     * Gets host address
     *
     * @return hostAddress Host address
     */
    public String getHostAddress() { return hostAddress; }
    /** 
     * Sets host address
     *
     * @param hostAddress Host address to set
     */
    public void setHostAddress( String hostAddress ) { this.hostAddress = hostAddress; }
    /** 
     * Gets port number
     *
     * @return port Port number
     */
    public int getPort() { return port; }
    /** 
     * Sets port number
     *
     * @param port Port number to set
     */
    public void setPort( int port ) { this.port = port; }
    /** 
     * Gets application context
     *
     * @return appCtx Application context
     */
    public String getAppCtx() { return appCtx; }
    /** 
     * Sets application context
     *
     * @param appCtx Application context to set
     */
    public void setAppCtx( String appCtx ) { this.appCtx = appCtx; }
    /** 
     * Gets entry point address
     *
     * @return entryAddress Entry point address
     */
    public String getEntryAddress() { return entryAddress; }
    /** 
     * Sets entry point address
     *
     * @param entryAddress Entry point address to set
     */
    public void setEntryAddress( String entryAddress ) { this.entryAddress = entryAddress; }
    /**
     * Gets servlet context path.
     * 
     * @return Servlet context path
     */
    public String getServletPath() {
        return ADDR_SEPARATOR + getAppCtx() + ADDR_SEPARATOR + getEntryAddress();
    }
    /**
     * Gets complete node address
     *
     * @return nodeAddress Complete node address
     */
    public String getNodeAddress() {
        if( scheme == null || scheme.isEmpty() || hostAddress == null || hostAddress.isEmpty()
                || port == 0 || appCtx == null || appCtx.isEmpty() || entryAddress == null
                || entryAddress.isEmpty() ) {
            return new String( "" );
        } else {
            nodeAddress = scheme + SCHEME_SEPARATOR + hostAddress + PORT_SEPARATOR +
                Integer.toString( port ) + ADDR_SEPARATOR + appCtx + ADDR_SEPARATOR + entryAddress;
            return nodeAddress;
        }
    }
}
//--------------------------------------------------------------------------------------------------

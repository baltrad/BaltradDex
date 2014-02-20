/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.net.request;

/**
 * Http servlet request wrapper.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public interface INodeRequest {

    static final String[] HEADERS = {"Content-Type", "Content-MD5", "Date"};
    static final String NODE_NAME_HDR = "Node-Name";
    static final String AUTH_HDR = "Authorization";
    static final String HDR_SEPARATOR = ":";
    static final String PROTOCOL_VERSION_HDR = "DEX-Protocol-Version";
    
    /**
     * @return Message to be signed
     */
    public String getMessage();
    
    /**
     * @return Signature string
     */
    public String getSignature();
    
    /**
     * @return User identity string
     */
    public String getUser();
    
    /**
     * @return Name of the requesting node
     */
    public String getNodeName();
    
    /**
     * @return the protocol version. If nothing found, empty string should be returned. null should never be returned.
     */
    public String getProtocolVersion();
}

/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.net.response;

/**
 * Http servlet response wrapper.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public interface INodeResponse {

    static final String NODE_NAME_HDR = "Node-Name";
    
    static final String PROTOCOL_VERSION_HDR = "DEX-Protocol-Version";

    static final String NODE_PROTOCOL_VERSION_HDR = "DEX-Node-Protocol-Version";

    /**
     * Sets node name.
     * @param nodeName Node name to set
     */
    public void setNodeName(String nodeName);
    
    /**
     * @param version the protocol version that this response is defined in.
     */
    public void setProtocolVersion(String version);
    
    /**
     * @param version the protocol version that this node is configured to support.
     */
    public void setSupportedProtocolVersion(String version);
    
    /**
     * @param uuid when storing a file this uuid is the result
     */
    public void setStoredUUID(String uuid);
    
    /**
     * @param message if some extra information should be provided to the user use this header
     */
    public void setMessage(String message);
}

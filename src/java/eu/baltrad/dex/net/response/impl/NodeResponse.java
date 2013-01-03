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

package eu.baltrad.dex.net.response.impl;

import eu.baltrad.dex.net.response.INodeResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Http servlet response wrapper.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.1.1
 * @since 1.1.1
 */
public class NodeResponse extends HttpServletResponseWrapper 
                                                    implements INodeResponse {
    /**
     * Constructor.
     * @param response Http servlet response 
     */
    public NodeResponse(HttpServletResponse response) {
        super(response);
    } 
    
    /**
     * Sets node name.
     * @param nodeName Node name to set
     */
    public void setNodeName(String nodeName) {
        this.addHeader(NODE_NAME_HDR, nodeName);
    }
   
}

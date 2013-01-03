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

package eu.baltrad.dex.net.model.impl;

import eu.baltrad.dex.net.model.INode;

/**
 * Baltrad node object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class Node implements INode {
    
    private int id;
    /** Baltrad node name */
    private String name;
    /** Baltrad node address */
    private String address;
    
    /**
     * Constructor.
     * @param name Node name
     * @param address Node address
     */
    public Node(String name, String address) {
        this.name = name;
        this.address = address;
    }
    
    /**
     * Constructor.
     * @param id Node id
     * @param name Node name
     * @param address Node address
     */
    public Node(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
    
    /**
     * Gets node ID.
     * @return Node ID
     */
    public int getId() { return id; }
    
    /**
     * Sets node id.
     * @param id Node id
     */
    public void setId(int id) { this.id = id; }
    
    /**
     * Gets node name.
     * @return Node name
     */
    public String getName() { return name; }
    
    /**
     * Sets node name.
     * @param name Node name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Get node address.
     * @return Node address
     */
    public String getAddress() { return address; }

    /**
     * Set node address
     * @param address Node address to set 
     */
    public void setAddress(String address) { this.address = address; }
}

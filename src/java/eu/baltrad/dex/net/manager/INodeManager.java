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

package eu.baltrad.dex.net.manager;

import eu.baltrad.dex.net.model.impl.Node;
import java.util.List;

/**
 * Node manager interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public interface INodeManager {
    
    public List<Node> load();
    
    public List<Node> loadOperators();
    
    public List<Node> loadPeers();
    
    public Node load(int id);
    
    public Node load(String name);
    
    public Node loadByUser(int id);
    
    public int store(Node node);
    
    public int store(int userId, int nodeId);
    
    public int update(int userId, int nodeId);
    
    public int delete(int id);
    
}

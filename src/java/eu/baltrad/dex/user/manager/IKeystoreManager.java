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

package eu.baltrad.dex.user.manager;

import eu.baltrad.dex.user.model.Key;

import java.util.List;

/**
 * Keystore manager interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.6.0
 * @since 1.6.0
 */
public interface IKeystoreManager {
    
    public List<Key> load();
    
    public Key load(int id);
    
    public Key load(String name);
    
    public int store(Key key) throws Exception;
    
    public int update(Key key) throws Exception;
    
    public int delete(int id);
    
}

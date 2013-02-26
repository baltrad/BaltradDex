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

package eu.baltrad.dex.user.manager;

import eu.baltrad.dex.user.model.User;

import java.util.List;

/**
 * User manager interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public interface IUserManager {
    
    public List<User> load();
    
    public User load(int id);
    
    public User load(String name);
    
    public List<String> loadPeers(); 
    
    public List<User> loadUsers();
    
    public List<User> loadOperators();
    
    public int store(User user) throws Exception;
    
    public void update(User user) throws Exception;
    
    public int updatePassword(int id, String password) throws Exception;
    
    public int delete(int id);
    
}

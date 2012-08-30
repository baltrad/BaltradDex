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

package eu.baltrad.dex.registry.model;

import java.util.List;

/**
 * Data delivery registry interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public interface IRegistryManager {
    
    public long count();
    
    public List<RegistryEntry> load();
    
    public RegistryEntry load(int userId, String uuid);
    
    public List<RegistryEntry> load(int offset, int limit);
    
    public int store(RegistryEntry entry);
    
    public int storeNoId(RegistryEntry entry);
    
    public int delete();
    
    public int delete(int id);
    
    public void setTrimmer(int limit);
    
    public void setTrimmer(int days, int hours, int minutes);
    
    public void removeTrimmer(String name);
}

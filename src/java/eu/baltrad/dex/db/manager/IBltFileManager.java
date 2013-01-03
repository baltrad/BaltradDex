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

package eu.baltrad.dex.db.manager;

import eu.baltrad.dex.db.model.BltFile;

import eu.baltrad.bdb.db.DatabaseError;
import eu.baltrad.bdb.db.FileEntry;

import eu.baltrad.beast.db.IFilter;
import eu.baltrad.dex.db.model.BltQueryParameter;

import java.util.List;

/**
 * File manager interface.
 * 
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.1.1
 */
public interface IBltFileManager {
    
    public long count(String dsName) throws DatabaseError;
    
    public long count(BltQueryParameter param) throws DatabaseError;
    
    public BltFile load(String uuid) throws DatabaseError;
    
    public List<BltFile> load(BltQueryParameter param) 
                throws DatabaseError;
    
    public List<BltFile> load(String dsName, int offset, int limit) 
                    throws DatabaseError;
    
    public IFilter loadFilter(String name);
    
    public List<String> loadDistinctRadarStations() throws DatabaseError;
    
}

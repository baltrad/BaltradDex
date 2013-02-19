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

package eu.baltrad.dex.datasource.manager;

import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.user.model.Account;

import java.util.List;

/**
 * Data source manager interface. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public interface IDataSourceManager {
    
    public DataSource load(int id);
    
    public List<DataSource> load(String type);
    
    public DataSource load(String name, String type);
    
    public List<DataSource> loadByUser(int id);
    
    public int store(DataSource dataSource);
    
    public int update(DataSource dataSource);
    
    public int delete(int dataSourceId); 
    
    public List<Radar> loadRadar(int dataSourceId);
    
    public int storeRadar(int dataSourceId, int radarId);
    
    public int deleteRadar(int dataSourceId);
    
    public List<FileObject> loadFileObject(int dataSourceId);
    
    public int storeFileObject(int dataSourceId, int fileObjectId);
    
    public int deleteFileObject(int dataSopurceId);
    
    public List<Account> loadUser(int dataSourceId);
    
    public int storeUser(int dataSourceId, int userId);
    
    public int deleteUser(int dataSourceId);
    
    public int loadFilterId(int dataSourceId);
    
    public int storeFilter(int dataSourceId, int filterId);
    
    public int deleteFilter(int dataSourceId);
}

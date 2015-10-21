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

package eu.baltrad.dex.registry.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.baltrad.dex.registry.model.impl.RegistryEntry;

/**
 * Row mapper for delivery registry entry object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class RegistryEntryMapper 
            implements RowMapper<RegistryEntry> {
   /**
    * Maps records to result set. 
    * @param rs Result set 
    * @param rowNum Row number
    * @return Registry entry object
    * @throws SQLException 
    */
    public RegistryEntry mapRow(ResultSet rs, int rowNum) 
            throws SQLException {
        return new RegistryEntry(
            rs.getInt("id"),
            rs.getLong("time_stamp"),
            rs.getString("type"),    
            rs.getString("uuid"),
            rs.getString("user_name"),    
            rs.getBoolean("status"));    
    }
    
}

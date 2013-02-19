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

package eu.baltrad.dex.datasource.model.mapper;

import eu.baltrad.dex.datasource.model.DataSource;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data source object mapper.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class DataSourceMapper implements ParameterizedRowMapper<DataSource> {
   /**
    * Maps records to result set. 
    * @param rs Result set 
    * @param rowNum Row number
    * @return Data source object
    * @throws SQLException 
    */
    public DataSource mapRow(ResultSet rs, int rowNum) 
            throws SQLException {
        DataSource ds = new DataSource();
        ds.setId(rs.getInt("id"));
        ds.setName(rs.getString("name"));
        ds.setType(rs.getString("type"));
        ds.setDescription(rs.getString("description"));
        return ds;
    }
}

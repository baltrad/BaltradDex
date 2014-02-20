/*******************************************************************************
*
* Copyright (C) 2009-2014 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.status.model.mapper;

import eu.baltrad.dex.status.model.Status;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

/**
 * Node status object mapper.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class StatusMapper implements ParameterizedRowMapper<Status> {
    /**
     * Maps records to result set. 
     * @param rs Result set 
     * @param rowNum Row number
     * @return Status object
     * @throws SQLException 
     */
    public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Status(
                rs.getInt("id"),
                rs.getString("node_name"),
                rs.getString("data_source"),
                rs.getString("type"),
                rs.getLong("start"),
                rs.getBoolean("active"),
                rs.getLong("downloads"),
                rs.getLong("uploads"),
                rs.getLong("upload_failures"));
    }
    
}

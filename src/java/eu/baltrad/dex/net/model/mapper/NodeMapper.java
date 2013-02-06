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

package eu.baltrad.dex.net.model.mapper;

import eu.baltrad.dex.net.model.impl.Node;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Node object mapper.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class NodeMapper implements ParameterizedRowMapper<Node> {
   /**
    * Maps records to result set. 
    * @param rs Result set 
    * @param rowNum Row number
    * @return User object
    * @throws SQLException 
    */
    public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Node(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"));
    } 
}
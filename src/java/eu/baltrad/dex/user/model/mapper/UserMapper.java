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

package eu.baltrad.dex.user.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import eu.baltrad.dex.user.model.User;

/**
 * Row mapper for user account object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class UserMapper implements RowMapper<User> { 
   /**
    * Maps records to result set. 
    * @param rs Result set 
    * @param rowNum Row number
    * @return User object
    * @throws SQLException 
    */
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("role"),
                rs.getString("password"),
                rs.getString("org_name"),
                rs.getString("org_unit"),
                rs.getString("locality"),
                rs.getString("state"),
                rs.getString("country_code"),
                rs.getString("node_address"));
        user.setRedirectedAddress(rs.getString("redirected_address"));
        return user;
    }
    
}

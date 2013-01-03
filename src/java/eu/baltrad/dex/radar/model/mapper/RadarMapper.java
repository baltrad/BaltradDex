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

package eu.baltrad.dex.radar.model.mapper;

import eu.baltrad.dex.radar.model.Radar;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Row mapper for radar object.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 1.2.2
 */
public class RadarMapper implements ParameterizedRowMapper<Radar> {
    /**
    * Maps records to result set. 
    * @param rs Result set 
    * @param rowNum Row number
    * @return User object
    * @throws SQLException 
    */
    public Radar mapRow(ResultSet rs, int rowNum) throws SQLException {
        Radar radar = new Radar(); 
        radar.setId(rs.getInt("id"));
        radar.setCountryCode(rs.getString("country_code"));
        radar.setCenterCode(rs.getString("center_code"));
        radar.setCenterNumber(rs.getInt("center_number"));
        radar.setRadarPlace(rs.getString("rad_place"));
        radar.setRadarCode(rs.getString("rad_code"));
        radar.setRadarWmo(rs.getString("rad_wmo"));
        return radar;
    }
}

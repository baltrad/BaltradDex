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

package eu.baltrad.dex.radar.manager.impl;

import eu.baltrad.dex.radar.manager.IRadarManager;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.radar.model.mapper.RadarMapper;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Radar manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class RadarManager implements IRadarManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private RadarMapper mapper;
    
    /**
     * Constructor.
     */
    public RadarManager() {
        this.mapper = new RadarMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all radars.
     * @return List of all available radars
     */
    public List<Radar> load() {
        String sql = "SELECT * FROM dex_radars";
	List<Radar> radars = jdbcTemplate.query(sql, mapper);
	return radars;
    }
    
    /**
     * Load radar with a given id.
     * @param id Radar id
     * @return Radar with a given id
     */
    public Radar load(int id) {
        String sql = "SELECT * FROM dex_radars WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load radar for a given place name.
     * @param place Radar location
     * @return Radar with a given location
     */
    public Radar load(String place) {
        String sql = "SELECT * FROM dex_radars WHERE rad_place = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, place);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Stores radar object in database.
     * @param radar Radar object to store
     * @return Number of records stored. 
     */
    public int store(Radar radar) {
        return jdbcTemplate.update("INSERT INTO dex_radars " +
            "(country_code, center_code, center_number, rad_place, rad_code, " +
                "rad_wmo ) VALUES (?, ?, ?, ?, ?, ?)",
            radar.getCountryCode(),
            radar.getCenterCode(),
            radar.getCenterNumber(),
            radar.getRadarPlace(),
            radar.getRadarCode(),
            radar.getRadarWmo());
    }
    
    /**
     * Delete radar with a given id.
     * @param id Radar id 
     * @return Number of records deleted.
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_radars WHERE id = ?", id);
    }
    
}

    
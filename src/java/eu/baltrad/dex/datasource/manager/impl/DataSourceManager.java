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

package eu.baltrad.dex.datasource.manager.impl;

import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.mapper.DataSourceMapper;
import eu.baltrad.dex.datasource.model.mapper.FileObjectMapper;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.user.model.Account;
import eu.baltrad.dex.user.model.mapper.AccountMapper;
import eu.baltrad.dex.radar.model.mapper.RadarMapper;

import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.List;

/**
 * Data source manager. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class DataSourceManager implements IDataSourceManager {
    
    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mappers */
    private DataSourceMapper dataSourceMapper;
    private RadarMapper radarMapper;
    private FileObjectMapper fileObjectMapper;
    private AccountMapper accountMapper;
    
    /**
     * Constructor.
     */
    public DataSourceManager() {
        this.dataSourceMapper = new DataSourceMapper();
        this.radarMapper = new RadarMapper();
        this.fileObjectMapper = new FileObjectMapper();
        this.accountMapper = new AccountMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all data sources.
     * @return List of data sources
     */
    public List<DataSource> load() {
	String sql = "SELECT * FROM dex_data_sources";
	List<DataSource> dataSources = jdbcTemplate.query(sql, dataSourceMapper);
	return dataSources;
    }
    
    /**
     * Load data source by id.
     * @param id Record id
     * @return Data source with a given id
     */
    public DataSource load(int id) {
        String sql = "SELECT * FROM dex_data_sources WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, dataSourceMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load data source by name.
     * @param id Data source name
     * @return Data source with a given name
     */
    public DataSource load(String name) {
        String sql = "SELECT * FROM dex_data_sources WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, dataSourceMapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load data sources by user.
     * @param id User id
     * @return List of data sources for a given user.
     */
    public List<DataSource> loadByUser(int id) {
        String sql = "SELECT * FROM dex_data_sources WHERE id IN (SELECT " +
                "data_source_id FROM dex_data_source_users WHERE user_id = ?)";
        return jdbcTemplate.query(sql, dataSourceMapper, id);
    }
    
    /**
     * Store data source object in the db. 
     * @param dataSource Data source to store
     * @return Auto-generated record id
     */
    public int store(DataSource dataSource) {
        final String sql = "INSERT INTO dex_data_sources " +
            "(name, description) VALUES (?,?)";
        final DataSource ds = dataSource;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.getJdbcOperations().update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setString(1, ds.getName());
                    ps.setString(2, ds.getDescription());
                    return ps;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }
    
    /**
     * Update data source object in the db. 
     * @param dataSource Data source to store
     * @return Number of records updated
     */
    public int update(DataSource dataSource) {
        return jdbcTemplate.update("UPDATE dex_data_sources SET name = ?," + 
                " description = ? WHERE id = ?",
            dataSource.getName(),
            dataSource.getDescription(),
            dataSource.getId());
    } 
    
    /**
     * Delete data source with a given id.
     * @param id Data source id
     * @return Number of deleted records
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_data_sources WHERE id = ?", 
                id);
    }
    
    /**
     * Get list of radars for a given data source id.
     * @param id Data source id
     * @return List of radars
     */
    public List<Radar> loadRadar(int id) {
        String sql = "SELECT * FROM dex_radars WHERE id IN (SELECT radar_id " +
                "FROM dex_data_source_radars WHERE data_source_id = ?)";
        try {
            return jdbcTemplate.query(sql, radarMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Save data source radar parameter.
     * @param dataSourceId Data source id
     * @param radarId Radar id
     * @return Number of stored records
     */
    public int storeRadar(int dataSourceId, int radarId) {
        return jdbcTemplate.update("INSERT INTO dex_data_source_radars " +
            "(data_source_id, radar_id) VALUES (?,?)",
            dataSourceId, radarId);
    }
    
    /**
     * Delete radar parameter for a given data source id.
     * @param dataSourceId Data source id
     * @return Number of deleted records
     */
    public int deleteRadar(int dataSourceId) {
        return jdbcTemplate.update("DELETE FROM dex_data_source_radars " +
                "WHERE data_source_id = ?", dataSourceId);
    }
    
    /**
     * Get list of file objects for a given data source id.
     * @param DataSourceId Data source id
     * @return List of file objects
     */
    public List<FileObject> loadFileObject(int DataSourceId) {
        String sql = "SELECT * FROM dex_file_objects WHERE id IN (SELECT " +
                "file_object_id FROM dex_data_source_file_objects WHERE " + 
                "data_source_id = ?)";
        try {
            return jdbcTemplate.query(sql, fileObjectMapper, DataSourceId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Save data source file object parameter.
     * @param dataSourceId Data source id
     * @param fileObjectId File object id
     * @return Number of stored records
     */
    public int storeFileObject(int dataSourceId, int fileObjectId) {
        return jdbcTemplate.update("INSERT INTO dex_data_source_file_objects " +
            "(data_source_id, file_object_id) VALUES (?,?)",
            dataSourceId, fileObjectId);
    }
    
    /**
     * Delete file object parameter for a given data source id.
     * @param dataSourceId Data source id
     * @return Number of deleted records
     */
    public int deleteFileObject(int dataSourceId) {
        return jdbcTemplate.update("DELETE FROM dex_data_source_file_objects " +
                "WHERE data_source_id = ?", dataSourceId);
    }
    
    /**
     * Get list of users for a given data source id.
     * @param DataSourceId Data source id
     * @return List of users
     */
    public List<Account> loadUser(int DataSourceId) {
        String sql = "SELECT dex_users.*, dex_roles.name AS role_name, " +
                "dex_nodes.address AS node_address FROM dex_users, " + 
                "dex_roles, dex_nodes, dex_users_roles, dex_users_nodes " + 
                "WHERE dex_users_roles.user_id = dex_users.id AND " + 
                "dex_users_roles.role_id = dex_roles.id AND " + 
                "dex_users_nodes.user_id = dex_users.id AND " + 
                "dex_users_nodes.node_id = dex_nodes.id AND " + 
                "dex_users.id IN (SELECT user_id FROM dex_data_source_users " +
                "WHERE data_source_id = ?)";
        try {
            return jdbcTemplate.query(sql, accountMapper, DataSourceId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Save data source user parameter.
     * @param dataSourceId Data source id
     * @param userId User id
     * @return Number of stored records
     */
    public int storeUser(int dataSourceId, int userId) {
        return jdbcTemplate.update("INSERT INTO dex_data_source_users " +
            "(data_source_id, user_id) VALUES (?,?)",
            dataSourceId, userId);
    }
    
    /**
     * Delete user parameter for a given data source id.
     * @param dataSourceId Data source id
     * @return Number of deleted records
     */
    public int deleteUser(int dataSourceId) {
        return jdbcTemplate.update("DELETE FROM dex_data_source_users " +
                "WHERE data_source_id = ?", dataSourceId);
    }
    
    /**
     * Load filter by data source id.
     * @param dataSourceId Data source id 
     * @return Filter id
     */
    public int loadFilterId(int dataSourceId) {
        String sql = "SELECT filter_id FROM dex_data_source_filters WHERE " + 
                "data_source_id = ?";
        try {
            return jdbcTemplate.queryForInt(sql, new Object[] {dataSourceId});
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    /**
     * Save data source filter parameter.
     * @param dataSourceId Data source id
     * @param filterId Filter id
     * @return Number of stored records
     */
    public int storeFilter(int dataSourceId, int filterId) {
        return jdbcTemplate.update("INSERT INTO dex_data_source_filters " +
            "(data_source_id, filter_id) VALUES (?,?)",
            dataSourceId, filterId);
    }
    
    /**
     * Delete user parameter for a given data source id.
     * @param dataSourceId Data source id
     * @return Number of deleted records
     */
    public int deleteFilter(int dataSourceId) {
        return jdbcTemplate.update("DELETE FROM dex_data_source_filters " +
                "WHERE data_source_id = ?", dataSourceId);
    }
    
}

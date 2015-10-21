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

package eu.baltrad.dex.datasource.manager.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import eu.baltrad.beast.db.AttributeFilter;
import eu.baltrad.beast.db.CombinedFilter;
import eu.baltrad.dex.datasource.manager.IDataSourceManager;
import eu.baltrad.dex.datasource.model.DataSource;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.datasource.model.mapper.DataSourceMapper;
import eu.baltrad.dex.datasource.model.mapper.FileObjectMapper;
import eu.baltrad.dex.radar.model.Radar;
import eu.baltrad.dex.radar.model.mapper.RadarMapper;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.user.model.mapper.UserMapper;

/**
 * Data source manager. 
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 1.2.1
 */
public class DataSourceManager implements IDataSourceManager {
    
    /** ODIM what/source attribute key */
    private static final String DS_SOURCE_ATTR_STR = "what/source:WMO";
    /** ODIM what/object attribute key */
    private static final String DS_OBJECT_ATTR_STR = "what/object";
    
    /** JDBC template */
    private JdbcOperations jdbcTemplate;
    /** Row mappers */
    private DataSourceMapper dataSourceMapper;
    private RadarMapper radarMapper;
    private FileObjectMapper fileObjectMapper;
    private UserMapper userMapper;
    
    /**
     * Constructor.
     */
    public DataSourceManager() {
        this.dataSourceMapper = new DataSourceMapper();
        this.radarMapper = new RadarMapper();
        this.fileObjectMapper = new FileObjectMapper();
        this.userMapper = new UserMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
     * Load data sources by type.
     * @return List of data sources of a given type
     */
    public List<DataSource> load(String type) {
        String sql = "SELECT * FROM dex_data_sources WHERE type = ?";
        try {
            return jdbcTemplate.query(sql, dataSourceMapper, type);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load data source by name and type.
     * @param id Data source name
     * @param type Data source type
     * @return Data source with a matching name and type
     */
    public DataSource load(String name, String type) {
        String sql = "SELECT * FROM dex_data_sources WHERE name = ? " + 
                "AND type = ?";
        try {
            return jdbcTemplate.queryForObject(sql, dataSourceMapper, name, 
                    type);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load data sources by user and type.
     * @param id User id
     * @param type Data source type
     * @return List of data sources for a given user.
     */
    public List<DataSource> load(int id, String type) {
        String sql = "SELECT * FROM dex_data_sources WHERE id IN (SELECT " +
                "data_source_id FROM dex_data_source_users WHERE user_id = ?)"
                + " AND type = ?";
        return jdbcTemplate.query(sql, dataSourceMapper, id, type);
    }
    
    /**
     * Store data source object in the db. 
     * @param dataSource Data source to store
     * @return Auto-generated record id
     */
    public int store(DataSource dataSource) {
        final String sql = "INSERT INTO dex_data_sources " +
            "(name, type, description, source, file_object) VALUES (?,?,?,?,?)";
        final DataSource ds = dataSource;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setString(1, ds.getName());
                    ps.setString(2, ds.getType());
                    ps.setString(3, ds.getDescription());
                    ps.setString(4, ds.getSource());
                    ps.setString(5, ds.getFileObject());
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
                " description = ?, source = ?, file_object = ? WHERE id = ?",
            dataSource.getName(),
            dataSource.getDescription(),
            dataSource.getSource(),
            dataSource.getFileObject(),
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
     * Get list of user accounts for a given data source id.
     * @param DataSourceId Data source id
     * @return List of users
     */
    public List<User> loadUser(int DataSourceId) {
        String sql = "SELECT u.*, r.name AS role " +
                "FROM dex_users u, dex_roles r, dex_users_roles ur " + 
                "WHERE ur.user_id = u.id AND ur.role_id = r.id " + 
                "AND u.id IN (SELECT user_id FROM " + 
                "dex_data_source_users WHERE data_source_id = ?)";
        try {
            return jdbcTemplate.query(sql, userMapper, DataSourceId);
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
            return jdbcTemplate.queryForObject(sql, int.class, dataSourceId);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }
    
    /**
     * Load filter by data source name and type matches
     * @param name the name of the data source
     * @param type the type of the data source
     * @return the filter id
     */
    public int loadFilterId(String name, String type) {
      String sql = "SELECT ddsf.filter_id FROM dex_data_source_filters ddsf, dex_data_sources dds " +
                   " WHERE dds.id = ddsf.data_source_id " +
                   " AND dds.name=? AND dds.type=?";
      try {
        return jdbcTemplate.queryForObject(sql, int.class, name, type);
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
    
    /**
     * Creates filter based on user selected parameters.
     * @param wmoNumbers Radar's WMO numbers
     * @param fileObjects File object names
     * @return Filter object
     */
    public CombinedFilter createFilter(String wmoNumbers, String fileObjects) {
        CombinedFilter combinedFilter = new CombinedFilter();
        combinedFilter.setMatchType(CombinedFilter
                .MatchType.ALL);
        AttributeFilter sourceFilter = new AttributeFilter();
        if (!wmoNumbers.isEmpty()) {
            sourceFilter.setAttribute(DS_SOURCE_ATTR_STR);
            sourceFilter.setValueType(
                    AttributeFilter.ValueType.STRING);
            sourceFilter.setOperator(AttributeFilter
                    .Operator.IN);
            sourceFilter.setValue(wmoNumbers);
            combinedFilter.addChildFilter(sourceFilter);
        }
        AttributeFilter fileObjectFilter = new AttributeFilter();
        if (!fileObjects.isEmpty()) {
            fileObjectFilter.setAttribute(DS_OBJECT_ATTR_STR);
            fileObjectFilter.setValueType(
                    AttributeFilter.ValueType.STRING);
            fileObjectFilter.setOperator(
                    AttributeFilter.Operator.IN);
            fileObjectFilter.setValue(fileObjects);
            combinedFilter.addChildFilter(fileObjectFilter);
        }
        return combinedFilter;
    }
    
}

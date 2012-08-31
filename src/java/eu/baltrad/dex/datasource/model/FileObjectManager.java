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

package eu.baltrad.dex.datasource.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

/**
 * File object manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.6.4
 */
public class FileObjectManager implements IFileObjectManager {

    /** JDBC template */
    private SimpleJdbcOperations jdbcTemplate;
    /** Row mapper */
    private FileObjectMapper mapper;

    /**
     * Constructor.
     */
    public FileObjectManager() {
        this.mapper = new FileObjectMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all file objects.
     * @return List of all available file objects 
     */
    public List<FileObject> load() {
        String sql = "SELECT * FROM dex_file_objects";
	List<FileObject> fileObjects = jdbcTemplate.query(sql, mapper);
	return fileObjects;
    }
    
    /**
     * Load file object by id.
     * @param id File object id
     * @return File object with a given id
     */
    public FileObject load(int id) {
        String sql = "SELECT * FROM dex_file_objects WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load file object with a given name
     * @param fileObject File object name
     * @return File object with a given name
     */
    public FileObject load(String fileObject) {
        String sql = "SELECT * FROM dex_file_objects WHERE file_object = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, fileObject);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store file object in database.
     * @param fileObject File object to be stored
     * @return Number of records stored
     */
    public int store(FileObject fileObject) {
        return jdbcTemplate.update("INSERT INTO dex_file_objects " +
            "(id, file_object, description) VALUES (?,?,?)",
            fileObject.getId(),
            fileObject.getFileObject(),
            fileObject.getDescription());
    }
    
    /**
     * Store file object in database.
     * @param fileObject File object to be stored
     * @return Number of records stored
     */
    public int storeNoId(FileObject fileObject) {
        return jdbcTemplate.update("INSERT INTO dex_file_objects " +
            "(file_object, description) VALUES (?,?)",
            fileObject.getFileObject(),
            fileObject.getDescription());
    }
    
    /**
     * Delete file object with a given id.
     * @param id File object
     * @return Number of deleted records
     */
    public int delete(int id) {
        return jdbcTemplate.update("DELETE FROM dex_file_objects WHERE id = ?", 
                id);
    }
    
    /**
     * File object row mapper.
     */
    private static final class FileObjectMapper
                            implements ParameterizedRowMapper<FileObject> {
        /**
         * Maps records to result set. 
         * @param rs Result set 
         * @param rowNum Row number
         * @return Registry entry object
         * @throws SQLException 
         */
        public FileObject mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            FileObject fileObject = new FileObject();
            fileObject.setId(rs.getInt("id"));
            fileObject.setFileObject(rs.getString("file_object"));
            fileObject.setDescription(rs.getString("description"));
            return fileObject;
        }
    }
    
}

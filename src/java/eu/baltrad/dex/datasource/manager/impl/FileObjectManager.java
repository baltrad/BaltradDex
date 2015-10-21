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

import eu.baltrad.dex.datasource.manager.IFileObjectManager;
import eu.baltrad.dex.datasource.model.FileObject;
import eu.baltrad.dex.datasource.model.mapper.FileObjectMapper;

/**
 * File object manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.2
 * @since 0.6.4
 */
public class FileObjectManager implements IFileObjectManager {

    /** JDBC template */
    private JdbcOperations jdbcTemplate;
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
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
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
        String sql = "SELECT * FROM dex_file_objects WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, fileObject);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store file object in database.
     * @param fileObject File object to be stored
     * @return Auto-generated record id
     */
    public int store(FileObject fileObject) {
        final String sql = "INSERT INTO dex_file_objects " +
            "(name, description) VALUES (?,?)";
        final FileObject fObject = fileObject;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setString(1, fObject.getName());
                    ps.setString(2, fObject.getDescription());
                    return ps;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
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
    
}

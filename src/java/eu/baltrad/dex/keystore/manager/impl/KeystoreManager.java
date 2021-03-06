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

package eu.baltrad.dex.keystore.manager.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import eu.baltrad.dex.keystore.manager.IKeystoreManager;
import eu.baltrad.dex.keystore.model.Key;
import eu.baltrad.dex.keystore.model.mapper.KeyMapper;

/**
 * Keystore manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 2.0.0
 * @since 1.6.0
 */
public class KeystoreManager implements IKeystoreManager {
    
    /** JDBC template */
    private JdbcOperations jdbcTemplate;
    
    /** Row mapper */
    private KeyMapper mapper;
    
    /**
     * Constructor.
     */
    public KeystoreManager() {
        this.mapper = new KeyMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load all keys.
     * @return List of all available keys.
     */
    public List<Key> load() {
        return jdbcTemplate.query("SELECT * FROM dex_keys ORDER BY name DESC;", 
                mapper);
    }
    
    /**
     * Load key with a given id
     * @param id Key id
     * @return Key
     */
    public Key load(int id) {
        String sql = "SELECT * FROM dex_keys WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Load key with a given name
     * @param name Key name
     * @return Key
     */
    public Key load(String name) {
        String sql = "SELECT * FROM dex_keys WHERE name = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, mapper, name);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store key object in the db.
     * @param key Key to store
     * @return Auto-generated record id
     * @throw Exception
     */
    public int store(Key key) throws Exception {
        
        final String sql = "INSERT INTO dex_keys (name, checksum, authorized, "
                + "injector) VALUES (?,?,?,?)";
        final Key k = key;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setString(1, k.getName());
                        ps.setString(2, k.getChecksum());
                        ps.setBoolean(3, k.isAuthorized());
                        ps.setBoolean(4, k.isInjector());
                        return ps;
                    }
                }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Update key.
     * @param key Key to update  
     * @return Number of rows affected
     * @throws Exception 
     */
    public int update(Key key) throws Exception {
        try {
            return jdbcTemplate.update("UPDATE dex_keys SET authorized = ?, " + 
                    "injector = ? WHERE id = ?",
                key.isAuthorized(),
                key.isInjector(),
                key.getId());
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Delete key with a given id.
     * @param id Key id
     * @return Number of deleted records.
     */
    public int delete(int id) {
         return jdbcTemplate.update("DELETE FROM dex_keys WHERE id = ?", id);
    }
    
}

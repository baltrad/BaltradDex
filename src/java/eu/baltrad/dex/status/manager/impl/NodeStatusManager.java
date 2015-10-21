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

package eu.baltrad.dex.status.manager.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import eu.baltrad.dex.status.manager.INodeStatusManager;
import eu.baltrad.dex.status.model.Status;
import eu.baltrad.dex.status.model.mapper.StatusMapper;

/**
 * Implements node status manager.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class NodeStatusManager implements INodeStatusManager {
    
    private JdbcOperations jdbcTemplate;
    private StatusMapper mapper;
    
    /**
     * Constructor.
     */
    public NodeStatusManager() {
        this.mapper = new StatusMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Load node names based on the information from status table.
     * @return List of node names
     * @throws DataAccessException 
     */
    public List<String> loadNodeNames() throws DataAccessException {
        String sql = 
            "SELECT DISTINCT ON (u.name) u.name AS user_name, " +
                "ds.name AS ds_name " +
            "FROM dex_subscriptions s, dex_status st, " + 
                "dex_status_subscriptions sts, dex_users u, " +
                "dex_subscriptions_users su, dex_data_sources ds, " +
                "dex_subscriptions_data_sources sds " +
            "WHERE sts.status_id = st.id AND sts.subscription_id = s.id " +
                "AND su.subscription_id = s.id AND su.user_id = u.id " +
                "AND sds.subscription_id = s.id " +
                "AND sds.data_source_id = ds.id;";
        
        return jdbcTemplate.query(sql, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int i) throws SQLException {
                    return rs.getString("user_name");
                }
            });    
    }
    
    /**
     * Load status matching given subscription.
     * @param subscriptionId Subscription id
     * @return Status record corresponding to a given subscription
     * @throws DataAccessException 
     */
    public Status load(int subscriptionId) throws DataAccessException {
        String sql =
            "SELECT s.type AS type, s.time_stamp AS start, " + 
                "s.active AS active, u.name AS node_name, " + 
                "ds.name AS data_source, st.* " +
            "FROM dex_subscriptions s, dex_status st, " + 
                "dex_status_subscriptions sts, dex_users u, " + 
                "dex_subscriptions_users su, dex_data_sources ds, " + 
                "dex_subscriptions_data_sources sds " +
            "WHERE sts.status_id = st.id AND sts.subscription_id = s.id " +
                "AND su.subscription_id = s.id AND su.user_id = u.id " + 
                "AND sds.subscription_id = s.id " + 
                "AND sds.data_source_id = ds.id " + 
                "AND s.id = ?;";
        
        return jdbcTemplate.queryForObject(sql, mapper, subscriptionId);
    }
    
    /**
     * Load status for a given peer node and subscriptions type.
     * @param peerName Peer node name
     * @param subscriptionType Subscription type
     * @return Status information for selected peer node
     * @throws DataAccessException 
     */
    public List<Status> load(String peerName, String subscriptionType) 
            throws DataAccessException {
        String sql =
            "SELECT s.type AS type, s.time_stamp AS start, " + 
                "s.active AS active, u.name AS node_name, " + 
                "ds.name AS data_source, st.* " +
            "FROM dex_subscriptions s, dex_status st, " + 
                "dex_status_subscriptions sts, dex_users u, " + 
                "dex_subscriptions_users su, dex_data_sources ds, " + 
                "dex_subscriptions_data_sources sds " +
            "WHERE sts.status_id = st.id AND sts.subscription_id = s.id " +
                "AND su.subscription_id = s.id AND su.user_id = u.id " + 
                "AND sds.subscription_id = s.id " + 
                "AND sds.data_source_id = ds.id " + 
                "AND u.name = ? AND s.type = ?;";    
    
        return jdbcTemplate.query(sql, mapper, peerName, subscriptionType);
    }
    
    /**
     * Store status record.
     * @param status Status object to store
     * @return Id of a stored record
     * @throws DataAccessException 
     */
    public int store(Status status) throws DataAccessException {
        final String sql = "INSERT INTO dex_status (downloads, uploads, " + 
                    "upload_failures) VALUES (?,?,?)";
        final Status s = status;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(
                        Connection conn) throws SQLException {
                    PreparedStatement ps = conn.prepareStatement(sql,
                            new String[] {"id"});
                    ps.setLong(1, s.getDownloads());
                    ps.setLong(2, s.getUploads());
                    ps.setLong(3, s.getUploadFailures());
                    return ps;
                }
            }, keyHolder);
        return keyHolder.getKey().intValue();
    }
    
    /**
     * Store reference to subscription record.
     * @param statusId Status record id
     * @param subscriptionId Subscription record id
     * @return Number of stored records
     * @throws DataAccessException 
     */
    public int store(int statusId, int subscriptionId) 
            throws DataAccessException {
        String sql = "INSERT INTO dex_status_subscriptions (status_id, " + 
                "subscription_id) VALUES (?,?)";
        return jdbcTemplate.update(sql, statusId, subscriptionId);
    }
    
    /**
     * Update status for selected subscription.
     * @param status Status object
     * @param subscriptionId Subscription id
     * @return Number of updated records
     * @throws DataAccessException 
     */
    public int update(Status status, int subscriptionId) 
            throws DataAccessException {
        String sql =
            "UPDATE dex_status SET " + 
                "downloads = ?, " +
                "uploads = ?, "+ 
                "upload_failures = ? " +
            "WHERE id IN (SELECT s.id FROM dex_status s, " + 
                "dex_status_subscriptions ss WHERE s.id = ss.status_id " + 
                "AND ss.subscription_id = ?);";
        
        return jdbcTemplate.update(sql, status.getDownloads(), 
            status.getUploads(), status.getUploadFailures(), subscriptionId);
        
    }
    
    /**
     * Delete status related to subscription with a given id.
     * @param subscriptionId Subscription id
     * @return Number of deleted records
     * @throws DataAccessException 
     */
    public int delete(int subscriptionId) throws DataAccessException {
        String sql = 
            "DELETE FROM dex_status " + 
                "WHERE id IN (SELECT s.id FROM dex_status s, " + 
                    "dex_status_subscriptions ss WHERE s.id = ss.status_id " + 
                    "AND ss.subscription_id = ?);";
        
        return jdbcTemplate.update(sql, subscriptionId);
    }
    
}

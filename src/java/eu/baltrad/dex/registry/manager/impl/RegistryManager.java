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

package eu.baltrad.dex.registry.manager.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import eu.baltrad.dex.config.manager.impl.ConfigurationManager;
import eu.baltrad.dex.config.model.RegistryConfiguration;
import eu.baltrad.dex.registry.manager.IRegistryManager;
import eu.baltrad.dex.registry.model.impl.RegistryEntry;
import eu.baltrad.dex.registry.model.mapper.RegistryEntryMapper;

/**
 * Class implements data delivery register handling functionality..
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.2.1
 * @since 0.1.6
 */
public class RegistryManager implements IRegistryManager, InitializingBean {
    
    /** JDBC template */
    private JdbcOperations jdbcTemplate;
    /** Row mapper */
    private RegistryEntryMapper mapper;
    
    /**
     * Log configuration is required
     */
    private ConfigurationManager configManager;
    
    /** Logger */
    private final static Logger logger = org.apache.log4j.LogManager.getLogger(RegistryManager.class);

    /**
     * Constructor.
     */
    public RegistryManager() {
        this.mapper = new RegistryEntryMapper();
    }
    
    /**
     * @param jdbcTemplate the jdbcTemplate to set
     */
    @Autowired
    public void setJdbcTemplate(JdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Counts entries.
     * @return Total number of entries in the registry
     */
    public long count(String type) {
        String sql = "SELECT count(*) FROM dex_delivery_registry " + 
                     "WHERE type = ?;";
        return jdbcTemplate.queryForObject(sql, long.class, type);
    }
    
    /**
     * Load registry entries.
     * @param offset Offset
     * @param limit Limit
     * @return List of registry entries
     */
    public List<RegistryEntry> load(String type, int offset, int limit) {
        String sql = "SELECT dr.*, u.name AS user_name " + 
            "FROM dex_delivery_registry dr, dex_users u, " + 
            "dex_delivery_registry_users dru WHERE " +
            "dru.entry_id = dr.id AND dru.user_id = u.id " + 
            "AND dr.type = ? ORDER BY time_stamp DESC OFFSET ? LIMIT ?";
        try {
            return jdbcTemplate.query(sql, mapper, type, offset, limit);
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    /**
     * Store entry in delivery registry. 
     * @param entry Entry to store
     * @return Auto-generated record id
     * @throws Exception 
     */
    @Transactional(propagation=Propagation.REQUIRED,
            rollbackFor=Exception.class)
    public int store(RegistryEntry registryEntry) throws Exception {
        
        final String sql = "INSERT INTO dex_delivery_registry " +
                "(time_stamp, type, uuid, status) VALUES (?, ?, ?, ?)";
        final RegistryEntry entry = registryEntry;
        
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(
                            Connection conn) throws SQLException {
                        PreparedStatement ps = conn.prepareStatement(sql,
                                new String[] {"id"});
                        ps.setLong(1, entry.getTimeStamp());
                        ps.setString(2, entry.getType());
                        ps.setString(3, entry.getUuid());
                        ps.setBoolean(4, entry.getStatus());
                        return ps;
                    }
                }, keyHolder);
            int entryId = keyHolder.getKey().intValue(); 
            int userId = entry.getUserId();
            int dataSourceId = entry.getDataSourceId();
            jdbcTemplate.update("INSERT INTO dex_delivery_registry_users " +
                    "(entry_id, user_id) VALUES (?,?)", entryId, userId);
            jdbcTemplate.update("INSERT INTO dex_delivery_registry_data_sources"
                    + " (entry_id, data_source_id) VALUES (?,?)", 
                    entryId, dataSourceId);
            return entryId;
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Delete all registry entries.
     * @return Number of records deleted
     */
    public int delete() throws Exception {
        try {
            return jdbcTemplate.update("DELETE FROM dex_delivery_registry");
        } catch (DataAccessException e) {
            throw new Exception(e.getMessage());
        }    
    }
    
    /**
     * @param configManager the configManager to set
     */
    @Autowired
    public void setConfigManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }
    
    /**
     * Runs the maintenance routine for keeping the dex_delivery_registry table in check.
     */
    protected void runMaintenance() {
      logger.info("Periodic maintenance of dex_delivery_registry triggered");
      RegistryConfiguration conf = configManager.getRegistryConf();
      try {
        if (Boolean.parseBoolean(conf.getRegTrimByNumber())) {
          logger.info("Cleaning by count");
          jdbcTemplate.update("DELETE FROM dex_delivery_registry " +
              " WHERE id IN (SELECT id FROM dex_delivery_registry ORDER BY time_stamp DESC OFFSET ?)",
              Integer.parseInt(conf.getRegRecordLimit()));
        }
      } catch (Exception e) {
        logger.error(e);
      }

      try {
        if (Boolean.parseBoolean(conf.getRegTrimByAge())) {
          logger.info("Cleaning by age");
          int days = Integer.parseInt(conf.getRegMaxAgeDays());
          int hours = Integer.parseInt(conf.getRegMaxAgeHours());
          int minutes = Integer.parseInt(conf.getRegMaxAgeMinutes());
          
          long epoclimit = System.currentTimeMillis() - (((days*24) + hours)*60 + minutes)*60*1000;
          jdbcTemplate.update("DELETE FROM dex_delivery_registry WHERE time_stamp < ?", epoclimit);
        }
      } catch (Exception e) {
        logger.error(e);
      }
    }

    /**
     * See {@link InitializingBean}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
      ScheduledExecutorService exec = Executors.newScheduledThreadPool(1,
          new ThreadFactory() {
            public Thread newThread(Runnable r) {
              Thread t = Executors.defaultThreadFactory().newThread(r);
              t.setDaemon(true); // Want thread to die silently if we are shutting down system
              return t;
            }
          }
      );
      Runnable maintenanceRunnable = new Runnable() {
        @Override
        public void run() {
          runMaintenance();
        }
      };
      exec.scheduleAtFixedRate(maintenanceRunnable , 0, 1, TimeUnit.MINUTES);
    }    
    
}

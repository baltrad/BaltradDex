/***************************************************************************************************
*
* Copyright (C) 2009-2011 Institute of Meteorology and Water Management, IMGW
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
***************************************************************************************************/

package eu.baltrad.dex.config.model;

import eu.baltrad.dex.util.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class implemens configuration object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class ConfigurationManager {
//---------------------------------------------------------------------------------------- Constants
    /** configuration record ID */
    public static final int CONF_REC_ID = 1;
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public ConfigurationManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets configuration record with a given ID.
     *
     * @param id Record ID
     * @return Configuration object with a given ID
     */
    public Configuration getNodeConfiguration( int id ) throws SQLException, Exception {
        Connection conn = null;
        Configuration conf = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_node_configuration WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int confId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String type = resultSet.getString( "type" );
                String address = resultSet.getString( "short_address" );
                String port = resultSet.getString( "port" );
                String orgName = resultSet.getString( "org_name" );
                String orgAddress = resultSet.getString( "org_address" );
                String timeZone = resultSet.getString( "time_zone" );
                String workDir = resultSet.getString( "temp_dir" );
                String adminEmail = resultSet.getString( "email" );
                conf = new Configuration( confId, name, type, address, port, orgName, orgAddress,
                        timeZone, workDir, adminEmail );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select configuration: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select configuration: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return conf;
    }
    /**
     * Gets configuration record for a log system with a given ID.
     *
     * @param logId Log system identification string
     * @return LogConfiguration
     * @throws SQLException
     * @throws Exception
     */
    public LogConfiguration getLogConfiguration( String logId ) throws SQLException, Exception {
        Connection conn = null;
        LogConfiguration logConf = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_log_configuration WHERE" +
                    " log_id = '" + logId + "';" );
            while( resultSet.next() ) {
                int Id = resultSet.getInt( "id" );
                String logSystemId = resultSet.getString( "log_id" );
                boolean trimByNumber = resultSet.getBoolean( "trim_by_number" );
                boolean trimByDate = resultSet.getBoolean( "trim_by_date" );
                int recordLimit = resultSet.getInt( "record_limit" );
                Timestamp dateLimit = resultSet.getTimestamp( "date_limit" );
                logConf = new LogConfiguration( Id, logSystemId, trimByNumber, trimByDate,
                        recordLimit, dateLimit );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select log configuration: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select log configuration: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return logConf;
    }
    /**
     * Saves or updates configuration.
     *
     * @param conf Configuration object
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveOrUpdate( Configuration conf ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( conf.getId() == 0 ) {
                sql = "INSERT INTO dex_node_configuration (name, type, short_address, port, " +
                    "org_name, org_address, time_zone, temp_dir, email) VALUES ('" +
                    conf.getNodeName() + "', '" + conf.getNodeType() + "', '" +
                    conf.getShortAddress() + "', '" + conf.getPortNumber() + "', '" +
                    conf.getOrgName() + "', '" + conf.getOrgAddress() + "', '" +
                    conf.getTimeZone() + "', '" + conf.getTempDir() + "', '" +
                    conf.getAdminEmail() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_node_configuration SET name = '" + conf.getNodeName() + "', " +
                    "type = '" + conf.getNodeType() + "', short_address = '" +
                    conf.getShortAddress() + "', port = '" + conf.getPortNumber() + "', " +
                    "org_name = '" + conf.getOrgName() + "', org_address = '" +
                    conf.getOrgAddress() + "', time_zone = '" + conf.getTimeZone() + "', " + 
                    "temp_dir = '" + conf.getTempDir() + "', email = '" + 
                    conf.getAdminEmail() + "' WHERE id = " + conf.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save configuration: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save configuration: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Saves or updates log configuration.
     *
     * @param logConfiguration Log configuration object
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveOrUpdate( LogConfiguration logConf ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( logConf.getId() == 0 ) {
                sql = "INSERT INTO dex_log_configuration (log_id, trim_by_number, trim_by_date, " +
                    "record_limit, date_limit) VALUES ('" +
                    logConf.getLogId() + "', " + logConf.getTrimByNumber() + ", " +
                    logConf.getTrimByDate() + ", " + logConf.getRecordLimit() + ", '" +
                    logConf.getDateLimit() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_log_configuration SET log_id = '" + logConf.getLogId() + "', " +
                    "trim_by_number = " + logConf.getTrimByNumber() + ", trim_by_date = " +
                    logConf.getTrimByDate() + ", record_limit = " + logConf.getRecordLimit() +
                    ", date_limit = '" + logConf.getDateLimit() + "' WHERE id = '" + logConf.getId()
                    + "';";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save log configuration: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save log configuration: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
}
//--------------------------------------------------------------------------------------------------

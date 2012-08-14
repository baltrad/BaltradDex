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

package eu.baltrad.dex.radar.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Data channel manager class implementing data channel handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class RadarManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public RadarManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all data channels.
     *
     * @return List of all available data channels
     */
    public List<Radar> getRadars() {
        Connection conn = null;
        List<Radar> channels = new ArrayList<Radar>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_radars" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                Radar channel = new Radar( chnlId, name, wmoNumber );
                channels.add( channel );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data channels", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channels;
    }
    /**
     * Gets data channel with a given ID.
     *
     * @param id Data channel ID
     * @return Data channel with a given ID
     */
    public Radar getRadar( int id ) {
        Connection conn = null;
        Radar channel = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_radars WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                channel = new Radar( chnlId, name, wmoNumber );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data channels", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channel;
    }
    /**
     * Gets data channel with a given name.
     *
     * @param channelName Data channel name
     * @return Data channel with a given name
     */
    public Radar getRadar( String channelName ) {
        Connection conn = null;
        Radar channel = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_radars WHERE" +
                    " name = '" + channelName + "';");
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                channel = new Radar( chnlId, name, wmoNumber );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data channels", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channel;
    }
    /**
     * Saves or updates data channel.
     *
     * @param channel Data channel
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate( Radar channel ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( channel.getId() == 0 ) {
                sql = "INSERT INTO dex_radars (name, wmo_number) VALUES ('" +
                    channel.getRadarName() + "', '" + channel.getWmoNumber() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_radars SET name = '" + channel.getRadarName() + "', " +
                    "wmo_number = '" + channel.getWmoNumber() + "' WHERE id = " +
                    channel.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to save data channel", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes channel with a given ID.
     *
     * @param id Channel ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int deleteRadar( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_radars WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete data channel", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
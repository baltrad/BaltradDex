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

package eu.baltrad.dex.channel.model;

import eu.baltrad.dex.util.JDBCConnectionManager;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

/**
 * Data channel manager class implementing data channel handling functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class ChannelManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods    
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public ChannelManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets all data channels.
     *
     * @return List of all available data channels
     */
    public List<Channel> getChannels() {
        Connection conn = null;
        List<Channel> channels = new ArrayList<Channel>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channels" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                Channel channel = new Channel( chnlId, name, wmoNumber );
                channels.add( channel );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
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
    public Channel getChannel( int id ) {
        Connection conn = null;
        Channel channel = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channels WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                channel = new Channel( chnlId, name, wmoNumber );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
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
    public Channel getChannel( String channelName ) {
        Connection conn = null;
        Channel channel = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channels WHERE" +
                    " name = '" + channelName + "';");
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String wmoNumber = resultSet.getString( "wmo_number" );
                channel = new Channel( chnlId, name, wmoNumber );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select data channels: " + e.getMessage() );
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
     * @throws SQLException
     * @throws Exception
     */
    public int saveOrUpdate( Channel channel ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( channel.getId() == 0 ) {
                sql = "INSERT INTO dex_channels (name, wmo_number) VALUES ('" +
                    channel.getChannelName() + "', '" + channel.getWmoNumber() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_channels SET name = '" + channel.getChannelName() + "', " +
                    "wmo_number = '" + channel.getWmoNumber() + "' WHERE id = " +
                    channel.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data channel: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data channel: " + e.getMessage() );
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
     * @throws SQLException
     * @throws Exception
     */
    public int deleteChannel( int id ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_channels WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete data channel: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete data channel: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets permission for a given channel and a given user.
     *
     * @param channelId Channel ID
     * @param userId User ID
     * @return ChannelPermission object
     */
    public ChannelPermission getPermission( int channelId, int userId ) {
        Connection conn = null;
        ChannelPermission channelPermission = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channel_permissions " +
                    "WHERE channel_id = " + channelId + " AND user_id = " + userId + ";" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "channel_id" );
                int usrId = resultSet.getInt( "user_id" );
                channelPermission = new ChannelPermission( chnlId, usrId );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } catch ( Exception e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channelPermission;
    }
    /**
     * Gets channel permissions for user with a given ID.
     *
     * @param userId User ID
     * @return List of permissions for a given user
     */
    public List<ChannelPermission> getPermissionByUser( int userId ) {
        Connection conn = null;
        List<ChannelPermission> channelPermissions = new ArrayList<ChannelPermission>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channel_permissions " +
                    "WHERE user_id = " + userId + ";" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "channel_id" );
                int usrId = resultSet.getInt( "user_id" );
                ChannelPermission permission = new ChannelPermission( chnlId, usrId );
                channelPermissions.add( permission );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } catch ( Exception e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channelPermissions;
    }
    /**
     * Gets channel permissions for a data channel with a given ID.
     *
     * @param channelId Data channel ID
     * @return List of permissions for a given data channel
     */
    public List<ChannelPermission> getPermissionByChannel( int channelId ) {
        Connection conn = null;
        List<ChannelPermission> channelPermissions = new ArrayList<ChannelPermission>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_channel_permissions " +
                    "WHERE channel_id = " + channelId + ";" );
            while( resultSet.next() ) {
                int chnlId = resultSet.getInt( "channel_id" );
                int usrId = resultSet.getInt( "user_id" );
                ChannelPermission permission = new ChannelPermission( chnlId, usrId );
                channelPermissions.add( permission );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } catch ( Exception e ) {
            System.err.println( "Failed to select channel permission: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return channelPermissions;
    }
    /**
     * Saves channel permission.
     *
     * @param channelPermission Channel permission object
     * @return Number of inserted records
     */
    public int savePermission( ChannelPermission permission ) {
        Connection conn = null;
        int insert = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO dex_channel_permissions (channel_id, user_id) VALUES ('" +
                    permission.getChannelId() + "', '" + permission.getUserId() + "');";
            insert = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save channel permission: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to save channel permission: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return insert;
    }
    /**
     * Removes channel permission.
     *
     * @param channelId Channel ID
     * @param userId User ID
     * @return Number of deleted records
     */
    public int deletePermission( int channelId, int userId ) {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_channel_permissions WHERE channel_id = " +
                    channelId + "AND user_id = " + userId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete channel permission: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to delete channel permission: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
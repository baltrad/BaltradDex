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

package eu.baltrad.dex.datasource.model;

import eu.baltrad.dex.util.JDBCConnectionManager;
import eu.baltrad.dex.log.model.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Data quantity manager class implementing data quantity object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class DataQuantityManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public DataQuantityManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all data quantities.
     *
     * @return List of all available data quantities.
     */
    public List<DataQuantity> getDataQuantities() {
        Connection conn = null;
        List<DataQuantity> dataQuantities = new ArrayList<DataQuantity>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_quantities" );
            while( resultSet.next() ) {
                int dataQuantityId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "data_quantity" );
                String unit = resultSet.getString( "unit" );
                String description = resultSet.getString( "description" );
                DataQuantity dataQuantity = new DataQuantity( dataQuantityId, identifier, unit,
                        description );
                dataQuantities.add( dataQuantity );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data quantities", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataQuantities;
    }
    /**
     * Gets data quantity with a given ID.
     *
     * @param id Data quantity ID
     * @return Data quantity with a given ID
     */
    public DataQuantity getDataQuantity( int id ) {
        Connection conn = null;
        DataQuantity dataQuantity = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_quantities WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int dataQuantityId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "data_quantity" );
                String unit = resultSet.getString( "unit" );
                String description = resultSet.getString( "description" );
                dataQuantity = new DataQuantity( dataQuantityId, identifier, unit,
                        description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data quantity", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataQuantity;
    }
    /**
     * Gets data quantity matching a given identifier.
     *
     * @param identifier Data quantity identifier
     * @return Data quantity matching a given identifier
     */
    public DataQuantity getDataQuantity( String identifier ) {
        Connection conn = null;
        DataQuantity dataQuantity = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_quantities WHERE" +
                    " data_quantity = '" + identifier + "';");
            while( resultSet.next() ) {
                int dataQuantityId = resultSet.getInt( "id" );
                String dqIdentifier = resultSet.getString( "data_quantity" );
                String unit = resultSet.getString( "unit" );
                String description = resultSet.getString( "description" );
                dataQuantity = new DataQuantity( dataQuantityId, dqIdentifier, unit,
                        description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select data quantity", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataQuantity;
    }
    /**
     * Saves or updates data quantity.
     *
     * @param dataQuantity Data quantity
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate( DataQuantity dataQuantity ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( dataQuantity.getId() == 0 ) {
                sql = "INSERT INTO dex_data_quantities (data_quantity, unit, description) VALUES ('"
                    + dataQuantity.getQuantity() + "', '" + dataQuantity.getUnit() +
                    "', '" + dataQuantity.getDescription() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_data_quantities SET data_quantity = '" +
                    dataQuantity.getQuantity() + "', unit = '" + dataQuantity.getUnit() +
                    "', description = '" + dataQuantity.getDescription() + "' WHERE id = " +
                    dataQuantity.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to save data quantity", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes data quantity with a given ID.
     *
     * @param id Data quantity ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int deleteDataQuantity( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_quantities WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete data quantity", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
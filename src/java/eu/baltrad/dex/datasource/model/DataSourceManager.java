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

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

/**
 * Product manager class implementing data source object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class DataSourceManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public DataSourceManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
    }
    /**
     * Gets all data sources.
     *
     * @return List of all available data sources.
     */
    public List<DataSource> getDataSources() {
        Connection conn = null;
        List<DataSource> dataSources = new ArrayList<DataSource>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_sources" );
            while( resultSet.next() ) {
                int dataSourceId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "name" );
                String description = resultSet.getString( "description" );
                DataSource dataSource = new DataSource( dataSourceId, identifier, description );
                dataSources.add( dataSource );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data sources: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select data sources: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataSources;
    }
    /**
     * Gets data source with a given ID.
     *
     * @param id Data source ID
     * @return Data source with a given ID
     */
    public DataSource getDataSource( int id ) throws SQLException, Exception {
        Connection conn = null;
        DataSource dataSource = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_sources WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int dataSourceId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "name" );
                String description = resultSet.getString( "description" );
                dataSource = new DataSource( dataSourceId, identifier, description );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data source: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select data source: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataSource;
    }
    /**
     * Gets data source matching a given identifier.
     *
     * @param identifier Data source identifier
     * @return Data source matching a given identifier
     */
    public DataSource getDataSource( String identifier ) throws SQLException, Exception {
        Connection conn = null;
        DataSource dataSource = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_sources WHERE" +
                    " name = '" + identifier + "';");
            while( resultSet.next() ) {
                int dataSourceId = resultSet.getInt( "id" );
                String dsIdentifier = resultSet.getString( "name" );
                String description = resultSet.getString( "description" );
                dataSource = new DataSource( dataSourceId, dsIdentifier, description );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data source: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select data source: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataSource;
    }
    /**
     * Saves or updates data source.
     *
     * @param dataSource Data source
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveOrUpdate( DataSource dataSource ) throws SQLException, Exception {
        Connection conn = null;
        long count = 0;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // check if record exists based on data source name
            ResultSet resultSet = stmt.executeQuery( "SELECT count(*) FROM dex_data_sources" +
                    " WHERE name = '" + dataSource.getName() + "';");
            while( resultSet.next() ) {
                count = resultSet.getLong( 1 );
            }
            // record does not exists, do insert
            if( count == 0 ) {
                sql = "INSERT INTO dex_data_sources (name, description) VALUES ('"
                    + dataSource.getName() + "', '" + dataSource.getDescription() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_data_sources SET description = '" + dataSource.getDescription() +
                    "' WHERE name = '" + dataSource.getName() + "';";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes data source with a given ID.
     *
     * @param id Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteDataSource( int id ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_sources WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete data source: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete data source: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Saves data source radar parameter.
     * 
     * @param dataSourceId Data source ID
     * @param radarId Radar station ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveRadar( int dataSourceId, int radarId ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_radars (data_source_id, radar_id) VALUES ("
                    + dataSourceId + ", " + radarId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source radar parameter: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source radar parameter: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes radar parameters for a data source with a given ID.
     * 
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteRadars( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_radars WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete radars for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete radars for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets radar IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return Radar IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getRadarIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> radarIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT radar_id FROM dex_data_source_radars "
                    + "WHERE data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                radarIds.add( resultSet.getInt( "radar_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select radar IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select radar IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return radarIds;
    }
    /**
     * Saves data source file object parameter.
     *
     * @param dataSourceId Data source ID
     * @param fileObjectId File object ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveFileObject( int dataSourceId, int fileObjectId ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_file_objects (data_source_id, file_object_id) " +
                    "VALUES ( " + dataSourceId + ", " + fileObjectId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source file object parameter: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source file object parameter: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes file object parameters for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteFileObjects( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_file_objects WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete file objects for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete file objects for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets file object IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return File object IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getFileObjectIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> fileObjectIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT file_object_id FROM " +
                    "dex_data_source_file_objects WHERE data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                fileObjectIds.add( resultSet.getInt( "file_object_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select file object IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select file object IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return fileObjectIds;
    }
    /**
     * Saves data source quantity parameter.
     *
     * @param dataSourceId Data source ID
     * @param dataQuantityId Quantity ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveDataQuantity( int dataSourceId, int dataQuantityId ) throws SQLException,
            Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_quantities (data_source_id, data_quantity_id) " +
                    "VALUES ( " + dataSourceId + ", " + dataQuantityId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source quantity parameter: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source quantity parameter: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes quantity parameters for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteDataQuantities( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_quantities WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete quantities for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete quantities for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets data quantity IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return Data quantity IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getDataQuantityIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> dataQuantityIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT data_quantity_id FROM " +
                    "dex_data_source_quantities WHERE data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                dataQuantityIds.add( resultSet.getInt( "data_quantity_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data quantity IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select data quantity IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataQuantityIds;
    }
    /**
     * Saves data source product parameter.
     *
     * @param dataSourceId Data source ID
     * @param productId Product ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveProduct( int dataSourceId, int productId ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_products (data_source_id, product_id) " +
                    "VALUES ( " + dataSourceId + ", " + productId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source product parameter: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source product parameter: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes product parameters for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteProducts( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_products WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete products for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete products for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets product IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return Product IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getProductIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> productIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT product_id FROM " +
                    "dex_data_source_products WHERE data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                productIds.add( resultSet.getInt( "product_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select product IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select product IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return productIds;
    }
    /**
     * Saves data source product parameter.
     *
     * @param dataSourceId Data source ID
     * @param parameterId Parameter ID
     * @param parameterValue Parameter value
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveProductParameter( int dataSourceId, int parameterId, String parameterValue ) 
            throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_product_parameter_values (data_source_id, " +
                    "parameter_id, parameter_value) VALUES ( " + dataSourceId + ", " +
                    parameterId + ", '" + parameterValue + "');";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source product parameter: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source product parameter: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes product parameters for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteProductParameters( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_product_parameter_values WHERE " +
                    "data_source_id = " + dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete products parameter values for a given " +
                    " data source ID: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete products parameter values for a given " +
                    " data source ID: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets product parameter IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return Product parameter IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getProductParameterIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> productParameterIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT parameter_id FROM " +
                    "dex_data_source_product_parameter_values WHERE data_source_id = " +
                    dataSourceId + ";" );
            while( resultSet.next() ) {
                productParameterIds.add( resultSet.getInt( "parameter_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select product parameter IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select product parameter IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return productParameterIds;
    }
    /**
     * Saves data source user.
     *
     * @param dataSourceId Data source ID
     * @param userId User ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveUser( int dataSourceId, int userId ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_users (data_source_id, user_id) " +
                    "VALUES ( " + dataSourceId + ", " + userId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source user: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source user: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes data source users for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteUsers( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_users WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete data source users for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete data source users for a given data source ID: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets user IDs for a given data source.
     *
     * @param dataSourceId Data source ID
     * @return User IDs for a given data source
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getUserIds( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> userIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT user_id FROM " +
                    "dex_data_source_users WHERE data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                userIds.add( resultSet.getInt( "user_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select user IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select user IDs for a given data source: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return userIds;
    }
    /**
     * Gets data source IDs for a given user.
     *
     * @param userId User ID
     * @return Data source IDs for a given user
     * @throws SQLException
     * @throws Exception
     */
    public List<Integer> getDataSourceIds( int userId ) throws SQLException, Exception {
        Connection conn = null;
        List<Integer> dataSourceIds = new ArrayList<Integer>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT data_source_id FROM " +
                    "dex_data_source_users WHERE user_id = " + userId + ";" );
            while( resultSet.next() ) {
                dataSourceIds.add( resultSet.getInt( "data_source_id" ) );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select data source IDs for a given user: " +
                    e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to select data source IDs for a given user: " +
                    e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return dataSourceIds;
    }
    /**
     * Saves data source filter.
     *
     * @param dataSourceId Data source ID
     * @param filterId Filter ID
     * @return Number of saved or updated records
     * @throws SQLException
     * @throws Exception
     */
    public int saveFilter( int dataSourceId, int filterId ) throws SQLException, Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            sql = "INSERT INTO dex_data_source_filters (data_source_id, filter_id) " +
                    "VALUES ( " + dataSourceId + ", " + filterId + ");";
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to save data source filter: " + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to save data source filter: " + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Gets filter ID for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Filter ID
     * @throws SQLException
     * @throws Exception
     */
    public int getFilterId( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int filterId = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_data_source_filters WHERE" +
                    " data_source_id = " + dataSourceId + ";" );
            while( resultSet.next() ) {
                filterId = resultSet.getInt( "filter_id" );
            }
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to select filter ID: " + e.getMessage() );
        } catch( Exception e ) {
            System.err.println( "Failed to select filter ID: " + e.getMessage() );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return filterId;
    }
    /**
     * Deletes data source filters for a data source with a given ID.
     *
     * @param dataSourceId Data source ID
     * @return Number of deleted records
     * @throws SQLException
     * @throws Exception
     */
    public int deleteFilters( int dataSourceId ) throws SQLException, Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_data_source_filters WHERE data_source_id = " +
                    dataSourceId + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( SQLException e ) {
            System.err.println( "Failed to delete data source filters for a given data source ID: "
                    + e.getMessage() );
            throw e;
        } catch( Exception e ) {
            System.err.println( "Failed to delete data source filters for a given data source ID: "
                    + e.getMessage() );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
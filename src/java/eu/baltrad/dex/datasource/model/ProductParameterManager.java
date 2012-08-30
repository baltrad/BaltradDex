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
import eu.baltrad.dex.log.util.MessageLogger;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Product manager class implementing product parameter object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class ProductParameterManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public ProductParameterManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all product parameters.
     *
     * @return List of all available product parameters.
     */
    public List<ProductParameter> getProductParameters() {
        Connection conn = null;
        List<ProductParameter> productParameters = new ArrayList<ProductParameter>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_product_parameters" );
            while( resultSet.next() ) {
                int productParameterId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "parameter" );
                String description = resultSet.getString( "description" );
                ProductParameter productParameter = new ProductParameter( productParameterId,
                        identifier, description );
                productParameters.add( productParameter );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product parameters", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return productParameters;
    }
    /**
     * Gets product parameter with a given ID.
     *
     * @param id Product parameter ID
     * @return Product parameter with a given ID
     */
    public ProductParameter getProductParameter( int id ) {
        Connection conn = null;
        ProductParameter productParameter = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_product_parameters WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int productParameterId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "parameter" );
                String description = resultSet.getString( "description" );
                productParameter = new ProductParameter( productParameterId, identifier,
                        description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product parameter", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return productParameter;
    }
    /**
     * Gets product parameter matching a given identifier.
     *
     * @param identifier Product parameter identifier
     * @return Product parameter matching a given identifier
     */
    public ProductParameter getProductParameter( String identifier ) {
        Connection conn = null;
        ProductParameter productParameter = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_product_parameters WHERE" +
                    " parameter = '" + identifier + "';");
            while( resultSet.next() ) {
                int productParameterId = resultSet.getInt( "id" );
                String ppIdentifier = resultSet.getString( "parameter" );
                String description = resultSet.getString( "description" );
                productParameter = new ProductParameter( productParameterId, ppIdentifier,
                        description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product parameter", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return productParameter;
    }
    /**
     * Saves or updates product.
     *
     * @param productParameter Product parameter
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate( ProductParameter productParameter ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( productParameter.getId() == 0 ) {
                sql = "INSERT INTO dex_product_parameters (parameter, description) VALUES ('"
                    + productParameter.getParameter() + "', '" + productParameter.getDescription() +
                    "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_product_parameters SET parameter = '" +
                    productParameter.getParameter() + "', description = '" +
                    productParameter.getDescription() + "' WHERE id = " +
                    productParameter.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to save product parameter", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes product parameter with a given ID.
     *
     * @param id Product parameter ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int deleteProductParameter( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_product_parameters WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete product parameter", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
    /**
     * Gets product parameter value.
     *
     * @param dataSourceId Data source ID
     * @param parameterId Product parameter ID
     * @return Product parameter value as string
     * @throws Exception
     */
    public String getProductParameterValue( int dataSourceId, int parameterId ) throws Exception {
        Connection conn = null;
        String parameterValue = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT parameter_value FROM " +
                    "dex_data_source_product_parameter_values WHERE data_source_id = " +
                    dataSourceId + " AND parameter_id = " + parameterId + ";" );
            while( resultSet.next() ) {
                parameterValue = resultSet.getString( "parameter_value" );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product parameter value", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return parameterValue;
    }
}
//--------------------------------------------------------------------------------------------------
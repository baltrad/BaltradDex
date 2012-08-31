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

//import eu.baltrad.dex.util.JDBCConnectionManager;
//import eu.baltrad.dex.log.util.MessageLogger;

//import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.List;
import java.util.ArrayList;

/**
 * Product manager class implementing product object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class ProductManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    //private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    //private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public ProductManager() {
        //this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        //this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all products.
     *
     * @return List of all available products.
     *
    public List<Product> getProducts() {
        Connection conn = null;
        List<Product> products = new ArrayList<Product>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_products" );
            while( resultSet.next() ) {
                int productId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "product" );
                String description = resultSet.getString( "description" );
                Product product = new Product( productId, identifier, description );
                products.add( product );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select products", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return products;
    }
    /**
     * Gets product with a given ID.
     *
     * @param id Product ID
     * @return Product with a given ID
     *
    public Product getProduct( int id ) {
        Connection conn = null;
        Product product = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_products WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int productId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "product" );
                String description = resultSet.getString( "description" );
                product = new Product( productId, identifier, description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return product;
    }
    /**
     * Gets product matching a given identifier.
     *
     * @param identifier Product identifier
     * @return Product matching a given identifier
     *
    public Product getProduct( String identifier ) {
        Connection conn = null;
        Product product = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_products WHERE" +
                    " product = '" + identifier + "';");
            while( resultSet.next() ) {
                int productId = resultSet.getInt( "id" );
                String pIdentifier = resultSet.getString( "product" );
                String description = resultSet.getString( "description" );
                product = new Product( productId, pIdentifier, description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select product", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return product;
    }
    /**
     * Saves or updates product.
     *
     * @param product Product
     * @return Number of saved or updated records
     * @throws Exception
     *
    public int saveOrUpdate( Product product ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( product.getId() == 0 ) {
                sql = "INSERT INTO dex_products (product, description) VALUES ('"
                    + product.getProduct() + "', '" + product.getDescription() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_products SET product = '" + product.getProduct() +
                    "', description = '" + product.getDescription() + "' WHERE id = " +
                    product.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to save product", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes product with a given ID.
     *
     * @param id Product ID
     * @return Number of deleted records
     * @throws Exception
     *
    public int deleteProduct( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_products WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete product", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }*/
}
//--------------------------------------------------------------------------------------------------
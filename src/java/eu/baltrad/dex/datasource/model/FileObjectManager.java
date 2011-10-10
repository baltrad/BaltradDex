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
 * File object manager class implementing file object handling functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.4
 * @since 0.6.4
 */
public class FileObjectManager {
//---------------------------------------------------------------------------------------- Variables
    /** Reference to JDBCConnector class object */
    private JDBCConnectionManager jdbcConnectionManager;
    /** Logger */
    private Logger log;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor gets reference to JDBCConnectionManager instance.
     */
    public FileObjectManager() {
        this.jdbcConnectionManager = JDBCConnectionManager.getInstance();
        this.log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    }
    /**
     * Gets all file objects.
     *
     * @return List of all available file objects
     */
    public List<FileObject> getFileObjects() {
        Connection conn = null;
        List<FileObject> fileObjects = new ArrayList<FileObject>();
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_file_objects" );
            while( resultSet.next() ) {
                int fileObjectId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "file_object" );
                String description = resultSet.getString( "description" );
                FileObject fileObject = new FileObject( fileObjectId, identifier, description );
                fileObjects.add( fileObject );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select file objects", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return fileObjects;
    }
    /**
     * Gets file object with a given ID.
     *
     * @param id File object ID
     * @return File object with a given ID
     */
    public FileObject getFileObject( int id ) {
        Connection conn = null;
        FileObject fileObject = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_file_objects WHERE" +
                    " id = " + id + ";" );
            while( resultSet.next() ) {
                int fileObjectId = resultSet.getInt( "id" );
                String identifier = resultSet.getString( "file_object" );
                String description = resultSet.getString( "description" );
                fileObject = new FileObject( fileObjectId, identifier, description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select file object", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return fileObject;
    }
    /**
     * Gets file object matching a given identifier.
     *
     * @param identifier File object identifier
     * @return File object matching a given identifier
     */
    public FileObject getFileObject( String identifier ) {
        Connection conn = null;
        FileObject fileObject = null;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery( "SELECT * FROM dex_file_objects WHERE" +
                    " file_object = '" + identifier + "';");
            while( resultSet.next() ) {
                int fileObjectId = resultSet.getInt( "id" );
                String foIdentifier = resultSet.getString( "file_object" );
                String description = resultSet.getString( "description" );
                fileObject = new FileObject( fileObjectId, foIdentifier, description );
            }
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to select file object", e );
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return fileObject;
    }
    /**
     * Saves or updates file object.
     *
     * @param fileObject File object
     * @return Number of saved or updated records
     * @throws Exception
     */
    public int saveOrUpdate( FileObject fileObject ) throws Exception {
        Connection conn = null;
        int update = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "";
            // record does not exists, do insert
            if( fileObject.getId() == 0 ) {
                sql = "INSERT INTO dex_file_objects (file_object, description) VALUES ('" +
                    fileObject.getFileObject() + "', '" + fileObject.getDescription() + "');";
            } else {
                // record exists, do update
                sql = "UPDATE dex_file_objects SET file_object = '" + fileObject.getFileObject() +
                    "', description = '" + fileObject.getDescription() + "' WHERE id = " +
                    fileObject.getId() + ";";
            }
            update = stmt.executeUpdate( sql ) ;
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to save file object", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return update;
    }
    /**
     * Deletes file object with a given ID.
     *
     * @param id File object ID
     * @return Number of deleted records
     * @throws Exception
     */
    public int deleteFileObject( int id ) throws Exception {
        Connection conn = null;
        int delete = 0;
        try {
            conn = jdbcConnectionManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM dex_file_objects WHERE id = " + id + ";";
            delete = stmt.executeUpdate( sql );
            stmt.close();
        } catch( Exception e ) {
            log.error( "Failed to delete file objects", e );
            throw e;
        } finally {
            jdbcConnectionManager.returnConnection( conn );
        }
        return delete;
    }
}
//--------------------------------------------------------------------------------------------------
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

package eu.baltrad.dex.util;

import eu.baltrad.dex.log.model.*;
import eu.baltrad.fc.CacheDirStorage;
import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileCatalogError;
import eu.baltrad.fc.LocalStorage;
import eu.baltrad.fc.db.Database;

import java.util.Properties;
import java.io.InputStream;

/**
 * Provides connection to FileCatalog. Implemented as sigleton in order to keep control over the
 * number of created connections.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 0.1.0
 * @since 0.1.0
 */
public class FileCatalogConnector {
//---------------------------------------------------------------------------------------- Constants
    /** Properties file name */
    private static final String PROPS_FILE_NAME = "dex.fc.properties";
    /** Database URI property */
    private static final String DB_URI_PROP = "database.uri";
    /** File catalog storage folder */
    private static final String DATA_STORAGE_FOLDER_PROP = "data.storage.folder";
//---------------------------------------------------------------------------------------- Variables
    /** Reference to LocalStorage object **/
    private static LocalStorage localStorage;
    /** Reference to Database object */
    private static Database database;
    /** Reference to FileCatalog object */
    private static FileCatalog fileCatalog;
    /** Reference to LogManager object */
    private LogManager logManager;
    /** Data storage folder */
    private static String dataStorageFolder;
    /** Data storage directory path */
    private static String dataStorageDirectory;
    /** Reference to the object of this class */
    private static FileCatalogConnector fileCatalogConnector;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Initializes object of this class in case it is null, otherwise returns existing object.
     *
     * @return Reference to the object of this class
     */
    public static synchronized FileCatalogConnector getInstance() {
        if( fileCatalogConnector == null ) {
            fileCatalogConnector = new FileCatalogConnector();
        }
        return fileCatalogConnector;
    }
    /**
     * Private constructor can be invoked by getInstance() method only.
     */
    private FileCatalogConnector() {
        init();
    }
    /**
     * Reads properties from stream and initializes FileCatalog.
     */
    public void init() {
        // Initialize LogManager
        this.logManager = new LogManager();
        // Initialize FileCatalog
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                // get db URI
                String dbURI = props.getProperty( DB_URI_PROP );
                // initialize storage folders
                dataStorageFolder = props.getProperty( DATA_STORAGE_FOLDER_PROP );
                // create data storage directory
                dataStorageDirectory = InitAppUtil.createDir( props.getProperty( 
                        DATA_STORAGE_FOLDER_PROP ), "New data storage directory created" );
                // initialize file catalog
                localStorage = new CacheDirStorage( dataStorageDirectory );
                database = Database.create( dbURI );
                fileCatalog = new FileCatalog( database, localStorage );
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, logManager.getLogger(),
                    System.currentTimeMillis(), LogEntry.LEVEL_INFO, "File catalog successfully " +
                    "initialized", null ) );
            } else {
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, logManager.getLogger(),
                    System.currentTimeMillis(), LogEntry.LEVEL_ERROR, "Failed to load properties " +
                    " file: " + PROPS_FILE_NAME, null ) );
            }
        } catch( FileCatalogError e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, logManager.getLogger(),
                System.currentTimeMillis(), LogEntry.LEVEL_ERROR, "File catalog error: " +
                e.getMessage(), null ) );
        } catch( Exception e ) {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, logManager.getLogger(),
                System.currentTimeMillis(), LogEntry.LEVEL_ERROR, "File catalog error: " +
                e.getMessage(), null ) );
        }
    }
    /**
     * Method gets reference to FileCatalog object.
     *
     * @return Reference to FileCatalog object
     */
    public synchronized FileCatalog getFileCatalog() { return fileCatalog; }
    /**
     * Gets data storage folder.
     *
     * @return Data storage folder
     */
    public static String getDataStorageFolder() { return dataStorageFolder; }
    /**
     * Sets data storage folder.
     *
     * @param dataStorageFolder Data storage folder to set
     */
    public static void setDataStorageFolder( String _dataStorageFolder ) {
        dataStorageFolder = _dataStorageFolder;
    }
    /**
     * Gets data storage directory.
     *
     * @return Data storage directory
     */
    public static String getDataStorageDirectory() { return dataStorageDirectory; }
    /**
     * Sets data storage directory.
     *
     * @param dataStorageDirectory Data storage directory to set
     */
    public static void setDataStorageDirectory( String _dataStorageDirectory ) {
        dataStorageDirectory = _dataStorageDirectory;
    }
}
//--------------------------------------------------------------------------------------------------

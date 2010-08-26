/***************************************************************************************************
*
* Copyright (C) 2009-2010 Institute of Meteorology and Water Management, IMGW
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

import eu.baltrad.dex.log.model.LogManager;
import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileCatalogError;

import java.util.Properties;
import java.util.Date;
import java.io.InputStream;
import java.io.File;

/**
 * Utility class implementing FileCatalog connection functionality.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class FileCatalogConnector {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.fc.properties";
    // Database URI property
    private static final String DB_URI_PROP = "database.uri";
    // File catalog storage directory
    private static final String STORAGE_DIR_PROP = "storage.dir";
//---------------------------------------------------------------------------------------- Variables
    // Reference to FileCatalog object
    private static FileCatalog fileCatalog;
    // Reference to LogManager object
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public FileCatalogConnector() {
        // Initialize LogManager
        this.logManager = new LogManager();
        // Initialize FileCatalog
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                String storageFolder = props.getProperty( STORAGE_DIR_PROP );
                String dbURI = props.getProperty( DB_URI_PROP );
                // Check if storage folder is relative or absolute path
                String storageDir = null;
                if( storageFolder.startsWith( File.separator ) ) {
                    storageDir = storageFolder;
                } else {
                    storageDir = ServletContextUtil.getServletContextPath() + storageFolder;
                }
                File f = new File( storageDir );
                if( !f.exists() ) {
                    f.mkdirs();
                    logManager.addEntry( new Date(), LogManager.MSG_WRN, "New storage directory " +
                            "initialized: " + storageDir );

                }
                fileCatalog = new FileCatalog( dbURI, storageDir );
                logManager.addEntry( new Date(), LogManager.MSG_INFO,
                        "File catalog successfully initialized" );
            } else {
                logManager.addEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
            }
        } catch( FileCatalogError e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "File catalog error: " +
                    e.getMessage() );
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "File catalog error: " +
                    e.getMessage() );
        }
    }
    /**
     * Method initializes storage directory and connects to the database.
     *
     * @return Reference to FileCatalog object
     */
    public static FileCatalog connect() { return fileCatalog; }
    /**
     * Method gets reference to FileCatalog object.
     *
     * @return Reference to FileCatalog object
     */
    public static FileCatalog getFileCatalog() { return fileCatalog; }
    /**
     * Method sets reference to FileCatalog object.
     *
     * @param fc Reference to file catalog object
     */
    public static void setFileCatalog( FileCatalog fc ) { fileCatalog = fc; }
}
//--------------------------------------------------------------------------------------------------

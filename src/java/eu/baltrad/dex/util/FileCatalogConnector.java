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
    // File catalog storage folder
    private static final String DATA_STORAGE_FOLDER_PROP = "data.storage.folder";
    // Image storage folder
    private static final String IMAGE_STORAGE_FOLDER_PROP = "image.storage.folder";
    // Image thumbs storage folder
    private static final String THUMBS_STORAGE_FOLDER_PROP = "thumbs.storage.folder";
//---------------------------------------------------------------------------------------- Variables
    // Reference to FileCatalog object
    private static FileCatalog fileCatalog;
    // Reference to LogManager object
    private LogManager logManager;
    // Data storage folder
    private static String dataStorageFolder;
    // Image storage folder
    private static String imageStorageFolder;
    // Thumbs storage folder
    private static String thumbsStorageFolder;
    // Data storage directory path
    private static String dataStorageDirectory;
    // Image storage directory
    private static String imageStorageDirectory;
    // Thumbs storage directory
    private static String thumbsStorageDirectory;
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
                // get db URI
                String dbURI = props.getProperty( DB_URI_PROP );
                // initialize storage folders
                dataStorageFolder = props.getProperty( DATA_STORAGE_FOLDER_PROP );
                imageStorageFolder = props.getProperty( IMAGE_STORAGE_FOLDER_PROP );
                thumbsStorageFolder = imageStorageFolder + File.separator
                        + props.getProperty( THUMBS_STORAGE_FOLDER_PROP );

                // create data storage directory
                dataStorageDirectory = createDir( props.getProperty( DATA_STORAGE_FOLDER_PROP ),
                        "New data storage directory created" );
                // create image storage directory
                imageStorageDirectory = createDir( props.getProperty( IMAGE_STORAGE_FOLDER_PROP ),
                        "New image storage directory created" );
                // create thumbs storage directory
                thumbsStorageDirectory = createDir( thumbsStorageFolder, "New thumbs storage "
                        + "directory created" );

                // initialize file catalog
                fileCatalog = new FileCatalog( dbURI, dataStorageDirectory );
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
     * Creates new storage directory. 
     * 
     * @param folder Either relative or absolute path to the folder that is to be created
     * @param msg Message to display
     * @return Name of the directory created
     */
    public String createDir( String folder, String msg ) {
        String dir = "";
        // Check if folder is relative or absolute path
        if( folder.startsWith( File.separator ) ) {
            dir = folder;
        } else {
            dir = ServletContextUtil.getServletContextPath() + folder;
        }
        File f = new File( dir );
        if( !f.exists() ) {
            if( f.mkdirs() ) {
                logManager.addEntry( new Date(), LogManager.MSG_WRN, msg + ": " + dir );
            } else {
                logManager.addEntry( new Date(), LogManager.MSG_WRN,  "Failed to create "
                        + "directory: " + dir );
            }
        }
        return dir;
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
     * Gets image storage folder.
     *
     * @return Image storage folder
     */
    public static String getImageStorageFolder() { return imageStorageFolder; }
    /**
     * Sets image storage folder.
     *
     * @param imageStorageFolder Image storage folder to set
     */
    public static void setImageStorageFolder( String _imageStorageFolder ) {
        imageStorageFolder = _imageStorageFolder;
    }
    /**
     * Gets thumbs storage folder.
     *
     * @return Thumbs storage folder
     */
    public static String getThumbsStorageFolder() { return thumbsStorageFolder; }
    /**
     * Sets thumbs storage folder.
     *
     * @param thumbsStorageFolder Thumbs storage folder to set
     */
    public static void setThumbsStorageFolder( String _thumbsStorageFolder ) {
        thumbsStorageFolder = _thumbsStorageFolder;
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
    /**
     * Gets image storage directory.
     *
     * @return Image storage directory
     */
    public static String getImageStorageDirectory() { return imageStorageDirectory; }
    /**
     * Sets image storage directory.
     *
     * @param imageStorageDirectory Image storage directory to set
     */
    public static void setImageStorageDirectory( String _imageStorageDirectory ) {
        imageStorageDirectory = _imageStorageDirectory;
    }
    /**
     * Gets thumbs storage directory.
     *
     * @return Thumbs storage directory
     */
    public static String getThumbsStorageDirectory() { return thumbsStorageDirectory; }
    /**
     * Sets thumbs storage directory.
     *
     * @param thumbsStorageDirectory Thumbs storage directory to set
     */
    public static void setThumbsStorageDirectory( String _thumbsStorageDirectory ) {
        thumbsStorageDirectory = _thumbsStorageDirectory;
    }
}
//--------------------------------------------------------------------------------------------------

/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import eu.baltrad.dex.model.LogManager;
import eu.baltrad.fc.FileCatalog;
import eu.baltrad.fc.FileCatalogError;

import java.util.Properties;
import java.util.Date;
import java.io.InputStream;
import java.io.File;

/**
 * Utility class providing connection to File Catalog.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class FileCatalogConnector {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.fc.properties";
    // Database URI property
    private static final String PROPS_DB_URI = "database.uri";
    // File catalog storage directory
    private static final String FC_STORAGE_DIR = "FileCatalogStorage";
//---------------------------------------------------------------------------------------- Variables
    // Reference to FileCatalog object
    private static FileCatalog fileCatalog;
    // Reference to LogManager object
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor.
     */
    public FileCatalogConnector() { this.logManager = new LogManager(); }
    /**
     * Method initializes storage directory and connects to the database.
     *
     * @param dbURI Database URI
     * @param storageDir Storage directory
     * @return Reference to FileCatalog object
     */
    public FileCatalog connect() {
        // Check if storage directory exists
        String storageDir = ServletContextUtil.getServletContextPath() + FC_STORAGE_DIR;
        File f = new File( storageDir );
        if( !f.exists() ) {
            f.mkdirs();
            logManager.addLogEntry( new Date(), LogManager.MSG_WRN, "New storage directory " +
                    "initialized: " + storageDir );
        }
        // Initialize FileCatalog
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                String dbURI = props.getProperty( PROPS_DB_URI );
                fileCatalog = new FileCatalog( dbURI, storageDir );
                logManager.addLogEntry( new Date(), LogManager.MSG_INFO,
                        "File catalog successfully initialized" );
            } else {
                logManager.addLogEntry( new Date(), LogManager.MSG_ERR, 
                        "Failed to load properties file: " + PROPS_FILE_NAME );
            }
        } catch( FileCatalogError e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "File catalog error: " +
                    e.getMessage() );
        } catch( Exception e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "File catalog error: " +
                    e.getMessage() );
        }
        return fileCatalog;
    }
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

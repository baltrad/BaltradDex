/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.util;

import eu.baltrad.dex.model.LogManager;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Properties;

/**
 * Utility class used to initialize application upon start.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class InitAppUtil {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.init.properties";
    // Property holding name of the directory storing data from local production system
    private static final String PROD_DIR_PROP = "local.production.dir";
    // Property holding name of the temporary directory where for local data
    private static final String TEMP_DIR_PROP = "local.temp.dir";
    // Property holding name of the directory storing incoming data from foreign nodes
    private static final String INCOMING_DIR_PROP = "incoming.data.dir";
//---------------------------------------------------------------------------------------- Variables
    // Reference to LogManager object
    private static LogManager logManager = new LogManager();
    // Directory storing data from local production system
    private static String localProdDir;
    // Temporary directory for local data
    private static String localTempDir;
    // Directory storing incoming data from foreign nodes
    private static String incomingDataDir;
    
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor performs initialization task
     */
    public InitAppUtil() {
        try {
            InputStream is = this.getClass().getResourceAsStream( PROPS_FILE_NAME );
            Properties props = new Properties();
            if( is != null ) {
                props.load( is );
                setLocalProdDir( ServletContextUtil.getServletContextPath() +
                        props.getProperty( PROD_DIR_PROP ) );
                setIncomingDataDir( ServletContextUtil.getServletContextPath() +
                        props.getProperty( INCOMING_DIR_PROP ) );
                setLocalTempDir( props.getProperty( TEMP_DIR_PROP ) );
                // Create directories
                //makeDir( getLocalProdDir() );
                makeDir( getIncomingDataDir() );
                logManager.addLogEntry( new Date(), LogManager.MSG_INFO,
                        "Application successfully initialized" );
            } else {
                logManager.addLogEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
            }
        } catch( IOException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
        }
    }
    /**
     * Method gets local production directory name.
     *
     * @return The name of local production directory
     */
    public static String getLocalProdDir() { return localProdDir; }
    /**
     * Method sets local production directory name.
     *
     * @param _localProdDir The name of local production directory
     */
    public void setLocalProdDir( String _localProdDir ) { localProdDir = _localProdDir; }
    /**
     * Method gets local temporary directory name.
     *
     * @return The name of local temporary directory
     */
    public static String getLocalTempDir() { return localTempDir; }
    /**
     * Method sets local temporary directory name.
     *
     * @param _localTempDir The name of local temporary directory
     */
    public void setLocalTempDir( String _localTempDir ) { localTempDir = _localTempDir; }
    /**
     * Method gets incoming data directory name.
     *
     * @return Incoming data directory name
     */
    public static String getIncomingDataDir() { return incomingDataDir; }
    /**
     * Method sets incoming data directory name.
     *
     * @param _incomingDataDir The name of incoming data directory
     */
    public void setIncomingDataDir( String _incomingDataDir ) {
        incomingDataDir = _incomingDataDir;
    }
    /**
     * Method extracts relative file name from absolute file path string.
     *
     * @param absFilePath Absolute file path string
     * @return Relative file name
     */
    public static String getRelFileName( String absFilePath ) {
        return absFilePath.substring( absFilePath.lastIndexOf( File.separator ) + 1,
                    absFilePath.length() );
    }
    /**
     * Method creates new directory.
     *
     * @param directoryPath Path to the directory
     */
    public static void makeDir( String directoryPath ) {
        File dir = new File( directoryPath );
        if( !dir.exists() ) {
            dir.mkdirs();
            logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "New directory created: \n"
                    + directoryPath );
        }
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFileName Output file name
     */
    public static void saveFile( InputStream is, String dstFileName ) {
        try {
            File dstFile = new File( dstFileName );
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = is.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len);
            }
            is.close();
            fos.close();
            logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "Incoming data succesfully " +
                    " saved: \n" + getRelFileName( dstFileName ) );
        } catch( FileNotFoundException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
    }
    /**
     * Method deletes file
     *
     * @param filePath Absolute file path
     */
    public static void deleteFile( String filePath ) {
        try {
            File f = new File( filePath );
            f.delete();
        } catch( Exception e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while deleting file: \n"
                    + e.getMessage() );
        }
    }
}
//--------------------------------------------------------------------------------------------------

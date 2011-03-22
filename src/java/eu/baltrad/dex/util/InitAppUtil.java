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
import eu.baltrad.dex.config.model.ConfigurationManager;
import eu.baltrad.dex.config.model.Configuration;
import eu.baltrad.dex.core.model.NodeConnection;

import java.sql.SQLException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Properties;

/**
 * Utility class used to initialize application on startup.
 *
 * @author <a href="mailto:maciej.szewczykowski@imgw.pl>Maciej Szewczykowski</a>
 * @version 1.0
 * @since 1.0
 */
public class InitAppUtil {
//---------------------------------------------------------------------------------------- Constants
    // Properties file name
    private static final String PROPS_FILE_NAME = "dex.init.properties";
    // Temporary file prefix
    private final static String TEMP_FILE_PREFIX = "dex";
    // Temporary file suffix
    private final static String TEMP_FILE_SUFFIX = ".dat";
    // Node version property
    private final static String NODE_VERSION_PROP = "node.version";
    // Image storage folder
    private static final String IMAGE_STORAGE_FOLDER_PROP = "image.storage.folder";
    // Image thumbs storage folder
    private static final String THUMBS_STORAGE_FOLDER_PROP = "thumbs.storage.folder";
    // Maximum age of temporary files in miliseconds, set to 3 minutes
    private static final long TEMP_FILE_MAX_AGE = 180000;
//---------------------------------------------------------------------------------------- Variables
    // Initialize LogManager object
    private static LogManager logManager;
    // Initialize Configuration manager
    private static ConfigurationManager configurationManager = new ConfigurationManager();
    // Node version
    private static String nodeVersion;
    // Node address
    private static String nodeAddress;
    // Node name
    private static String nodeName;
    // Node type
    private static String nodeType;
    // Organization name
    private static String orgName;
    // Organization address
    private static String orgAddress;
    // Time zone
    private static String timeZone;
    // Node administrator email
    private static String adminEmail;
    // Work directory
    private static String workDir;
    // Image storage folder
    private static String imageStorageFolder;
    // Thumbs storage folder
    private static String thumbsStorageFolder;
    // Image storage directory
    private static String imageStorageDirectory;
    // Thumbs storage directory
    private static String thumbsStorageDirectory;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor performs initialization task
     */
    public InitAppUtil() { 
        logManager = new LogManager();
        initApp();
    }
    /**
     * Method initializes application by reading configuration from database.
     */
    public static void initApp() {
        Configuration conf = null;
        try {
            conf = configurationManager.getConfiguration( ConfigurationManager.CONF_REC_ID );
        } catch( SQLException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while reading configuration"
                    + " from database: " + e.getMessage() );
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while reading configuration"
                    + " from database: " + e.getMessage() );
        }
        if( conf != null ) {
            // reconstruct node's full address
            setNodeAddress( NodeConnection.HTTP_PREFIX + conf.getShortAddress() +
                    NodeConnection.PORT_SEPARATOR + conf.getPortNumber() +
                    NodeConnection.ADDRESS_SEPARATOR + NodeConnection.APP_CONTEXT +
                    NodeConnection.ADDRESS_SEPARATOR + NodeConnection.ENTRY_ADDRESS );
            setNodeName( conf.getNodeName() );
            setNodeType( conf.getNodeType() );
            setOrgName( conf.getOrgName() );
            setOrgAddress( conf.getOrgAddress() );
            setTimeZone( conf.getTimeZone() );
            setAdminEmail( conf.getAdminEmail() );
            // Create relative or absolute work directory
            String tmpDir = createDir( conf.getTempDir(), "Created work directory" );
            setWorkDir( tmpDir );
            try {
                InputStream is = InitAppUtil.class.getResourceAsStream( PROPS_FILE_NAME );
                Properties props = new Properties();
                if( is != null ) {
                    props.load( is );
                    nodeVersion = props.getProperty( NODE_VERSION_PROP );
                    imageStorageFolder = props.getProperty( IMAGE_STORAGE_FOLDER_PROP );
                    thumbsStorageFolder = props.getProperty( THUMBS_STORAGE_FOLDER_PROP );
                    // create image storage directory
                    imageStorageDirectory = createDir( getWorkDir() + File.separator +
                        imageStorageFolder, "New image storage directory created" );
                    // create thumbs storage directory
                    thumbsStorageDirectory = createDir( getWorkDir() + File.separator +
                        thumbsStorageFolder, "New thumbs storage directory created" );
                } else {
                    logManager.addEntry( new Date(), LogManager.MSG_ERR,
                        "Failed to load properties file: " + PROPS_FILE_NAME );
                }
            } catch( Exception e ) {
                logManager.addEntry( new Date(), LogManager.MSG_ERR, "Failed to initialize " + 
                        "application: " + e.getMessage() );
            }
            logManager.addEntry( new Date(), LogManager.MSG_INFO,
                    "Application successfully initialized" );
        }
    }
    
    /**
     * Gets node version property.
     *
     * @return Node version property
     */
    public static String getNodeVersion() { return nodeVersion; }
    
    /**
     * Sets node version property
     *
     * @param _nodeVersion Node version property to set
     */
    public static void setNodeVersion(String _nodeVersion) { nodeVersion = _nodeVersion; }

    /**
     * Gets node address property.
     *
     * @return Node address property
     */
    public static String getNodeAddress() { return nodeAddress; }
    /**
     * Sets node address property.
     *
     * @param _nodeAddress Node address property to set
     */
    public static void setNodeAddress( String _nodeAddress ) { nodeAddress = _nodeAddress; }
    /**
     * Gets node name property.
     *
     * @return Node name property
     */
    public static String getNodeName() { return nodeName; }
    /**
     * Sets node name property.
     *
     * @param _nodeName Node name property to set
     */
    public static void setNodeName( String _nodeName ) { nodeName = _nodeName; }
    /**
     * Gets node type property.
     *
     * @return Node type property
     */
    public static String getNodeType() { return nodeType; }
    /**
     * Sets node type property.
     *
     * @param _nodeType Node type property to set
     */
    public static void setNodeType( String _nodeType ) { nodeType = _nodeType; }
    /**
     * Gets organization name property.
     *
     * @return Organization name property
     */
    public static String getOrgName() { return orgName; }
    /**
     * Sets organization name property.
     *
     * @param _orgName Organization name property to set
     */
    public static void setOrgName( String _orgName ) { orgName = _orgName; }
    /**
     * Gets organization address property.
     *
     * @return Organization address property
     */
    public static String getOrgAddress() { return orgAddress; }
    /**
     * Sets organization address property.
     *
     * @param _orgAddress Organization address property to set
     */
    public static void setOrgAddress( String _orgAddress ) { orgAddress = _orgAddress; }
    /**
     * Gets time zone property.
     *
     * @return Time zone property
     */
    public static String getTimeZone() { return timeZone; }
    /**
     * Sets time zone property.
     *
     * @param _timeZone Time zone property to set
     */
    public static void setTimeZone( String _timeZone ) { timeZone = _timeZone; }
    /**
     * Gets administrator email property.
     *
     * @return Administrator email property
     */
    public static String getAdminEmail() { return adminEmail; }
    /**
     * Sets administrator email property.
     *
     * @param _adminEmail Administrator email property to set
     */
    public static void setAdminEmail( String _adminEmail ) { adminEmail = _adminEmail; }
    /**
     * Method gets work directory name.
     *
     * @return The name of work directory
     */
    public static String getWorkDir() { return workDir; }
    /**
     * Method sets work directory name.
     *
     * @param _workDir The name of work directory
     */
    public static void setWorkDir( String _workDir ) { workDir = _workDir; }
     /**
     * Gets image storage folder extended with base directory / work directory.
     *
     * @return Image storage folder
     */
    public static String getImageStorageFolder() { 
        String baseDir = getWorkDir().substring( getWorkDir().lastIndexOf( File.separator ) + 1,
                getWorkDir().length() );
        String imageFolder = baseDir + File.separator + imageStorageFolder;
        return imageFolder;
    }
    /**
     * Sets image storage folder.
     *
     * @param imageStorageFolder Image storage folder to set
     */
    public static void setImageStorageFolder( String _imageStorageFolder ) {
        imageStorageFolder = _imageStorageFolder;
    }
    /**
     * Gets thumbs storage folder extended with base directory / work directory.
     *
     * @return Thumbs storage folder
     */
    public static String getThumbsStorageFolder() {
        String baseDir = getWorkDir().substring( getWorkDir().lastIndexOf( File.separator ) + 1,
                getWorkDir().length() );
        String thumbsFolder = baseDir + File.separator + thumbsStorageFolder;
        return thumbsFolder;
    }
    /**
     * Sets thumbs storage folder.
     *
     * @param thumbsStorageFolder Thumbs storage folder to set
     */
    public static void setThumbsStorageFolder( String _thumbsStorageFolder ) {
        thumbsStorageFolder = _thumbsStorageFolder;
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
     * Creates new storage directory.
     *
     * @param folder Either relative or absolute path to the folder that is to be created
     * @param msg Message to display
     * @return Name of the directory created
     */
    public static String createDir( String folder, String msg ) {
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
     * Creates temporary file.
     *
     * @param tempDir Temporary directory
     * @return Reference to temporary file
     */
    public static File createTempFile( File tempDir ) {
        File f = null;
        try {
            f = File.createTempFile( TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, tempDir );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while creating temporary " +
                    "file: \n" + e.getMessage() );
        }
        return f;
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFileName Output file name
     * @retunr dstFile Reference to saved file
     */
    public static File saveFile( InputStream is, String dstFileName ) {
        File dstFile = null;
        try {
            dstFile = new File( dstFileName );
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = is.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len);
            }
            is.close();
            fos.close();
        } catch( FileNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
        return dstFile;
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFile Output file
     * @return dstFile Refernce to saved file
     */
    public static File saveFile( InputStream is, File dstFile ) {
        try {
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = is.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len );
            }
            is.close();
            fos.close();
        } catch( FileNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: \n"
                    + e.getMessage() );
        }
        return dstFile;
    }
    /**
     * Deletes file.
     *
     * @param filePath Absolute file path
     */
    public static void deleteFile( String filePath ) {
        try {
            File f = new File( filePath );
            f.delete();
        } catch( Exception e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while deleting file: \n"
                    + e.getMessage() );
        }
    }
    /**
     * Deletes file.
     * 
     * @param f File object
     */
    public static void deleteFile( File f ) {
        if( !f.delete() ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while deleting file: "
                    + f.getName() );
        }
    }
    /**
     * Deletes temporary files from a given directory. Files must be older than a given age.
     *
     * @param directory Directory where temporary files are stored
     * @param maxAge Maximum age of temporary file
     */
    public static void cleanUpTempFiles( String directory ) {
        File dir = new File( directory );
        File[] tempFiles = dir.listFiles();
        for( int i = 0; i < tempFiles.length; i++ ) {
            if( tempFiles[ i ].getName().startsWith( TEMP_FILE_PREFIX ) &&
                    tempFiles[ i ].getName().endsWith( TEMP_FILE_SUFFIX ) ) {
                Date date = new Date();
                long now = date.getTime();
                long modified = tempFiles[ i ].lastModified();
                // check if the files is older than a given age
                if( ( now - modified ) > TEMP_FILE_MAX_AGE ) {
                    deleteFile( tempFiles[ i ] );
                }
            }
        }
    }
    /**
     * Creates ObjectOutputStream connected to a given file and writes given object to the stream.
     *
     * @param o Input object to write
     * @param f Input file for writing object
     */
    public static void writeObjectToStream( Object obj, File f ) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( f ) );
            oos.writeObject( obj );
            oos.close();
        } catch( FileNotFoundException e )  {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while writing object to stream: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while writing object to stream: " + e.getMessage() );
        }
    }
    /**
     * Creates ObjectInputStream connected to a given file and reads object from the stream.
     *
     * @param f Input file
     * @return Object read from file upon success, null in case of a failure
     */
    public static Object readObjectFromStream( File f ) {
        Object obj = null;
        try {
            ObjectInputStream ois = new ObjectInputStream( new FileInputStream( f ) );
            obj = ois.readObject();
            ois.close();
        } catch( ClassNotFoundException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while reading object from stream: " + e.getMessage() );
        } catch( IOException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR,
                    "Error while reading object from stream: " + e.getMessage() );
        }
        return obj;
    }
}
//--------------------------------------------------------------------------------------------------

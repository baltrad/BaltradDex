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

import eu.baltrad.dex.log.model.MessageLogger;
import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.model.ConfigurationManager;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;

/**
 * Utility class used to initialize application on startup.
 *
 * @author Maciej Szewczykowski :: maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class InitAppUtil extends NodeAddress {
//---------------------------------------------------------------------------------------- Constants
    /** Temporary file prefix */
    private final static String TEMP_FILE_PREFIX = "dex";
    /** Temporary file suffix */
    private final static String TEMP_FILE_SUFFIX = ".dat";
    /** Maximum age of temporary files in miliseconds, set to 3 minutes */
    private static final long TEMP_FILE_MAX_AGE = 180000;
//---------------------------------------------------------------------------------------- Variables
    /** References logger object */
    private static Logger log;
    /** Configuration object holding all necessary settings */
    private static AppConfiguration config;
    /** Absolute path to work directory */
    private static String workDirPath;
    /** Absolute path to image storage */
    private static String imagesDirPath;
    /** Absolute path to thumbs storage */
    private static String thumbsDirPath;
    /** Reference to the object of this class */
    private static InitAppUtil initAppUtil;
    /** Initialization status */
    private static int status;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Constructor invokes initializing method.
     */
    public InitAppUtil() { initApp(); }
    /**
     * Method initializes application by reading configuration from database.
     */
    public static synchronized void initApp() {
        log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
        ConfigurationManager cm = new ConfigurationManager();
        config = cm.loadAppConf();
        if( config != null ) {
            // Create work directory
            workDirPath = createDir( config.getWorkDir(), "Created work directory" );
            // Create image storage directory
            imagesDirPath = createDir( config.getWorkDir() + File.separator + config.getImagesDir(),
                    "New image storage directory created" );
            // Create thumbs storage directory
            thumbsDirPath = createDir( config.getWorkDir() + File.separator + config.getThumbsDir(),
                    "New thumbs storage directory created" );
            log.info( "Application successfully initialized" );
            setStatus( 0 );
        } else {
            log.error( "Failed to initialize application" );
            setStatus( 1 );
        }
    }
    /**
     * Get initialization status.
     *
     * @return status Initialization status
     */
    public static int getStatus() { return status; }
    /**
     * Set initialization status.
     *
     * @param _status Initialization status to set
     */
    public static void setStatus( int _status ) { status = _status; }
    /**
     * Gets configuration object.
     * 
     * @return Configuration object
     */
    public AppConfiguration getConfiguration() { return config; }
    /**
     * Gets absolute work directory path.
     *
     * @return Work directory path
     */
    public String getWorkDirPath() { return workDirPath; }
    /**
     * Gets absolute images storage folder path.
     *
     * @return Path to images storage folder
     */
    public String getImagesDirPath() { return imagesDirPath; }
    /**
     * Gets absolute thumbnails storage folder path.
     *
     * @return Path to thumbnails storage folder
     */
    public String getThumbsDirPath() { return thumbsDirPath; }
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
                log.warn( msg + ": " + dir );
            } else {
                log.warn( "Failed to create directory: " + dir );
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
            f = File.createTempFile ( TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX, tempDir );
        } catch( IOException e ) {
            log.error( "Error while creating temporary file\n", e );
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
        } catch( Exception e ) {
            log.error( "Error while saving file\n", e );
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
            log.error( "Error while saving file\n", e );
        } catch( IOException e ) {
            log.error( "Error while saving file\n", e );
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
            log.error( "Error while deleting file\n", e );
        }
    }
    /**
     * Deletes file.
     * 
     * @param f File object
     */
    public static void deleteFile( File f ) {
        if( !f.delete() ) {
            log.error( "Error while deleting file:\n" + f.getName() );
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
        } catch( Exception e )  {
            log.error( "Error while writing object to stream", e );
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
        } catch( Exception e ) {
            log.error( "Error while reading object from stream", e );
        }
        return obj;
    }
}
//--------------------------------------------------------------------------------------------------

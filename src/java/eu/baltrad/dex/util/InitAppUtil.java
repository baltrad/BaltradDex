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
import java.util.List;
import java.util.Date;

/**
 * Utility class used to initialize application on startup.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
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
    /** Keystore file path */
    public static final String KS_FILE_PATH = "WEB-INF/conf/.dex_keystore.jks";
//---------------------------------------------------------------------------------------- Variables
    /** References logger object */
    private static Logger log = MessageLogger.getLogger( MessageLogger.SYS_DEX );
    /** Configuration object holding all necessary settings */
    private static AppConfiguration appConf; 
    /** Work directory */
    private static String workDir;
    /** Images directory */
    private static String imagesDir;
    /** Thumbnails directory */
    private static String thumbsDir;
    /** Certificates directory */
    private static String certsDir;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Load application settings.
     */
    public static synchronized AppConfiguration loadAppConf() {
        if( appConf == null ) {
            ConfigurationManager cm = new ConfigurationManager();
            appConf = cm.loadAppConf();
            createDirs();
            log.info( "Application successfully initialized" );   
        }
        return appConf;
    }
    /**
     * Save modified application settings.
     * 
     * @param _appConf Application settings to save
     */
    public static void saveAppConf( AppConfiguration _appConf ) {
        if( !appConf.equals( _appConf ) ) {
            appConf = _appConf;
            createDirs();
            log.info( "Application successfully initialized" );   
        }   
    }
    /**
     * Create necessary directories and initialize variables. 
     */
    private static void createDirs() {
        workDir = createDir( appConf.getWorkDir(), "Created work directory" );
        imagesDir = createDir( appConf.getWorkDir() + File.separator + 
                appConf.getImagesDir(), "New image storage directory created" );
        thumbsDir = createDir( appConf.getWorkDir() + File.separator + 
                appConf.getThumbsDir(), "New thumbs storage directory created" );
        certsDir = createDir( appConf.getWorkDir() + File.separator + 
                appConf.getCertsDir(), "New certificate storage directory created" );
    }
    /**
     * Gets configuration object.
     * 
     * @return Configuration object
     */
    public static AppConfiguration getConf() { return appConf; }
    /**
     * Gets work directory.
     *
     * @return Work directory
     */
    public static String getWorkDir() { return workDir; }
    /**
     * Gets images directory.
     *
     * @return Images directory
     */
    public static String getImagesDir() { return imagesDir; }
    /**
     * Gets thumbnails directory.
     *
     * @return Thumbnails directory
     */
    public static String getThumbsDir() { return thumbsDir; }
    /**
     * Gets certificates directory.
     * 
     * @return Certificates directory
     */
    public static String getCertsDir() { return certsDir; }
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
     * Deletes a file.
     * 
     * @param f File to delete 
     * @return True if file was successfully deleted
     */
    public static boolean deleteFile(File f) {
        return f.delete();
    }
    /**
     * Validates a string parameter.
     * 
     * @param param String parameter
     * @return True upon successful validation
     */
    public static boolean validate(String param) {
        boolean result = false;
        if (param != null) {
            if (!param.trim().isEmpty()) {
                result = true;
            }
        }
        return result;
    }
    /**
     * Validates a list parameter.
     * 
     * @param param List parameter
     * @return True upon successful validation
     */
    public static boolean validate(List list) {
        boolean result = false;
        if (list != null) {
            if (list.size() > 0) {
                result = true;
            }
        }
        return result;
    }
    /**
     * Deletes temporary files from a given directory. Files must be older than a given age.
     *
     * @param directory Directory where temporary files are stored
     * @param maxAge Maximum age of temporary file
     * @deprecated Use deleteFile() instead
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
}
//--------------------------------------------------------------------------------------------------

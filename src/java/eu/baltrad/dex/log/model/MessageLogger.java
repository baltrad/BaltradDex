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
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
***************************************************************************************************/

package eu.baltrad.dex.log.model;

import org.apache.log4j.Logger;

/**
 * Implements custom log message logger.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 0.6.6
 * @since 0.6.6
 *
 * <style type="text/css">
 *     #code { font-family:Courier; font-size:14px; background:#DCDCDC; }
 *     .key { color:blue; }
 *     .bold { font-weight:bold; }
 * </style>
 * <pre>
 * This class is of interest to you if you want to store messages generated by your software
 * component in the database.
 * Custom appender implemented in the DEX allows to store messages in the database. You can
 * also add your own appenders to e.g. save a message to a file.
 *
 * To log a message from your software component using default log appender, initialize
 * Logger object in the following way:
 * <div id="code">
 *      <span class="key">import</span> eu.baltrad.dex.log.model.MessageLogger;
 *      <span class="key">import</span> org.apache.log4j.Logger;
 *
 *      <span class="key">public class</span> <span class="bold">MyClass</span> {
 *          <span class="key">private</span> Logger log;
 *
 *          <span class="key">public</span> <span class="bold">MyClass()</span> {
 *              <span class="key">this</span>.log = MessageLogger.getLogger( MessageLogger.SYS_BEAST );
 *          }
 *
 *          <span class="key">public</span> <span class="bold">logStuff()</span> {
 *              log.info( "Info message" );
 *              log.warn( "Warning" );
 *              log.error( "Error" );
 *              // ...
 *          }
 *      }
 * </div>
 * Message is passed to LogAppender object, where LogEntry object is created and stored in
 * the database.
 * </pre>
 */
public class MessageLogger {
//---------------------------------------------------------------------------------------- Constants
    /** Marks messages generated by the DEX */
    public final static String SYS_DEX = "DEX";
    /** Marks messages generated by the BEAST */
    public final static String SYS_BEAST = "BEAST";
    /** Marks messages generated by the PGF */
    public final static String SYS_PGF = "PGF";
//---------------------------------------------------------------------------------------- Variables
    /** References logges object */
    private static Logger logger;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Retrieves logger object as singleton.
     * 
     * @param system Source system name used as logger label
     * @return Logger object
     */
    public static Logger getLogger( String system ) {
        if( logger == null ) {
            logger = Logger.getLogger( system );
            logger.addAppender( new LogAppender( system ) );
        }
        return logger;
    }
}
//--------------------------------------------------------------------------------------------------

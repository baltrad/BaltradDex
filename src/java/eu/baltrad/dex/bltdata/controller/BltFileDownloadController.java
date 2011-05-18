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

package eu.baltrad.dex.bltdata.controller;

import eu.baltrad.dex.log.model.*;
import eu.baltrad.dex.user.model.User;
import eu.baltrad.dex.util.ApplicationSecurityManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Download controller class implementing data download functionality.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0
 * @since 1.0
 */
public class BltFileDownloadController implements Controller {
//---------------------------------------------------------------------------------------- Constants
    public static final String FILE_PATH = "path";
//---------------------------------------------------------------------------------------- Variables
    private LogManager logManager;
    private String successView;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles file download request.
     * 
     * @param request Http request
     * @param response Http response
     * @return Model and view
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) {

        User user = ( User )ApplicationSecurityManager.getUser( request );
        ServletContext servletContext = request.getSession().getServletContext();
        String filePath = request.getParameter( FILE_PATH );
        String fileName = filePath.substring( filePath.lastIndexOf( File.separator ) + 1 );
        File file = new File( filePath );
        int fileSize = ( int )file.length();
        BufferedInputStream in = null;
        if( fileSize > 0 ) {
            try {
                in = new BufferedInputStream( new FileInputStream( file ) );
            } catch( FileNotFoundException e ) {
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_WARN, 
                        "File not found: " + e.getMessage() ) );
            }
            String mimeType = servletContext.getMimeType( filePath );
            response.setBufferSize( fileSize );
            response.setContentType( mimeType );
            response.setHeader( "Content-Disposition", "attachement; filename=\"" + fileName + "\"" );
            response.setContentLength( fileSize );
            try {
                FileCopyUtils.copy( in, response.getOutputStream() );
                in.close();
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch( IOException e ) {
                logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR, 
                        "Error downloading file: " + e.getMessage() ) );
            }
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_INFO, "User " +
                    user.getName() + " downloading file: " + file.getName() ) );
        } else {
            logManager.append( new LogEntry( LogEntry.LOG_SRC_DEX, LogEntry.LEVEL_ERROR,
                    "Invalid file size: " + fileSize ) );
        }
        return null;
    }
    /**
     * Method returns reference to success view name string.
     *
     * @return Reference to success view name string
     */
    public String getSuccessView() {
        return successView;
    }
    /**
     * Method sets reference to success view name string.
     *
     * @param Reference to success view name string
     */
    public void setSuccessView( String successView ) {
        this.successView = successView;
    }
    /**
     * Method gets reference to LogManager class instance.
     *
     * @return Reference to LogManager class instance
     */
    public LogManager getLogManager() { return logManager; }
    /**
     * Method sets reference to LogManager class instance.
     *
     * @param logManager Reference to LogManager class instance
     */
    public void setLogManager( LogManager logManager ) { this.logManager = logManager; }
}
//--------------------------------------------------------------------------------------------------

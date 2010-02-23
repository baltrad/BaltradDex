/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.controller;

import pl.imgw.baltrad.dex.model.LogManager;
import pl.imgw.baltrad.dex.model.User;

import pl.imgw.baltrad.dex.util.ApplicationSecurityManager;

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
import java.util.Date;


/**
 * Download controller class implementing data download functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DownloadController implements Controller {

//---------------------------------------------------------------------------------------- Constants
    public static final String FILE_PATH = "path";
//---------------------------------------------------------------------------------------- Variables
    private ApplicationSecurityManager applicationSecurityManager;
    private LogManager logManager;
    private String successView;

//------------------------------------------------------------------------------------------ Methods
    /**
     * Method handles http request.
     * 
     * @param request Http request
     * @param response Http response
     * @return Model and view
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response ) {

        User user = ( User )applicationSecurityManager.getUser( request );
        ServletContext servletContext = request.getSession().getServletContext();
        String filePath = request.getParameter( FILE_PATH );
        String fileName = filePath.substring( filePath.lastIndexOf( File.separator ) + 1 );
        File file = new File( servletContext.getRealPath( "/" ), filePath );
        int fileSize = ( int )file.length();
        BufferedInputStream in = null;

        if( fileSize > 0 ) {
            try {
                in = new BufferedInputStream( new FileInputStream( file ) );
            } catch( FileNotFoundException e ) {
                logManager.addLogEntry( new Date(), logManager.MSG_INFO, "File not found" );
            }
            String mimeType = servletContext.getMimeType( filePath );
            response.setBufferSize( fileSize );
            response.setContentType( mimeType );
            response.setHeader( "Content-Disposition", "attachement; filename=\""
                                                                               + fileName + "\"" );
            response.setContentLength( fileSize );
            try {
                FileCopyUtils.copy( in, response.getOutputStream() );
                in.close();
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch( IOException e ) {}

            logManager.addLogEntry( new Date(), logManager.MSG_INFO, "User " + user.getName() +
                                                        " downloading file: " + file.getName() );
        } else {
            logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Invalid file size: "
                                                                                    + fileSize );
        }
        return null;
    }

    /**
     * Method returns reference to ApplicationSecurityManager object.
     *
     * @return applicationSecurityManager Reference to ApplicationSecurityManager object
     */
    public ApplicationSecurityManager getApplicationSecurityManager() {
        return applicationSecurityManager;
    }

    /**
     * Method sets reference to ApplicationSecurityManager object.
     *
     * @param applicationSecurityManager Reference to ApplicationSecurityManager object
     */
    public void setApplicationSecurityManager(
                                        ApplicationSecurityManager applicationSecurityManager ) {
        this.applicationSecurityManager = applicationSecurityManager;
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

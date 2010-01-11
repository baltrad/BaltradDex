/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.FileUploadException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * Class implementing data data receiver functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Receiver extends HttpServlet implements Controller {

//---------------------------------------------------------------------------------------- Constants
    // Servlet virtual path
    private static final String SERVLET_VIRTUAL_PATH = "/";
    // Incoming data directory
    private static final String INCOMING_DATA_DIR = "incoming";
//---------------------------------------------------------------------------------------- Variables

//------------------------------------------------------------------------------------------ Methods

    /**
     * Method handles HTTP request.
     *
     * @param request Http request
     * @param response Http response
     * @return ModelAndView
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
                                                            throws ServletException, IOException {
        doGet( request, response );
        return new ModelAndView();
    }

    /**
     * Method overrides HTTP GET method.
     *
     * @param request Http request
     * @param response Http response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
                                                            throws ServletException, IOException {
        try {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            String ctxPath = request.getSession().getServletContext().getRealPath(
                                                                            SERVLET_VIRTUAL_PATH );
            File incomingDir = new File( ctxPath + INCOMING_DATA_DIR );
            if( !incomingDir.exists() ) {
                incomingDir.mkdir();
            }

            //String repositoryPath = ctxPath + REPOSITORY_PATH;
            //File repositoryFile = new File( repositoryPath );
            //diskFileItemFactory.setRepository( repositoryFile );

            List fileItemsList = null;
            ServletFileUpload servletFileUpload = new ServletFileUpload( diskFileItemFactory );
            try {
                fileItemsList = servletFileUpload.parseRequest( request );
            } catch( FileUploadException e ) {
                System.out.println( "Failed to complete upload request: " + e.getMessage() );
            }
            Iterator it = fileItemsList.iterator();
            while( it.hasNext() ) {
                DiskFileItem fileItem = ( DiskFileItem )it.next();
                if( !fileItem.isFormField() ) {
                    fileItem.write( new File( incomingDir + "/" + fileItem.getName() ) );
                }
            }
        } catch( Exception e ) {
            System.out.println( "Error while reading request: " + e.getMessage() );
        }
    }
}

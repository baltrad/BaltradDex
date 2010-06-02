/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model.core;

import eu.baltrad.dex.model.frame.BaltradFrameHandler;
import eu.baltrad.dex.model.frame.BaltradFrame;
import eu.baltrad.dex.model.log.LogManager;
import eu.baltrad.dex.util.InitAppUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.Date;

/**
 * Class implementing data receiver functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class Receiver extends HttpServlet implements Controller {
//---------------------------------------------------------------------------------------- Variables
    private LogManager logManager;
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
        return null;
    }
    /**
     * Method overrides HTTP POST method.
     *
     * @param request Http request
     * @param response Http response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
                                                            throws ServletException, IOException {
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterator = upload.getItemIterator( request );
            FileItemStream hdrItem = iterator.next();
            // Check if incoming frame is BaltradFrame
            if( hdrItem.getFieldName().equals( BaltradFrame.BF_XML_PART ) ) {
                InputStream hdrStream = hdrItem.openStream();
                BaltradFrameHandler bfHandler = new BaltradFrameHandler();
                // Handle form field / message XML header
                if( hdrItem.isFormField() ) {
                    // Get header string
                    String hdrStr = Streams.asString( hdrStream );
                    // Check frame content type
                    // Handle message frame
                    if( bfHandler.getBFContentType( hdrStr ).equals(
                            BaltradFrameHandler.BF_MSG_CONTENT ) ) {
                        logManager.addEntry( new Date(), bfHandler.getBFMessageClass( hdrStr ),
                                bfHandler.getBFMessageText( hdrStr ) );
                    }
                    // Handle data frame / file content
                    if( bfHandler.getBFContentType( hdrStr ).equals(
                            BaltradFrameHandler.BF_FILE_CONTENT ) && iterator.hasNext() ) {
                        // Get file content
                        logManager.addEntry( new Date(), LogManager.MSG_INFO, "New file " +
                                " received from " + bfHandler.getBFSender( hdrStr ) +
                                "\n: " + bfHandler.getBFFileName( hdrStr ) );
                        // Save file content to local disk
                        String incomingDir = InitAppUtil.getIncomingDataDir() +
                                File.separator + bfHandler.getBFSender( hdrStr );
                        InitAppUtil.makeDir( incomingDir );
                        FileItemStream fileItem = iterator.next();
                        InputStream fileStream = fileItem.openStream();
                        String absFilePath = incomingDir + File.separator +
                                bfHandler.getBFFileName( hdrStr );
                        InitAppUtil.saveFile( fileStream, absFilePath );
                    }
                } 
            } else {

                // ... redirect the frame in case it was not recognized as BaltradFrame ...

            }
        } catch( FileUploadException e ) {
            logManager.addEntry( new Date(), LogManager.MSG_ERR, "Error while processing " +
                    "incoming frame: " + e.getMessage() );
        }
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


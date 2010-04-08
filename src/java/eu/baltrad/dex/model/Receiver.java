/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package eu.baltrad.dex.model;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Date;

/**
 * Class implementing data receiver functionality.
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
        return new ModelAndView();
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
                        logManager.addLogEntry( new Date(), bfHandler.getBFMessageClass( hdrStr ),
                                bfHandler.getBFMessageText( hdrStr ) );
                    }
                    // Handle data frame / file content
                    if( bfHandler.getBFContentType( hdrStr ).equals(
                            BaltradFrameHandler.BF_FILE_CONTENT ) && iterator.hasNext() ) {
                        // Get file content
                        String fileId = bfHandler.getBFFileId( hdrStr );
                        logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "New file " +
                                " received from " + bfHandler.getBFSender( hdrStr ) +
                                ", file ID: " + fileId );
                        // Save file content to local disk
                        String ctxPath = request.getSession().getServletContext().getRealPath(
                            SERVLET_VIRTUAL_PATH );
                        // Create local directory if not exists
                        String incomingDir = ctxPath + INCOMING_DATA_DIR + File.separator +
                                bfHandler.getBFSender( hdrStr );
                        makeDir( incomingDir );
                        FileItemStream fileItem = iterator.next();
                        InputStream fileStream = fileItem.openStream();
                        String absFilePath = incomingDir + File.separator +
                                bfHandler.getBFFileName( hdrStr );
                        saveFile( fileStream, absFilePath, fileId );
                    }
                } 
            } else {

                // ... redirect the frame in case it was not recognized as BaltradFrame ...

            }
        } catch( FileUploadException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while processing " +
                    "incoming frame: " + e.getMessage() );
        }
    }
    /**
     * Method extracts relative file name from absolute file path string.
     *
     * @param absFilePath Absolute file path string
     * @return Relative file name
     */
    public String getRelFileName( String absFilePath ) {
        return absFilePath.substring( absFilePath.lastIndexOf( File.separator ) + 1,
                    absFilePath.length() );
    }
    /**
     * Method creates new directory for incoming data files.
     *
     * @param directoryPath Path to the directory
     */
    public void makeDir( String directoryPath ) {
        File dir = new File( directoryPath );
        if( !dir.exists() ) {
            dir.mkdirs();
            logManager.addLogEntry( new Date(), LogManager.MSG_INFO, "New directory created: "
                    + directoryPath );
        }
    }
    /**
     * Method saves data from input stream to file with a given name.
     *
     * @param is Input data stream
     * @param dstFileName Output file name
     */
    public void saveFile( InputStream is, String dstFileName, String fileId ) {
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
                    " saved, file ID: " + fileId );
        } catch( FileNotFoundException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: "
                    + e.getMessage() );
        } catch( IOException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Error while saving file: "
                    + e.getMessage() );
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


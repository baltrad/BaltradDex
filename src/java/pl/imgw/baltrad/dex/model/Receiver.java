/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
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
        doPost( request, response );
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
    public void doPost( HttpServletRequest request, HttpServletResponse response )
                                                            throws ServletException, IOException {
        try {
            BaltradEnvelope baltradEnvelope = null;
            // Get object input stream
            ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream(
                    request.getInputStream() ) );
            baltradEnvelope = ( BaltradEnvelope )ois.readObject();
            ois.close();
            // Check envelope content type
            String responseStr = "";
            if( baltradEnvelope.getContentType().equals( BaltradEnvelope.BE_DATA_OBJECT ) ) {
                // Create response message
                responseStr = logManager.MSG_INFO + "_New file object received: " +
                        getRelFileName( baltradEnvelope.getAbsFilePath() );
                // Save file in the local file system
                String contextPath = request.getSession().getServletContext().getRealPath(
                        SERVLET_VIRTUAL_PATH );
                /* Sender'a address have to be added to the local data directory */

                // Incoming directory name
                String incomingDirName = contextPath + INCOMING_DATA_DIR + File.separator +
                        baltradEnvelope.getChannelName();
                // Absolute incoming file name
                String incomingFileName = incomingDirName + File.separator + getRelFileName(
                        baltradEnvelope.getAbsFilePath() );
                makeDir( incomingDirName );

                //

                saveFileAs( baltradEnvelope.getDataFile(), incomingFileName );
            }
            if( baltradEnvelope.getContentType().equals( BaltradEnvelope.BE_MSG_OBJECT ) ) {
                // Create response message
                responseStr = logManager.MSG_INFO + "_New message object received";
                
                /* Handle the message */

            }
            // Create object output stream
            ObjectOutputStream oos = new ObjectOutputStream( new BufferedOutputStream(
                    response.getOutputStream() ) );
            // Reset the response
            response.reset();
            // Write response to the stream
            oos.writeObject( responseStr );
            oos.flush();
            oos.close();
        } catch( Exception e ) {
            logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Receiver error: "
                                                                                + e.getMessage() );
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
            logManager.addLogEntry( new Date(), logManager.MSG_INFO, "New directory created: "
                    + directoryPath );
        }
    }
    /**
     * Method saves file object under a given name
     *
     * @param srcFile Source file object
     * @param dstFileName Output file name
     */
    public void saveFileAs( File srcFile, String dstFileName ) {
        try {
            File dstFile = new File( dstFileName );
            FileInputStream fis = new FileInputStream( srcFile );
            FileOutputStream fos = new FileOutputStream( dstFile );
            byte[] bytes = new byte[ 1024 ];
            int len;
            while( ( len = fis.read( bytes ) ) > 0 ) {
                fos.write( bytes, 0, len);
            }
            fis.close();
            fos.close();
            logManager.addLogEntry( new Date(), logManager.MSG_INFO, "Incoming file saved: "
                    + dstFileName );
        } catch( FileNotFoundException e ) {
            logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Error while saving file: "
                    + e.getMessage() );

        } catch( IOException e ) {
            logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Error while saving file: "
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

/*
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.FileUploadException;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.MultipartStream;
*/

/*
DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
String ctxPath = request.getSession().getServletContext().getRealPath( SERVLET_VIRTUAL_PATH );
File incomingDir = new File( ctxPath + INCOMING_DATA_DIR );
if( !incomingDir.exists() ) {
    incomingDir.mkdir();
}
String repositoryPath = ctxPath + REPOSITORY_PATH;
File repositoryFile = new File( repositoryPath );
diskFileItemFactory.setRepository( repositoryFile );
List fileItemsList = null;
ServletFileUpload servletFileUpload = new ServletFileUpload( diskFileItemFactory );
try {
    fileItemsList = servletFileUpload.parseRequest( request );
} catch( FileUploadException e ) {
    logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Receiver error: "
                                                                    + e.getMessage() );
}
/*Iterator it = fileItemsList.iterator();
while( it.hasNext() ) {
    DiskFileItem fileItem = ( DiskFileItem )it.next();
    if( !fileItem.isFormField() ) {
        fileItem.write( new File( incomingDir + "/" + fileItem.getName() ) );
        logManager.addLogEntry( new Date(), logManager.MSG_INFO, "Incoming file: " +
                                                            fileItem.getName() );
    }
}
*/



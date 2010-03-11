/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

/**
 * Class encapsulating standard message structure and functionality to be used
 * within Baltrad data exchange, storage and production systems.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class BaltradFrame extends MultipartEntity {
//---------------------------------------------------------------------------------------- Constants
    // XML element / document encoding
    private static final String XML_ENCODING = "UTF-8";
    // XML elements / available MIME types
    public static final String BF_MIME_MULTIPART = "multipart/form-data";
    // Character set
    private static final Charset BF_CHARSET = Charset.forName( XML_ENCODING );
    // Multipart message parts identifiers
    public static final String BF_XML_PART = "<baltrad_frame_xml/>";
    public static final String BF_FILE_PART = "<baltrad_frame_file/>";
//---------------------------------------------------------------------------------------- Variables
    // Log manager
    private LogManager logManager = new LogManager();
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public BaltradFrame() {}
    /**
     * Constructor creates data frame.
     *
     * @param xmlHdrStr XML header string
     * @param absFilePath Absolute file path
     */
    public BaltradFrame( String xmlHdrStr, String absFilePath ) {
        try {
            // Create XML string header using given encoding
            StringBody sbXMLHeader = new StringBody( xmlHdrStr, BF_MIME_MULTIPART, BF_CHARSET );
            // Create file content body
            File f = new File( absFilePath );
            ContentBody cbFile = new FileBody( f, BF_MIME_MULTIPART );
            // Add XML header string
            this.addPart( BF_XML_PART, sbXMLHeader );
            // Add file body content
            this.addPart( BF_FILE_PART, cbFile );
        } catch( UnsupportedEncodingException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Unsupported encoding " +
                    "type: " + e.getMessage() );
        }
    }
    /**
     * Constructor creates message frame.
     *
     * @param xmlHdrStr XML header string
     */
    public BaltradFrame( String xmlHdrStr ) {
        try {
            // Create XML string header using given encoding
            StringBody sbXMLHeader = new StringBody( xmlHdrStr, BF_MIME_MULTIPART, BF_CHARSET );
            // Add XML header string
            this.addPart( BF_XML_PART, sbXMLHeader );
        } catch( UnsupportedEncodingException e ) {
            logManager.addLogEntry( new Date(), LogManager.MSG_ERR, "Unsupported encoding " +
                    "type: " + e.getMessage() );
        }
    }
}
//--------------------------------------------------------------------------------------------------
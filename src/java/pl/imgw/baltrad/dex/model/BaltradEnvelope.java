/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2010
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.io.Serializable;
import java.io.File;

/**
 * Class encapsulating standard message structure and functionality to be used
 * within Baltrad data exchange, storage and production systems.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class BaltradEnvelope implements Serializable {
//---------------------------------------------------------------------------------------- Constants
    // Message types
    public static final String BE_MIME_MULTIPART = "multipart/form-data";
    public static final String BE_MIME_APPLICATION = "application/octet-stream";
    public static final String BE_MIME_TEXT = "text/plain";
    public static final String BE_MIME_TEXT_XML = "text/xml";
    // Content type identifiers
    public static final String BE_MSG_OBJECT = "<baltrad_msg_object/>";
    public static final String BE_DATA_OBJECT = "<baltrad_data_object/>";
//---------------------------------------------------------------------------------------- Variables
    // Message MIME type
    private String mimeType;
    // Content type identifier
    private String contentType;
    // Message text string
    private String msgText;
    // Envelope sender's address / Baltrad node address
    private String senderAddress;
    // Data channel name / radar station of origin
    private String channelName;
    // Absolute file path
    private String absFilePath;
    // Data file
    private File dataFile;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public BaltradEnvelope() {}
    /**
     * Constructor creates envelope with data content.
     *
     * @param mimeType Message mime type
     * @param contentType Content type
     * @param senderAddress Envelope sender's address
     * @param channelName Data channel name
     * @param absFilePath Absolute file path
     * @param dataFile Data file
     */
    public BaltradEnvelope( String mimeType, String contentType, String senderAddress,
            String channelName, String absFilePath, File dataFile ) {
        this.mimeType = mimeType;
        this.contentType = contentType;
        this.senderAddress = senderAddress;
        this.channelName = channelName;
        this.absFilePath = absFilePath;
        this.dataFile = dataFile;
    }
    /**
     * Constructor creates envelope with message content.
     *
     * @param mimeType Message mime type
     * @param contentType Content type
     * @param senderAddress Envelope sender's address
     * @param msgText Message text string
     */
    public BaltradEnvelope( String mimeType, String contentType, String senderAddress,
            String msgText ) {
        this.mimeType = mimeType;
        this.contentType = contentType;
        this.senderAddress = senderAddress;
        this.msgText = msgText;
    }
    /**
     * Method gets message mime type.
     *
     * @return Message mime type
     */
    public String getMimeType() { return mimeType; }
    /**
     * Method sets message mime type.
     *
     * @param mimeType Message mime type
     */
    public void setMimeType( String mimeType ) { this.mimeType = mimeType; }
    /**
     * Method gets envelope content type.
     *
     * @return Envelope content type
     */
    public String getContentType() { return contentType; }
    /**
     * Method sets envelope content type.
     *
     * @param contentType Envelope content type
     */
    public void setContentType( String contentType ) { this.contentType = contentType; }
    /**
     * Method gets address of the sender.
     *
     * @return Address of the sender
     */
    public String getSenderAddress() { return senderAddress; }
    /**
     * Method sets address of the sender.
     *
     * @param senderAddress Address of the sender
     */
    public void setSenderAddress( String senderAddress ) { this.senderAddress = senderAddress; }
    /**
     * Method gets data channel name
     *
     * @return Channel name
     */
    public String getChannelName() { return channelName; }
    /**
     * Method sets data channel name
     *
     * @param channelName Data channel name
     */
    public void setChannelName( String channelName ) { this.channelName = channelName; }
    /**
     * Method gets message text. 
     * 
     * @return Message text
     */
    public String getMsgText() { return msgText; }
    /**
     * Method sets message text.
     * 
     * @param msgText Message text
     */
    public void setMsgText( String msgText ) { this.msgText = msgText; }
    /**
     * Method gets absolute file path.
     *
     * @return Absolute file path
     */
    public String getAbsFilePath() { return absFilePath; }
    /**
     * Method sets absolute file path.
     *
     * @param absFilePath Absolute file path
     */
    public void setAbsFilePath( String absFilePath ) { this.absFilePath = absFilePath; }
    /**
     * Method gets data file.
     *
     * @return Data file
     */
    public File getDataFile() { return dataFile; }
    /**
     * Method sets data file.
     *
     * @param dataFile Data file
     */
    public void setDataFile( File dataFile ) { this.dataFile = dataFile; }
}
//--------------------------------------------------------------------------------------------------


/*
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
*/
/**
 * Method creates Baltrad message
 *
 * @param uri Receiver's URI
 * @param fileName Absolute content file name
 * @return HttpPost class object ready to be used HttpClient
 *
public HttpPost packBaltradMessage( String uri, String fileName ) {
    try {
        httpPost = new HttpPost( uri );
        File file = new File( fileName );
        MultipartEntity mpMessage = new MultipartEntity();
        // Create boundary string content using given encoding
        StringBody sbBoundary = new StringBody( BM_BOUNDARY_STR, BM_MIME_TYPE, BM_CHARSET );
        // Create file content
        ContentBody cbData = new FileBody( file, BM_MIME_TYPE );
        // Add boundary string
        mpMessage.addPart( BM_STR_BODY_NAME, sbBoundary );
        // Add file content
        mpMessage.addPart( BM_FILE_BODY_NAME, cbData );
        httpPost.setEntity( mpMessage );
    } catch( UnsupportedEncodingException e ) {
        logManager.addLogEntry( new Date(), logManager.MSG_ERR, "Unsupported encoding " +
                                                                "type: " + e.getMessage() );
    }
    return httpPost;
}*/

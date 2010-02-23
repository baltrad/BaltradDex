/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

import java.net.URL;
import java.net.HttpURLConnection;

import java.util.Date;

/**
 * Class implementing Baltrad message handling functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class EnvelopeHandler {
//---------------------------------------------------------------------------------------- Constants
    private static final String REQUEST_POST_METHOD = "POST";
    private static final String REQUEST_CONTENT_TYPE = "Content-Type";
    private static final String SERVER_RESPONSE_STR = "[Server response] ";
    private static final String RESPONSE_SEPARATOR = "_";
//---------------------------------------------------------------------------------------- Variables
    // Envelope sender's address / Baltrad node address
    private String senderAddress;
    // Data channel name / radar station of origin
    private String channelName;
    // Absolute file path
    private String absFilePath;
    // Reference to LoManager class object
    private LogManager logManager;
//------------------------------------------------------------------------------------------ Methods
    /**
     * Default constructor.
     */
    public EnvelopeHandler() {}
    /**
     * Constructor sets field values.
     *
     * @param senderAddress Envelope sender's address
     * @param channelName Data channel name
     * @param absFilePath Absolute data file path
     * @param logManager Reference to log manager class instance
     */
    public EnvelopeHandler( String senderAddress, String channelName, String absFilePath,
            LogManager logManager ) {
        this.senderAddress = senderAddress;
        this.channelName = channelName;
        this.absFilePath = absFilePath;
        this.logManager = logManager;
    }
    /**
     * Method posts data on the receiver's server.
     */
    public void postEnvelope() {
        try {
            BaltradEnvelope baltradEnvelope = new BaltradEnvelope(
                    BaltradEnvelope.BE_MIME_APPLICATION, BaltradEnvelope.BE_DATA_OBJECT,
                    getSenderAddress(), getChannelName(), getAbsFilePath(),
                    new File( getAbsFilePath() ) );
            // Create url object
            URL url = new URL( getSenderAddress() );
            // Prepare connection
            HttpURLConnection con = ( HttpURLConnection )url.openConnection();
            con.setDoOutput( true );
            con.setDoInput( true );
            con.setUseCaches(false);
            con.setRequestMethod( REQUEST_POST_METHOD );
            con.setRequestProperty( REQUEST_CONTENT_TYPE, BaltradEnvelope.BE_MIME_APPLICATION );
            con.connect();
            // Create object output stream
            ObjectOutputStream oos = new ObjectOutputStream( new BufferedOutputStream(
                    con.getOutputStream() ) );
            // Write object to the stream
            oos.writeObject( baltradEnvelope );
            oos.flush();
            oos.close();
            // Get server response
            ObjectInputStream ois = new ObjectInputStream( new BufferedInputStream(
                    con.getInputStream() ) );
            String responseStr = ( String )ois.readObject();
            ois.close();
            // Parse response string
            String entryType = responseStr.substring( 0, responseStr.lastIndexOf( 
                    RESPONSE_SEPARATOR ) );
            String entryMsg = responseStr.substring( responseStr.lastIndexOf( RESPONSE_SEPARATOR )
                    + 1, responseStr.length() );
            getLogManager().addLogEntry( new Date(), entryType, SERVER_RESPONSE_STR + entryMsg );
        } catch( IOException e ) {
            getLogManager().addLogEntry( new Date(), getLogManager().MSG_ERR, 
                    "Envelope handler error: " + e.getMessage() );
        } catch( Exception e ) {
            getLogManager().addLogEntry( new Date(), getLogManager().MSG_ERR, 
                    "Envelope handler error: " + e.getMessage() );
        }
    }
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
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
*/
/*
httpClient = new DefaultHttpClient();
httpClient.getParams().setParameter( CoreProtocolPNames.PROTOCOL_VERSION,
                                                                HttpVersion.HTTP_1_1 );
// Create Baltrad message
BaltradEnvelope baltradMsg = new BaltradEnvelope();
HttpPost httpPost = baltradMsg.packBaltradMessage( getUrl(), getFileName() );
HttpResponse response = httpClient.execute( httpPost );
HttpEntity resEntity = response.getEntity();
if( resEntity != null ) {
    resEntity.consumeContent();
}
httpClient.getConnectionManager().shutdown();
*/

/*
 * BaltradNode :: Radar data exchange and communication system
 * Remote Sensing Department, Institute of Meteorology and Water Management
 * Maciej Szewczykowski, 2009
 *
 * maciej.szewczykowski@imgw.pl
 */

package pl.imgw.baltrad.dex.model;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;


/**
 * Class implementing data pushing functionality.
 *
 * @author szewczenko
 * @version 1.0
 * @since 1.0
 */
public class DataPusher {

//---------------------------------------------------------------------------------------- Constants
    private static final String MESSAGE_TYPE = "multipart/form-data";
    private static final String MESSAGE_PART_NAME = "data_file";
//---------------------------------------------------------------------------------------- Variables
    // Receiver address
    private String urlAddress;
    // User name
    private String user;
    // Password
    private String password;
    // Name of the file to send
    private String fileName;
    // Http client instance
    private HttpClient httpClient;
//------------------------------------------------------------------------------------------ Methods

    /**
     * Constructor.
     */
    public DataPusher() {}

    /**
     * Method pushes data to the receiver.
     */
    public void push() {
        try {
            httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter( CoreProtocolPNames.PROTOCOL_VERSION,
                                                                            HttpVersion.HTTP_1_1 );
            HttpPost httpPost = new HttpPost( getUrlAddress() );
            File file = new File( getFileName() );
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody cbFile = new FileBody( file, MESSAGE_TYPE );
            mpEntity.addPart( MESSAGE_PART_NAME, cbFile );
            httpPost.setEntity( mpEntity );
            HttpResponse response = httpClient.execute( httpPost );
            /*HttpEntity resEntity = response.getEntity();
            System.out.println( response.getStatusLine() );
            if( resEntity != null ) {
                System.out.println( "Server's response: " + EntityUtils.toString( resEntity ) );
            }
            if( resEntity != null ) {
                resEntity.consumeContent();
            }*/
            httpClient.getConnectionManager().shutdown();
            
        } catch( IOException e ) {
            System.out.println( "Transmitter error: " + e.getMessage() );
        } catch( Exception e ) {
            System.out.println( "Transmitter error: " + e.getMessage() );
        }
    }

    /**
     * Method returns receiver's URL address.
     *
     * @return Receiver's URL address
     */
    public String getUrlAddress() {
        return urlAddress;
    }

    /**
     * Method sets receiver's URL address.
     *
     * @param urlAddress Receiver's URL address
     */
    public void setUrlAddress( String urlAddress ) {
        this.urlAddress = urlAddress;
    }

    /**
     * Method returns user name.
     *
     * @return User name
     */
    public String getUser() {
        return user;
    }

    /**
     * Method sets user name.
     *
     * @param user User name
     */
    public void setUser( String user ) {
        this.user = user;
    }

    /**
     * Method returns password.
     *
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method sets password.
     *
     * @param password Password
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * Method returns name of the file to send.
     *
     * @return Name of the file to send
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Method sets name of the file to send.
     *
     * @param fileName Name of the file to send
     */
    public void setFileName( String fileName ) {
        this.fileName = fileName;
    }

}

//--------------------------------------------------------------------------------------------------
